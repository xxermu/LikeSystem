package com.example.like.controller;

import com.example.like.common.BaseResponse;
import com.example.like.common.ErrorCode;
import com.example.like.common.ResultUtils;
import com.example.like.constant.UserConstant;
import com.example.like.dao.entity.User;
import com.example.like.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public BaseResponse<User> login(long userId, HttpServletRequest request) {
        //校验userId
        if(userId <= 0) throw new RuntimeException("参数错误");

        User user = userService.getById(userId);
        if(user == null) throw new RuntimeException("用户不存在");
        request.getSession().setAttribute(UserConstant.LOGIN_USER,user);
        return ResultUtils.success(user);
    }

    @GetMapping("/get/login")
    public BaseResponse<User> getLoginUser(HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute(UserConstant.LOGIN_USER);
        return ResultUtils.success(loginUser);
    }



}
