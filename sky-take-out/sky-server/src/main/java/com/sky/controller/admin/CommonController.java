package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用相关接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传，原始文件名：{}", file.getOriginalFilename());
        try {
            String fileName = file.getOriginalFilename();
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension; // 新文件名 = 随机UUID + 文件扩展名
            log.info("文件上传，新文件名：{}", newFileName);
            String url = aliOssUtil.upload(file.getBytes(), newFileName);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败，文件名：{}", file.getOriginalFilename(), e);
            return Result.error("文件上传失败");
        }
    }
}
