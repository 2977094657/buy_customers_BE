package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.buy_customers.common.config.api.ResultData;
import com.buy_customers.entity.Address;
import com.buy_customers.entity.User;
import com.buy_customers.service.AddressService;
import com.buy_customers.service.UserService;
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
    public ResultData<?> getAddressesByUserId(@RequestParam Integer userId) {
        // 查找用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        int userCount = userService.count(wrapper);

        // 如果用户不存在，返回404
        if (userCount == 0) {
            return ResultData.fail(404, "用户不存在");
        }

        // 获取地址列表
        QueryWrapper<Address> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("user_id", userId);
        List<Address> addressList = addressService.list(addressQueryWrapper);

        return ResultData.success(addressList);
    }


    @PostMapping("/add")
    public ResultData<String> addAddress(@RequestBody Address request) {
        // 去除前后空格
        String consignee = request.getConsignee().trim();
        String area = request.getArea().trim();
        String fullAddress = request.getFullAddress().trim();
        String phone = request.getPhone().trim();

        // 查找用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", request.getUserId());
        int user = userService.count(wrapper);

        // 查找用户所有收货地址
        QueryWrapper<Address> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("user_id", request.getUserId());
        int count = addressService.count(addressQueryWrapper);
        if (user == 0) {
            return ResultData.fail(404,"用户不存在");
        }
        if (count == 20) {
            return ResultData.fail(400,"地址已满，请删除或修改不常用的地址");
        }

        Map<String, String> fieldDescriptions = new HashMap<>();
        fieldDescriptions.put("userId", "用户id");
        fieldDescriptions.put("consignee", "收货人");
        fieldDescriptions.put("area", "所在地区");
        fieldDescriptions.put("fullAddress", "详细地址");
        fieldDescriptions.put("phone", "手机号");

        Map<String, Object> fields = new HashMap<>();
        fields.put("userId", request.getUserId());
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
            return ResultData.fail(400,"以下字段不能为空: " + String.join(", ", errors));
        }

        // 判断 area 和 fullAddress 是否为纯数字
        if (area.matches("\\d+") || fullAddress.matches("\\d+")) {
            return ResultData.fail(400,"所在地区和详细地址不能为纯数字");
        }

        // 判断 consignee 是否大于20字符
        if (consignee.length() > 20) {
            return ResultData.fail(400,fieldDescriptions.get("consignee") + "不能超过20个字符");
        }

        // 验证手机号是否合法
        Pattern pattern = Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");
        if (!pattern.matcher(phone).matches()) {
            return ResultData.fail(400,"手机号不合法");
        }

        // 构造并保存 Address 对象
        Address address = new Address();
        address.setUserId(request.getUserId());
        address.setConsignee(consignee);
        address.setArea(area);
        address.setFullAddress(fullAddress);
        address.setPhone(phone);
        addressService.save(address);

        // 返回成功信息
        return ResultData.success("收货地址添加成功");
    }

    /**
     * 修改数据
     */
    @PutMapping("update")
    public ResultData<String> update(@RequestBody Address request) {
        Integer id = request.getId();
        String consignee = request.getConsignee();
        String area = request.getArea();
        String fullAddress = request.getFullAddress();
        String phone = request.getPhone();
        // 验证非空
        if (id == null || consignee == null || consignee.isEmpty() || area == null || area.isEmpty()
                || fullAddress == null || fullAddress.isEmpty() || phone == null || phone.isEmpty()) {
            return ResultData.fail(400,"所有字段不能为空");
        }

        // 去除前后空格
        consignee = consignee.trim();
        area = area.trim();
        fullAddress = fullAddress.trim();
        phone = phone.trim();

        // 验证 area 和 fullAddress 不为纯数字
        if (area.matches("\\d+") || fullAddress.matches("\\d+")) {
            return ResultData.fail(400,"区域和详细地址不能为纯数字");
        }

        // 验证手机号是否合法
        Pattern pattern = Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");
        if (!pattern.matcher(phone).matches()) {
            return ResultData.fail(400,"手机号不合法");
        }

        // 查找地址
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        int address = addressService.count(wrapper);
        if (address == 0) {
            return ResultData.fail(404,"地址不存在");
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
                return ResultData.success("修改成功");
            } catch (Exception e) {
                return ResultData.fail(400,"修改失败");
            }
        }
    }

    /**
     * 删除数据
     */
    @DeleteMapping("delete")
    public ResultData<String> delete(@RequestParam Integer id) {
        Address byId = addressService.getById(id);
        if (byId == null) {
            return ResultData.fail(404, "地址不存在");
        } else {
            boolean b = addressService.removeById(id);
            if (b) {
                return ResultData.success("删除成功");
            } else {
                return ResultData.fail(400, "删除失败");
            }
        }
    }

    @PutMapping("updateDefault")
    public ResultData<String> updateDefault(
            @RequestParam Integer id,
            @RequestParam Integer defaultOperate
    ){
        Address byId = addressService.getById(id);
        if (byId == null) {
            return ResultData.fail(404,"地址不存在");
        }else {
            Address address = new Address();
            address.setId(id);
            if (defaultOperate==1){
                address.setDefaultOperate(1);
            }else {
                address.setDefaultOperate(0);
            }
            addressService.updateById(address);
            return ResultData.success("修改成功");
        }
    }
}

