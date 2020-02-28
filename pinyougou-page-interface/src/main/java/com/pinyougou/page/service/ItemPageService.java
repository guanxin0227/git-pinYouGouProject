package com.pinyougou.page.service;

/**
 *  @author: guanx
 *  @Date: 2020/2/27 21:37
 *  @Description: 生成静态页
 */
public interface ItemPageService {

    /**
    * @Description 根据GoodsId生成静态页
    * @Author  guanx
    * @Date   2020/2/27 21:37
    * @Param
    * @Return
    * @Exception
    *
    */
    Boolean buildHtml(Long goodsId) throws Exception;
}
