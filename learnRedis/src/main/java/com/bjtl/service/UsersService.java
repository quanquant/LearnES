package com.bjtl.service;

import com.bjtl.domain.Users;

import java.util.List;

public interface UsersService {
    List<Users> findAllUsers();

    Users insertUser(Users users);

    int deleteUser(int userId);

    Users updateUser(Users users);

    Users findUserById(int userId);
}
