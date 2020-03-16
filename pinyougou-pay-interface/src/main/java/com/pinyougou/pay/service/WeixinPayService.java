package com.pinyougou.pay.service;

import java.util.Map;

/**
 *  @author: guanx
 *  @Date: 2020/3/15 20:39
 *  @Description: 微信支付
 */
public interface WeixinPayService {
    
    /**
    * @Description  获取支付二维码的url
    * @Author  guanx
    * @Date   2020/3/15 20:39
    * @Param  out_trade_no:商户生成的交易编号
     *        total_fee：交易金额
    * @Return      
    * @Exception   
    * 
    */
    Map createNative(String out_trade_no,String total_fee);

    /**
    * @Description 查询订单状态
    * @Author  guanx
    * @Date   2020/3/15 20:42
    * @Param
    * @Return
    * @Exception
    *
    */
    Map queryPayStatus(String out_trade_on);
}
