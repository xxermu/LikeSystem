package com.example.like;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.like.dao.mapper")
public class ClLikeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClLikeApplication.class, args);
    }

}
