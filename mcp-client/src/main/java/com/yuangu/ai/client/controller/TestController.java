package com.yuangu.ai.client.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private ChatClient chatClient;


    @GetMapping(value = "/chat")
    public String chat(@RequestParam String prompt) {
        return chatClient.prompt(prompt).call().content();
    }


    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> stream(@RequestParam String prompt) {
        return chatClient.prompt(prompt).stream().content();
    }
}
