package com.yuangu.ai.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class DateTool {

    /**
     * 大模型会从用户的输入中抽槽到cityName 和 zoneId
     * 并在调用这个工具时传入两个值
     *
     * @param cityName 城市名称
     * @param zoneId   时区Id
     * @return 当前时间
     */
    @Tool(description = "根据城市所在的时区id来获取当前时间")
    public String getCurrentTime(String cityName, String zoneId) {
        log.info("========调用MCP工具：getCurrentTime()========");
        log.info("========参数 cityName = {}", cityName);
        log.info("========参数 zoneId = {}", zoneId);

        ZoneId zone = ZoneId.of(zoneId);
        return ZonedDateTime.now(zone).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
