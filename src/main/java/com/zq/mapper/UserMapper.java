package com.zq.mapper;

import com.zq.bean.User;

public interface UserMapper {

    User login(String username,String password);

    User findUserByName(String username);
}
