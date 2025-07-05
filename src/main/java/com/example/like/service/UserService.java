package com.example.like.service;

import com.example.like.dao.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author Ermu
* @description 针对表【user】的数据库操作Service
* @createDate 2025-07-05 12:49:41
*/
public interface UserService extends IService<User> {
    User getLoginUser(HttpServletRequest request);
}
