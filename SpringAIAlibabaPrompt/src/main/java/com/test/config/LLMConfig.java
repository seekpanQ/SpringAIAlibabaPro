package com.test.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {
    // dashscopeChatModel 是 Spring AI Alibaba 自动向容器中注册的 ChatModel 实例。
    // 这也证明了ChatClient的构建是基于ChatModel的
    @Bean
    public ChatClient chatClient(ChatModel dashscopeChatModel) {
        return ChatClient.builder(dashscopeChatModel).build();
    }
}
