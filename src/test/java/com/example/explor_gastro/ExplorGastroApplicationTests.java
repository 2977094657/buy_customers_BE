package com.example.explor_gastro;

import com.example.explor_gastro.controller.UserController;
import com.example.explor_gastro.dao.UserDao;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ExplorGastroApplicationTests {

    @Autowired
    UserDao userDao;
    @Autowired
    UserService userService;


    @Test
    void contextLoads() {


    }

    @Test
    public void testSelectUserById() throws Exception {
//        userDao.selectList(null);
        User expected = new User();
        expected.setUserId(41);
        expected.setName("John");
//        expected.setDescription("A regular user");
//        expected.setAddress("123 Main St");
//        expected.setPhone("1234567890");
//        int i = userDao.updateById(expected);
        userService.updateById(expected);
        System.out.println("修改后的值："+userDao.selectUserById(41));
//        assertEquals(expected, i);
    }


}
