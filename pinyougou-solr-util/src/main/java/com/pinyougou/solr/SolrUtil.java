package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.http.ShopStatus;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 *  @author: guanx
 *  @Date: 2020/2/19 11:14
 *  @Description 迁移数据库数据到solr索引库:
 */
@Component
public class SolrUtil {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
    * @Description 商品数据批量导入索引库
    * @Author  guanx
    * @Date   2020/2/19 11:24
    * @Param
    * @Return
    * @Exception
    *
    */
    public void batchAdd(){

        //查询所有商品状态为上架的商品
        Item item = new Item();
        item.setStatus(ShopStatus.NORMAL);
        List<Item> itemList = itemMapper.select(item);

        //循环，将json转化为map
        for (Item itemMap : itemList) {
            //规格字符串值
            String specString = itemMap.getSpec();
            //将字符串值转为Map
            Map<String,String> dateMap = JSON.parseObject(specString, Map.class);

            itemMap.setSpecMap(dateMap);
        }

        //将数据带入索引库
        solrTemplate.saveBeans(itemList);

        //提交
        solrTemplate.commit();

    }

    /**
    * @Description 动态域收索
    * @Author  guanx
    * @Date   2020/2/19 12:18
    * @Param
    * @Return
    * @Exception
    *
    */
    public void queryByCondtion(String fieldName,String keywords){
        //创建query指定查询条件
        Query query = new SimpleQuery();

        //增加条件
        Criteria criteria = new Criteria("item_spec_" + fieldName).is(keywords);

        //将条件对象给query查询
        query.addCriteria(criteria);

        //指定分页
        query.setOffset(0);
        query.setRows(5);

        //执行分页收索
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);

        //获取结果集
        List<Item> itemList = scoredPage.getContent();

        //总记录数
        long totalElements = scoredPage.getTotalElements();

        System.out.println("总记录数："+totalElements);
        for (Item item : itemList) {
            System.out.println(item);
        }
    }
}
