package com.yuangu.ai.client.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GetChatRequest
 *
 * @author ckliu
 * @since 2026-01-16 14:53:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetChatRequest {

    private String uid;

    @NotBlank(message = "用户输入不能为空.")
    private String userInput;

    /**
     * 是否选中联网搜索
     */
    private Boolean choseSearch = Boolean.FALSE;

    /**
     * 是否选中知识库搜索
     */
    private Boolean choseKnowledge = Boolean.FALSE;
}
