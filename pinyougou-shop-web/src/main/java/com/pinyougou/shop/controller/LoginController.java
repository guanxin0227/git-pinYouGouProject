package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *  @author: guanx
 *  @Date: 2020/2/7 10:48
 *  @Description: 主界面显示登陆人
 */

@RequestMapping(value = "/login")
@RestController
public class LoginController {

    /**
    * @Description 获取用户登录名字
    * @Author  guanx
    * @Date   2020/2/7 10:54
    * @Param  
    * @Return      
    * @Exception   
    * 
    */
    @RequestMapping(value = "/getUserName")
    public Map<String,Object> getUserName(){
        //获取当前登录用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String,Object> map = new HashMap<>();
        map.put("loginUserName",name);
        return map;
    }
}
