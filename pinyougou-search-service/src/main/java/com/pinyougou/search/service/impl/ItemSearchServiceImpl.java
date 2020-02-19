package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.model.Item;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *  @author: guanx
 *  @Date: 2020/2/19 14:50
 *  @Description: solr 搜索
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * @Description 搜索方法
     * @Author  guanx
     * @Date   2020/2/19 14:51
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {

        //条件搜索
        Query query = new SimpleQuery("*:*");

        //如果searchMap为null,也就是传入查询条件为null，即查询所有
        //不为空，则搜索对应关键词的数据
        if(null != searchMap){
            //关键词的key是keyword
            String keyword = (String) searchMap.get("keyword");

            //根据关键词查询
            if(StringUtils.isNotBlank(keyword)){
                //第一个参数要查询的域
                Criteria criteria = new Criteria("item_keywords").is(keyword);

                query.addCriteria(criteria);
            }
        }

        //分页
        query.setRows(10);
        query.setOffset(0);

        //查询
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);

        //封装到Map
        Map<String,Object> dataMap = new HashMap<>();

        //总记录数
        long totalElements = scoredPage.getTotalElements();
        dataMap.put("total",totalElements);

        //结果集对象
        List<Item> itemList = scoredPage.getContent();
        dataMap.put("rows",itemList);

        return dataMap;
    }
}
