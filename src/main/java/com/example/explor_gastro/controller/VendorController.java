package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.entity.Product;
import com.example.explor_gastro.entity.Star;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.entity.Vendor;
import com.example.explor_gastro.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

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
    @PostMapping("/{id}/vendorid")
    @Operation(summary = "查看商家信息")
    @Parameters({
            @Parameter(name = "id", description = "商家id"),
    })
    public R selectOne(
            @RequestParam(name = "id",defaultValue = "1") int id1,
            @PathVariable Serializable id) {
        return success(this.vendorService.getById(id));
    }



        /**
         * 商家登录接口
         *
         * phone 手机号
         * password 商家密码
         */
        @PostMapping(value = "/login",produces  =  "text/plain;charset=UTF-8")
        @Operation(summary = "商家登录")
        @Parameters({
                @Parameter(name = "phone", description = "手机号"),
                @Parameter(name = "password", description = "商家密码"),
        })
        public String login(@RequestParam(defaultValue = "12222222222") String phone, @RequestParam(defaultValue = "5555") String password){
            Vendor vendor=vendorService.LoginIn(phone, password);
            if (vendor==null){
                return "登录失败";
            }
            return "登录成功";
        }

        /**
         * 商家注册接口
         *
         * username 商家名
         * phone 手机号
         * password 商家密码
         *
         */
        @PostMapping(value = "/register",produces  =  "text/plain;charset=UTF-8")
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
     * 修改数据
     *
     * @param vendor 实体对象
     * @return 修改结果
     */
    @PutMapping("update")
    @Operation(summary = "修改商家信息")
    @Parameters({
            @Parameter(name = "vendor", description = "修改商家信息"),
    })
    public R update(@RequestBody Vendor vendor) {
        return success(this.vendorService.updateById(vendor));
    }




//    /**
//     * 新增数据
//     *
//     * @param vendor 实体对象
//     * @return 新增结果
//     */
//    @PostMapping("add")
//    @Operation(summary = "新增数据")
//    @Parameters({
//            @Parameter(name = "vendor", description = "实体对象"),
//    })
//    public R insert(@RequestBody Vendor vendor) {
//        return success(this.vendorService.save(vendor));
//    }
//    /**
//     * 删除数据
//     *
//     * @param idList 主键结合
//     * @return 删除结果
//     */
//    @DeleteMapping("delete")
//    @Operation(summary = "删除数据")
//    @Parameters({
//            @Parameter(name = "idList", description = "主键结合"),
//    })
//    public R delete(@RequestParam("idList") List<Long> idList) {
//        return success(this.vendorService.removeByIds(idList));
//    }
}