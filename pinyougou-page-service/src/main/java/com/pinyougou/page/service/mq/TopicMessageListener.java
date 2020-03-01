package com.pinyougou.page.service.mq;

import com.pinyougou.model.Item;
import com.pinyougou.mq.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* @Description 静态页消息接收
* @Author  guanx
* @Date   2020/2/29 19:56
* @Param
* @Return
* @Exception
*
*/
public class TopicMessageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        //判断接收数据leix
        if(message instanceof ObjectMessage){

            //强转类型ObjectMessage
            ObjectMessage objectMessage = (ObjectMessage)message;

            try {
                //获取消息内容，转为MessageInfo
                MessageInfo messageInfo = (MessageInfo) objectMessage.getObject();

                //判断，如果是修改，则增加索引   ,如果是删除，则删除索引
                if(messageInfo.getMethod() == MessageInfo.METHOD_UPDATE){

                    //获取传输数据
                    List<Item> itemList = (List<Item>) messageInfo.getContext();

                    //获取所有goodsId,并去重
                    Set<Long> goodsId = getGoodsId(itemList);

                    //循环创建静态页
                    for (Long id : goodsId) {
                        itemPageService.buildHtml(id);
                    }
                }else if(messageInfo.getMethod() == MessageInfo.METHOD_DELETE){

                    //获取传输数据
                    List<Long> ids = (List<Long>) messageInfo.getContext();

                    //循环删除静态页
                    for (Long id : ids) {
                        itemPageService.deleteHtml(id);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
    * @Description 从itemList中获取goodsId,并去重
    * @Author  guanx
    * @Date   2020/2/29 20:08
    * @Param
    * @Return
    * @Exception
    *
    */

    public Set<Long> getGoodsId(List<Item> itemList){

        //创建set集合
        Set<Long> ids = new HashSet<>();

        for (Item item : itemList) {
            ids.add(item.getGoodsId());
        }

        return ids;
    }
}
