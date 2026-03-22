package com.test.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/prompttemplate/chat")
    public Flux<String> chat2(@RequestParam("candidateName") String candidateName,
                              @RequestParam("jobPosition") String jobPosition,
                              @RequestParam("entryDate") String entryDate,
                              @RequestParam("salaryRange") String salaryRange,
                              @RequestParam("welfare") String welfare,
                              // System模板的动态变量：企业名称、Offer类型
                              @RequestParam("companyName") String companyName,
                              @RequestParam("offerType") String offerType) {
        /*
         * String systemTemplateStr = """...""";
         * 是 Java 15 及以上版本 引入的「文本块（Text Blocks）」语法，
         * 核心作用是简化多行字符串的编写，解决传统字符串拼接 / 转义的痛点，
         * 让代码中的长文本、多行文本更易读、易维护。
         */
        // ========== 1. System提示词模板（包含占位符） ==========
        String systemTemplateStr = """
                你是{companyName}的资深人力资源专员，精通{offerType}入职Offer的撰写规范。
                请根据用户提供的信息，生成一份符合{companyName}企业规范的{offerType}Offer，要求如下：
                1. 语言正式且温馨，符合{companyName}的官方文书风格；
                2. 包含核心要素：入职岗位、入职日期、薪资范围（税前）、核心福利、欢迎语；
                3. 以html格式输出
                4. 结尾必须带上{companyName}的名称和人力资源部联系方式提示。
                """;

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
        String userTemplateStr = """
                请生成一份入职Offer，具体信息如下：
                1. 候选人姓名：{candidateName}
                2. 入职岗位：{jobPosition}
                3. 入职日期：{entryDate}
                4. 税前薪资范围：{salaryRange}
                5. 核心福利：{welfare}
                """;
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
