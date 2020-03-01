package com.pinyougou.manager.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.http.Result;
import com.pinyougou.http.ShopStatus;
import com.pinyougou.manager.service.MessageSender;
import com.pinyougou.model.Goods;
import com.pinyougou.model.Item;
import com.pinyougou.mq.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

//    @Reference
//    private ItemSearchService itemSearchService;

    @Reference
    private ItemPageService itemPageService;

    @Autowired
    private MessageSender messageSender;

    public GoodsController() {
    }

    /***
     * 根据ID批量删除  引入消息队列
     * @param ids
     * @return
     */
    @RequestMapping(value = "/delete")
    public Result delete(@RequestBody List<Long> ids){
        try {
            //根据ID删除数据
            int dcount = goodsService.deleteByIds(ids);

            if(dcount>0){

                //如果删除成功，删除索引库
                //itemSearchService.deleteByGoodsIds(ids);

                //发送消息到消息队列
                MessageInfo messageInfo = new MessageInfo(MessageInfo.METHOD_DELETE, ids);

                //发送消息
                messageSender.sendObjectMessage(messageInfo);

                return new Result(true,"删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"删除失败");
    }

    /***
     * 修改信息
     * @param goods
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public Result modify(@RequestBody Goods goods){
        try {
            //根据ID修改Goods信息
            int mcount = goodsService.updateGoodsById(goods);
            if(mcount>0){
                return new Result(true,"修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"修改失败");
    }

    /***
     * 根据ID查询Goods信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Goods getById(@PathVariable(value = "id")long id){
        //根据ID查询Goods信息
        Goods goods = goodsService.getOneById(id);
        return goods;
    }


    /***
     * 增加Goods数据
     * @param goods
     * 响应数据：success
     *                  true:成功  false：失败
     *           message
     *                  响应的消息
     *
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public Result add(@RequestBody Goods goods){
        try {
            //执行增加
            int acount = goodsService.add(goods);

            if(acount>0){
                //增加成功
               return new Result(true,"增加成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"增加失败");
    }



    /***
     * 分页查询数据
     * 获取JSON数据
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public PageInfo<Goods> list(@RequestBody Goods goods,@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return goodsService.getAll(goods,page, size);
    }



    /***
     * 查询所有
     * 获取JSON数据
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public List<Goods> list() {
        return goodsService.getAll();
    }

    /***
     * 审核操作 ，更新审核状态   引入activeMQ消息中间件
     * 获取JSON数据
     * @return
     */
    @RequestMapping("/update/status")
    public Result updateStatus(@RequestBody List<Long> ids,String status){
        try {
            //更新状态
            int mcount = goodsService.updateStatus(ids,status);

            if(mcount>0){

                if(status.equals(ShopStatus.YES_EXAMINE)){

                    //如果审核通过，将新增商品信息添加到索引库
                    //根据GoodsIds查询item
                    List<Item> itemList = goodsService.getByGoodsIds(ids,status);

                    //批量导入索引库
                    //itemSearchService.importList(itemList);

                    //创建静态页面
//                    for (Long id : ids) {
//                        itemPageService.buildHtml(id);
//                    }

                    //发送消息时的封装
                    MessageInfo messageInfo = new MessageInfo(MessageInfo.METHOD_UPDATE,itemList);

                    //消息发送
                    messageSender.sendObjectMessage(messageInfo);

                }

                return new Result(true,"审核通过");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Result(true,"审核失败");
    }
}
