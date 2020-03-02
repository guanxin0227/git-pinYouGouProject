package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.model.User;
import com.pinyougou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* @Description 用户注册
* @Author  guanx
* @Date   2020/3/1 18:39
* @Param
* @Return
* @Exception
*
*/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination destination;

    @Value("${sign_name}")
    private String signName;

    @Value("${template_code}")
    private String templateCode;

    /**
    * @Description 增加用户信息
    * @Author  guanx
    * @Date   2020/3/1 18:53
    * @Param
    * @Return
    * @Exception
    *
    */
    @Override
    public int add(User user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        return userMapper.insertSelective(user);
    }

    /**
     * @Description 创建验证码
     * @Author  guanx
     * @Date   2020/3/2 11:29
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public void createCode(String phone) {

        //生成验证码
        String code = String.valueOf((int) (Math.random() * 10000));

        //将验证码存入到redis中
        redisTemplate.boundHashOps("MobileInfo").put(phone,code);

        //将code存入map，转成json
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("code",code);
        String json = JSON.toJSONString(dataMap);

        //调用方法，将消息发送给activeMQ
        sendMessage(phone, json);

    }

    /**
    * @Description 将消息发送给activeMQ
    * @Author  guanx
    * @Date   2020/3/2 13:37
    * @Param
    * @Return
    * @Exception
    *
    */
    public void sendMessage(String phone, String json) {
        //将消息发送给activeMQ
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                //创建MapMessage
                MapMessage mapMessage = session.createMapMessage();

                mapMessage.setString("signName",signName);
                mapMessage.setString("templateCode",templateCode);
                mapMessage.setString("mobile",phone);
                mapMessage.setString("param",json);

                return mapMessage;
            }
        });
    }
}
