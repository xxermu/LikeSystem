package com.example.like.dao.mapper;

import com.example.like.dao.entity.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Ermu
* @description 针对表【blog】的数据库操作Mapper
* @createDate 2025-07-05 12:49:29
* @Entity com.example.like.dao.entity.Blog
*/
@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

}




