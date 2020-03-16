package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 *  @author: guanx
 *  @Date: 2020/3/15 20:44
 *  @Description: 微信支付
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notifyurl;

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
    @Override
    public Map createNative(String out_trade_no, String total_fee) {

        try {
            //1、封装参数
            Map param = new HashMap();
            param.put("appid", appid);      //应用ID
            param.put("mch_id", partner);   //商户ID号
            param.put("nonce_str", WXPayUtil.generateNonceStr());   //随机数
            param.put("body", "品优购");   //订单描述
            param.put("out_trade_no",out_trade_no); //商户订单号
            param.put("total_fee", total_fee);//交易金额
            param.put("spbill_create_ip", "127.0.0.1"); //终端IP
            param.put("notify_url", notifyurl);//回调地址
            param.put("trade_type", "NATIVE");  //交易类型

            String paramXml = WXPayUtil.generateSignedXml(param, partnerkey);

            //2、执行请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();


            //3、获取参数
            String content = httpClient.getContent();
            Map<String, String> stringMap = WXPayUtil.xmlToMap(content);

            Map<String,String> dataMap = new HashMap<String,String>();
            dataMap.put("code_url",stringMap.get("code_url"));
            dataMap.put("out_trade_no",out_trade_no);
            dataMap.put("total_fee",total_fee);

            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description 查询订单状态
     * @Author  guanx
     * @Date   2020/3/15 20:42
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public Map queryPayStatus(String out_trade_on) {
        try {
            //1.封装参数
            Map param = new HashMap();
            param.put("appid",appid);           //应用ID
            param.put("mch_id",partner);        //商户号
            param.put("out_trade_no",out_trade_on);//商户订单编号
            param.put("nonce_str",WXPayUtil.generateNonceStr());    //随机字符
            String paramXml = WXPayUtil.generateSignedXml(param,partnerkey);

            //2.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();

            //3.获取返回值
            String content = httpClient.getContent();
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
