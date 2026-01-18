package com.yuangu.ai.client.service.impl;

import com.yuangu.ai.client.entity.request.GetChatRequest;
import com.yuangu.ai.client.entity.searxng.SearchResult;
import com.yuangu.ai.client.service.IChatService;
import com.yuangu.ai.client.service.IDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
    public Flux<String> chat(GetChatRequest request) {

        String userInput = request.getUserInput();

        // 从知识库查询相关知识
        String contentFromKnowledge = Optional.ofNullable(documentService.similaritySearch(userInput)).orElse(Collections.emptyList())
                .stream()
                .map(Document::getText)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("\n"));
        // 封装 Prompt 模版
        Prompt prompt = Prompt.builder().content(RAG_PROMPT.replaceAll("\\{\\{context}}", contentFromKnowledge).replaceAll("\\{\\{question}}", userInput)).build();

        // 流式调用 AI
        return chatClient.prompt(prompt).stream().content();
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
