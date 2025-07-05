package com.example.like.dao.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DoThumbRequest {
    //博客id
    private Long blogId;

    //博客过期时间
    private Date createTime;
}
