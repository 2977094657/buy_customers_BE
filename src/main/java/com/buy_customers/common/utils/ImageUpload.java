package com.buy_customers.common.utils;

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
    public static ResponseEntity<Map<String, String>> upload(MultipartFile file) throws IOException {
        if (file.getSize() > 1024 * 1024) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "请将图片控制在1MB内");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "请上传图片");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        response.put("fileName", dest.getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
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

    /**
     * @param files       商品多个图片，以数组存入
     * @return 返回图片名字和访问链接
     */
    public static Object add(MultipartFile[] files) {
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

    public static ResponseEntity<List<Map<String, String>>> update(MultipartFile[] files) throws IOException {
        List<Map<String, String>> responseList = new ArrayList<>();
        boolean allValid = true;
        List<String> imageUrls = new ArrayList<>();
        List<String> imageFileNames = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file.getSize() > 10 * 1024 * 1024) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "第 " + (i + 1) + " 张图片的文件大小超过了10MB，请将其控制在10MB以内");
                responseList.add(error);
                allValid = false;
                break;
            } else {
                String fileName = file.getOriginalFilename();
                String contentType = file.getContentType();
                if (contentType != null && !contentType.startsWith("image/")) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "第 " + (i + 1) + " 张图片不是有效的图像文件，请上传有效的图像文件");
                    responseList.add(error);
                    allValid = false;
                    break;
                } else {
                    File dest = null;
                    if (fileName != null) {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                        String newFileName = timeStamp + fileName.substring(fileName.lastIndexOf("."));
                        dest = new File(newFileName);
                    }
                    if (dest != null) {
                        file.transferTo(dest);
                    }
                    String url = null;
                    if (dest != null) {
                        url = "http://124.221.7.201:5000/add/" + dest.getName();
                        imageUrls.add(url);
                        imageFileNames.add(dest.getName());
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

        if (!allValid) {
            for (String imageFileName : imageFileNames) {
                try {
                    File file = new File("/home/img/add/" + imageFileName);
                    if (file.exists()) {
                        boolean deleted = file.delete();
                        if (!deleted) {
                            Map<String, String> error = new HashMap<>();
                            error.put("message", "删除图片 " + imageFileName + " 失败");
                            responseList.add(error);
                        }
                    }
                } catch (Exception e) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "删除图片 " + imageFileName + " 失败，错误信息为 " + e.getMessage());
                    responseList.add(error);
                }
            }
            return new ResponseEntity<>(responseList, HttpStatus.BAD_REQUEST);
        }

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "所有图片上传成功");
        successResponse.put("urls", String.join(",", imageUrls));
        responseList.add(successResponse);

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    /**
     * 修改评价图片
     */
    // public ResponseEntity<List<Map<String, String>>> updateComments(int id, @RequestParam String comments, MultipartFile[] files) {
    //     List<ProductComments> productImgList = new ArrayList<>(); // 用于存储数据库实体的列表
    //     ProductComments productComments = productCommentsService.getById(id); // 根据id获取评论信息
    //     List<Map<String, String>> responseList = new ArrayList<>(); // 用于存储上传结果的列表
    //     if (files != null) {
    //         boolean allValid = true; // 标记所有文件是否都合法
    //         List<String> imageUrls = new ArrayList<>(); // 用于存储上传成功的图片的 URL
    //         List<String> imageUrls1 = new ArrayList<>();
    //         try {
    //             if (productComments == null) { // 如果评论不存在，返回错误信息
    //                 Map<String, String> error = new HashMap<>();
    //                 error.put("message", "评论不存在");
    //                 responseList.add(error);
    //                 return new ResponseEntity<>(responseList, HttpStatus.NOT_FOUND);
    //             }
    //             // 遍历上传的文件数组
    //             for (int i = 0; i < files.length; i++) {
    //                 MultipartFile file = files[i];
    //                 // 判断文件大小是否超过限制
    //                 if (file.getSize() > 1024 * 1024) {
    //                     Map<String, String> error = new HashMap<>();
    //                     error.put("message", "第 " + (i + 1) + " 张图片的文件大小超过了1MB，请将其控制在1MB以内");
    //                     responseList.add(error);
    //                     allValid = false;
    //                     break; // 如果有不合法的文件，立即退出循环
    //                 } else {
    //                     String fileName = file.getOriginalFilename();
    //                     String contentType = file.getContentType();
    //                     // 判断文件是否为有效的图像文件
    //                     if (contentType != null && !contentType.startsWith("image/")) {
    //                         Map<String, String> error = new HashMap<>();
    //                         error.put("message", "第 " + (i + 1) + " 张图片不是有效的图像文件，请上传有效的图像文件");
    //                         responseList.add(error);
    //                         allValid = false;
    //                         break; // 如果有不合法的文件，立即退出循环
    //                     } else {
    //                         File dest = null;
    //                         // 生成新的文件名并保存文件
    //                         if (fileName != null) {
    //                             String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
    //                             String newFileName = timeStamp + fileName.substring(fileName.lastIndexOf("."));
    //                             dest = new File(newFileName);
    //                         }
    //                         if (dest != null) {
    //                             file.transferTo(dest);
    //                         }
    //                         String url = null;
    //                         String url1;
    //                         // 生成访问图片的 URL，并将其加入列表中
    //                         if (dest != null) {
    //                             url = "http://124.221.7.201:5000/" + dest.getName(); // 修改为包含前缀的 URL
    //                             imageUrls.add(url);
    //                             url1 = dest.getName();
    //                             imageUrls1.add(url1);
    //                         }
    //                         Map<String, String> response = new HashMap<>();
    //                         response.put("url", url);
    //                         if (dest != null) {
    //                             response.put("fileName", dest.getName());
    //                         }
    //                         responseList.add(response);
    //                     }
    //                 }
    //             }
    //             // 如果存在不合法的文件，清除已经上传的图片
    //             if (!allValid) {
    //                 for (String imageUrl : imageUrls1) {
    //                     try {
    //                         File file = new File("/home/img/add/" + imageUrl); // 修改后的文件路径
    //                         if (file.exists()) {
    //                             boolean deleted = file.delete();
    //                             if (!deleted) {
    //                                 Map<String, String> error = new HashMap<>();
    //                                 error.put("message", "删除图片 " + imageUrl + " 失败");
    //                                 responseList.add(error);
    //                             }
    //                         }
    //                     } catch (Exception e) {
    //                         Map<String, String> error = new HashMap<>();
    //                         error.put("message", "删除图片 " + imageUrl + " 失败，错误信息为 " + e.getMessage());
    //                         responseList.add(error);
    //                     }
    //                 }
    //                 return new ResponseEntity<>(responseList, HttpStatus.BAD_REQUEST);
    //             }
    //             // 将上传成功的图片信息保存到数据库中
    //             List<String> imgUrls = new ArrayList<>(imageUrls);
    //             productComments.setImgId(imgUrls.toString());
    //             productComments.setComments(comments);
    //             productComments.setTime(new Date());
    //             productImgList.add(productComments);
    //             productCommentsService.updateBatchById(productImgList);
    //             return new ResponseEntity<>(responseList, HttpStatus.OK);
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    //         }
    //     }
    //     productComments.setComments(comments);
    //     productImgList.add(productComments);
    //     productComments.setTime(new Date());
    //     productCommentsService.updateBatchById(productImgList);
    //     Map<String, String> msg = new HashMap<>();
    //     msg.put("msg", "修改成功");
    //     responseList.add(msg);
    //     return new ResponseEntity<>(responseList, HttpStatus.OK);
    // }
}