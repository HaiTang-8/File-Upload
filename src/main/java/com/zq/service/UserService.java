package com.zq.service;

import com.zq.bean.User;

public interface UserService {

    User login(String username,String password);

    User findUserByName(String username);
}
