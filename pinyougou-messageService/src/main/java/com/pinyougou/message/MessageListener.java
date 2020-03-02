package com.pinyougou.message;

import com.aliyuncs.exceptions.ClientException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
* @Description 短信服务平台
* @Author  guanx
* @Date   2020/3/1 17:08
* @Param
* @Return
* @Exception
*
*/
@Component
public class MessageListener {

    private MessageSender messageSender;

    /**
    * @Description 接收各个服务发来的短信
    * @Author  guanx
    * @Date   2020/3/1 17:09
    * @Param  签名  ， 模板  ， 发送的手机号  ， 模板对应参数
    * @Return
    * @Exception
    *
    */
    @JmsListener(destination = "message-list")
    public void readMessage(Map<String,String> dataMap) throws ClientException {

        //调用阿里大于实现短信发送
        messageSender.sendSms(dataMap.get("signName"),dataMap.get("templateCode"),dataMap.get("mobile"),dataMap.get("param"));
    }
}
