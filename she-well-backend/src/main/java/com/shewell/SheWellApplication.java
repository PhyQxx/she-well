package com.shewell;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shewell.mapper")
public class SheWellApplication {
    public static void main(String[] args) {
        SpringApplication.run(SheWellApplication.class, args);
    }
}
