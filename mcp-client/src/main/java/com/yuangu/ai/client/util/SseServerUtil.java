package com.yuangu.ai.client.util;

import com.yuangu.ai.client.enums.SseMessageType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SseServerUtil
 *
 * @author ckliu
 * @since 2026-01-16 14:20:16
 */
@Slf4j
public class SseServerUtil {

    // 管理有用户 sse 连接
    final static Map<String, Optional<SseEmitter>> CLIENTS = new ConcurrentHashMap<>();


    public static SseEmitter connection(String uid) {

        SseEmitter emitter = new SseEmitter(0L);

        //
        emitter.onCompletion(() -> {
            log.info("SSE 完成...");
            removeUid(uid);
        });

        // Sse 错误回调
        emitter.onError(e -> {
            log.error("SSE 异常...");
            removeUid(uid);
        });

        // Sse 超时
        emitter.onTimeout(() -> {
            log.info("SSE 超时...");
            removeUid(uid);
        });

        CLIENTS.put(uid, Optional.of(emitter));
        return emitter;
    }


    public static void removeUid(String uid) {
        CLIENTS.remove(uid);
    }

    /**
     * 向指定用户发送消息
     *
     * @param uid     用户 ID
     * @param message 消息内容
     * @param type    消息类型
     */
    public static void sendMessage(String uid, String message, SseMessageType type) {
        if (uid == null || type == null) {
            log.warn("发送消息参数不完整: uid={}, type={}", uid, type);
            return;
        }

        Optional<SseEmitter> emitterOpt = CLIENTS.get(uid);
        if (emitterOpt == null || !emitterOpt.isPresent()) {
            log.warn("用户 {} 的 SSE 连接不存在", uid);
            return;
        }

        emitterOpt.ifPresent(sseEmitter -> {
            try {
                SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event().data(message != null ? message : "").name(type.getCode());

                sseEmitter.send(eventBuilder);
                log.debug("成功向用户 {} 发送 {} 类型消息", uid, type.getCode());
            } catch (IOException e) {
                log.error("向用户 {} 发送消息失败", uid, e);
                removeUid(uid);
            } catch (Exception e) {
                log.error("向用户 {} 发送消息时发生异常", uid, e);
                removeUid(uid);
            }
        });
    }

    public static void sendMessage(SseEmitter sseEmitter, String uid, String message, SseMessageType type) {

        try {
            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event().data(message != null ? message : StringUtils.EMPTY).name(type.getCode());

            sseEmitter.send(eventBuilder);
            log.debug("成功向用户 {} 发送 {} 类型消息", uid, type.getCode());
        } catch (IOException e) {
            log.error("向用户 {} 发送消息失败", uid, e);
            removeUid(uid);
        } catch (Exception e) {
            log.error("向用户 {} 发送消息时发生异常", uid, e);
            removeUid(uid);
        }
    }


    public static Optional<SseEmitter> getEmitter(String uid) {
        return CLIENTS.get(uid);
    }


    public static void sendEndMessage(String uid) {
        // 发送完成标记
        Optional<SseEmitter> emitterOpt = getEmitter(uid);
        if (emitterOpt != null && emitterOpt.isPresent()) {
            emitterOpt.ifPresent(sseEmitter -> {
                try {
                    sseEmitter.send(SseEmitter.event().data("[DONE]").name(SseMessageType.DONE.getCode()));
                    log.debug("向用户 {} 发送完成标记", uid);
                    removeUid(uid);
                } catch (IOException e) {
                    log.debug("发送完成标记时连接已关闭: {}", e.getMessage());
                    // 忽略关闭错误
                } catch (Exception e) {
                    log.warn("发送完成标记时发生异常", e);
                }
            });
        }
    }
}
