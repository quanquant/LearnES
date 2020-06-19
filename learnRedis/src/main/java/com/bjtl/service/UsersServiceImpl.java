package com.bjtl.service;

import com.bjtl.domain.Users;
import com.bjtl.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @Override
    public List<Users> findAllUsers() {
        return usersMapper.queryAllUser();
    }

    //插入用户的成功后的结果的Id
    @CachePut(cacheNames = "user", key = "#result.userId") // 每次都把数据放入缓存里面 result 代表返回值
    @Override
    public Users insertUser(Users users) {
        usersMapper.insertUser(users);
        return users;
    }

    //删除用户的用户Id
    @CacheEvict(cacheNames = "user", key = "#userId") // #id el表达式，代表去id 的值
    @Override
    public int deleteUser(int userId) {
        return usersMapper.deleteUser(1);
    }

    //更新用户的用户Id
    @CachePut(cacheNames = "user", key = "#user.userId")
    @Override
    public Users updateUser(Users users) {
        usersMapper.updateUser(users);
        return users;
    }

    //查找的Id
    @Cacheable(key = "#userId", cacheNames = "user")
    @Override
    public Users findUserById(int userId) {
        System.out.println("没查到");
        Users user = usersMapper.findUserById(userId);
        return user;
    }


}
