package com.test.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class DemoController {

    @Autowired
    private ChatClient chatClient;

    @GetMapping("/prompt/chat")
    public Flux<String> chat(@RequestParam(name = "question", defaultValue = "抽烟犯法吗？") String question) {
        return chatClient.prompt()
                // AI 能力边界
                .system("你是一个法律助手，只回答法律问题，其它问题回复，我只能回答法律相关问题，其它无可奉告")
                .user(question)
                .stream()
                .content();
    }
}
