package com.pinyougou.cart.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author: guanx
 *  @Date: 2020/3/3 17:51
 *  @Description: 认证授权
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
    * @Description 登录认证：cas
     *              SpringSecurity 授权
    * @Author  guanx
    * @Date   2020/3/3 17:55
    * @Param
    * @Return
    * @Exception
    *
    */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(userName,"",grantedAuthorities);
    }
}
