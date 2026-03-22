package com.test.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
public class DemoController2 {

    @Autowired
    private ChatClient chatClient;

    @GetMapping("/prompttemplate/chat2")
    public Flux<String> chat2(@RequestParam("candidateName") String candidateName,
                              @RequestParam("jobPosition") String jobPosition,
                              @RequestParam("entryDate") String entryDate,
                              @RequestParam("salaryRange") String salaryRange,
                              @RequestParam("welfare") String welfare,
                              // System模板的动态变量：企业名称、Offer类型
                              @RequestParam("companyName") String companyName,
                              @RequestParam("offerType") String offerType) throws IOException {
        /*
         * String systemTemplateStr = """...""";
         * 是 Java 15 及以上版本 引入的「文本块（Text Blocks）」语法，
         * 核心作用是简化多行字符串的编写，解决传统字符串拼接 / 转义的痛点，
         * 让代码中的长文本、多行文本更易读、易维护。
         */
        // ========== 1. System提示词模板（包含占位符） ==========
        // 加载resources/prompts/下的模板文件
        ClassPathResource systemTemplateFile = new ClassPathResource("/prompts/system.txt");
        // 读取文件内容（指定UTF-8编码，避免中文乱码）
        String systemTemplateStr = new String(
                systemTemplateFile.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        // 2. 创建System模板对象，填充System侧的变量
        PromptTemplate systemPromptTemplate = new PromptTemplate(systemTemplateStr);
        Map<String, Object> systemVariables = Map.of(
                "companyName", companyName,  // 企业名称（动态）
                "offerType", offerType      // Offer类型（如"正式员工"/"实习生"）
        );
        // 渲染System模板，得到填充后的完整System提示词
        String systemContent = systemPromptTemplate.render(systemVariables);
        // 生成SystemMessage
        SystemMessage systemMessage = new SystemMessage(systemContent);

        // ========== 2. User提示词模板（原有逻辑不变） ==========
        ClassPathResource userTemplateFile = new ClassPathResource("prompts/user.txt");
        String userTemplateStr = new String(
                userTemplateFile.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
        PromptTemplate userPromptTemplate = new PromptTemplate(userTemplateStr);
        Map<String, Object> userVariables = Map.of(
                "candidateName", candidateName,
                "jobPosition", jobPosition,
                "entryDate", entryDate,
                "salaryRange", salaryRange,
                "welfare", welfare
        );
        String userContent = userPromptTemplate.render(userVariables);
        UserMessage userMessage = new UserMessage(userContent);

        // ========== 3. 组合消息并调用大模型 ==========
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        // 4. 调用阿里云大模型，获取结果
        return chatClient.prompt(prompt)
                .stream()
                .content();

    }
}
