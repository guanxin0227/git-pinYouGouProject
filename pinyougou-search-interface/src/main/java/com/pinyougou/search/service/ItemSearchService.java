package com.pinyougou.search.service;

import java.util.Map;

/**
 *  @author: guanx
 *  @Date: 2020/2/19 14:50
 *  @Description: solr 搜索
 */
public interface ItemSearchService {
    /**
    * @Description 搜索方法
    * @Author  guanx
    * @Date   2020/2/19 14:51
    * @Param
    * @Return
    * @Exception
    *
    */
    Map<String,Object> search(Map<String,Object> searchMap);
}
