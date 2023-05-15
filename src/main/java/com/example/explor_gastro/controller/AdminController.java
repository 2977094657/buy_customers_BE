package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.explor_gastro.dao.UserDao;
import com.example.explor_gastro.entity.Admin;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.AdminService;
import com.example.explor_gastro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 管理员表(Admin)表控制层
 *
 * @author makejava
 * @since 2023-05-06 19:46:24
 */
@RestController
@RequestMapping("admin")
@Tag(name = "管理员")
public class AdminController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private AdminService adminService;

    @Resource
    private UserDao userDao;

    /**
     * 修改用户密码
     * @param userId
     * @param pwd
     * @return
     */
    @PutMapping(value = "/{userId}/pwd",produces  =  "text/plain;charset=UTF-8")
    @Operation(summary  =  "修改用户密码")
    public String updateUserPwd(@PathVariable Integer userId, @RequestParam String pwd) {
        User user = new User();
        user.setUserId(userId);
        user.setPwd(pwd);
        userDao.updateById(user);
        return "修改成功";
    }

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @PutMapping(value = "/{userId}/deleteuser",produces  =  "text/plain;charset=UTF-8")
    @Operation(summary  =  "删除用户")
    public String deleteUserPwd(@PathVariable Integer userId) {
        userDao.deleteById(userId);
        return "删除成功";
    }
    @PostMapping(value = "login",produces  =  "text/plain;charset=UTF-8")
    @Operation(summary  =  "管理员登录")
    public String login(@RequestBody Admin admin) {
        Admin result = adminService.getOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getAdmin, admin.getAdmin())
                        .eq(Admin::getPwd, admin.getPwd())
        );
        if (result == null) {
            // 登录失败，返回错误提示
            return "账号或密码错误";
        } else {
            // 登录成功，返回成功提示
            return "登录成功";
        }
    }

}

