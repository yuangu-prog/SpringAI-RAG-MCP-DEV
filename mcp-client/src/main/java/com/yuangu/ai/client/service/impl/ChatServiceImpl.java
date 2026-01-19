package com.yuangu.ai.client.service.impl;

import com.yuangu.ai.client.entity.request.GetChatRequest;
import com.yuangu.ai.client.entity.searxng.SearchResult;
import com.yuangu.ai.client.enums.SseMessageType;
import com.yuangu.ai.client.service.IChatService;
import com.yuangu.ai.client.service.IDocumentService;
import com.yuangu.ai.client.util.SseServerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {

    private final IDocumentService documentService;
    private final ChatClient chatClient;


    private static final String RAG_PROMPT = """ 
            基于上下文的知识库内容回答问题：
            【上下文】
             {{context}}
            
            【问题】
            {{question}}
            
            【输出】
            如果没有查到，请回复：不知道。
            如果查到，请回复具体的内容。不相关的近似内容不必提到。
            """;

    private static final String SEARXNG_PROMPT = """ 
            基于上下文的知识库内容回答问题：
            【上下文】
             {{context}}
            
            【问题】
            {{question}}
            
            【输出】
            如果没有查到，请回复：不知道。
            如果查到，请回复具体的内容。不相关的近似内容不必提到。
            """;


    @Override
    public void chat(GetChatRequest request, SseEmitter sseEmitter) {

        String userInput = request.getUserInput();

        Prompt prompt = null;
        if (request.getChoseKnowledge()) {
            // 从知识库查询相关知识
            String contentFromKnowledge = Optional.ofNullable(documentService.similaritySearch(userInput)).orElse(Collections.emptyList())
                    .stream()
                    .map(Document::getText)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("\n"));
            // 封装 Prompt 模版
            prompt = Prompt.builder().content(RAG_PROMPT.replaceAll("\\{\\{context}}", contentFromKnowledge).replaceAll("\\{\\{question}}", userInput)).build();
        }

        // 使用响应式方式处理，确保在正确的线程上下文中执行
        try {
            // 发送开始消息
            SseServerUtil.sendMessage(request.getUid(), StringUtils.EMPTY, SseMessageType.ADD);

            // 流式调用 AI
            Flux<String> responseStream = null;
            if (prompt == null) {
                responseStream = chatClient.prompt().user(request.getUserInput()).stream().content();
            } else {
                responseStream = chatClient.prompt(prompt).user(request.getUserInput()).stream().content();
            }

            // 订阅并处理流式响应
            responseStream.
                    takeWhile(str -> {
                        log.info("take while result = {}", str);
                        return Boolean.TRUE;
                    }).
                    doOnNext(content -> {
                        // 发送流式内容
                        log.debug("发送流式内容: {}", content);
                        SseServerUtil.sendMessage(sseEmitter, request.getUid(), content, SseMessageType.MESSAGE);
                    }).doOnComplete(() -> {
                        // 发送完成消息
                        log.info("流式响应完成");
                        SseServerUtil.sendMessage(request.getUid(), StringUtils.EMPTY, SseMessageType.FINISH);
                        SseServerUtil.sendEndMessage(request.getUid());
                    }).doOnError(error -> {
                        // 发送错误消息
                        log.error("AI 对话异常", error);
                        try {
                            SseServerUtil.sendMessage(request.getUid(), "发生错误: " + error.getMessage(), SseMessageType.ERROR);
                            SseServerUtil.sendEndMessage(request.getUid());
                        } catch (Exception ex) {
                            log.error("发送错误消息失败", ex);
                        }
                    }).onErrorResume(error -> {
                        return Flux.empty();
                    })
                    .doOnSubscribe(subscription -> {
                        log.info("开始订阅响应流");
                    }).doFinally(signal -> {
                        // 确保资源清理
                        if (signal == SignalType.ON_ERROR || signal == SignalType.CANCEL) {
                            sseEmitter.complete();
                        }
                    })
                    .subscribeOn(Schedulers.boundedElastic()).subscribe();
        } catch (Exception e) {
            log.error("处理对话请求异常", e);
            try {
                SseServerUtil.sendMessage(request.getUid(), "发生错误: " + e.getMessage(), SseMessageType.ERROR);
                SseServerUtil.sendEndMessage(request.getUid());
            } catch (Exception ex) {
                log.error("发送错误消息失败", ex);
            }
        }
    }

    /**
     * 构建联网搜索 Prompt
     *
     * @param userInput 用户输入
     * @param list      联网搜索结果
     * @return Prompt
     */
    private Prompt buildContext(String userInput, List<SearchResult> list) {

        String context = list.stream().map(searchResult -> String.format("<context>\n[来源] %s \n[摘要] %s \n </context> \n", searchResult.getUrl(), searchResult.getContent())).collect(Collectors.joining());
        return Prompt.builder().content(RAG_PROMPT.replaceAll("\\{\\{context}}", context).replaceAll("\\{\\{question}}", userInput)).build();
    }
}
