package com.test.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class DemoController {

    @Autowired
    @Qualifier("deepseek")
    private ChatModel deepseekchatModel;

    @Autowired
    @Qualifier("qwen")
    private ChatModel qwenchatModel;

    @GetMapping("/hello/chat")
    public String chat(@RequestParam(name = "question", defaultValue = "你是谁") String question) {
        String result = deepseekchatModel.call(question);
        return result;
    }

    @GetMapping("/hello/streamchat")
    public Flux<String> stream(@RequestParam(name = "msg", defaultValue = "你是谁") String question) {
        return qwenchatModel.stream(question);
    }


}
