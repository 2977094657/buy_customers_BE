package com.example.explor_gastro.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.explor_gastro.entity.Product;
import com.example.explor_gastro.entity.ProductComments;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.ProductCommentsService;
import com.example.explor_gastro.service.ProductService;
import com.example.explor_gastro.service.impl.ProductServiceImpl;
import com.example.explor_gastro.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Tag(name = "图片上传")
public class ImageUpload {
    @Resource
    private UserServiceImpl userServiceImpl;
    @Resource
    private ProductServiceImpl productServiceImpl;
    @Resource
    private ProductService productService;
    @Resource
    private ProductCommentsService productCommentsService;

    @PostMapping("/upload")
    @Operation(summary = "单图上传,最大1MB")
    @Parameters({
            @Parameter(name = "image", description = "传入的图片"),
            @Parameter(name = "userid", description = "用户id"),
    })
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam(name = "image") MultipartFile file,
            @RequestParam(name = "userid") Integer userid) throws IOException {
        if (file.getSize() > 1024 * 1024) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "请将图片控制在1MB内");
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
            url = "http://124.221.7.201:5000/add/" + dest.getName();
        }
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        if (dest != null) {
            response.put("fileName", dest.getName());
        }
        User user = new User();
        user.setUserAvatar(url);
        user.setUserId(userid);

        userServiceImpl.updateById(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<List<Map<String, String>>> comments(
            @RequestParam int userId,
            @RequestParam String comments,
            @RequestParam(name = "imgId") MultipartFile[] files,
            @RequestParam int productId,
            @RequestParam int score
    ) {
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
            ProductComments productComments = new ProductComments();
            productComments.setUserId(userId);
            productComments.setComments(comments);
            productComments.setProductId(productId);
            productComments.setImgId(productImgList.toString());
            productComments.setScore(score);
            productCommentsService.save(productComments);

            QueryWrapper<ProductComments> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("product_id", productId);
            // 获取该商品的所有评分
            List<ProductComments> scores = productCommentsService.list(queryWrapper);

            // 计算平均分数
            Double averageScore = calculateAverageScore(scores);

            // 更新商品实体的分数和总评分人数
            Product product = productService.getById(productId);  // 从数据库中获取当前的商品实体
            UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("product_id", productId);  // 设置要更新的商品ID
            updateWrapper.set("score", averageScore);  // 设置要更新的字段
            updateWrapper.set("total_comments", scores.size());

            // 根据用户评分更新对应的星级数量字段
            switch (score) {
                case 5:
                    updateWrapper.set("five", product.getFive() + 1);
                    break;
                case 4:
                    updateWrapper.set("four", product.getFour() + 1);
                    break;
                case 3:
                    updateWrapper.set("three", product.getThree() + 1);
                    break;
                case 2:
                    updateWrapper.set("two", product.getTwo() + 1);
                    break;
                case 1:
                    updateWrapper.set("one", product.getOne() + 1);
                    break;
            }
            productService.update(updateWrapper);
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
        return ResponseEntity.ok(responseList);
    }

    /**
     * 计算平均分数。
     *
     * @param scores 评分实体列表
     * @return 平均分数
     */
    private Double calculateAverageScore(List<ProductComments> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }

        Double sum = 0.0;
        for (ProductComments score : scores) {
            sum += score.getScore();
        }

        return sum / scores.size();
    }

    /**
     * @param files       商品多个图片，以数组存入
     * @param productName 商品名字
     * @param name        商家名字，根据商家登陆的账号来传入此参数，不允许商家填入
     * @param price       价格
     * @param category    商品分类，此处应为下拉栏，不允许商家填入，四个分类:主食、小吃、甜品、饮料
     * @return 返回图片名字和访问链接
     */
    public ResponseEntity<List<Map<String, String>>> add(
            @RequestParam("images") MultipartFile[] files,
            @RequestParam("productName") String productName,
            @RequestParam("name") String name,
            @RequestParam("price") Integer price,
            @RequestParam("category") String category
    ) {
        List<Map<String, String>> responseList = new ArrayList<>(); // 用于存储上传结果的列表
        List<String> productImgList = new ArrayList<>(); // 用于存储数据库实体的列表
        boolean allValid = true; // 标记所有文件是否都合法
        List<String> imageUrls = new ArrayList<>(); // 用于存储上传成功的图片的 URL
        List<String> imageUrls1 = new ArrayList<>();
        try {
            // 遍历上传的文件数组
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                // 判断文件大小是否超过限制
                if (file.getSize() > 5 * 1024 * 1024) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "第 " + (i + 1) + " 张图片的文件大小超过了5MB，请将其控制在5MB以内");
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
                        String url = null;
                        // 生成访问图片的 URL，并将其加入列表中
                        if (newFileName != null) {
                            url = "http://124.221.7.201:5000/add/" + newFileName;
                            imageUrls.add(url);
                            imageUrls1.add(newFileName);
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
            Product product = new Product();
            product.setProductName(productName);
            product.setCategory(category);
            product.setPrice(price);
            product.setImg(productImgList.toString());
            product.setName(name);
            productService.save(product);
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
        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "根据商品id修改商品图片")
    @Parameters({
            @Parameter(name = "productId", description = "商品id"),
            @Parameter(name = "images", description = "多个图片，以数组存入")
    })
    public ResponseEntity<List<Map<String, String>>> update(@RequestParam(name = "productId") Integer productId, @RequestParam(name = "images") MultipartFile[] files) throws IOException {
        List<Map<String, String>> responseList = new ArrayList<>(); // 用于存储上传结果的列表
        List<Product> productImgList = new ArrayList<>(); // 用于存储数据库实体的列表
        boolean allValid = true; // 标记所有文件是否都合法
        List<String> imageUrls = new ArrayList<>(); // 用于存储上传成功的图片的 URL
        List<String> imageUrls1 = new ArrayList<>();
        try {
            Product product = productServiceImpl.getById(productId); // 根据id获取商品信息
            if (product == null) { // 如果商品不存在，返回错误信息
                Map<String, String> error = new HashMap<>();
                error.put("message", "商品不存在");
                responseList.add(error);
                return new ResponseEntity<>(responseList, HttpStatus.NOT_FOUND);
            }
            // 遍历上传的文件数组
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                // 判断文件大小是否超过限制
                if (file.getSize() > 10 * 1024 * 1024) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "第 " + (i + 1) + " 张图片的文件大小超过了10MB，请将其控制在10MB以内");
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
                return new ResponseEntity<>(responseList, HttpStatus.BAD_REQUEST);
            }
            // 将上传成功的图片信息保存到数据库中
            List<String> imgUrls = new ArrayList<>(imageUrls);
            product.setImg(imgUrls.toString());
            productImgList.add(product);
            productServiceImpl.updateBatchById(productImgList);
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 修改评价图片
     */
    public ResponseEntity<List<Map<String, String>>> updateComments(int id, @RequestParam String comments, MultipartFile[] files) {
        List<ProductComments> productImgList = new ArrayList<>(); // 用于存储数据库实体的列表
        ProductComments productComments = productCommentsService.getById(id); // 根据id获取评论信息
        List<Map<String, String>> responseList = new ArrayList<>(); // 用于存储上传结果的列表
        if (files != null) {
            boolean allValid = true; // 标记所有文件是否都合法
            List<String> imageUrls = new ArrayList<>(); // 用于存储上传成功的图片的 URL
            List<String> imageUrls1 = new ArrayList<>();
            try {
                if (productComments == null) { // 如果评论不存在，返回错误信息
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "评论不存在");
                    responseList.add(error);
                    return new ResponseEntity<>(responseList, HttpStatus.NOT_FOUND);
                }
                // 遍历上传的文件数组
                for (int i = 0; i < files.length; i++) {
                    MultipartFile file = files[i];
                    // 判断文件大小是否超过限制
                    if (file.getSize() > 10 * 1024 * 1024) {
                        Map<String, String> error = new HashMap<>();
                        error.put("message", "第 " + (i + 1) + " 张图片的文件大小超过了10MB，请将其控制在10MB以内");
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
                            String url1;
                            // 生成访问图片的 URL，并将其加入列表中
                            if (dest != null) {
                                url = "http://124.221.7.201:5000/" + dest.getName(); // 修改为包含前缀的 URL
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
                    return new ResponseEntity<>(responseList, HttpStatus.BAD_REQUEST);
                }
                // 将上传成功的图片信息保存到数据库中
                List<String> imgUrls = new ArrayList<>(imageUrls);
                productComments.setImgId(imgUrls.toString());
                productComments.setComments(comments);
                productComments.setTime(new Date());
                productImgList.add(productComments);
                productCommentsService.updateBatchById(productImgList);
                return new ResponseEntity<>(responseList, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        productComments.setComments(comments);
        productImgList.add(productComments);
        productComments.setTime(new Date());
        productCommentsService.updateBatchById(productImgList);
        Map<String, String> msg = new HashMap<>();
        msg.put("msg", "修改成功");
        responseList.add(msg);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}