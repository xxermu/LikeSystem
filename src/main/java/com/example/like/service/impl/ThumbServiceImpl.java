package com.example.like.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.like.constant.ThumbConstant;
import com.example.like.dao.dto.ThumbCacheModel;
import com.example.like.dao.entity.Blog;
import com.example.like.dao.entity.Thumb;
import com.example.like.dao.entity.User;
import com.example.like.dao.dto.DoThumbRequest;
import com.example.like.service.BlogService;
import com.example.like.service.ThumbService;
import com.example.like.dao.mapper.ThumbMapper;
import com.example.like.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/**
* @author Ermu
* @description 针对表【thumb】的数据库操作Service实现
* @createDate 2025-07-05 12:49:41
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    private final UserService userService;

    private final BlogService blogService;

    private final TransactionTemplate transactionTemplate;

    private final RedisTemplate<String,Object> redisTemplate;

    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        // 外层悲观锁防止同一个用户多次点赞
        synchronized (loginUser.getId().toString().intern()) {

            // 编程式事务
            return transactionTemplate.execute(status -> {
                Long blogId = doThumbRequest.getBlogId();
                ThumbCacheModel record = this.hasThumb(doThumbRequest, loginUser.getId());
                if (record != null) {
                    throw new RuntimeException("用户已点赞");
                }

                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, blogId)
                        .setSql("thumbCount = thumbCount + 1")
                        .update();

                Thumb thumb = new Thumb();
                thumb.setUserId(loginUser.getId());
                thumb.setBlogId(blogId);
                // 更新成功才执行
                boolean success = update && this.save(thumb);

                //点赞记录存入redis
                if (success) {
                    ThumbCacheModel model = new ThumbCacheModel();
                    //过期日期为博客/视频创建日期+30天
                    Date expireTime = new Date(doThumbRequest.getCreateTime().getTime() + 30 * 24 * 60 * 60 * 1000);
                    if (expireTime.before(new Date())) {
                        //如果过期时间在当前时间之前，则不保存到缓存
                        return success;
                    }
                    model.setExpireTime(expireTime);
                    model.setThumbId(thumb.getId());
                    redisTemplate.opsForHash().put(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId(), blogId.toString(), model);
                }
                return success;
            });
        }
    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        // 加锁
        synchronized (loginUser.getId().toString().intern()) {

            // 编程式事务
            return transactionTemplate.execute(status -> {
                Long blogId = doThumbRequest.getBlogId();
                ThumbCacheModel model = this.hasThumb(doThumbRequest, loginUser.getId());
                if (model == null) {
                    throw new RuntimeException("用户未点赞");
                }
                Long thumbId = model.getThumbId();
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, blogId)
                        .setSql("thumbCount = thumbCount - 1")
                        .update();

                boolean success = update && this.removeById(thumbId);

                // 点赞记录从 Redis 删除
                if (success) {
                    //有没有记录都直接删除，没有记录时删除不会异常会报错
                    redisTemplate.opsForHash().delete(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId(), blogId.toString());
                }
                return success;

            });
        }
    }


    /**
     * 点赞缓存记录实现冷热分离
     * 当查询博客/视频该用户是否点赞时，如果该博客/视频是早于当前30天的，则从数据库中查询
     * 否则从缓存中查询
     *
     * 由于Redis不支持对Hash结构数据单个字段设置过期时间，只能对key设置
     * 所以每个记录都要单独加个字段expireTime记录其过期时间
     * 由定时任务定时清理过期数据
     *
     * 缓存结构  使用HashMap存储
     *  key ->  thumb:userId:blogId
     *  value -> {"thumbId":xxx,"expireTime":xxx}
     * @param doThumbRequest
     * @param userId
     * @return
     */
    @Override
    public ThumbCacheModel hasThumb(DoThumbRequest doThumbRequest,Long userId) {
        Date now = new Date();
        ThumbCacheModel model = null;
        if (now.getTime() - doThumbRequest.getCreateTime().getTime() > 30 * 24 * 60 * 60 * 1000) {
            log.info("点赞缓存记录已过期，查询数据库");
            Thumb thumb = this.lambdaQuery()
                    .eq(Thumb::getUserId, userId)
                    .eq(Thumb::getBlogId, doThumbRequest.getBlogId())
                    .one();
            if (thumb == null) {
                return null;
            }
            model = new ThumbCacheModel();
            model.setThumbId(thumb.getId());
            model.setExpireTime(thumb.getCreateTime());

            return model;
        }
        Object obj = redisTemplate.opsForHash().get(ThumbConstant.USER_THUMB_KEY_PREFIX + userId, doThumbRequest.getBlogId().toString());
        if (obj == null) return null;
        model = (ThumbCacheModel) obj;
        return model;
    }


}




