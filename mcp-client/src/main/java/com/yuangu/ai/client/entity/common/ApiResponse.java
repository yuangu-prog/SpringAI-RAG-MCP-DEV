package com.yuangu.ai.client.entity.common;

import com.yuangu.ai.client.enums.ApiResponseCode;
import lombok.Data;

/**
 * 统一响应体
 *
 * @param <T>
 */
@Data
public class ApiResponse<T> {
    /**
     * code
     */
    private Integer code;
    /**
     * 消息
     */
    private String message;
    /**
     * 消息
     */
    private String subMsg;
    /**
     * traceId
     */
    private String traceId;
    /**
     * 数据
     */
    private T data;

    // 构造函数
    public ApiResponse(Integer code, String message, String traceId, String subMsg, T data) {
        this.code = code;
        this.message = message;
        this.subMsg = subMsg;
        this.traceId = traceId;
        this.data = data;
    }

    // 静态工厂方法
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ApiResponseCode.SUCCESS.getCode(), ApiResponseCode.SUCCESS.getMessage(), RequestContext.getTraceId(), "", data);
    }

    // 静态工厂方法
    public static <T> ApiResponse<T> success(T data, String traceId) {
        return new ApiResponse<>(ApiResponseCode.SUCCESS.getCode(), ApiResponseCode.SUCCESS.getMessage(), traceId, "", data);
    }

    public static <Void> ApiResponse<Void> success() {
        return new ApiResponse<>(ApiResponseCode.SUCCESS.getCode(), ApiResponseCode.SUCCESS.getMessage(), RequestContext.getTraceId(), "", null);
    }

    public static <T> ApiResponse<T> failure(ApiResponseCode codeEnum, String subMsg) {
        return new ApiResponse<>(codeEnum.getCode(), codeEnum.getMessage(), RequestContext.getTraceId(), subMsg, null);
    }

    public static <T> ApiResponse<T> failure(ApiResponseCode codeEnum) {
        return new ApiResponse<>(codeEnum.getCode(), codeEnum.getMessage(), null, RequestContext.getTraceId(), null);
    }

    public static <T> ApiResponse<T> failure(Integer code, String message) {
        return new ApiResponse<>(code, message, RequestContext.getTraceId(), "", null);
    }

    public static <T> ApiResponse<T> failure(Integer code, String message, String subMsg) {
        return new ApiResponse<>(code, message, RequestContext.getTraceId(), subMsg, null);
    }

    public static <T> ApiResponse<T> failure(ApiResponseCode codeEnum, String message, String subMessage) {
        return new ApiResponse<>(codeEnum.getCode(), message, RequestContext.getTraceId(), subMessage, null);
    }
}

