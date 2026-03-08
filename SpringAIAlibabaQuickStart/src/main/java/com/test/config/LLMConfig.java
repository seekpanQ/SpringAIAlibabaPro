package com.test.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean(name = "deepseek")
    public ChatModel deepseek() {

        return DashScopeChatModel.builder()
                .dashScopeApi(
                        DashScopeApi.builder().apiKey(apiKey)
                                .build())
                .defaultOptions(DashScopeChatOptions.builder().withModel("deepseek-v3.2").build()).build();

    }

    @Bean(name = "qwen")
    public ChatModel qwen() {

        return DashScopeChatModel.builder()
                .dashScopeApi(
                        DashScopeApi.builder().apiKey(apiKey)
                                .build())
                .defaultOptions(DashScopeChatOptions.builder().withModel("qwen3-max").build()).build();

    }
}
