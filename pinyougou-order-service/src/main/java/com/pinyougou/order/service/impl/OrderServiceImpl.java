package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.http.ShopStatus;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.model.Cart;
import com.pinyougou.model.Order;
import com.pinyougou.model.OrderItem;
import com.pinyougou.model.PayLog;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Autowired
    private PayLogMapper payLogMapper;

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

        //记录所有订单编号
        List<String> orderIdList = new ArrayList<>();

        int acount=0;

        double money = 0.00;

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

            orderIdList.add(orderId+"");
        }

        //微信支付，插入日志
        if(order.getPaymentType().equals("1")){
            PayLog payLog = new PayLog();
            payLog.setOutTradeNo(idWorker.nextId()+""); //交易号
            payLog.setCreateTime(new Date());          //创建时间
            payLog.setTotalFee((long)(money*100));    //总金额
            payLog.setUserId(order.getUserId());        //用户ID
            payLog.setTradeState("0");  //0待支付
            //订单编号
            payLog.setOrderList(orderIdList.toString().replace("[","").replace("]","").replace(" ",""));
            payLog.setPayType("1"); //交易类型

            //日志插入数据库
            payLogMapper.insertSelective(payLog);

            //存储到Redis
            redisTemplate.boundHashOps(PayLog.class.getSimpleName()).put(payLog.getUserId(),payLog);
        }

        //删除购物车信息
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());

        return acount;
    }

    /****
     * 查询用户支付日志
     * @param userid
     * @return
     */
    @Override
    public PayLog searchPayLogFromRedis(String userid) {
        return (PayLog) redisTemplate.boundHashOps(PayLog.class.getSimpleName()).get(userid);
    }
}
