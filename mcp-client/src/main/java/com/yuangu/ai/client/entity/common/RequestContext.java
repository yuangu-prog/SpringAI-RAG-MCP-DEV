package com.yuangu.ai.client.entity.common;

import org.slf4j.MDC;

public class RequestContext {


    public static String getTraceId() {
        return MDC.get("traceId");
    }
}
