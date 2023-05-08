package com.example.explor_gastro.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ImageUploadController {

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("image") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "请上传图片");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        // 判断文件名长度是否超过20位
        if (fileName != null && fileName.length() > 20) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "文件名过长，请控制在20位以内");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        File dest = null;
        if (fileName != null) {
            // 生成新的文件名，格式为：原文件名_时间戳.扩展名
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
            String newFileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + timeStamp + fileName.substring(fileName.lastIndexOf("."));
            dest = new File(newFileName);
        }
        if (dest != null) {
            file.transferTo(dest);
        }
        String url = null;
        if (dest != null) {
            url = "http://1.14.126.98:5000/add/" + dest.getName();
        }
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        if (dest != null) {
            response.put("fileName", dest.getName());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}