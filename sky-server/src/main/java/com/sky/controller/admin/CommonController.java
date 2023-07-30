package com.sky.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@Api("通用接口")
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    @ApiOperation("上传文件到OSS")
    public Result uploadFile(@RequestParam("file") MultipartFile file){
        log.info("文件上传：{}", file);

        try {

            String suffix = StrUtil.subAfter(file.getOriginalFilename(), '.', true);
            String name = UUID.randomUUID().toString() + "." + suffix;
            String fileUrl = aliOssUtil.upload(file.getBytes(), name);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
