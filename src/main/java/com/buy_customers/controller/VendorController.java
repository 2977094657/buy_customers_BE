package com.buy_customers.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.buy_customers.common.utils.ErnieBotTurbo;
import com.buy_customers.common.utils.PromptImg;
import com.buy_customers.entity.Vendor;
import com.buy_customers.service.VendorService;
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

    /**
     * 处理来自客户端的请求，获取特定提示信息后的供应商响应。
     *
     * @param prompt 客户端请求中包含的提示信息。
     * @return 返回一个JSON对象，包含供应商的响应信息。
     * @throws IOException 如果在处理过程中发生IO异常。
     */
    @GetMapping("vendorPrompt")
    public JSON vendorPrompt(String prompt) throws IOException {
        // 调用ErnieBotTurbo类中的vendorPrompt方法，传入提示信息并返回响应的JSON对象
        return ErnieBotTurbo.vendorPrompt(prompt);
    }

    /**
     * 根据提供的提示文本生成对应的图片。
     *
     * @param prompt 提示文本，用于生成图片的内容。
     * @return 返回一个字符串，表示生成图片的过程或结果。
     * @throws IOException 如果在生成图片过程中发生IO异常。
     */
    @GetMapping("promptImg")
    public String promptImg(@RequestParam String prompt) throws IOException {
        // 调用PromptImg类的promptImg方法，传入prompt参数，并返回结果
        return PromptImg.promptImg(prompt);
    }

    /**
     * 通过GET请求获取图片信息。
     *
     * @param task_id 任务ID，用于指定要获取图片信息的任务。
     * @return 返回一个JSON对象，包含图片的相关信息。这个对象是通过解析从PromptImg.getImg(task_id)方法返回的JSON字符串得到的。
     * @throws IOException 如果在获取图片信息过程中发生IO异常。
     */
    //注意，此接口如果在本地测试，连接的有vpn时访问会导致连接超时！！！！！
    @GetMapping("getImg")
    public JSON getImg(@RequestParam String task_id) throws IOException {
        // 调用PromptImg.getImg(task_id)方法获取图片信息的JSON字符串
        String jsonString = PromptImg.getImg(task_id);
        // 将获取到的JSON字符串解析成JSON对象并返回
        return JSON.parseObject(jsonString);
    }
}