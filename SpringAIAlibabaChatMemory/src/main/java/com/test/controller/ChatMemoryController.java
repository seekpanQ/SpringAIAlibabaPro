package com.test.controller;

import com.test.memory.MysqlChatMemory;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatMemoryController {

    // 注入ChatClient
    @Resource
    private ChatClient chatClient;

    // 注入并持有一个自定义的会话记忆管理器实例，用于实现大模型对话的上下文持久化存储。
    @Resource
    private MysqlChatMemory mysqlChatMemory;

    /**
     * 简化版问答接口：参数为用户ID + 用户问题
     *
     * @param userId   用户ID（必传）
     * @param question 用户问题（必传）
     * @return 包含会话ID和大模型回答的结果
     */
    @GetMapping("/ask")
    public String chat(@RequestParam("userId") String userId,  // 第一个参数：用户ID
                       @RequestParam("question") String question  // 第二个参数：用户问题
    ) {

        // 读取该会话的历史上下文（首次调用为空）
        List<Message> historyMessages = mysqlChatMemory.get(userId);
        System.out.println(">>> 读取到的历史消息数量: " + historyMessages.size());
        // 构造本次用户提问消息
        UserMessage currentUserMessage = new UserMessage(question);

        // 调用大模型（结合历史上下文）
        ChatResponse response = chatClient.prompt()
                .messages(historyMessages)
                .user(question)
                .call()
                .chatResponse();


        // 解析结果
        String assistantAnswer = response.getResult().getOutput().getText();
        // 直接通过构造函数创建 AssistantMessage
        AssistantMessage assistantMessage = new AssistantMessage(assistantAnswer);


        // 保存本次问答记录到MySQL（用户提问+AI回答）
        mysqlChatMemory.add(userId, List.of(currentUserMessage, assistantMessage));


        // 返回结果（包含会话ID、用户ID、回答）
        return String.format("用户ID：%s\n会话ID：%s\n回答：%s", userId, userId, assistantAnswer);

    }


}
