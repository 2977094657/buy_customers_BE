package com.buy_customers.controller;


import com.baomidou.mybatisplus.extension.api.ApiController;
import com.buy_customers.common.config.api.ResultData;
import com.buy_customers.common.config.DataEncryptionAndDecryption.RSAKeyPairGenerator;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 管理员表(Admin)表控制层
 *
 * @author makejava
 * @since 2023-05-06 19:46:24
 */
@RestController
@CrossOrigin
@RequestMapping("admin")
public class AdminController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private RSAKeyPairGenerator keyPairGenerator;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("publicKey")
    public ResultData<String> publicKey() {
        return ResultData.success(keyPairGenerator.getPublicKey());
    }

    @PostMapping("privateKey")
    public ResultData<String> privateKey(@RequestBody Map<String, String> body) {
        return ResultData.success(stringRedisTemplate.opsForValue().get(body.get("publicKey")));
    }

    @PostMapping("setNonce")
    public ResultData<String> setNonce(@RequestBody Map<String, String> nonce) {
        try {
            if (nonce != null) {
                String nonce1 = nonce.get("nonce");
                stringRedisTemplate.opsForValue().set(nonce1, nonce1, 60, TimeUnit.SECONDS);
                return ResultData.success("nonce存储成功");
            } else {
                return ResultData.fail(400,"请求正文中缺少nonce");
            }
        } catch (Exception e) {
            return ResultData.fail(500,"无法存储nonce");
        }
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamSource> download(@RequestParam String url) throws IOException {
        InputStream in = new URL(url).openStream();
        InputStreamSource resource = new InputStreamResource(in);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=image.webp");
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}

