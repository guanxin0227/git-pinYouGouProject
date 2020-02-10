package com.pinyougou.sellergoods.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.http.ShopStatus;
import com.pinyougou.mapper.*;
import com.pinyougou.model.*;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private ItemMapper itemMapper;

	/**
	 * 返回Goods全部列表
	 * @return
	 */
	@Override
    public List<Goods> getAll(){
        return goodsMapper.selectAll();
    }


    /***
     * 分页返回Goods列表
     * @param pageNum
     * @param pageSize
     * @return
     */
	@Override
    public PageInfo<Goods> getAll(Goods goods,int pageNum, int pageSize) {
        //执行分页
        PageHelper.startPage(pageNum,pageSize);
       
        //执行查询
        List<Goods> all = goodsMapper.select(goods);
        PageInfo<Goods> pageInfo = new PageInfo<Goods>(all);
        return pageInfo;
    }



    /***
     * 增加Goods信息
     * @param goods
     * @return
     */
    @Override
    public int add(Goods goods) {

        //增加goods表
        int aumont = goodsMapper.insertSelective(goods);

        //增加goodsDesc表
        //@GeneratedValue(strategy = GenerationType.IDENTITY) 用于获取主键自增值
        GoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescMapper.insertSelective(goodsDesc);

        //判断是否启动规格
        if(goods.getIsEnableSpec().equals(ShopStatus.ENABLE)){
            //增加item表 SKU
            for (Item item : goods.getItems()) {

                //标题
                String title = "";

                //获取规格
                Map<String,String> specMap = JSON.parseObject(item.getSpec(), Map.class);
                for (Map.Entry<String,String> entity:specMap.entrySet()){
                    title += " " + entity.getValue();
                }
                item.setTitle(title);

                //调用抽取公共方法
                goodsParameterInit(goods, item);

                //添加到数据库
                itemMapper.insertSelective(item);
            }
        }else{

                Item item = new Item();

                //获取goods的名称
                String goodsName = goods.getGoodsName();
                item.setTitle(goodsName);

                //调用抽取公共方法
                goodsParameterInit(goods, item);

                //价格
                item.setPrice(goods.getPrice());
                //是否启用
                item.setStatus(ShopStatus.ENABLE);
                //数量
                item.setNum(1);
                //是否默认的商品
                item.setIsDefault(ShopStatus.DEFAULT);

                //添加到数据库
                itemMapper.insertSelective(item);
        }

        return aumont;
    }

    public void goodsParameterInit(Goods goods, Item item) {

        //图片，无图片服务器，注释掉
//      String goodDescImages = goods.getGoodsDesc().getItemImages();
//      List<Map> imagesMap = JSON.parseArray(goodDescImages, Map.class);
//      String imagesUrl = imagesMap.get(0).get("url").toString();
        item.setImage("");

        //分类id
        item.setCategoryid(goods.getCategory3Id());

        //创建时间，修改时间
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());

        //goodId
        item.setGoodsId(goods.getId());

        //sellerId
        item.setSellerId(goods.getSellerId());

        //category
        ItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
        item.setCategory(itemCat.getName());

        //brand
        Brand brand = brandMapper.selectByPrimaryKey(goods.getBrandId());
        item.setBrand(brand.getName());

        //seller
        Seller seller = sellerMapper.selectByPrimaryKey(goods.getSellerId());
        item.setSeller(seller.getName());
    }


    /***
     * 根据ID查询Goods信息
     * @param id
     * @return
     */
    @Override
    public Goods getOneById(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }


    /***
     * 根据ID修改Goods信息
     * @param goods
     * @return
     */
    @Override
    public int updateGoodsById(Goods goods) {
        return goodsMapper.updateByPrimaryKeySelective(goods);
    }


    /***
     * 根据ID批量删除Goods信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //创建Example，来构建根据ID删除数据
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_goods where id in(1,2,5,6)
        criteria.andIn("id",ids);
        return goodsMapper.deleteByExample(example);
    }
}
