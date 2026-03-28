package com.test.memory;

import com.test.mapper.ChatMemoryMapper;
import com.test.vo.AIChatMemory;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 基于MySQL+原生MyBatis实现的聊天记忆存储
 * 实现Spring AI的ChatMemory接口
 */
@Component
public class MysqlChatMemory implements ChatMemory {
    @Resource
    private ChatMemoryMapper chatMemoryMapper;

    /**
     * 批量添加消息到会话记忆
     *
     * @param conversationId 会话ID
     * @param messages       消息列表
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        // 参数校验
        if (conversationId == null || conversationId.isBlank()) {
            throw new IllegalArgumentException("会话ID不能为空");
        }
        if (messages == null || messages.isEmpty()) {
            return;
        }
         /*
         批量转换Message到PO对象
         */
        List<AIChatMemory> poList = messages.stream()
                .filter(Objects::nonNull)
                .map(message -> {
                    AIChatMemory memoryPO = new AIChatMemory();
                    memoryPO.setConversationId(conversationId);
                    memoryPO.setContent(message.getText());
                    memoryPO.setMessageType(message.getMessageType().name());
                    memoryPO.setCreateTime(LocalDateTime.now());
                    memoryPO.setUserId(conversationId);
                    return memoryPO;
                })
                .collect(Collectors.toList());

        // 批量插入
        if (!poList.isEmpty()) {
            chatMemoryMapper.batchInsert(poList);
        }

    }

    /**
     * 获取指定会话的历史消息
     *
     * @param conversationId 会话ID
     * @return 按时间排序的消息列表
     */
    @Override
    public List<Message> get(String conversationId) {
        System.out.println(">>> [Step 1] 准备查询 conversationId: [" + conversationId + "]");

        if (conversationId == null || conversationId.isBlank()) {
            throw new IllegalArgumentException("会话ID不能为空");
        }

        // 执行查询
        List<AIChatMemory> memoryPOList = chatMemoryMapper.selectByConversationId(conversationId);

        // 打印数据库到底返回了几条
        System.out.println(">>> [Step 2] 数据库查询返回记录数: " + memoryPOList.size());

        if (memoryPOList.isEmpty()) {
            System.out.println(">>> [警告] 数据库里没找到数据！请检查 userId 是否完全一致（有无空格）。");
            return List.of();
        }

        // 打印第一条数据的详情，确认字段值
        AIChatMemory first = memoryPOList.get(0);
        System.out.println(">>> [调试] 第一条数据详情 -> ID:" + first.getId()
                           + ", Type:[" + first.getMessageType() + "]"
                           + ", Content:[" + first.getContent() + "]");
        // 继续转换...
        List<Message> result = memoryPOList.stream()
                .map(this::convertPOToMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        System.out.println(">>> [Step 3] 转换后的 Message 数量: " + result.size());

        return result;
    }

    /**
     * 清空指定会话的所有历史消息
     *
     * @param conversationId 会话ID
     */
    @Override
    public void clear(String conversationId) {
        // 参数校验
        if (conversationId == null || conversationId.isBlank()) {
            throw new IllegalArgumentException("会话ID不能为空");
        }

        chatMemoryMapper.deleteByConversationId(conversationId);
    }

    /**
     * 将数据库PO对象转换为Spring AI的Message对象
     *
     * @param po 数据库实体对象
     * @return Message对象
     */
    private Message convertPOToMessage(AIChatMemory po) {
        if (po == null || po.getMessageType() == null) {
            return null;
        }

        return switch (po.getMessageType()) {
            case "USER" -> new UserMessage(po.getContent());
            case "ASSISTANT" -> new AssistantMessage(po.getContent());
            case "SYSTEM" -> new SystemMessage(po.getContent());
            default -> null;
        };
    }

    // 兼容单个消息添加的重载方法（可选）
    public void add(String conversationId, Message message) {
        this.add(conversationId, List.of(message));
    }

}
