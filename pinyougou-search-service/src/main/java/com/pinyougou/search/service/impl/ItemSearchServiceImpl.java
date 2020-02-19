package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.model.Item;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
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
     * @Description 搜索方法  (高亮数据显示)
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
        //Query query = new SimpleQuery("*:*");
        SimpleHighlightQuery query = new SimpleHighlightQuery(new SimpleStringCriteria("*:*"));

        //调用高亮配置方法
        highlightSetting(query);

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

        //查询  返回结果包含高亮数据和非高亮数据
        //ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);
        HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(query, Item.class);

        //高亮替换
        ighlighthReplace(highlightPage);

        //封装到Map
        Map<String,Object> dataMap = new HashMap<>();

        //分组查询，条件封装都使用上面的Query查询
        GroupOptions groupOptions = new GroupOptions();

        //指定对应的分组域
        groupOptions.addGroupByField("item_category");

        //将分组设置添加到Query中
        query.setGroupOptions(groupOptions);

        //查询
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(query, Item.class);

        //获取对应的分组结果集
        GroupResult<Item> groupResult = groupPage.getGroupResult("item_category");

        //groupResult为有键值对的结果集
        List<String> categoryList = new ArrayList<>();
        for (GroupEntry<Item> groupEntry : groupResult.getGroupEntries()) {
             //获取对应结果集
            String groupValue = groupEntry.getGroupValue();
            categoryList.add(groupValue);
        }

        dataMap.put("categoryList",categoryList);

        //总记录数
        long totalElements = highlightPage.getTotalElements();
        dataMap.put("total",totalElements);

        //结果集对象
        List<Item> itemList = highlightPage.getContent();
        dataMap.put("rows",itemList);

        return dataMap;
    }

    /**
    * @Description 高亮替换
    * @Author  guanx
    * @Date   2020/2/19 19:41
    * @Param
    * @Return
    * @Exception
    *
    */
    public void ighlighthReplace(HighlightPage<Item> highlightPage) {

        //高亮信息和非高亮信息集合
        List<HighlightEntry<Item>> highlighted = highlightPage.getHighlighted();

        //循环所有数据
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //获取被循环的非高亮数据
            Item item = itemHighlightEntry.getEntity();

            //获取被循环的高亮数据
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();

            //有高亮数据，则替换非高亮数据
            if(null != highlights && highlights.size()>0){
                //获取高亮记录
                HighlightEntry.Highlight highlight = highlights.get(0);

                //获取高亮数据
                List<String> snipplets = highlight.getSnipplets();

                if(null != snipplets && snipplets.size()>0){
                    //获取高亮字符
                    String glstr = snipplets.get(0);

                    //替换
                    item.setTitle(glstr);
                }
            }
        }
    }

    /**
    * @Description 高亮配置
    * @Author  guanx
    * @Date   2020/2/19 19:39
    * @Param
    * @Return
    * @Exception
    *
    */
    public void highlightSetting(SimpleHighlightQuery query) {

        //高亮信息设置
        HighlightOptions highlightOptions = new HighlightOptions();

        //设置item_title为高亮域
        highlightOptions.addField("item_title");

        //设置前缀
        highlightOptions.setSimplePrefix("<span style=\"color:red;\">");
        //设置后缀
        highlightOptions.setSimplePostfix("</span>");

        query.setHighlightOptions(highlightOptions);
    }


    /**
     * @Description 搜索方法  (非高亮数据显示)
     * @Author  guanx
     * @Date   2020/2/19 14:51
     * @Param
     * @Return
     * @Exception
     *
     */
//    @Override
//    public Map<String, Object> searchNotH(Map<String, Object> searchMap) {
//
//        //条件搜索
//        Query query = new SimpleQuery("*:*");
//
//        //如果searchMap为null,也就是传入查询条件为null，即查询所有
//        //不为空，则搜索对应关键词的数据
//        if(null != searchMap){
//            //关键词的key是keyword
//            String keyword = (String) searchMap.get("keyword");
//
//            //根据关键词查询
//            if(StringUtils.isNotBlank(keyword)){
//                //第一个参数要查询的域
//                Criteria criteria = new Criteria("item_keywords").is(keyword);
//
//                query.addCriteria(criteria);
//            }
//        }
//
//        //分页
//        query.setRows(10);
//        query.setOffset(0);
//
//        //查询
//        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);
//
//        //封装到Map
//        Map<String,Object> dataMap = new HashMap<>();
//
//        //总记录数
//        long totalElements = scoredPage.getTotalElements();
//        dataMap.put("total",totalElements);
//
//        //结果集对象
//        List<Item> itemList = scoredPage.getContent();
//        dataMap.put("rows",itemList);
//
//        return dataMap;
//    }
}
