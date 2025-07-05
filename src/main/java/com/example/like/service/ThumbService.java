package com.example.like.service;

import com.example.like.dao.dto.ThumbCacheModel;
import com.example.like.dao.entity.Thumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.like.dao.dto.DoThumbRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author Ermu
* @description 针对表【thumb】的数据库操作Service
* @createDate 2025-07-05 12:49:41
*/
public interface ThumbService extends IService<Thumb> {
    /**
     * 点赞
     * @param doThumbRequest
     * @param request
     * @return {@link Boolean }
     */
    Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

    /**
     * 取消点赞
     * @param doThumbRequest
     * @param request
     * @return {@link Boolean }
     */
    Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

    /**
     * 查看是否点赞
     * @param doThumbRequest
     * @param userId
     * @return
     */
    ThumbCacheModel hasThumb(DoThumbRequest doThumbRequest, Long userId);


}
