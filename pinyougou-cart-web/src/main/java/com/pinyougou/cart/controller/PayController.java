package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.model.PayLog;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.util.IdWorker;
import org.opensaml.xml.signature.P;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *  @author: guanx
 *  @Date: 2020/3/15 20:56
 *  @Description: 微信支付
 */
@RestController
@RequestMapping(value = "/pay")
public class PayController {

    @Autowired
    private IdWorker idWorker;

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;

    /**
    * @Description 创建支付url
    * @Author  guanx
    * @Date   2020/3/15 21:41
    * @Param  
    * @Return      
    * @Exception   
    * 
    */
    @RequestMapping(value = "/createNative")
    public Map createNative(){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        PayLog payLog = orderService.searchPayLogFromRedis(name);

        if(null != payLog){
            return weixinPayService.createNative(payLog.getOutTradeNo(),(payLog.getTotalFee()*100) + "");
        }
        return  new HashMap();
    }
    
    /**
    * @Description 查询订单状态
    * @Author  guanx
    * @Date   2020/3/15 21:41
    * @Param  
    * @Return      
    * @Exception   
    * 
    */
    @RequestMapping(value = "/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) throws InterruptedException {

        int count = 0;

        while (true){
            Map map = weixinPayService.queryPayStatus(out_trade_no);

            //支付异常
            if(map==null){
                return  new Result(false,"支付发生错误！");
            }

            //支付成功
            if(map.get("trade_state").equals("SUCCESS")){
                return  new Result(true,"支付成功！");
            }

            //支付超时
            if(map.get("trade_state").equals("PAYERROR")){
                return  new Result(false,"timeout");
            }

            //每3秒查询一次
            Thread.sleep(3000);
            count++;

            if(count>10){
                return  new Result(false,"支付超时");
            }
        }
    }
}
