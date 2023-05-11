package com.example.explor_gastro.controller;

import com.example.explor_gastro.dao.ProductImgDao;
import com.example.explor_gastro.entity.ProductImg;
import com.example.explor_gastro.service.impl.ProductImgServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/Image")
@Tag(name = "图片上传")
public class ImageUploadController {
    @Autowired
    private ProductImgDao productImgDao;
    @Autowired
    ProductImgServiceImpl productImgServiceImpl;
    // 创建一个新的ProductImg实体并设置其属性
    ProductImg productImg = new ProductImg();

    @PostMapping("/upload")
    @Operation(summary = "单图上传,最大5MB")
    @Parameters({
            @Parameter(name = "image", description = "传入的图片"),
            @Parameter(name = "productId", description = "商品id"),
    })
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam(name = "image",required = true) MultipartFile file,
            @RequestParam(name = "productId" ,required = true) Integer productId) throws IOException {
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
        productImg.setImg(url);
        productImg.setProductId(productId);

        // 保存ProductImg实体
        productImgDao.insert(productImg);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/uploads")
    @Operation(summary = "多图上传,最多10张,总共50MB内,如果多图中包含不合法的图片则清除已经上传的图片并给出不合法的提示")
    @Parameters({
            @Parameter(name = "images", description = "多个图片，以数组存入"),
            @Parameter(name = "productId", description = "商品id"),
    })
    public ResponseEntity<List<Map<String, String>>> uploads(@RequestParam("images") MultipartFile[] files, @RequestParam("productId") Integer productId) throws IOException {
        List<Map<String, String>> responseList = new ArrayList<>(); // 用于存储上传结果的列表
        List<ProductImg> productImgList = new ArrayList<>(); // 用于存储数据库实体的列表
        boolean allValid = true; // 标记所有文件是否都合法
        List<String> imageUrls = new ArrayList<>(); // 用于存储上传成功的图片的 URL
        try {
            // 遍历上传的文件数组
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                // 判断文件大小是否超过限制
                if (file.getSize() > 50 * 1024 * 1024) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "第 " + (i + 1) + " 张图片的文件大小超过了50MB，请将其控制在50MB以内");
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
                    } else if (fileName != null && fileName.length() > 20) {
                        Map<String, String> error = new HashMap<>();
                        error.put("message", "第 " + (i + 1) + " 张图片的文件名过长，请控制在20位以内");
                        responseList.add(error);
                        allValid = false;
                        break; // 如果有不合法的文件，立即退出循环
                    } else {
                        File dest = null;
                        // 生成新的文件名并保存文件
                        if (fileName != null) {
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                            String newFileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + timeStamp + fileName.substring(fileName.lastIndexOf("."));
                            dest = new File(newFileName);
                        }
                        if (dest != null) {
                            file.transferTo(dest);
                        }
                        String url = null;
                        // 生成访问图片的 URL，并将其加入列表中
                        if (dest != null) {
                            url = "add/" + dest.getName(); // 修改为不包含前缀的 URL
                            imageUrls.add(url);
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
            // 如果存在不合法的文件，清除已经上传的图片
            if (!allValid) {
                for (String imageUrl : imageUrls) {
                    try {
                        URL url = new URL("http://1.14.126.98:5000/" + imageUrl); // 修改为包含前缀的 URL
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("DELETE");
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (responseCode != 200) {
                            Map<String, String> error = new HashMap<>();
                            error.put("message", "删除图片 " + imageUrl + " 失败，错误码为 " + responseCode);
                            responseList.add(error);
                        }
                    } catch (IOException e) {
                        Map<String, String> error = new HashMap<>();
                        error.put("message", "删除图片 " + imageUrl + " 失败，错误信息为 " + e.getMessage());
                        responseList.add(error);
                    }
                }
                return new ResponseEntity<>(responseList, HttpStatus.BAD_REQUEST);
            } else {
                // 如果所有文件都合法，则将生成的图片 URL 存入数据库
                ProductImg productImg = new ProductImg();
                productImg.setImg(imageUrls.toString());
                productImg.setProductId(productId);
                productImgList.add(productImg);
                productImgServiceImpl.saveBatch(productImgList);
                return new ResponseEntity<>(responseList, HttpStatus.OK);
            }
        } catch (Exception e) {
            // 如果出现异常，将异常信息存入结果列表并返回 500 错误
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            responseList.clear(); // 清空上传结果列表
            responseList.add(error);
            // 清除已经上传的图片
            for (String imageUrl : imageUrls) {
                try {
                    URL url = new URL("http://1.14.126.98:5000/" + imageUrl); // 修改为包含前缀的 URL
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("DELETE");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode != 200) {
                        Map<String, String> deleteError = new HashMap<>();
                        deleteError.put("message", "删除图片 " + imageUrl + " 失败，错误码为 " + responseCode);
                        responseList.add(deleteError);
                    }
                } catch (IOException ioException) {
                    Map<String, String> deleteError = new HashMap<>();
                    deleteError.put("message", "删除图片 " + imageUrl + " 失败，错误信息为 " + ioException.getMessage());
                    responseList.add(deleteError);
                }
            }
            return new ResponseEntity<>(responseList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}