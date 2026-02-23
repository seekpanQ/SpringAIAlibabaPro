package com.test.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class DemoController {

    @Autowired
    private ChatModel chatModel;

    @GetMapping("/hello/chat")
    public String chat(@RequestParam(name = "question", defaultValue = "你是谁") String question) {
        String result = chatModel.call(question);
        return result;
    }


}
