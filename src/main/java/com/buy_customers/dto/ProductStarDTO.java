package com.buy_customers.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 如果查处为null的字段则不显示，可以单独给某字段加
public class ProductStarDTO {
    private String productName; // 商品名
    private String name; // 商家名
    private String img; // 商品图片
}
