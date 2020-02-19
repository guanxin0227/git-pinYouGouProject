package com.pinyougou.test;

import com.pinyougou.solr.SolrUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *  @author: guanx
 *  @Date: 2020/2/19 11:39
 *  @Description: 测试数据库商品数据导入索引库
 */
public class SolrImportTest {

    private SolrUtil solrUtil;

    @Before
    public void init(){
        //spring 容器
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:spring/spring-solr.xml");

        //获取SolrUtil实例
        solrUtil = classPathXmlApplicationContext.getBean(SolrUtil.class);
    }

    //数据批量导入
    @Test
    public void testAdd(){
        solrUtil.batchAdd();
    }

    //获取域对象
    @Test
    public void testGetBySpec(){
        solrUtil.queryByCondtion("机身内存","16G");
    }
}
