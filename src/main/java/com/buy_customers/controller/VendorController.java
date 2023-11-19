package com.buy_customers.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.buy_customers.common.utils.ErnieBotTurbo;
import com.buy_customers.common.utils.PromptImg;
import com.buy_customers.entity.Vendor;
import com.buy_customers.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

/**
 * 商家表(Vendor)表控制层
 *
 * @author makejava
 * @since 2023-05-08 11:55:49
 */
@RestController
@RequestMapping("vendor")
@Tag(name = "商家功能")
public class VendorController extends ApiController {
    //
    /**
     * 服务对象
     */
    @Resource
    private VendorService vendorService;

    /**
     * 分页查询所有商家
     *
     * @param page   分页对象
     * @param vendor 查询实体
     * @return 所有数据
     */
    @GetMapping("vendorInquire")
    @Operation(summary = "分页查询所有商家")
    @Parameters({
            @Parameter(name = "page", description = "查询的页面"),
            @Parameter(name = "vendor", description = "每页显示多少商家"),
            @Parameter(name = "isAsc", description = "是否升序排列 false(否) true(是)"),
            @Parameter(name = "sortField", description = "根据此参数传入的字段排序")
    })
    public IPage<Vendor> page(
            @RequestParam(name = "page",defaultValue = "1") int page,
            @RequestParam(name = "vendor",defaultValue = "10") int vendor,
            @RequestParam(name = "isAsc", required = false,defaultValue = "true") Optional<Boolean> isAsc,
            @RequestParam(name = "sortField", required = false,defaultValue = "vendor_id") Optional<String> sortField) {
        return vendorService.testSelectPage(page, vendor, isAsc, sortField);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 商家id
     * @return 单条数据
     */
    @PostMapping("vendoradd")
    @Operation(summary = "查看商家信息")
    @Parameters({
            @Parameter(name = "id", description = "商家id"),
    })
    public R selectOne(
            @RequestParam Serializable id) {
        return success(this.vendorService.getById(id));
    }



        /**
         * 商家登录接口
         * phone 手机号
         * password 商家密码
         */
        @PostMapping(value = "/login",produces  =  "text/plain;charset=UTF-8")
        @Operation(summary = "商家登录")
        @Parameters({
                @Parameter(name = "phone", description = "手机号"),
                @Parameter(name = "password", description = "商家密码"),
        })
        public String login(
                @RequestParam(defaultValue = "12222222222") String phone,
                @RequestParam(defaultValue = "5555") String password){
            Vendor vendor=vendorService.LoginIn(phone, password);
            if (vendor == null){
                return "登录失败";
            }
            return "登录成功";
        }

        /**
         * 商家注册接口
         * username 商家名
         * phone 手机号
         * password 商家密码
         *
         */
        @PostMapping(value = "register",produces  =  "text/plain;charset=UTF-8")
        @Operation(summary = "商家注册")
        @Parameters({
                @Parameter(name = "username", description = "商家名"),
                @Parameter(name = "phone", description = "手机号"),
                @Parameter(name = "password", description = "商家密码"),
        })
        public String register(@RequestParam String username, @RequestParam String phone, @RequestParam String password) {
            Vendor vendor=vendorService.register(username, phone, password);
            if (vendor==null){
                return "该手机号已经注册过，请直接登录";
            }
            return "注册成功";
        }

    /**
     * 修改商家信息
     *
     * @param vendorId 商家id
     * @param phone 手机号
     * @param name  商家名
     * @param pwd   商家密码
     * @param description 商家简介
     * @param openingTime 营业时间
     */

    @PutMapping("update")
    @Operation(summary = "修改商家信息")
    public R updateVendor(@RequestParam("vendorId") Integer vendorId,
                          @RequestParam(value = "phone", required = false) String phone,
                          @RequestParam(value = "name", required = false) String name,
                          @RequestParam(value = "pwd", required = false) String pwd,
                          @RequestParam(value = "description", required = false) String description,
                          @RequestParam(value = "openingTime", required = false) String openingTime) {

        Vendor vendor = vendorService.getById(vendorId);
        if (vendor == null) {
            return failed("商家不存在");
        }

        // 判断手机号是否为空，为空则不进行set操作
        if (phone != null && !phone.isEmpty()) {
            vendor.setPhone(phone);
        }

        // 判断商家名是否为空，为空则不进行set操作
        if (name != null && !name.isEmpty()) {
            vendor.setName(name);
        }

        // 判断商家密码是否为空，为空则不进行set操作
        if (pwd != null && !pwd.isEmpty()) {
            vendor.setPwd(pwd);
        }

        // 判断商家简介是否为空，为空则不进行set操作
        if (description != null && !description.isEmpty()) {
            vendor.setDescription(description);
        }

        // 判断营业时间是否为空，为空则不进行set操作
        if (openingTime != null && !openingTime.isEmpty()) {
            vendor.setOpeningTime(openingTime);
        }

        boolean result = vendorService.updateById(vendor);
        if (result) {
            return success("商家信息修改成功！");
        } else {
            return failed("商家信息修改失败！");
        }
    }

    @GetMapping("vendorPrompt")
    @Operation(summary = "根据提示生成产品标题")
    public JSON vendorPrompt(String prompt) throws IOException {
        return ErnieBotTurbo.vendorPrompt(prompt);
    }

    @GetMapping("promptImg")
    @Operation(summary = "根据提示生成产品图片的task_id")
    public String promptImg(@RequestParam String prompt) throws IOException {
        return PromptImg.promptImg(prompt);
    }

    @GetMapping("getImg")
    @Operation(summary = "根据task_id返回图片的地址")
    public JSON getImg(@RequestParam String task_id) throws IOException {
        String jsonString = PromptImg.getImg(task_id); // 这是你的函数返回的JSON字符串
        return JSON.parseObject(jsonString);
    }
}