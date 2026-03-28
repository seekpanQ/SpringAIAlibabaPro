package com.test;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.test.mapper")// 在启动类上添加注解，表示mapper接口所在位置
@SpringBootApplication
public class SpringAIAlibabaChatMemoryApp {
    public static void main(String[] args) {

        SpringApplication.run(SpringAIAlibabaChatMemoryApp.class, args);
    }
}
