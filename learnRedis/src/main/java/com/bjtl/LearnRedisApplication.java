package com.bjtl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.bjtl.mapper")
@EnableCaching
public class LearnRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnRedisApplication.class, args);
    }

}
