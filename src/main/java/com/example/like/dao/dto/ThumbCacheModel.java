package com.example.like.dao.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ThumbCacheModel {
    private Long thumbId;
    private Date expireTime;
}
