package com.pinyougou.cart.service;

import com.pinyougou.model.Cart;

import java.util.List;

/**
 *  @author: guanx
 *  @Date: 2020/3/3 13:40
 *  @Description: 购物车接口
 */
public interface CartService {

    /**
    * @Description 加入购物车
    * @Author  guanx
    * @Date   2020/3/3 13:40
    * @Param
    * @Return
    * @Exception
    *
    */
    List<Cart> add(List<Cart> cartList,Long itemId, Integer num);

    /**
     * @Description 加入购物车 到Redis
     * @Author  guanx
     * @Date   2020/3/3 13:40
     * @Param
     * @Return
     * @Exception
     *
     */
    void addGoodsToRedis(String userName,List<Cart> cartList);
    
    /**
    * @Description 查询购物车 从Redis
    * @Author  guanx
    * @Date   2020/3/3 20:09
    * @Param  
    * @Return      
    * @Exception   
    * 
    */
    List<Cart> findCartListFromRedis(String userName);

    /**
     * @Description 合并cookie信息和redis信息
     * @Author  guanx
     * @Date   2020/3/3 20:57
     * @Param
     * @Return
     * @Exception
     *
     */
    List<Cart> mergeList(List<Cart> redisCartList,List<Cart> cookieCartList);
}
