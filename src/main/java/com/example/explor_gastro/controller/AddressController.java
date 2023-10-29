package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.example.explor_gastro.common.utils.Response;
import com.example.explor_gastro.entity.Address;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.AddressService;
import com.example.explor_gastro.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 收货地址(Address)表控制层
 *
 * @author makejava
 * @since 2023-08-25 19:15:04
 */
@RestController
@RequestMapping("address")
public class AddressController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private AddressService addressService;

    @Resource
    private UserService userService;

    @GetMapping("all")
    public ResponseEntity<Response<?>> getAddressesByUserId(@RequestParam Integer userId) {
        // 查找用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        int userCount = userService.count(wrapper);

        // 如果用户不存在，返回404
        if (userCount == 0) {
            Response<String> response = new Response<>();
            response.setCode(200);
            response.setMsg("用户不存在");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // 获取地址列表
        QueryWrapper<Address> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("user_id", userId);
        List<Address> addressList = addressService.list(addressQueryWrapper);

        // 构建响应
        Response<List<Address>> response = new Response<>();
        response.setCode(200);
        response.setMsg("获取成功");
        response.setData(addressList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/add")
    public ResponseEntity<Response<String>> addAddress(
            @RequestParam Integer userId,
            @RequestParam String consignee,
            @RequestParam String area,
            @RequestParam String fullAddress,
            @RequestParam String phone) {

        // 去除前后空格
        consignee = consignee.trim();
        area = area.trim();
        fullAddress = fullAddress.trim();
        phone = phone.trim();

        // 查找用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        int user = userService.count(wrapper);

        // 查找用户所有收货地址
        QueryWrapper<Address> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("user_id", userId);
        int count = addressService.count(addressQueryWrapper);
        if (user == 0) {
            Response<String> response = new Response<>();
            response.setCode(404);
            response.setMsg("用户不存在");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (count == 20) {
            Response<String> response = new Response<>();
            response.setCode(400);
            response.setMsg("地址已满，请删除或修改不常用的地址");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Map<String, String> fieldDescriptions = new HashMap<>();
        fieldDescriptions.put("userId", "用户id");
        fieldDescriptions.put("consignee", "收货人");
        fieldDescriptions.put("area", "所在地区");
        fieldDescriptions.put("fullAddress", "详细地址");
        fieldDescriptions.put("phone", "手机号");

        Map<String, Object> fields = new HashMap<>();
        fields.put("userId", userId);
        fields.put("consignee", consignee);
        fields.put("area", area);
        fields.put("fullAddress", fullAddress);
        fields.put("phone", phone);

        List<String> errors = new ArrayList<>();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null || value.toString().trim().isEmpty()) {
                errors.add(fieldDescriptions.get(key));
            }
        }

        if (!errors.isEmpty()) {
            Response<String> response = new Response<>();
            response.setCode(400);
            response.setMsg("以下字段不能为空: " + String.join(", ", errors));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // 判断 area 和 fullAddress 是否为纯数字
        if (area.matches("\\d+") || fullAddress.matches("\\d+")) {
            Response<String> response = new Response<>();
            response.setCode(400);
            response.setMsg("所在地区和详细地址不能为纯数字");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // 判断 consignee 是否大于20字符
        if (consignee.length() > 20) {
            Response<String> response = new Response<>();
            response.setCode(400);
            response.setMsg(fieldDescriptions.get("consignee") + "不能超过20个字符");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // 验证手机号是否合法
        Pattern pattern = Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");
        if (!pattern.matcher(phone).matches()) {
            Response<String> response = new Response<>();
            response.setCode(400);
            response.setMsg("手机号不合法");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // 构造并保存 Address 对象
        Address address = new Address();
        address.setUserId(userId);
        address.setConsignee(consignee);
        address.setArea(area);
        address.setFullAddress(fullAddress);
        address.setPhone(phone);
        addressService.save(address);

        // 返回成功信息
        Response<String> response = new Response<>();
        response.setCode(200);
        response.setMsg("收货地址添加成功");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 修改数据
     */
    @PutMapping("update")
    public ResponseEntity<Response<?>> update(
            @RequestParam Integer id,
            @RequestParam String consignee,
            @RequestParam String area,
            @RequestParam String fullAddress,
            @RequestParam String phone) {
        Response<String> response = new Response<>();
        // 验证非空
        if (id == null || consignee == null || consignee.isEmpty() || area == null || area.isEmpty()
                || fullAddress == null || fullAddress.isEmpty() || phone == null || phone.isEmpty()) {
            response.setCode(400);
            response.setMsg("所有字段不能为空");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // 去除前后空格
        consignee = consignee.trim();
        area = area.trim();
        fullAddress = fullAddress.trim();
        phone = phone.trim();

        // 验证 area 和 fullAddress 不为纯数字
        if (area.matches("\\d+") || fullAddress.matches("\\d+")) {
            response.setCode(400);
            response.setMsg("区域和详细地址不能为纯数字");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // 验证手机号是否合法
        Pattern pattern = Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");
        if (!pattern.matcher(phone).matches()) {
            response.setCode(400);
            response.setMsg("手机号不合法");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // 查找地址
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        int address = addressService.count(wrapper);
        if (address == 0) {
            response.setCode(400);
            response.setMsg("地址不存在");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }else {
            // 执行更新操作
            try {
                Address address1 = new Address();
                address1.setId(id);
                address1.setConsignee(consignee);
                address1.setArea(area);
                address1.setFullAddress(fullAddress);
                address1.setPhone(phone);
                addressService.updateById(address1);
                response.setCode(200);
                response.setMsg("修改成功");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (Exception e) {
                response.setCode(400);
                response.setMsg("修改失败");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }

    /**
     * 删除数据
     */
    @DeleteMapping("delete")
    public ResponseEntity<Response<?>> delete(@RequestParam Integer id) {
        Response<String> response = new Response<>();
        Address byId = addressService.getById(id);
        if (byId == null) {
            response.setCode(400);
            response.setMsg("地址不存在");
        } else {
            boolean b = addressService.removeById(id);
            if (b) {
                response.setCode(200);
                response.setMsg("删除成功");
            } else {
                response.setCode(400);
                response.setMsg("删除失败");
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("updateDefault")
    public ResponseEntity<Response<?>> updateDefault(
            @RequestParam Integer id,
            @RequestParam Integer defaultOperate
    ){
        Response <String> response = new Response<>();
        Address byId = addressService.getById(id);
        System.out.println(byId);
        if (byId == null) {
            response.setCode(400);
            response.setMsg("地址不存在");
        }else {
            Address address = new Address();
            address.setId(id);
            if (defaultOperate==1){
                address.setDefaultOperate(1);
            }else {
                address.setDefaultOperate(0);
            }
            addressService.updateById(address);
            response.setCode(200);
            response.setMsg("修改成功");
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}

