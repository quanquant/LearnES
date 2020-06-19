package com.bjtl.mapper;

import com.bjtl.domain.Users;

import java.util.List;

public interface UsersMapper {
    List<Users> queryAllUser();

    int insertUser(Users users);

    int deleteUser(int userId);

    int updateUser(Users users);

    Users findUserById(int userId);
}
