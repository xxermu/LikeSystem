package com.example.like.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.like.constant.ThumbConstant;
import com.example.like.dao.dto.DoThumbRequest;
import com.example.like.dao.dto.ThumbCacheModel;
import com.example.like.dao.entity.Blog;
import com.example.like.dao.entity.Thumb;
import com.example.like.dao.entity.User;
import com.example.like.dao.vo.BlogVO;
import com.example.like.service.BlogService;
import com.example.like.dao.mapper.BlogMapper;
import com.example.like.service.ThumbService;
import com.example.like.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Ermu
* @description 针对表【blog】的数据库操作Service实现
* @createDate 2025-07-05 12:49:29
*/
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService{
    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private ThumbService thumbService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public BlogVO getBlogVOById(long blogId, HttpServletRequest request) {
        Blog blog = this.getById(blogId);
        User loginUser = userService.getLoginUser(request);
        return this.getBlogVO(blog, loginUser);
    }

    private BlogVO getBlogVO(Blog blog, User loginUser) {
        BlogVO blogVO = new BlogVO();
        BeanUtil.copyProperties(blog, blogVO);

        if (loginUser == null) {
            return blogVO;
        }

        DoThumbRequest request = new DoThumbRequest();
        request.setBlogId(blog.getId());
        request.setCreateTime(blog.getCreateTime());

        ThumbCacheModel model = thumbService.hasThumb(request, loginUser.getId());
        boolean exist = model == null ? false : true;
        blogVO.setHasThumb(exist);
        return blogVO;
    }

    @Override
    public List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Map<Long, Boolean> blogIdHasThumbMap = new HashMap<>();
        if (ObjUtil.isNotEmpty(loginUser)) {
            List<Object> blogIdList = blogList.stream().map(blog -> blog.getId().toString()).collect(Collectors.toList());
            // 获取点赞
            //使用multiGet获取到的结果列表跟传入参数列表顺序是一一对应的
            List<Object> thumbList = redisTemplate.opsForHash().multiGet(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId(), blogIdList);
            for (int i = 0; i < thumbList.size(); i++) {
                boolean exist = thumbList.get(i) == null ? false : true;
                blogIdHasThumbMap.put(Long.valueOf(blogIdList.get(i).toString()), exist);
            }
        }

        return blogList.stream()
                .map(blog -> {
                    BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
                    blogVO.setHasThumb(blogIdHasThumbMap.get(blog.getId()));
                    return blogVO;
                })
                .toList();
    }
}




