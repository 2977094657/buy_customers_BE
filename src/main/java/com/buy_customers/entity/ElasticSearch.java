// package com.buy_customers.entity;
//
// import lombok.Data;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.elasticsearch.annotations.Document;
// import org.springframework.data.elasticsearch.annotations.Field;
// import org.springframework.data.elasticsearch.annotations.FieldType;
//
// //@Document 文档对象 （索引信息、文档类型 ）
// @Document(indexName="blog3")
// @Data
// public class ElasticSearch {
//
//     //@Id 文档主键 唯一标识
//     @Id
//     //@Field 每个文档的字段配置（类型、是否分词、是否存储、分词器 ）
//     @Field(store=true, index = false,type = FieldType.Integer)
//     private Integer id;
//
//     @Field(index=true,analyzer="ik_smart",store=true,searchAnalyzer="ik_smart",type = FieldType.Text)
//     private String title;
//
//     @Field(index=true,analyzer="ik_smart",store=true,searchAnalyzer="ik_smart",type = FieldType.Text)
//     private String content;
//
//     @Field(index=true,store=true,type = FieldType.Double)
//     private Double price;
// }
//
