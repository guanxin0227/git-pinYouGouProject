package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.http.ShopStatus;
import com.pinyougou.model.Order;
import com.pinyougou.order.service.OrderService;
import javafx.scene.chart.ValueAxis;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.dnd.DropTarget;
import java.util.Date;

/**
 *  @author: guanx
 *  @Date: 2020/3/15 12:35
 *  @Description: 保存订单信息
 */
@RestController
@RequestMapping(value = "/order")
public class OrderController {

    @Reference
    private OrderService orderService;
    /**
    * @Description 保存订单信息
    * @Author  guanx
    * @Date   2020/3/15 12:36
    * @Param
    * @Return
    * @Exception
    *
    */
    @RequestMapping(value = "/add")
    public Result add(@RequestBody Order order){

        try {
            //获取当前登陆人
            String name = SecurityContextHolder.getContext().getAuthentication().getName();

            order.setUserId(name);
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            order.setStatus(ShopStatus.NORMAL);

            int count = orderService.add(order);

            if(count>0){
                return new Result(true,"添加订单成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Result(false,"添加订单失败");
    }
}
