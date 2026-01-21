package com.yuangu.ai.tool;

import com.alibaba.fastjson2.JSONObject;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.yuangu.ai.entity.SendEmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailTool {


    private final JavaMailSender javaMailSender;
    private final MailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;


    @Tool(description = "给指定的邮箱发送消息，email为收件人邮箱，subject为邮件标题，message为邮件的内容")
    public void sendEmail(SendEmailRequest request) throws MessagingException {

        log.info("============调用 MCP 工具：sendEmail==============");
        log.info("============发送信息：{}==============", JSONObject.toJSONString(request));

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        // 设置发送信息
        helper.setFrom(from);
        helper.setTo(request.getEmail());
        helper.setSubject(request.getSubject());

        if (request.getContentType() == 1) {
            helper.setText(request.getMessage(), true);
        } else if (request.getContentType() == 2) {
            helper.setText(convertToHtml(request.getMessage()), true);
        } else {
            helper.setText(convertToHtml(request.getMessage()));
        }

        javaMailSender.send(mimeMessage);
    }

    /**
     * markdown 转 html
     *
     * @param markdownStr
     * @return
     */
    public static String convertToHtml(String markdownStr) {

        MutableDataSet dataSet = new MutableDataSet();
        Parser parser = Parser.builder(dataSet).build();
        HtmlRenderer htmlRenderer = HtmlRenderer.builder(dataSet).build();

        return htmlRenderer.render(parser.parse(markdownStr));
    }

    @Tool(description = "查询我的邮箱地址")
    public String getMyEmailAddress() {
        log.info("============调用 MCP 工具：getMyEmailAddress==============");
        return "17347690192@163.com";
    }
}
