package com.bjtl.Controller;

import com.bjtl.domain.Users;
import com.bjtl.service.UsersService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
public class UsersController {
    @Autowired
    private UsersService usersService;


    @RequestMapping("find")
    public int findAll(){
        System.out.println("我来了");
        List<Users> list = usersService.findAllUsers();
        for (Users users : list) {
            System.out.println(users);
        }
        return 1;
    }

    @RequestMapping("add")
    public Users addUser(@RequestBody Users users){
        Users user = usersService.insertUser(users);
        return user;
    }

    @RequestMapping("delete")
    public String deleteUser(int userId){
        int flag = usersService.deleteUser(userId);
        if (flag !=0){
            return "删除成功";
        }else {
            return "删除失败";
        }
    }

    @RequestMapping("findById")
    public Users findById(int userId){
        Users user= usersService.findUserById(userId);
        return user;
    }
}




