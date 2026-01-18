package com.yuangu.ai.client.controller;

import com.yuangu.ai.client.entity.request.GetChatRequest;
import com.yuangu.ai.client.enums.SseMessageType;
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
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

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

    @PostMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@Validated @RequestBody GetChatRequest request) {
        log.info("收到聊天请求，uid: {}, message: {}", request.getUid(), request.getUserInput());
        SseEmitter emitter = SseServerUtil.connection(request.getUid());

        // 使用响应式方式处理，确保在正确的线程上下文中执行
        try {
            // 发送开始消息
            SseServerUtil.sendMessage(request.getUid(), "", SseMessageType.ADD);

            // 流式调用 AI
            Flux<String> responseStream = chatClient.prompt().user(request.getUserInput()).stream().content();

            // 订阅并处理流式响应
            responseStream.doOnNext(content -> {
                // 发送流式内容
                log.debug("发送流式内容: {}", content);
                SseServerUtil.sendMessage(request.getUid(), content, SseMessageType.MESSAGE);
            }).doOnComplete(() -> {
                // 发送完成消息
                log.info("流式响应完成");
                SseServerUtil.sendMessage(request.getUid(), "", SseMessageType.FINISH);
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
            }).doOnSubscribe(subscription -> {
                log.info("开始订阅响应流");
            }).subscribeOn(Schedulers.boundedElastic()).subscribe();

        } catch (Exception e) {
            log.error("处理对话请求异常", e);
            try {
                SseServerUtil.sendMessage(request.getUid(), "发生错误: " + e.getMessage(), SseMessageType.ERROR);
                SseServerUtil.sendEndMessage(request.getUid());
            } catch (Exception ex) {
                log.error("发送错误消息失败", ex);
            }
        }

        return emitter;
    }


    @PostMapping(value = "/chatKnowledge", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatKnowledge(@Validated @RequestBody GetChatRequest request) {
        log.info("收到知识库聊天请求，uid: {}, message: {}", request.getUid(), request.getUserInput());
        SseEmitter emitter = SseServerUtil.connection(request.getUid());

        // 使用响应式方式处理，确保在正确的线程上下文中执行
        try {
            // 发送开始消息
            SseServerUtil.sendMessage(request.getUid(), "", SseMessageType.ADD);

            // 流式调用 AI
            Flux<String> responseStream = chatService.chat(request);

            // 订阅并处理流式响应
            responseStream.doOnNext(content -> {
                // 发送流式内容
                log.debug("发送流式内容: {}", content);
                SseServerUtil.sendMessage(request.getUid(), content, SseMessageType.MESSAGE);
            }).doOnComplete(() -> {
                // 发送完成消息
                log.info("流式响应完成");
                SseServerUtil.sendMessage(request.getUid(), "", SseMessageType.FINISH);
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
            }).doOnSubscribe(subscription -> {
                log.info("开始订阅响应流");
            }).subscribeOn(Schedulers.boundedElastic()).subscribe();

        } catch (Exception e) {
            log.error("处理对话请求异常", e);
            try {
                SseServerUtil.sendMessage(request.getUid(), "发生错误: " + e.getMessage(), SseMessageType.ERROR);
                SseServerUtil.sendEndMessage(request.getUid());
            } catch (Exception ex) {
                log.error("发送错误消息失败", ex);
            }
        }

        return emitter;
    }
}
