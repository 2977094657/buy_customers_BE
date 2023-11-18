package com.example.explor_gastro.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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


    @GetMapping("getOrder")
    @Operation(summary = "通过订单号查询订单")
    public ResponseEntity<Response<?>> getOrder(@RequestParam String orderNumber) {
        // 从Redis中查找订单
        String key = "order:" + orderNumber + ":*";
        RedisConnection connection = Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()).getConnection();
        ScanOptions options = ScanOptions.scanOptions().match(key).count(1).build();
        Cursor<byte[]> cursor = connection.scan(options);
        Response<Orders> response = new Response<>();
        while (cursor.hasNext()) {
            byte[] keyByte = cursor.next();
            String orderString = stringRedisTemplate.opsForValue().get(new String(keyByte));
            if (orderString != null) {
                Orders order;
                try {
                    order = new ObjectMapper().readValue(orderString, Orders.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                response.setCode(200);
                response.setMsg("查询成功");
                response.setData(order);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        response.setCode(404);
        response.setMsg("订单不存在");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("all")
    @Operation(summary = "查询所有订单,管理员使用")
    public ResponseEntity<Response<IPage<Orders>>> getAllOrders(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "state", required = false) String state) {

        Response<IPage<Orders>> response = new Response<>();

        // 创建分页对象
        Page<Orders> page = new Page<>(pageNo, pageSize);

        // 创建查询条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        if (state != null) {
            // 如果用户提供了状态参数，添加到查询条件中
            queryWrapper.eq(Orders::getState, state);
        }

        // 执行分页查询
        IPage<Orders> ordersList = ordersService.page(page, queryWrapper);

        if (ordersList != null && !ordersList.getRecords().isEmpty()) {
            response.setCode(200);
            response.setMsg("查询成功");
            response.setData(ordersList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setCode(404);
            response.setMsg("没有找到订单");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @NotNull
    private ResponseEntity<Response<List<Orders>>> getResponseResponseEntity(Response<List<Orders>> response, List<Orders> ordersList) {
        if (!ordersList.isEmpty()) {
            response.setCode(200);
            response.setMsg("查询成功");
            response.setData(ordersList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setCode(404);
        response.setMsg("没有找到订单");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


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
    public String SnowflakeIdGenerator() {
        // 创建SnowflakeIdGenerator对象,设置工作机器ID为1,数据中心ID为1
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        return String.valueOf(generator.nextId());
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

        // 验证手机号是否合法
        Pattern pattern = Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");
        if (!pattern.matcher(request.getPhone()).matches()) {
            Response<String> response = new Response<>();
            response.setCode(400);
            response.setMsg("手机号不合法");
            return new ResponseEntity<>(response, HttpStatus.OK);
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
        orders.setConsignee(request.getConsignee());
        orders.setPhone(request.getPhone());
        orders.setPayMethod(request.getPayMethod());
        orders.setState("待付款");
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
     * 删除数据
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

    @DeleteMapping("deleteOrder")
    @Operation(summary = "根据订单ID删除订单")
    public ResponseEntity<Response<String>> deleteOrder(@RequestParam Integer id) {
        Response<String> response = new Response<>();

        Orders byId = ordersService.getById(id);

        if(byId == null) {
            response.setCode(404);
            response.setMsg("订单不存在");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        ordersService.removeById(id);
        response.setCode(200);
        response.setMsg("订单删除成功");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("confirmOrder")
    @Operation(summary = "确认订单并存储到数据库")
    public ResponseEntity<Response<?>> confirmOrder(@RequestBody Map<String, String> params) throws JsonProcessingException {
        String orderLong = params.get("orderLong");
        String userId = params.get("userId");

        // 构造键
        String key = "order:" + orderLong + ":user:" + userId;

        // 从Redis中获取订单数据
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            Response<String> response = new Response<>();
            response.setCode(404);
            response.setMsg("订单不存在");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // 将JSON字符串转换为Orders对象
        ObjectMapper objectMapper = new ObjectMapper();
        Orders orders = objectMapper.readValue(value, Orders.class);
        orders.setState("待发货");

        // 将订单数据保存到MySQL数据库
        ordersService.save(orders);

        // 从Redis中删除订单数据
        stringRedisTemplate.delete(key);

        // 构建响应
        Response<Orders> response = new Response<>();
        response.setCode(200);
        response.setMsg("订单已确认并保存到数据库");
        response.setData(orders);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("getOrdersByUserId")
    @Operation(summary = "根据用户ID查询所有订单，包括未付款的订单")
    public ResponseEntity<Response<List<Orders>>> getOrdersByUserId(@RequestBody Map<String, Integer> body) throws JsonProcessingException {
        Response<List<Orders>> response = new Response<>();
        Integer userId = body.get("userId");

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        List<Orders> ordersList = ordersService.list(queryWrapper);

        // 获取未付款订单的返回数据
        ResponseEntity<Response<List<Orders>>> unpaidOrdersResponse = getUnpaidOrders(userId.toString());
        // 如果未付款订单的返回数据不为空，则将其添加到 ordersList 中
        if (unpaidOrdersResponse.getBody() != null && unpaidOrdersResponse.getBody().getData() != null) {
            ordersList.addAll(unpaidOrdersResponse.getBody().getData());
        }

        return getResponseResponseEntity(response, ordersList);
    }

    @PostMapping("shipOrder")
    @Operation(summary = "发货订单")
    public ResponseEntity<Response<String>> shipOrder(@RequestBody Map<String, Integer> body) {
        Response<String> response = new Response<>();
        Integer orderId = body.get("orderId");

        Orders order = ordersService.getById(orderId);
        if (order != null) {
            order.setState("已发货");
            order.setSendDate(new Date());

            if (ordersService.updateById(order)) {
                response.setCode(200);
                response.setMsg("发货成功");
            } else {
                response.setCode(500);
                response.setMsg("更新订单失败");
            }
        } else {
            response.setCode(404);
            response.setMsg("没有找到订单");
        }

        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }


    @PostMapping("getOrdersByUserIdAndState")
    @Operation(summary = "根据用户ID和订单状态查询订单")
    public ResponseEntity<Response<List<Orders>>> getOrdersByUserIdAndState(@RequestBody Map<String, Object> body) {
        Response<List<Orders>> response = new Response<>();
        Integer userId = (Integer) body.get("userId");
        String state = (String) body.get("state");

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId).eq(Orders::getState, state);
        List<Orders> ordersList = ordersService.list(queryWrapper);

        return getResponseResponseEntity(response, ordersList);
    }


}

