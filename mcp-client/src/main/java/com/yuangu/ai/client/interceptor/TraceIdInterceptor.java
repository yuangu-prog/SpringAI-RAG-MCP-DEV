package com.yuangu.ai.client.interceptor;

import com.yuangu.ai.client.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * trace-id拦截器
 */
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 请求开始之前构造trace-id放到MDC和response中
        String traceId = request.getHeader("traceId");
        if (StringUtils.isBlank(traceId)) {
            traceId = TraceIdUtil.generateTraceId();
        }

        MDC.put("traceId", traceId);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);

        // 请求结束移除trace-id内容
        MDC.remove("traceId");
    }
}
