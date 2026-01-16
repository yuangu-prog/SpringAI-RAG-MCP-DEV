package com.yuangu.ai.client.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SseMessageType
 *
 * @author ckliu
 * @since 2026-01-16 15:28:09
 */
@Getter
@AllArgsConstructor
public enum SseMessageType {

    MESSAGE("message"),
    ADD("add"),
    FINISH("finish"),
    DONE("done"),
    CUSTOM_EVENT("custom_event"),
    ERROR("error"),

    ;
    private final String code;
}
