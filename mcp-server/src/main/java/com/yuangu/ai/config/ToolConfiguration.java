package com.yuangu.ai.config;

import com.yuangu.ai.tool.DateTool;
import com.yuangu.ai.tool.EmailTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfiguration {

    /**
     * 4.注册 MCP 工具
     *
     * @param dateTool
     * @return
     */
    @Bean
    public ToolCallbackProvider toolCallbackProvider(DateTool dateTool, EmailTool emailTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(dateTool, emailTool)
                .build();

    }
}
