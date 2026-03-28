package com.test.sqlprovider;

import com.test.vo.AIChatMemory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public class ChatMemorySqlProvider {

    /**
     * 构建批量插入 SQL
     * 正确逻辑：字段列表只写一次，VALUES 后面循环拼接多组数据
     */
    public String batchInsert(@Param("list") List<AIChatMemory> list) {
        if (list == null || list.isEmpty()) {
            return ""; // 或者抛异常
        }
        StringBuilder sql = new StringBuilder();
        // 1. 固定部分：INSERT INTO 表名 (字段列表)
        // 注意：字段列表只写一次！
        sql.append("INSERT INTO ai_chat_memory (conversation_id, user_id, message_type, content, create_time) VALUES ");

        // 2. 循环部分：只拼接 values 的具体值
        for (int i = 0; i < list.size(); i++) {
            sql.append("(#{list[").append(i).append("].conversationId},");
            sql.append("#{list[").append(i).append("].userId},");
            sql.append("#{list[").append(i).append("].messageType},");
            sql.append("#{list[").append(i).append("].content},");
            sql.append("#{list[").append(i).append("].createTime})");

            // 如果不是最后一个，加逗号
            if (i < list.size() - 1) {
                sql.append(",");
            }
        }

        return sql.toString();
    }
}
