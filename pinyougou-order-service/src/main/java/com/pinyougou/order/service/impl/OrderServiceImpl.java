package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.http.ShopStatus;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.model.Cart;
import com.pinyougou.model.Order;
import com.pinyougou.model.OrderItem;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  @author: guanx
 *  @Date: 2020/3/15 15:00
 *  @Description: 创建订单
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
    * @Description 创建订单
    * @Author  guanx
    * @Date   2020/3/15 15:01
    * @Param
    * @Return
    * @Exception
    *
    */
    @Override
    public int add(Order order) {

        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CartList").get(order.getUserId());

        int acount=0;

        //填充数据
        for (Cart cart : cartList) {

            long orderId = idWorker.nextId();
            System.out.println("sellerId:" + cart.getSellerId());
            //创建新的订单信息
            Order tborder = new Order();
            tborder.setOrderId(orderId);//订单ID
            tborder.setUserId(order.getUserId());//用户名
            tborder.setPaymentType(order.getPaymentType());//支付类型
            tborder.setStatus(order.getStatus());//状态：未付款
            tborder.setCreateTime(new Date());//订单创建日期
            tborder.setUpdateTime(new Date());//订单更新日期
            tborder.setReceiverAreaName(order.getReceiverAreaName());//地址
            tborder.setReceiverMobile(order.getReceiverMobile());//手机号
            tborder.setReceiver(order.getReceiver());//收货人
            tborder.setSourceType(order.getSourceType());//订单来源
            tborder.setSellerId(cart.getSellerId());//商家ID
            tborder.setSourceType("2"); //2:PC

            //循环购物车明细
            double money=0;
            for(OrderItem orderItem :cart.getOrderItemList()){
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId);//订单ID
                orderItem.setSellerId(cart.getSellerId());
                money+=orderItem.getTotalFee().doubleValue();//金额累加
                acount+=orderItemMapper.insertSelective(orderItem);
            }

            tborder.setPayment(new BigDecimal(money));

            //增加订单信息
            acount+=orderMapper.insertSelective(tborder);
        }

        //删除购物车信息
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
        return acount;
    }
}
