package com.example.explor_gastro.utils;

import com.example.explor_gastro.dao.UserDao;
import com.example.explor_gastro.entity.User;
import com.example.explor_gastro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;
    @Autowired
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
                user.getName(),
                user.getPwd(),
                new ArrayList<>()
        );
    }

    public UserDetails loadUserByUsername(Object credentials) throws UsernameNotFoundException {
        if (credentials instanceof String[]) {
            String[] userCredentials = (String[]) credentials;
            // 使用电话号码和密码来获取用户的详细信息
            User user = userService.LoginIn(userCredentials[0], userCredentials[1]);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with phone: " + userCredentials[0]);
            }
            // 创建一个 UserDetails 对象，包含用户的认证信息
            return new org.springframework.security.core.userdetails.User(
                    user.getPhone(),
                    user.getPwd(),
                    new ArrayList<>()
            );
        } else if (credentials instanceof List) {
            List<String> userCredentials = (List<String>) credentials;
            // 使用电话号码和密码来获取用户的详细信息
            User user = userService.LoginIn(userCredentials.get(0), userCredentials.get(1));
            if (user == null) {
                throw new UsernameNotFoundException("User not found with phone: " + userCredentials.get(0));
            }
            // 创建一个 UserDetails 对象，包含用户的认证信息
            return new org.springframework.security.core.userdetails.User(
                    user.getPhone(),
                    user.getPwd(),
                    new ArrayList<>()
            );
        } else {
            throw new IllegalArgumentException("Invalid credentials type");
        }
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MessageDigestPasswordEncoder("MD5");
    }

}