package com.yuangu.ai.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@RestController
@SpringBootApplication
public class McpClientApplication {


    @RequestMapping("/")
    String home()    {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SpringApplication.run(McpClientApplication.class, args);
    }
}