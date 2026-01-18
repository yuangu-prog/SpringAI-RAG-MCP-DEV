package com.yuangu.ai.client.service;

import com.yuangu.ai.client.entity.request.GetChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

public interface IChatService {

    /**
     * 对话
     *
     * @param request
     * @return
     */
    Flux<String> chat(GetChatRequest request);
}
