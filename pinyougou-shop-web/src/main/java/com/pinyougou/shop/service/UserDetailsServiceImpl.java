package com.pinyougou.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.ShopStatus;
import com.pinyougou.model.Seller;
import com.pinyougou.sellergoods.service.SellerService;
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
 *  @Date: 2020/2/7 19:57
 *  @Description: 商家系统登录与安全控制
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    private SellerService sellerService;
    /**
    * @Description  自定义认证类
    * @Author  guanx
    * @Date   2020/2/7 19:58
    * @Param  name
    * @Return
    * @Exception
    *
    */
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        //授权信息  构建一个角色集合
        List<GrantedAuthority> granteds = new ArrayList<GrantedAuthority>();
        granteds.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //在数据库中查询数据
        Seller seller = sellerService.getOneById(name);
        if(null == seller){
            return null;
        }
        if(!seller.getStatus().equals(ShopStatus.YES_EXAMINE)){
            return null;
        }
        return new User(name,seller.getPassword(),granteds);
    }
}
