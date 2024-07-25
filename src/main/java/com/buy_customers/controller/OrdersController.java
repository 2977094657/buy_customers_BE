package com.buy_customers.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.buy_customers.common.config.api.ResultData;
import com.buy_customers.common.utils.SnowflakeIdGenerator;
import com.buy_customers.entity.Orders;
import com.buy_customers.entity.Product;
import com.buy_customers.entity.User;
import com.buy_customers.service.OrdersService;
import com.buy_customers.service.ProductService;
import com.buy_customers.service.UserService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    @Resource
    private ProductService productService;

    @GetMapping("getOrder")
    public ResultData<Orders> getOrder(@RequestParam String orderNumber) throws JsonProcessingException {
        // 从Redis中查找订单
        String key = "order:" + orderNumber + ":*";
        RedisConnection connection = Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()).getConnection();
        ScanOptions options = ScanOptions.scanOptions().match(key).count(1).build();
        Cursor<byte[]> cursor = connection.scan(options);
        while (cursor.hasNext()) {
            byte[] keyByte = cursor.next();
            String orderString = stringRedisTemplate.opsForValue().get(new String(keyByte));
            if (orderString != null) {
                Orders order;
                order = new ObjectMapper().readValue(orderString, Orders.class);
                return ResultData.success(order);
            }
        }

        // 如果Redis中找不到订单，则从数据库中查找
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_long", orderNumber);
        Orders order = ordersService.getOne(queryWrapper);
        if (order != null) {
            return ResultData.success(order);
        } else {
            return ResultData.fail(404,"订单不存在");
        }
    }


    @GetMapping("all")
    public ResultData<IPage<Orders>> getAllOrders(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "state", required = false) String state) {

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
            return ResultData.success(ordersList);
        } else {
            return ResultData.fail(404,"没有找到订单");
        }
    }


    /**
     * 通过主键查询单条数据
     */
    @GetMapping("getUnpaidOrder")
    public ResultData<List<Orders>> getUnpaidOrders(@RequestParam String userId) throws JsonProcessingException {

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
            return ResultData.success(ordersList);
        }

        return ResultData.fail(404,"没有找到未付款订单");
    }

    /**
     * 校验数据
     *
     * @param vendorNames    商家数组
     * @param productIds     商品id数组
     * @param productNumbers 商品数量数组
     */
    private ResultData<String> validateInputs(String vendorNames, String productIds, String productNumbers) {
        if (vendorNames != null && productIds != null) {
            String[] splitVendorNames = vendorNames.split(",");
            String[] splitProductIds = productIds.split(",");
            if (splitVendorNames.length != splitProductIds.length) {
                return ResultData.fail(400,"商家数量必须等于产品数量");
            }
        }

        if (productIds != null && productNumbers != null) {
            String[] splitProductIds = productIds.split(",");
            String[] splitProductNumbers = productNumbers.split(",");
            if (splitProductIds.length != splitProductNumbers.length) {
                return ResultData.fail(400,"商品ID数量必须等于商品数量");
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
    public ResultData<?> createOrder(@RequestBody Orders request) throws JsonProcessingException {
        // 查找用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", request.getUserId());
        int userCount = userService.count(wrapper);

        // 如果用户不存在，返回404
        if (userCount == 0) {
            return ResultData.fail(404,"用户不存在");
        }

        // 验证手机号是否合法
        Pattern pattern = Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");
        if (!pattern.matcher(request.getPhone()).matches()) {
            return ResultData.fail(400,"手机号不合法");
        }

        // 创建新订单
        Orders orders = new Orders();
        orders.setOrderLong(SnowflakeIdGenerator()); // 使用雪花算法生成订单号

        // 验证输入
        ResultData<String> validateResult = validateInputs(request.getVendorName(), request.getProductId(), request.getProductNumber());
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
        return ResultData.success(orders);
    }


    /**
     * 删除数据
     */
    @DeleteMapping("deleteUnpaidOrder")
    public ResultData<String> deleteUnpaidOrder(@RequestParam String orderLong, @RequestParam String userId) {

        // 构造键
        String key = "order:" + orderLong + ":user:" + userId;

        // 检查订单是否存在
        Boolean exist = stringRedisTemplate.hasKey(key);
        if (exist == null || !exist) {
            return ResultData.fail(404,"订单不存在");
        }

        // 删除订单
        stringRedisTemplate.delete(key);

        return ResultData.success("订单删除成功");
    }

    @DeleteMapping("deleteOrder")
    public ResultData<String> deleteOrder(@RequestParam Integer id) {

        Orders byId = ordersService.getById(id);

        if(byId == null) {
            return ResultData.fail(404,"订单不存在");
        }

        ordersService.removeById(id);
        return ResultData.success("订单删除成功");
    }


    @PostMapping("confirmOrder")
    public ResultData<Orders> confirmOrder(@RequestBody Map<String, String> params) throws JsonProcessingException {
        String orderLong = params.get("orderLong");
        String userId = params.get("userId");

        // 构造键
        String key = "order:" + orderLong + ":user:" + userId;

        // 从Redis中获取订单数据
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            return ResultData.fail(404,"订单不存在");
        }

        // 将JSON字符串转换为Orders对象
        ObjectMapper objectMapper = new ObjectMapper();
        Orders orders = objectMapper.readValue(value, Orders.class);
        Product product = productService.getById(orders.getProductId());
        Integer buys = product.getBuys();
        product.setBuys(++buys);
        orders.setState("待发货");

        // 将订单数据保存到MySQL数据库
        ordersService.save(orders);
        productService.updateById(product);
        // 从Redis中删除订单数据
        stringRedisTemplate.delete(key);

        // 构建响应
        return ResultData.success(orders);
    }

    @PostMapping("getOrdersByUserId")
    public ResultData<List<Orders>> getOrdersByUserId(@RequestBody Map<String, Integer> body) throws JsonProcessingException {
        Integer userId = body.get("userId");

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        List<Orders> ordersList = ordersService.list(queryWrapper);

        // 获取未付款订单的返回数据
        ResultData<List<Orders>> unpaidOrdersResponse = getUnpaidOrders(userId.toString());
        // 如果未付款订单的返回数据不为空，则将其添加到 ordersList 中
        if (unpaidOrdersResponse != null && unpaidOrdersResponse.getData() != null) {
            ordersList.addAll(unpaidOrdersResponse.getData());
        }
        if (!ordersList.isEmpty()) {
            return ResultData.success(ordersList);
        }

        return ResultData.fail(404,"没有找到订单");
    }

    @PostMapping("shipOrder")
    public ResultData<String> shipOrder(@RequestBody Map<String, Integer> body) {
        Integer orderId = body.get("orderId");

        Orders order = ordersService.getById(orderId);
        if (order != null) {
            order.setState("待收货");
            order.setSendDate(new Date());

            if (ordersService.updateById(order)) {
                return ResultData.success("发货成功");
            } else {
                return ResultData.fail(500,"更新订单失败");
            }
        } else {
            return ResultData.fail(404,"没有找到订单");
        }
    }


    @PostMapping("getOrdersByUserIdAndState")
    public ResultData<List<Orders>> getOrdersByUserIdAndState(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        String state = (String) body.get("state");

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId).eq(Orders::getState, state);
        List<Orders> ordersList = ordersService.list(queryWrapper);

        if (!ordersList.isEmpty()) {
            return ResultData.success(ordersList);
        }

        return ResultData.fail(404,"没有找到订单");
    }

    @PostMapping("receiveOrder")
    public ResultData<String> receiveOrder(@RequestBody Map<String, Integer> body) {
        Integer orderId = body.get("orderId");

        Orders order = ordersService.getById(orderId);
        if (order != null) {
            order.setState("待评价");
            order.setReceiveDate(new Date());

            if (ordersService.updateById(order)) {
                return ResultData.success("收货成功");
            } else {
                return ResultData.fail(500,"更新订单失败");
            }
        } else {
            return ResultData.fail(404,"没有找到订单");
        }
    }

    @PutMapping("updateOrderStatus")
    public ResultData<Orders> updateOrderStatus(@RequestBody Orders order) {
        // 从数据库中获取订单
        Orders existingOrder = ordersService.getById(order.getOrderId());
        if (existingOrder != null) {
            // 修改订单状态
            existingOrder.setState(order.getState());
            // 保存更新后的订单
            ordersService.updateById(existingOrder);
            return ResultData.success(existingOrder);
        } else {
            return ResultData.fail(404,"订单不存在");
        }
    }
}

