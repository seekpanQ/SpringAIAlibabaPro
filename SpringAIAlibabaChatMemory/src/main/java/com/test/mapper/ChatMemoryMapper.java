package com.test.mapper;

import com.test.sqlprovider.ChatMemorySqlProvider;
import com.test.vo.AIChatMemory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ChatMemoryMapper {
    /**
     * 批量插入会话消息。
     */
    @InsertProvider(type = ChatMemorySqlProvider.class, method = "batchInsert")
    void batchInsert(@Param("list") List<AIChatMemory> list);

    /**
     * 根据会话ID查询消息列表（按创建时间升序）
     */
    @Select("SELECT id, conversation_id, content, message_type AS messageType, create_time, user_id " +
            "FROM ai_chat_memory WHERE conversation_id = #{conversationId} ORDER BY create_time ASC")
    List<AIChatMemory> selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID删除所有消息
     */
    @Delete("DELETE FROM ai_chat_memory WHERE conversation_id = #{conversationId}")
    void deleteByConversationId(@Param("conversationId") String conversationId);
}
