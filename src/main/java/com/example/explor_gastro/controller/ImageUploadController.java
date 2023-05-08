package com.example.explor_gastro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/Image")
@Tag(name = "图片上传")
public class ImageUploadController {

    @PostMapping("/upload")
    @Operation(summary = "单图上传,最大5MB")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("image") MultipartFile file) throws IOException {
        if (file.getSize() > 5 * 1024 * 1024) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "请将图片控制在5MB内");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "请上传图片");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        if (fileName != null && fileName.length() > 20) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "文件名过长，请控制在20位以内");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        File dest = null;
        if (fileName != null) {
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

    @PostMapping("/uploads")
    @Operation(summary = "多图上传,最多10张,总共50MB内")
    public ResponseEntity<List<Map<String, String>>> uploads(@RequestParam("images") MultipartFile[] files) throws IOException {
        List<Map<String, String>> responseList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file.getSize() > 50 * 1024 * 1024) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "第 " + (i + 1) + " 张图片的文件大小超过了50MB，请将其控制在50MB以内");
                responseList.add(error);
            } else {
                String fileName = file.getOriginalFilename();
                String contentType = file.getContentType();
                if (contentType != null && !contentType.startsWith("image/")) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "第 " + (i + 1) + " 张图片不是有效的图像文件，请上传有效的图像文件");
                    responseList.add(error);
                } else if (fileName != null && fileName.length() > 20) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "第 " + (i + 1) + " 张图片的文件名过长，请控制在20位以内");
                    responseList.add(error);
                } else {
                    File dest = null;
                    if (fileName != null) {
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
                    responseList.add(response);
                }
            }
        }
        List<Map<String, String>> errorResponses = new ArrayList<>();
        for (Map<String, String> response : responseList) {
            if (response.containsKey("message")) {
                errorResponses.add(response);
            }
        }
        if (errorResponses.size() > 0) {
            return new ResponseEntity<>(errorResponses, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        }
    }
}