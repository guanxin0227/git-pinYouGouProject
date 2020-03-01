package com.pinyougou.search.service.mq;

import com.pinyougou.model.Item;
import com.pinyougou.mq.MessageInfo;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.List;

/**
* @Description 接收消息队列
* @Author  guanx
* @Date   2020/2/29 17:48
* @Param
* @Return
* @Exception
*
*/
public class TopicMessageListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        //解析数据
        if(message instanceof ObjectMessage){

            //强转为ObjectMessage
            ObjectMessage objectMessage = (ObjectMessage) message;

            try {

                //获取消息内容，转为MessageInfo
                MessageInfo messageInfo = (MessageInfo) objectMessage.getObject();

                //判断，如果是修改，则增加索引   ,如果是删除，则删除索引
                if(messageInfo.getMethod()==MessageInfo.METHOD_UPDATE){

                    //获取传输数据
                    List<Item> itemList = (List<Item>) messageInfo.getContext();

                    itemSearchService.importList(itemList);

                }else if(messageInfo.getMethod()==MessageInfo.METHOD_DELETE){

                    //获取传输数据
                    List<Long> ids = (List<Long>) messageInfo.getContext();

                    itemSearchService.deleteByGoodsIds(ids);

                }

            }catch (Exception e){
                e.printStackTrace();
            }
            //
        }


    }
}
