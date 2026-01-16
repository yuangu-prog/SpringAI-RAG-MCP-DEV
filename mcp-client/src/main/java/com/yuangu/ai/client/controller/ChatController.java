package com.yuangu.ai.client.controller;

import com.yuangu.ai.client.entity.request.GetChatRequest;
import com.yuangu.ai.client.util.SseServerUtil;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/v1/chat")
@RestController
public class ChatController {

    @Resource
    private ChatClient chatClient;

    @PostMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void chat(@RequestBody GetChatRequest request) {
        SseEmitter emitter = SseServerUtil.connection(request.getUid());
    }
}
