package com.bjtl;

import com.bjtl.domain.Users;
import com.bjtl.service.UsersService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestConnect {
    @Autowired
    UsersService usersService;

    @Test
    public void find(){
        System.out.println("我来了");
        List<Users> list = usersService.findAllUsers();
        for (Users users : list) {
            System.out.println(users);
        }
    }

    @Test
    public void add(){
        Users users = new Users(2,"权",1);
        Users user= usersService.insertUser(users);
        System.out.println(user);
    }
}
