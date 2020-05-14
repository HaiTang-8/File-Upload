package com.zq.service.Impl;

import com.zq.bean.User;
import com.zq.mapper.UserMapper;
import com.zq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        return userMapper.login(username, password);
    }

    @Override
    public User findUserByName(String username) {
        return userMapper.findUserByName(username);
    }
}
