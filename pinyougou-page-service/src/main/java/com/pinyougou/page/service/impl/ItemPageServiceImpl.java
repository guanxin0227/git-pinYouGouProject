package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.page.service.ItemPageService;

/**
 *  @author: guanx
 *  @Date: 2020/2/27 21:37
 *  @Description: 生成静态页
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

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
    public Boolean buildHtml(Long goodsId) {
        return null;
    }
}
