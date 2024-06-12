// package com.buy_customers;
//
// import com.buy_customers.entity.ElasticSearch;
// import com.buy_customers.service.ElasticSearchService;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.elasticsearch.core.SearchHit;
// import org.springframework.test.context.junit4.SpringRunner;
//
// import javax.annotation.Resource;
// import java.util.List;
//
// @RunWith(SpringRunner.class)
// @SpringBootTest(classes = BuyCustomersApplication.class)
// public class EsTest {
//
//     @Resource
//     private ElasticSearchService elasticSearchService;
//
//     /**添加文档或者修改文档(以id为准)*/
//     @Test
//     public void saveElasticSearch(){
//         ElasticSearch elasticSearch = new ElasticSearch();
//         elasticSearch.setId(1);
//         elasticSearch.setTitle("SpringData ElasticSearch");
//         elasticSearch.setContent("Spring Data ElasticSearch 基于 spring data API 简化 elasticSearch操作，将原始操作elasticSearch的客户端API 进行封装 \n" +
//                 "    Spring Data为Elasticsearch Elasticsearch项目提供集成搜索引擎");
//         elasticSearchService.save(elasticSearch);
//     }
//     @Test
//     public void findById(){
//         ElasticSearch byId = elasticSearchService.findById(1);
//         System.out.println(byId);
//     }
//     @Test
//     public void deleteById(){
//         elasticSearchService.deleteById(1);
//
//     }
//     @Test
//     public void count(){
//         long count = elasticSearchService.count();
//         System.out.println(count);
//     }
//     @Test
//     public void existsById(){
//         boolean b = elasticSearchService.existsById(102);
//
//         System.out.println(b);
//     }
//     @Test
//     public void findByTitleOrContent(){
//         List<SearchHit<ElasticSearch>> byTitleOrContent = elasticSearchService.findByTitleOrContent("xxxxxxSpringData","elasticSearch");
//         for (SearchHit<ElasticSearch> elasticSearchService : byTitleOrContent) {
//             List<String> title = elasticSearchService.getHighlightField("title");
//             System.out.println(title);
//             List<String> content = elasticSearchService.getHighlightField("content");
//             System.out.println(content);
//
//         }
//     }
// }
//
