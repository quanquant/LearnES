package com.bjtl.projectmanagement.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @Description: Redis配置类
 * @Author: leitianquan
 * @Date: 2020/06/23
 **/
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 配置缓存管理器,服务于Redis注解
     *
     * @param connectionFactory Redis连接工厂
     * @return Redis缓存管理
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        LettuceConnectionFactory jedisConnectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        //指定dbindex
        jedisConnectionFactory.setDatabase(2);
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        jedisConnectionFactory.resetConnection();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 20分钟缓存失效
                .entryTtl(Duration.ofSeconds(60 * 20))
                // 设置key的序列化方式
                // .entryTtl(Duration.ofSeconds(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value的序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new FastJsonRedisSerializer(Object.class)))
                // 不缓存null值
                .disableCachingNullValues();
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
        return redisCacheManager;
    }

    /**
     * 自己定义一个RedisTemplate类，重写默认的RedisTemplate，默认的为RedisTemplate<Object,Object>
     *
     * @param factory Redis链接工厂对象
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        /*
         * Json序列化配置
         * 使用Json去解析任意的对象，把任意的对象变成json序列化
         */
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        // 序列化通过ObjectMapper进行转义
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // String的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用Jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用Jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 将所有属性都设置进去
        template.afterPropertiesSet();
        return template;
    }
}
