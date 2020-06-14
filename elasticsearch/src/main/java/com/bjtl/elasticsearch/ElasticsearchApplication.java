package com.bjtl.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 *SpringBoot默认支持两种技术与ES交互
 * 1. Jest（默认不生效）
 *
 * 2. SpringData ElasticSearchJ
 */
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.bjtl.repository")
public class ElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class, args);
    }

}
