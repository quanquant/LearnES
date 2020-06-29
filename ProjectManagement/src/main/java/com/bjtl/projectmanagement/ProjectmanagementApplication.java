package com.bjtl.projectmanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.io.IOException;


/**
 * @author leitianquan
 */
@SpringBootApplication
@MapperScan("com.bjtl.projectmanagement.mapper")
@EnableCaching
public class ProjectmanagementApplication {


    public static void main(String[] args) throws IOException {
        SpringApplication.run(ProjectmanagementApplication.class, args);
    }
}
