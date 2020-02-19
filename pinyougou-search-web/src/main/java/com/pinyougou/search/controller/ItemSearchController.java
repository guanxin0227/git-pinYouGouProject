package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *  @author: guanx
 *  @Date: 2020/2/19 15:42
 *  @Description: 商品搜索
 */
@RestController
@RequestMapping(value = "/item")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;
    /**
    * @Description 搜索商品信息
    * @Author  guanx
    * @Date   2020/2/19 15:43
    * @Param
    * @Return
    * @Exception
    *
    */
    @RequestMapping(value = "/search")
    public Map<String,Object> search(@RequestBody(required = false) Map<String,Object> searchMap){
        //执行搜索
        Map<String, Object> dataMap = itemSearchService.search(searchMap);

        return dataMap;
    }
}
