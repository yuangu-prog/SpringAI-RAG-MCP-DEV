package com.yuangu.ai.client.service;

import com.yuangu.ai.client.entity.request.GetChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface IChatService {

    /**
     * 对话
     *
     * @param request
     * @return
     */
    void chat(GetChatRequest request, SseEmitter sseEmitter);
}
