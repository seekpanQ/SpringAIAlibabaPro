package com.test.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    // ChatModel和ChatClient都是混合使用，一般两个我们都要，后面交替使用，所以一次性将两个都注入
    @Autowired
    private ChatModel chatModel;
    @Autowired
    private ChatClient chatClient;

    @GetMapping(value = "/chatmodel/dochat")
    public String doChat1(@RequestParam(name = "question", defaultValue = "你是谁") String question) {
        String result = chatModel.call(question);
        return result;
    }

    @GetMapping("/chatclient/dochat")
    public String doChat2(@RequestParam(name = "msg", defaultValue = "你是谁") String question) {
        String result = chatClient.prompt().user(question).call().content();
        System.out.println("ChatClient响应：" + result);
        return result;
    }

}
