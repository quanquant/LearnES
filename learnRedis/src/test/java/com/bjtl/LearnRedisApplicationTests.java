package com.bjtl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LearnRedisApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testString() {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        // 1. 给redis里面set一个key
        //opsForValue.set("kk","vv");
        // 2. 取redis中的存入数据
        //String value = opsForValue.get("kk");
        //System.out.println(value);

        // 3.设置时间 30秒后自动消除
        //opsForValue.set("session","user1", Duration.ofSeconds(30));

        // 4. 批量添加
       /* Map<String,String> map = new HashMap<>();
        map.put("k1","v1");
        map.put("k2","v2");
        map.put("k3","v3");
        map.put("k4","v4");
        opsForValue.multiSet(map);*/

        // 5. 多个查看
        /*Collection<String> keys = new ArrayList<>();
        keys.add("k1");
        keys.add("k2");
        keys.add("k3");
        List<String> list = opsForValue.multiGet(keys);
        for (String string:list) {
            System.out.println(string);
        }*/

        //opsForValue.set("age","20");
        // 6.增长
        opsForValue.increment("age", 5);
        // 7. 降低
        opsForValue.decrement("age");
        System.out.println("操作成功");
    }

    @Test
    public void testList() {
        ListOperations<String, String> opsForList = redisTemplate.opsForList();
        // 放
        opsForList.leftPushAll("mylist", "a", "b");
        System.out.println("操作成功");

        List<String> mylist = opsForList.range("mylist", 0, -1);
        for (String s : mylist) {
            System.out.println(s);
        }
    }

    @Test
    public void testHash() {
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        // hset
        opsForHash.put("object-1", "name", "sxt"); // 后面的2 个参数都是object,但是只支持String 类型
        opsForHash.put("object-1", "age", "27"); // 后面的2 个参数都是object,但是只支持String 类型
        opsForHash.put("object-1", "sex", "man"); // 后面的2 个参数都是object,但是只支持String 类型
        Object value = opsForHash.get("object-1", "sex");
        System.out.println(value);
        // 取多个值
        List<Object> multiGet = opsForHash.multiGet("object-1", Arrays.asList("name", "sex"));
        System.out.println(multiGet);
    }


}
