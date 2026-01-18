package com.yuangu.ai.client.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * TraceId Util
 */
public class TraceIdUtil {

    private static final String ACTIVE_PROFILE = System.getProperty("spring.profiles.active");

    private TraceIdUtil() {
    }

    public static String generateTraceId() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String timestampStr = (new SimpleDateFormat("yyyyMMddHHmmssSSS")).format(new Date());
        return uuid.length() > 10 ? timestampStr + "-" + ACTIVE_PROFILE + "-" + uuid.substring(uuid.length() - 10) : timestampStr + "-" + ACTIVE_PROFILE + "-" + uuid;
    }
}

