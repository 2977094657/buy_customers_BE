package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.example.explor_gastro.common.utils.Response;
import com.example.explor_gastro.common.utils.SnowflakeIdGenerator;
import com.example.explor_gastro.entity.Orders;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.OrdersService;
import com.example.explor_gastro.service.UserService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 订单表(Orders)表控制层
 *
 * @author makejava
 * @since 2023-10-29 15:31:07
 */
@RestController
@RequestMapping("order")
@Tag(name = "订单功能")
public class OrdersController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private OrdersService ordersService;
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 通过主键查询单条数据
     */
    @GetMapping("getUnpaidOrder")
    @Operation(summary = "根据用户ID查询未付款订单")
    public ResponseEntity<Response<List<Orders>>> getUnpaidOrders(@RequestParam String userId) throws JsonProcessingException {
        Response<List<Orders>> response = new Response<>();

        // 从Redis中获取指定用户的所有订单键
        Set<String> keys = stringRedisTemplate.keys("order:*:user:" + userId);
        List<Orders> ordersList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                String value = stringRedisTemplate.opsForValue().get(key);
                if (value != null) {
                    Orders orders = objectMapper.readValue(value, Orders.class);
                    ordersList.add(orders);
                }
            }
        }

        if (!ordersList.isEmpty()) {
            response.setCode(200);
            response.setMsg("查询成功");
            response.setData(ordersList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setCode(404);
        response.setMsg("没有找到未付款订单");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 校验数据
     *
     * @param vendorNames    商家数组
     * @param productIds     商品id数组
     * @param productNumbers 商品数量数组
     */
    private ResponseEntity<Response<?>> validateInputs(String vendorNames, String productIds, String productNumbers) {
        if (vendorNames != null && productIds != null) {
            String[] splitVendorNames = vendorNames.split(",");
            String[] splitProductIds = productIds.split(",");
            if (splitVendorNames.length != splitProductIds.length) {
                Response<String> response = new Response<>();
                response.setCode(400);
                response.setMsg("商家数量必须等于产品数量");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        if (productIds != null && productNumbers != null) {
            String[] splitProductIds = productIds.split(",");
            String[] splitProductNumbers = productNumbers.split(",");
            if (splitProductIds.length != splitProductNumbers.length) {
                Response<String> response = new Response<>();
                response.setCode(400);
                response.setMsg("商品ID数量必须等于商品数量");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
        return null;
    }

    /**
     * 通过雪花算法生产订单号
     */
    public long SnowflakeIdGenerator() {
        // 创建SnowflakeIdGenerator对象,设置工作机器ID为1,数据中心ID为1
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        return generator.nextId();
    }

    /**
     * 新增数据
     */
    @PostMapping("add")
    @Operation(summary = "创建订单")
    public ResponseEntity<Response<?>> createOrder(@RequestBody Orders request) throws JsonProcessingException {
        // 查找用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", request.getUserId());
        int userCount = userService.count(wrapper);

        // 如果用户不存在，返回404
        if (userCount == 0) {
            Response<String> response = new Response<>();
            response.setCode(404);
            response.setMsg("用户不存在");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // 创建新订单
        Orders orders = new Orders();
        orders.setOrderLong(SnowflakeIdGenerator()); // 使用雪花算法生成订单号

        // 验证输入
        ResponseEntity<Response<?>> validateResult = validateInputs(request.getVendorName(), request.getProductId(), request.getProductNumber());
        if (validateResult != null) {
            return validateResult;
        }

        orders.setVendorName(request.getVendorName());
        orders.setUserId(request.getUserId());
        orders.setAddress(request.getAddress());
        orders.setProductId(request.getProductId()); // 添加订单中的所有商品
        orders.setProductNumber(request.getProductNumber()); // 添加订单中的所有商品
        orders.setNotes(request.getNotes());
        orders.setPrice(request.getPrice());
        orders.setCreateDate(new Date()); // 设置创建时间为当前时间

        // 保存订单到Redis
        String key = "order:" + orders.getOrderLong() + ":user:" + orders.getUserId();
        // 配置 ObjectMapper 来忽略所有为 null 的字段
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String value = objectMapper.writeValueAsString(orders); // 将orders对象转换为JSON字符串
        stringRedisTemplate.opsForValue().set(key, value, 60 * 24, TimeUnit.MINUTES); // 设置过期时间为1天

        // 构建响应
        Response<Orders> response = new Response<>();
        response.setCode(200);
        response.setMsg("订单创建成功");
        response.setData(orders);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * 修改数据
     */
    @DeleteMapping("deleteUnpaidOrder")
    @Operation(summary = "根据订单编号删除未付款订单")
    public ResponseEntity<Response<String>> deleteUnpaidOrder(@RequestParam String orderLong, @RequestParam String userId) {
        Response<String> response = new Response<>();

        // 构造键
        String key = "order:" + orderLong + ":user:" + userId;

        // 检查订单是否存在
        Boolean exist = stringRedisTemplate.hasKey(key);
        if (exist == null || !exist) {
            response.setCode(404);
            response.setMsg("订单不存在");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // 删除订单
        stringRedisTemplate.delete(key);

        response.setCode(200);
        response.setMsg("订单删除成功");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("updateOrder")
    @Operation(summary = "修改未付款的订单")
    public ResponseEntity<Response<?>> updateOrder(@RequestBody Orders request) throws JsonProcessingException {
        // 获取订单
        String key = "order:" + request.getOrderLong() + ":user:" + request.getUserId();
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            Response<String> response = new Response<>();
            response.setCode(404);
            response.setMsg("订单不存在");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // 解析订单
        ObjectMapper objectMapper = new ObjectMapper();
        Orders orders = objectMapper.readValue(value, Orders.class);

        // 验证输入
        ResponseEntity<Response<?>> validateResult = validateInputs(request.getVendorName(), request.getProductId(), request.getProductNumber());
        if (validateResult != null) {
            return validateResult;
        }

        // 更新商品对象
        orders.setVendorName(orders.getVendorName());
        orders.setAddress(request.getAddress());
        orders.setProductId(request.getProductId());
        orders.setProductNumber(request.getProductNumber());
        orders.setPrice(request.getPrice());
        orders.setNotes(request.getNotes());

        // 更新订单到Redis
        value = objectMapper.writeValueAsString(orders);
        stringRedisTemplate.opsForValue().set(key, value, 60 * 24, TimeUnit.MINUTES);

        // 构建响应
        Response<Orders> response = new Response<>();
        response.setCode(200);
        response.setMsg("订单更新成功");
        response.setData(orders);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}

