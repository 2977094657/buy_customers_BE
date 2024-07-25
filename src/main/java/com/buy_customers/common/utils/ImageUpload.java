package com.buy_customers.common.utils;

import com.buy_customers.common.config.api.ResultData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ImageUpload {
    public static ResultData<String> upload(MultipartFile file) throws IOException {
        if (file.getSize() > 1024 * 1024) {
            return ResultData.fail(400, "请将图片控制在1MB内");
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            return ResultData.fail(400, "请上传图片");
        }
        String fileName = file.getOriginalFilename();
        String suffix = null;
        if (fileName != null) {
            suffix = fileName.substring(fileName.lastIndexOf("."));
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        String newFileName = timeStamp + suffix;
        File dest = new File(newFileName);
        file.transferTo(dest);
        String url = "http://124.221.7.201:5000/add/" + dest.getName();
        return ResultData.success(url);
    }


    public static Object comments(MultipartFile[] files) {
        List<Map<String, String>> responseList = new ArrayList<>(); // 用于存储上传结果的列表
        List<String> productImgList = new ArrayList<>(); // 用于存储数据库实体的列表
        boolean allValid = true; // 标记所有文件是否都合法
        List<String> imageUrls = new ArrayList<>(); // 用于存储上传成功的图片的 URL
        List<String> imageUrls1 = new ArrayList<>();

        // 判断图片数量是否超过5张
        if (files.length > 5) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "上传的图片数量超过了5张，请控制在5张以内");
            responseList.add(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseList);
        }

        try {
            // 遍历上传的文件数组
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                // 判断文件大小是否超过限制
                if (file.getSize() > 1024 * 1024) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "第 " + (i + 1) + " 张图片的文件大小超过了1MB，请将其控制在1MB以内");
                    responseList.add(error);
                    allValid = false;
                    break; // 如果有不合法的文件，立即退出循环
                } else {
                    String fileName = file.getOriginalFilename();
                    String contentType = file.getContentType();
                    // 判断文件是否为有效的图像文件
                    if (contentType != null && !contentType.startsWith("image/")) {
                        Map<String, String> error = new HashMap<>();
                        error.put("message", "第 " + (i + 1) + " 张图片不是有效的图像文件，请上传有效的图像文件");
                        responseList.add(error);
                        allValid = false;
                        break; // 如果有不合法的文件，立即退出循环
                    } else {
                        File dest = null;
                        // 生成新的文件名并保存文件
                        if (fileName != null) {
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                            // 在生成新的文件名时添加随机数
                            String newFileName = timeStamp + "_" + new Random().nextInt(1000000) + fileName.substring(fileName.lastIndexOf("."));
                            dest = new File(newFileName);
                        }
                        if (dest != null) {
                            file.transferTo(dest);
                        }
                        String url = null;
                        String url1;
                        // 生成访问图片的 URL，并将其加入列表中
                        if (dest != null) {
                            url = "http://124.221.7.201:5000/add/" + dest.getName(); // 修改为包含前缀的 URL
                            imageUrls.add(url);
                            url1 = dest.getName();
                            imageUrls1.add(url1);
                        }
                        Map<String, String> response = new HashMap<>();
                        response.put("url", url);
                        if (dest != null) {
                            response.put("fileName", dest.getName());
                        }
                        responseList.add(response);
                        // 创建数据库实体对象并保存到数据库中
                        productImgList.add(url);
                    }
                }
            }
            // 如果存在不合法的文件，清除已经上传的图片
            if (!allValid) {
                for (String imageUrl : imageUrls1) {
                    try {
                        File file = new File("/home/img/add/" + imageUrl); // 修改后的文件路径
                        if (file.exists()) {
                            boolean deleted = file.delete();
                            if (!deleted) {
                                Map<String, String> error = new HashMap<>();
                                error.put("message", "删除图片 " + imageUrl + " 失败");
                                responseList.add(error);
                            }
                        }
                    } catch (Exception e) {
                        Map<String, String> error = new HashMap<>();
                        error.put("message", "删除图片 " + imageUrl + " 失败，错误信息为 " + e.getMessage());
                        responseList.add(error);
                    }
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseList);
            }
            return productImgList.toString();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "上传文件失败，错误信息为 " + e.getMessage());
            responseList.add(error);
            // 如果发生异常，清除已经上传的图片
            for (String imageUrl : imageUrls) {
                try {
                    File file = new File("/home/img/add/" + imageUrl); // 修改后的文件路径
                    if (file.exists()) {
                        boolean deleted = file.delete();
                        if (!deleted) {
                            Map<String, String> error2 = new HashMap<>();
                            error2.put("message", "删除图片 " + imageUrl + " 失败");
                            responseList.add(error2);
                        }
                    }
                } catch (Exception e2) {
                    Map<String, String> error2 = new HashMap<>();
                    error2.put("message", "删除图片 " + imageUrl + " 失败，错误信息为 " + e2.getMessage());
                    responseList.add(error2);
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseList);
        }
    }


    public static ResultData<List<String>> add(MultipartFile[] files) throws IOException {
        String url = null;
        List<String> imageUrls = new ArrayList<>(); // 用于存储上传成功的图片的 URL
        List<String> imageUrls1 = new ArrayList<>();
        // 遍历上传的文件数组
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            // 判断文件大小是否超过限制
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResultData.fail(400, "第 " + (i + 1) + " 张图片的文件大小超过了5MB，请将其控制在5MB以内");
            } else {
                String fileName = file.getOriginalFilename();
                String contentType = file.getContentType();
                // 判断文件是否为有效的图像文件
                if (contentType != null && !contentType.startsWith("image/")) {
                    return ResultData.fail(400, "第 " + (i + 1) + " 张图片不是有效的图像文件，请上传有效的图像文件");
                } else {
                    File dest = null;
                    // 生成新的文件名并保存文件
                    String newFileName = null;
                    if (fileName != null) {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                        String extension = fileName.substring(fileName.lastIndexOf(".")); // 获取源文件的扩展名
                        newFileName = timeStamp + "_" + (i + 1) + extension; // 在时间戳后加上索引
                        dest = new File(newFileName);
                    }
                    if (dest != null) {
                        file.transferTo(dest);
                    }
                    // 生成访问图片的 URL，并将其加入列表中
                    if (newFileName != null) {
                        url = "http://124.221.7.201:5000/add/" + newFileName;
                        imageUrls.add(url);
                    }
                }
            }
        }
        return ResultData.success(imageUrls);
    }
}