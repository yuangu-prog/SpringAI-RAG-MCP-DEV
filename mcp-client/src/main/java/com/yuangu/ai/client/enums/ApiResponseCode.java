package com.yuangu.ai.client.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ApiResponseCode {
    SUCCESS(0, "成功"),

    /* -----------------------------------------------------------
    *    参数异常信息
    ----------------------------------------------------------- */
    PARAM_ERROR(10001, "请求参数错误"),
    AGENT_MISSING(10002, "智能体id不存在"),
    DATA_EMPTY(10003, "数据不存在"),
    RESULT_MISSING(10004, "请求结果为空"),
    TOOL_MISSING(10005, "工具id不存在"),
    WORKFLOW_MISSING(10006, "工作流id不存在"),
    TOOL_ERROR(10007, "工具业务异常"),

    /* -----------------------------------------------------------
    *    用户相关的异常信息
    ----------------------------------------------------------- */
    USER_FAIL(20000, "获取用户信息失败"),
    ORG_ROLE_FAIL(20001, "获取用户组织角色信息失败"),
    USER_DELETE(20002, "用户已删除"),
    USER_FORBIDDEN(20003, "用户已冻结"),
    NO_PERMISSION(20004, "暂无权限"),


    UNKNOWN_FAIL(99999, "服务异常，请稍后重试"),
    ;
    private final Integer code;
    private final String message;
}
