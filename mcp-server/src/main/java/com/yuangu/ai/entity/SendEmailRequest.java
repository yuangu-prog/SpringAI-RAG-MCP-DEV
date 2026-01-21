package com.yuangu.ai.entity;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
public class SendEmailRequest {

    @ToolParam(description = "收件人邮箱")
    private String email;

    @ToolParam(description = "邮件标题")
    private String subject;

    @ToolParam(description = "邮件信息")
    private String message;

    @ToolParam(description = "邮件内容的格式，如果是HTML格式则设置为1，如果是markdown格式则设置为2")
    private Integer contentType;
}
