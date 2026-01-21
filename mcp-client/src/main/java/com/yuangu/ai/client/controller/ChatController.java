package com.yuangu.ai.client.controller;

import com.yuangu.ai.client.entity.request.GetChatRequest;
import com.yuangu.ai.client.service.IChatService;
import com.yuangu.ai.client.util.SseServerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * ChatController
 *
 * @author ckliu
 * @since 2026-01-16 14:47:53
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;
    private final IChatService chatService;

    // @PostMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    // public Flux<String> chat(@Validated @RequestBody GetChatRequest request) {
    //     log.info("收到聊天请求，uid: {}, message: {}", request.getUid(), request.getUserInput());
    //     SseEmitter emitter = SseServerUtil.connection(request.getUid());
    //     return chatClient.prompt(request.getUserInput()).stream().content();
    // }

    @PostMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@Validated @RequestBody GetChatRequest request) {
        log.info("收到聊天请求，uid: {}, message: {}", request.getUid(), request.getUserInput());
        SseEmitter emitter = SseServerUtil.connection(request.getUid());
        chatService.chat(request, emitter);
        return emitter;
    }
}
