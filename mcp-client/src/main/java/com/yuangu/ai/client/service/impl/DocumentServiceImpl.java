package com.yuangu.ai.client.service.impl;

import com.yuangu.ai.client.service.IDocumentService;
import com.yuangu.ai.client.util.CustomTextSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements IDocumentService {

    private final RedisVectorStore redisVectorStore;

    @Override
    public void upload(String fileName, InputStream is) {
        try {
            log.info("开始上传文档: {}", fileName);

            TextReader textReader = new TextReader(new InputStreamResource(is));
            // 添加元数据
            textReader.getCustomMetadata().put("fileName", fileName);

            // 获取文档内容
            List<Document> documents = textReader.get();
            log.info("文档读取完成，共 {} 个原始文档片段", documents.size());

            // 对文本进行切分
            // TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
            // List<Document> documentList = tokenTextSplitter.apply(documents);

            // TODO 市面主流的切分规则有哪些
            // 自定义文本切分器
            CustomTextSplitter textSplitter = new CustomTextSplitter();
            // 一定要过滤掉 Document 中 text 为空字符串的
            documents = textSplitter.apply(documents).stream().filter(document -> StringUtils.isNotBlank(document.getText())).toList();
            log.info("文档切分完成，共 {} 个文档片段", documents.size());

            // 存储到向量数据库
            if (documents.isEmpty()) {
                log.warn("文档切分后为空，跳过向量存储: {}", fileName);
                return;
            }

            // 批量处理文档，避免一次性发送过多文档导致 API 超载
            int batchSize = 5; // 每批处理 50 个文档
            int totalBatches = (documents.size() + batchSize - 1) / batchSize;
            log.info("开始批量存储文档到向量数据库，共 {} 个文档片段，分 {} 批处理", documents.size(), totalBatches);

            for (int i = 0; i < documents.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, documents.size());
                List<Document> batch = documents.subList(i, endIndex);
                int batchNumber = (i / batchSize) + 1;

                try {
                    log.debug("处理第 {}/{} 批，包含 {} 个文档片段", batchNumber, totalBatches, batch.size());
                    redisVectorStore.add(batch);
                    log.debug("第 {}/{} 批文档成功存储", batchNumber, totalBatches);
                } catch (Exception e) {
                    log.error("第 {}/{} 批文档存储失败，包含 {} 个文档片段", batchNumber, totalBatches, batch.size(), e);
                    throw e; // 重新抛出异常，让外层异常处理逻辑处理
                }
            }

            log.info("文档成功存储到 Redis 向量数据库: {}, 共 {} 个文档片段，分 {} 批处理完成", fileName, documents.size(), totalBatches);

        } catch (TransientAiException e) {
            // Spring AI 重试异常，通常包装了底层的 HTTP 错误
            log.error("Spring AI 临时异常 - 无法存储文档: {}", fileName, e);

            Throwable cause = e.getCause();
            String errorMsg = "向量化过程中发生错误";

            // 检查是否是 HTTP 错误
            if (cause instanceof HttpServerErrorException httpError) {
                errorMsg = String.format("OpenAI API 服务器错误 (HTTP %d): %s",
                        httpError.getStatusCode().value(), httpError.getStatusText());

                // 检查响应内容是否为 HTML（通常意味着端点配置错误）
                String responseBody = httpError.getResponseBodyAsString();
                if (responseBody.contains("<!DOCTYPE html>") || responseBody.contains("<html")) {
                    String detailedMsg = String.format(
                            "OpenAI API 返回了 HTML 错误页面而非 JSON 响应，这通常意味着：" +
                                    "\n1. OPENAI_BASE_URL 配置错误，可能指向了错误的端点" +
                                    "\n2. 该端点不支持 OpenAI 兼容的 API" +
                                    "\n3. API 服务可能未正确启动或配置" +
                                    "\n当前请求状态码: %d，请检查环境变量 OPENAI_BASE_URL 的配置",
                            httpError.getStatusCode().value()
                    );
                    log.error(detailedMsg);
                    throw new RuntimeException(detailedMsg, e);
                }
                throw new RuntimeException(errorMsg + "，请检查 OpenAI API 服务状态", e);
            } else if (cause instanceof HttpClientErrorException clientError) {
                errorMsg = String.format("OpenAI API 客户端错误 (HTTP %d): %s",
                        clientError.getStatusCode().value(), clientError.getStatusText());
                log.error("{} - 无法存储文档: {}", errorMsg, fileName, e);

                if (clientError.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new RuntimeException("OpenAI API 认证失败，请检查 OPENAI_API_KEY 环境变量", e);
                } else if (clientError.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new RuntimeException("OpenAI API 端点不存在，请检查 OPENAI_BASE_URL 环境变量", e);
                }
                throw new RuntimeException(errorMsg, e);
            } else {
                // 其他类型的异常
                String message = e.getMessage();
                if (message != null && message.contains("HTTP 500")) {
                    String detailedMsg = """
                            检测到 HTTP 500 错误，可能是 OpenAI API 配置问题。\
                            
                            请检查以下配置：\
                            
                            1. OPENAI_BASE_URL 环境变量是否正确（应为 OpenAI 兼容的 API 端点，例如: https://api.openai.com/v1）\
                            
                            2. OPENAI_API_KEY 环境变量是否有效\
                            
                            3. 目标 API 服务是否正常运行\
                            
                            4. 端点路径是否正确（应包含 /v1 路径）""";
                    log.error(detailedMsg);
                    throw new RuntimeException(detailedMsg + "\n原始错误: " + message, e);
                }
                throw new RuntimeException("向量化失败: " + (message != null ? message : e.getClass().getSimpleName()), e);
            }
        } catch (HttpServerErrorException e) {
            // HTTP 5xx 错误处理
            String errorMsg = String.format("OpenAI API 服务器错误 (HTTP %d): %s",
                    e.getStatusCode().value(), e.getStatusText());
            log.error("{} - 无法存储文档: {}", errorMsg, fileName, e);

            // 检查响应内容是否为 HTML（通常意味着端点配置错误）
            String responseBody = e.getResponseBodyAsString();
            if (responseBody != null && (responseBody.contains("<!DOCTYPE html>") || responseBody.contains("<html"))) {
                String detailedMsg = String.format(
                        "OpenAI API 返回了 HTML 错误页面而非 JSON 响应，这通常意味着：" +
                                "\n1. OPENAI_BASE_URL 配置错误，可能指向了错误的端点" +
                                "\n2. 该端点不支持 OpenAI 兼容的 API" +
                                "\n当前请求状态码: %d，请检查环境变量 OPENAI_BASE_URL 的配置",
                        e.getStatusCode().value()
                );
                log.error(detailedMsg);
                throw new RuntimeException(detailedMsg, e);
            }
            throw new RuntimeException(errorMsg + "，请检查 OpenAI API 服务状态", e);
        } catch (HttpClientErrorException e) {
            // HTTP 4xx 错误处理
            String errorMsg = String.format("OpenAI API 客户端错误 (HTTP %d): %s",
                    e.getStatusCode().value(), e.getStatusText());
            log.error("{} - 无法存储文档: {}", errorMsg, fileName, e);

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("OpenAI API 认证失败，请检查 OPENAI_API_KEY 环境变量", e);
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("OpenAI API 端点不存在，请检查 OPENAI_BASE_URL 环境变量", e);
            }
            throw new RuntimeException(errorMsg, e);
        } catch (RestClientException e) {
            // 网络连接错误
            log.error("无法连接到 OpenAI API，无法存储文档: {}", fileName, e);
            throw new RuntimeException("无法连接到 OpenAI API，请检查网络连接和 OPENAI_BASE_URL 配置: " + e.getMessage(), e);
        } catch (org.springframework.data.redis.RedisConnectionFailureException e) {
            log.error("Redis 连接失败，无法存储文档: {}", fileName, e);
            throw new RuntimeException("Redis 连接失败: " + e.getMessage(), e);
        } catch (org.springframework.data.redis.serializer.SerializationException e) {
            log.error("序列化失败，无法存储文档: {}", fileName, e);
            throw new RuntimeException("文档序列化失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("上传文档时发生未知异常: {}", fileName, e);

            // 检查异常消息中是否包含 HTTP 500 相关信息
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("HTTP 500")) {
                String detailedMsg = """
                        检测到 HTTP 500 错误，可能是 OpenAI API 配置问题。\
                        
                        请检查以下配置：\
                        
                        1. OPENAI_BASE_URL 环境变量是否正确（应为 OpenAI 兼容的 API 端点，例如: https://api.openai.com/v1）\
                        
                        2. OPENAI_API_KEY 环境变量是否有效\
                        
                        3. 目标 API 服务是否正常运行\
                        
                        4. 端点路径是否正确（应包含 /v1 路径）""";
                log.error(detailedMsg);
                throw new RuntimeException(detailedMsg + "\n原始错误: " + errorMessage, e);
            }

            throw new RuntimeException("文档上传失败: " + (errorMessage != null ? errorMessage : e.getClass().getSimpleName()), e);
        }
    }

    @Override
    public List<Document> similaritySearch(String question) {
        return redisVectorStore.similaritySearch(question);
    }
}
