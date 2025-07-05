package com.example.like.controller;

import com.example.like.common.BaseResponse;
import com.example.like.common.ResultUtils;
import com.example.like.dao.dto.DoThumbRequest;
import com.example.like.service.ThumbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("thumb")
@Tag(name = "点赞接口")
public class ThumbController {  
    @Resource
    private ThumbService thumbService;
  
    @PostMapping("/do")
    @Operation(summary = "点赞")
    public BaseResponse<Boolean> doThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request) {
        Boolean success = thumbService.doThumb(doThumbRequest, request);  
        return ResultUtils.success(success);
    }

    @PostMapping("/undo")
    @Operation(summary = "取消点赞")
    public BaseResponse<Boolean> undoThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request) {
        Boolean success = thumbService.undoThumb(doThumbRequest, request);
        return ResultUtils.success(success);
    }

}
