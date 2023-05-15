package com.example.explor_gastro.utils;

import com.example.explor_gastro.dao.UserDao;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private UserService userService;
    @Resource
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        // 使用用户名来获取用户的详细信息
        User user = userDao.selectUserByPhone(phone);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + phone);
        }
        // 创建一个 UserDetails 对象，包含用户的认证信息
        return new org.springframework.security.core.userdetails.User(
                user.getPhone(),
                user.getPwd(),
                new ArrayList<>()
        );
    }
}