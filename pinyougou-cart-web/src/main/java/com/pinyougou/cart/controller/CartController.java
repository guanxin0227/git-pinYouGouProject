package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.http.Result;
import com.pinyougou.model.Cart;
import com.pinyougou.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author: guanx
 *  @Date: 2020/3/3 14:58
 *  @Description: 购物车
 */
@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse respone;

    /**
    * @Description 从cookie中获取购物车
    * @Author  guanx
    * @Date   2020/3/3 15:04
    * @Param
    * @Return
    * @Exception
    *
    */
    @RequestMapping(value = "/cartList")
    public List<Cart> cartList(){

        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //用户未登录，数据存在cookie中
        String json = CookieUtil.getCookieValue(request, "CookieName", "UTF-8");

        //将购物车数据转成List
        List<Cart> cookieCartList = JSON.parseArray(json, Cart.class);

        //判断用户是否是匿名（未登录）
        if(username.equals("anonymousUser")){


            return cookieCartList;
        }else{

            //非匿名，（已登录）
            List<Cart> redisCartList = cartService.findCartListFromRedis(username);

            //将cookie和redis合并
            if(null != cookieCartList && cookieCartList.size()>0){

                //合并
                redisCartList = cartService.mergeList(redisCartList, cookieCartList);

                //将集合加入redis覆盖原来的集合
                cartService.addGoodsToRedis(username,redisCartList);

                //清除cookie
                CookieUtil.deleteCookie(request,respone,"CookieName");

            }

            return redisCartList;
        }

    }

    /**
    * @Description 添加购物车
    * @Author  guanx
    * @Date   2020/3/3 15:00
    * @Param
    * @Return
    * @Exception
    *
    */
    @RequestMapping(value = "/add")
    public Result add(Long itemId,Integer num){

        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //调用方法，获取购物车集合数据
        List<Cart> cartList = cartList();

        //调用加入购物车方法
        cartList = cartService.add(cartList, itemId, num);

        //判断用户是否是匿名（未登录）
        if(username.equals("anonymousUser")){

            //List-->JSON
            String json = JSON.toJSONString(cartList);

            //将json数据存入到cookie中
            CookieUtil.setCookie(request,respone,"CookieName",json,2600*24*30,"UTF-8");
        }else{

            //非匿名，（已登录）
            cartService.addGoodsToRedis(username,cartList);

        }



        return new Result(true,"加入购物车成功");

    }
}
