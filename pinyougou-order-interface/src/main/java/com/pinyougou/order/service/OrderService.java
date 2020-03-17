package com.pinyougou.order.service;

import com.pinyougou.model.Order;
import com.pinyougou.model.PayLog;

/**
 *  @author: guanx
 *  @Date: 2020/3/15 14:59
 *  @Description: 创建订单
 */
public interface OrderService {

    /**
    * @Description 创建订单
    * @Author  guanx
    * @Date   2020/3/15 15:00
    * @Param
    * @Return
    * @Exception
    *
    */
    int add(Order order);

    /****
     * 查询用户支付日志
     * @param userid
     * @return
     */
    PayLog searchPayLogFromRedis(String userid);
}
