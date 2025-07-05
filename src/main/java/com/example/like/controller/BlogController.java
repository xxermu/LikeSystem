package com.example.like.controller;

import com.example.like.common.BaseResponse;
import com.example.like.common.ResultUtils;
import com.example.like.dao.entity.Blog;
import com.example.like.dao.vo.BlogVO;
import com.example.like.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("blog")
@Tag(name = "博客接口")
public class BlogController {  
    @Resource
    private BlogService blogService;
  
    @GetMapping("/get")
    @Operation(summary = "获取博客")
    public BaseResponse<BlogVO> get(long blogId, HttpServletRequest request) {
        BlogVO blogVO = blogService.getBlogVOById(blogId, request);  
        return ResultUtils.success(blogVO);
    }

    @GetMapping("/list")
    @Operation(summary = "获取博客列表")
    public BaseResponse<List<BlogVO>> list(HttpServletRequest request) {
        List<Blog> blogList = blogService.list();
        List<BlogVO> blogVOList = blogService.getBlogVOList(blogList, request);
        return ResultUtils.success(blogVOList);
    }

}
