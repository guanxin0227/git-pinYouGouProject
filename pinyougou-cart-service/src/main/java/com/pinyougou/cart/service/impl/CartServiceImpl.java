package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Cart;
import com.pinyougou.model.Item;
import com.pinyougou.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author: guanx
 *  @Date: 2020/3/3 13:40
 *  @Description: 购物车接口
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @Description 加入购物车
     * @Author  guanx
     * @Date   2020/3/3 13:40
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public List<Cart> add(List<Cart> cartList,Long itemId, Integer num) {

        //防止空指针
        cartList=(cartList==null?  new ArrayList<Cart>():cartList);

        //查询商品信息
        Item item = itemMapper.selectByPrimaryKey(itemId);

        //判断该商品的购物车是否存在
        Cart cart = searchCart(cartList, item.getSellerId());

        //如果存在，获取该商家购物车信息
        if(null != cart){

            //获取购物车里所有商品明细，检查当前购买商品是否加入购物车
            OrderItem orderItem = searchOrderItem(cart.getOrderItemList(), itemId);

            //如果存在在商品购买明细，则数量增加，价格重新计算
            if(null != orderItem){

                //数量+ num
                orderItem.setNum(orderItem.getNum() + num);

                //计算价格
                double totalFee = orderItem.getPrice().doubleValue() * orderItem.getNum();

                orderItem.setTotalFee(new BigDecimal(totalFee));

                //如果数量小于0，将商品从购物车集合移除
                if(orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }

                //如果没有商品明细，移除购物车
                if(cart.getOrderItemList().size()<=0){
                    cartList.remove(cart);
                }
            }else{

                //如果不存在，新建OrderList商品明细，加入到该商家对应购物车的商品明细集合中
                orderItem = createOrderItem(item, num);

                cart.getOrderItemList().add(orderItem);

            }

        }else{
            
            cart = createCart(item, num);

            //将新建Cart加入到购物车
            cartList.add(cart);
        }

        return cartList;
    }

    /**
     * @Description 加入购物车 到Redis
     * @Author  guanx
     * @Date   2020/3/3 13:40
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public void addGoodsToRedis(String userName, List<Cart> cartList) {

        redisTemplate.boundHashOps("CartList").put(userName,cartList);
    }

    /**
     * @Description 查询购物车 从Redis
     * @Author  guanx
     * @Date   2020/3/3 20:09
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public List<Cart> findCartListFromRedis(String userName) {
        return (List<Cart>) redisTemplate.boundHashOps("CartList").get(userName);
    }

    /**
    * @Description 构建一个购物车对象
    * @Author  guanx
    * @Date   2020/3/3 16:49
    * @Param
    * @Return
    * @Exception
    *
    */
    private Cart createCart(Item item, Integer num) {

            //创建商家购物车对象
            Cart cart = new Cart();
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());


            //创建商品明细
            OrderItem orderItem = createOrderItem(item,num);

            List<OrderItem> orderItems = new ArrayList<OrderItem>();

            //将商品明细加入到购物车商品明细中
            orderItems.add(orderItem);

            cart.setOrderItemList(orderItems);

            return cart;
    }

    /**
     * @Description 查询商品明细
     * @Author  guanx
     * @Date   2020/3/3 14:13
     * @Param
     * @Return
     * @Exception
     *
     */
    private OrderItem searchOrderItem(List<OrderItem> orderItemList, Long itemId) {

        for (OrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().longValue() == itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    /**
    * @Description 查询购物车数据
    * @Author  guanx
    * @Date   2020/3/3 14:13
    * @Param
    * @Return
    * @Exception
    *
    */
    private Cart searchCart(List<Cart> cartList, String sellerId) {

        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    /**
    * @Description  创建OrderItem对象
    * @Author  guanx
    * @Date   2020/3/3 13:50
    * @Param
    * @Return
    * @Exception
    *
    */
    public OrderItem createOrderItem(Item item,Integer num){

        OrderItem orderItem = new OrderItem();

        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setPicPath(item.getImage());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);

        double totalFee = orderItem.getPrice().doubleValue() * orderItem.getNum();
        orderItem.setTotalFee(new BigDecimal(totalFee));

        return orderItem;
    }

    /**
    * @Description 合并cookie信息和redis信息
    * @Author  guanx
    * @Date   2020/3/3 20:57
    * @Param
    * @Return
    * @Exception
    *
    */

    public List<Cart> mergeList(List<Cart> redisCartList,List<Cart> cookieCartList){
        
        //循环cookie购物车数据
        for (Cart cart : cookieCartList) {
           
            //获取cookie购物车明细
            List<OrderItem> orderItemList = cart.getOrderItemList();
            
            //循环购物车明细
            for (OrderItem orderItem : orderItemList) {

                Long itemId = orderItem.getItemId();

                Integer num = orderItem.getNum();

                //循环加入reids
                redisCartList = add(redisCartList,itemId,num);
            }
        }
        return redisCartList;
    }
}
