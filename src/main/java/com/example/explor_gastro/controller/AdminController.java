package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import java.util.Optional;

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
     * 搜索功能
     * @param keyword
     * @return
     */
    @GetMapping("/search")
    @Operation(summary = "搜索用户")
    @Parameters({
            @Parameter(name = "keyword", description = "关键词，示例值：陈;刘"),
    })
    public List<User> searchUsers(@RequestParam String keyword) {
        return userDao.searchUsers(keyword);
    }

    /**
     * @param current   当前所在页面
     * @param size      每页显示数量
     * @param isAsc     是否升序排列，不传或传入空值则不排序
     * @param sortField 根据传入的此字段来排序，不传或传入空值则不排序
     * @return 返回所有数据
     * cy
     */
    @GetMapping("all")
    @Operation(summary = "分页查询所有用户")
    @Parameters({
            @Parameter(name = "current", description = "所在页面"),
            @Parameter(name = "size", description = "每页显示数据"),
            @Parameter(name = "isAsc", description = "是否升序排列"),
            @Parameter(name = "sortField", description = "根据此参数传入的字段排序")
    })
    public IPage<User> page(
            @RequestParam(name = "current",defaultValue = "1") int current,
            @RequestParam(name = "size",defaultValue = "10") int size,
            @RequestParam(name = "isAsc", required = false,defaultValue = "true") Optional<Boolean> isAsc,
            @RequestParam(name = "sortField", required = false,defaultValue = "user_id") Optional<String> sortField) {
        return adminService.testSelectPage(current, size, isAsc, sortField);
    }
    /**
     * 修改用户密码
     * @param userId
     * @param pwd
     * @return
     * cy
     */
    @PutMapping(value = "/{userId}/pwd",produces  =  "text/plain;charset=UTF-8")
    @Operation(summary  =  "修改用户密码")
    @Parameters({
            @Parameter(name = "userId", description = "用户id ;示例值：2"),
            @Parameter(name = "pwd", description = "修改的密码 ;示例值:12345678"),
    })
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
     * cy
     */
    @PutMapping(value = "/{userId}/deleteuser",produces  =  "text/plain;charset=UTF-8")
    @Operation(summary  =  "删除用户")
    public String deleteUserPwd(@PathVariable Integer userId) {
        if (userDao.selectById(userId) == null) {
            return "删除失败，该用户不存在";
        } else {
            userDao.deleteById(userId);
            return "删除成功";
        }
    }

    /**
     * 管理员登录
     * @param admin
     * @return
     */
    @PostMapping(value = "login",produces  =  "text/plain;charset=UTF-8")
    @Operation(summary  =  "管理员登录")
    @Parameters({
            @Parameter(name = "admin", description = "管理员账号;示例值(admin)"),
            @Parameter(name = "pwd", description = "管理员密码;示例值(admin)"),
    })
    public String login(@RequestParam(value = "admin",defaultValue = "admin") String admin,
                        @RequestParam(value = "pwd",defaultValue = "admin") String pwd) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("admin", admin)
                .eq("pwd", pwd);

        Admin result = adminService.getOne(queryWrapper);
        if (result != null) {
            return "登录成功";
        } else {
            return "登录失败";
        }
    }

}

