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
import org.springframework.util.DigestUtils;

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

        //设置时间
        user.setCreated(new Date());
        user.setUpdated(new Date());

        //MD5加密
        String pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(pwd);


        int count = userMapper.insertSelective(user);

        if(count>0){
            //添加成功，删除验证码
            redisTemplate.boundHashOps("MobileInfo").delete(user.getPhone());
        }

        return count;
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
     * @Description 判断验证码是否有效
     * @Author  guanx
     * @Date   2020/3/2 14:03
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public boolean checkCode(String phone, String code) {

        //redis中查询验证码
        String mobileCode = (String) redisTemplate.boundHashOps("MobileInfo").get(phone);

        if(null == mobileCode){
            return false;
        }

        //匹配验证码是否一致
        if(!mobileCode.equals(code)){
            return false;
        }
        return true;
    }

    /**
     * @Description 判断用户名是否存在
     * @Author  guanx
     * @Date   2020/3/2 14:08
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public int getUserByUserName(String username) {

        User user = new User();
        user.setUsername(username);

        return userMapper.selectCount(user);
    }

    /**
     * @Description 判断手机号是否存在
     * @Author  guanx
     * @Date   2020/3/2 14:12
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public int getPhoneCount(String phone) {

        User user = new User();
        user.setPhone(phone);

        return userMapper.selectCount(user);
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
