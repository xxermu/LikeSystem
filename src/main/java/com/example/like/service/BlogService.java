package com.example.like.service;

import com.example.like.dao.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.like.dao.vo.BlogVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author Ermu
* @description 针对表【blog】的数据库操作Service
* @createDate 2025-07-05 12:49:29
*/
public interface BlogService extends IService<Blog> {
    BlogVO getBlogVOById(long blogId, HttpServletRequest request);
}
