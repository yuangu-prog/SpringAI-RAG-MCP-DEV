package com.yuangu.ai.client.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SendMessageRequest
 *
 * @author ckliu
 * @since 2026-01-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    private String uid;
    private String message;
}

