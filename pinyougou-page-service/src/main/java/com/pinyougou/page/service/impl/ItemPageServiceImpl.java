package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.GoodsDescMapper;
import com.pinyougou.mapper.GoodsMapper;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Goods;
import com.pinyougou.model.GoodsDesc;
import com.pinyougou.model.Item;
import com.pinyougou.page.service.ItemPageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import tk.mybatis.mapper.entity.Example;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  @author: guanx
 *  @Date: 2020/2/27 21:37
 *  @Description: 生成静态页
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean;

    @Autowired
    private ItemMapper itemMapper;

    //注入生成路径properties
    @Value("${ITEM_PATH}")
    private String ITEM_PATH;

    @Value("${ITEM_SUFFIX}")
    private String ITEM_SUFFIX;

    /**
     * @Description 根据GoodsId生成静态页
     * @Author  guanx
     * @Date   2020/2/27 21:37
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public Boolean buildHtml(Long goodsId) throws Exception{

        Map<String,Object> dataMap = new HashMap<>();

        //获取数据模型
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        List<Item> items = skuList(goodsId);

        dataMap.put("goods",goods);
        dataMap.put("goodsDesc",goodsDesc);
        dataMap.put("items", JSON.toJSONString(items));

        //创建configration对象
        Configuration configuration = freeMarkerConfigurationFactoryBean.createConfiguration();

        //创建模板对象
        Template template = configuration.getTemplate("item.ftl");

        //指定文件输出对象
        //Writer writer = new FileWriter(new File("D:/Workplace/pinyougou/pinyougou-page-service/src/main/webapp/"+ goodsId + ".html"));
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ITEM_PATH + goodsId + ITEM_SUFFIX),"UTF-8"));

        //合成输出文件
        template.process(dataMap,writer);

        //关闭资源
        writer.flush();
        writer.close();

        return true;
    }

    /**
     * @Description 根据id删除生成的静态html页面
     * @Author  guanx
     * @Date   2020/2/29 20:16
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public void deleteHtml(Long id) {

        //创建要删除的文件对象
        File file = new File(ITEM_PATH + id + ITEM_SUFFIX);

        //判断文件是否存在
        if(file.exists()){
            file.delete();
        }
    }

    /**
    * @Description 根据goodsId查询List<Item>
    * @Author  guanx
    * @Date   2020/2/28 16:15
    * @Param
    * @Return
    * @Exception
    *
    */
    public List<Item> skuList(Long goodsId){

        Example example = new Example(Item.class);
        Example.Criteria criteria = example.createCriteria();

        //商品处于上架状态
        criteria.andEqualTo("status","1");

        criteria.andEqualTo("goodsId",goodsId);

        //商品详情默认选中。isDefault=1
        example.orderBy("isDefault").desc();

        return itemMapper.selectByExample(example);
    }
}
