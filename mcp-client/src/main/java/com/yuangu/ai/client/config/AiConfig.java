package com.yuangu.ai.client.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider toolCallbackProvider) {
        return builder
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultSystem("你是AI助手，名字叫LaGoGo")
                .build();
    }

    // @Bean
    // public OpenAiEmbeddingModel openAiEmbeddingModel(){
    //
    //     OpenAiApi openAiApi = OpenAiApi.builder()
    //             .apiKey("sk-abc746abac1f4125b6bea2712430302e")
    //             .baseUrl("https://api.deepseek.com")
    //             .embeddingsPath("/v1/embedding")
    //             .build();
    //     return new OpenAiEmbeddingModel(openAiApi);
    // }

    /**
     * 验证 OpenAI API 配置
     * 在应用启动时检查配置，提供清晰的错误提示
     */
    @PostConstruct
    public void validateOpenAiConfiguration() {
        String baseUrl = System.getProperty("OPENAI_BASE_URL");
        String apiKey = System.getProperty("OPENAI_API_KEY");
        String embeddingModel = System.getProperty("OPENAI_EMBEDDING_MODEL", "text-embedding-3-small");

        log.info("=== OpenAI API 配置检查 ===");

        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            log.error("❌ OPENAI_BASE_URL 环境变量未设置！");
            log.error("   如果使用 OpenAI 官方 API，请设置为: https://api.openai.com/v1");
            log.error("   如果使用兼容 API，请确保包含 /v1 路径，例如: https://your-api.com/v1");
            log.error("   注意：base-url 应该以 /v1 结尾，Spring AI 会自动追加 /embeddings");
        } else {
            // 验证 base URL 格式
            String trimmedUrl = baseUrl.trim();
            if (!trimmedUrl.endsWith("/v1")) {
                log.warn("⚠️  OPENAI_BASE_URL 可能格式不正确: {}", trimmedUrl);
                log.warn("   建议格式: https://api.openai.com/v1 或 https://your-api.com/v1");
                log.warn("   Spring AI 会在 base-url 后追加 /embeddings");
                log.warn("   如果遇到 404 错误，请检查 base-url 是否正确");
            } else {
                log.info("✓ OPENAI_BASE_URL: {}", trimmedUrl);
            }
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("❌ OPENAI_API_KEY 环境变量未设置！");
        } else {
            String maskedKey = apiKey.length() > 8
                    ? apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4)
                    : "***";
            log.info("✓ OPENAI_API_KEY: {} (长度: {})", maskedKey, apiKey.length());
        }

        log.info("✓ OPENAI_EMBEDDING_MODEL: {}", embeddingModel);
        log.info("==========================");

        // 如果配置有问题，给出解决建议
        if ((baseUrl == null || baseUrl.trim().isEmpty()) ||
                (apiKey == null || apiKey.trim().isEmpty())) {
            log.warn("");
            log.warn("💡 解决 404 错误的步骤：");
            log.warn("1. 检查 .env 文件或环境变量中是否设置了 OPENAI_BASE_URL 和 OPENAI_API_KEY");
            log.warn("2. 确认 OPENAI_BASE_URL 格式正确（应以 /v1 结尾）");
            log.warn("3. 如果使用第三方 API，确认其兼容 OpenAI API 格式");
            log.warn("4. 如果暂时无法配置 API，可以在 application.yml 中设置:");
            log.warn("   spring.ai.vectorstore.redis.initialize-schema: false");
            log.warn("   这样可以避免启动时调用 API，但向量存储功能可能受限");
            log.warn("");
        }
    }
}
