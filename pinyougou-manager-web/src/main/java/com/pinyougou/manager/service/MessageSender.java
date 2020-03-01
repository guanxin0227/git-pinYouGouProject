package com.pinyougou.manager.service;

import com.pinyougou.mq.MessageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 *  @author: guanx
 *  @Date: 2020/2/29 17:20
 *  @Description: 提取消息发送公用部分
 */

@Component
public class MessageSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination destination;

    /**
    * @Description 消息发送
    * @Author  guanx
    * @Date   2020/2/29 17:24
    * @Param  messageInfo
    * @Return
    * @Exception
    *
    */
    public void sendObjectMessage(MessageInfo messageInfo){

        //发送消息
        jmsTemplate.send(destination,new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                //创建ObjectMessahe对象
                ObjectMessage objectMessage = session.createObjectMessage();

                //封装数据
                objectMessage.setObject(messageInfo);

                return objectMessage;
            }
        });
    }
}
