package com.bjtl;

import com.bjtl.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class LearnRedisApplicationTests2 {

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;
    @Test
    public void testString() {
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // key的序列化使用String 类型来完成 因为key 很多时候都是一个字符串
        // 默认使用jdk序列化方式  则必须对象实现序列化 占用也较大
        //redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());

        // 使用这种序列化方式对象可以不实现序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
        opsForValue.set("user:1",new User(1,"张三"));
        User object = (User)opsForValue.get("user:1");
        System.out.println(object);

        System.out.println("操作成功");
    }




}
