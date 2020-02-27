package com.pinyougou.search.service;

import com.pinyougou.model.Item;

import java.util.List;
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

    /**
     * @Description 批量导入索引库
     * @Author  guanx
     * @Date   2020/2/19 14:51
     * @Param
     * @Return
     * @Exception
     *
     */
    void importList(List<Item> itemList);

    /**
     * @Description 删除索引库
     * @Author  guanx
     * @Date   2020/2/19 14:51
     * @Param
     * @Return
     * @Exception
     *
     */
    void deleteByGoodsIds(List<Long> ids);
}
