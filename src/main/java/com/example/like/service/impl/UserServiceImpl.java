package com.example.like.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.like.constant.UserConstant;
import com.example.like.dao.entity.User;
import com.example.like.dao.mapper.UserMapper;
import com.example.like.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Override
    public User getLoginUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(UserConstant.LOGIN_USER);
    }

}
