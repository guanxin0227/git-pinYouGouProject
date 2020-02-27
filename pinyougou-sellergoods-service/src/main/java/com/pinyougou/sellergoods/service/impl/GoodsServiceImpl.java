package com.pinyougou.sellergoods.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.http.ShopStatus;
import com.pinyougou.mapper.*;
import com.pinyougou.model.*;
import com.pinyougou.sellergoods.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
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
//        List<Goods> all = goodsMapper.select(goods);

        //条件查询实现
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();

        if(goods != null){

            //商家sellerId查询
            if(StringUtils.isNotEmpty(goods.getSellerId())){
                criteria.andEqualTo("sellerId",goods.getSellerId());
            }

            //状态查询
            if(StringUtils.isNotEmpty(goods.getAuditStatus())){
                criteria.andEqualTo("auditStatus",goods.getAuditStatus());
            }

            //模糊查询
            if(StringUtils.isNotEmpty(goods.getGoodsName())){
                criteria.andLike("goodsName","%" + goods.getGoodsName() + "%");
            }
        }

        criteria.andEqualTo("isDelete",ShopStatus.ISNOTDELETE);

        //查询
        List<Goods> list = goodsMapper.selectByExample(example);

        PageInfo<Goods> pageInfo = new PageInfo<Goods>(list);
        return pageInfo;
    }



    /***
     * 增加Goods信息
     * @param goods
     * @return
     */
    @Override
    public int add(Goods goods) {

        //新建IsDelete默认0:未删除；1：删除
        goods.setIsDelete(ShopStatus.ISNOTDELETE);
        //增加goods表
        int aumont = goodsMapper.insertSelective(goods);

        //增加goodsDesc表
        //@GeneratedValue(strategy = GenerationType.IDENTITY) 用于获取主键自增值
        GoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescMapper.insertSelective(goodsDesc);

        //调用公共增加方法
        addItems(goods);

        return aumont;
    }

    /***
     * 抽取公共item增加方法
     * @param goods
     * @return
     */
    public void addItems(Goods goods) {
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

                item.setTitle(goods.getGoodsName() + " " + title);

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
    }
    /***
     * 抽取公共goods增加方法
     * @param goods
     * @return
     */
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
        //查询goods表
        Goods goods = goodsMapper.selectByPrimaryKey(id);

        //查询goodsDesc表
        GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(goodsDesc);

        //查询item表
        Item item = new Item();
        item.setGoodsId(id);
        List<Item> itemList = itemMapper.select(item);
        goods.setItems(itemList);

        return goods;
    }


    /***
     * 根据ID修改Goods信息
     * @param goods
     * @return
     */
    @Override
    public int updateGoodsById(Goods goods) {

        //修改goods表  状态由审核通过变成待审核
        goods.setAuditStatus(ShopStatus.NO_EXAMINE);
        int goodsCount = goodsMapper.updateByPrimaryKeySelective(goods);

        //修改goodsDesc表
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

        //删除item
        Item item = new Item();
        item.setGoodsId(goods.getId());
        itemMapper.delete(item);

        //新增item   调用公共增加方法
        addItems(goods);

        return goodsCount;
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
        //return goodsMapper.deleteByExample(example);
        //非物理删除 改变数据库字段状态
        Goods goods = new Goods();
        goods.setIsDelete(ShopStatus.ISDELETE);
        return goodsMapper.updateByExampleSelective(goods,example);
    }

    /***
     * 审核：根据IDs批量更新goods状态
     * @param ids
     * @param status
     * @return
     */
    @Override
    public int updateStatus(List<Long> ids, String status) {

        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);

        //修改操作
        Goods goods = new Goods();
        goods.setAuditStatus(status);

        return goodsMapper.updateByExampleSelective(goods,example);
    }

    /***
     * 根据GoodsIds查询item
     * @param ids
     * @param status
     * @return
     */
    @Override
    public List<Item> getByGoodsIds(List<Long> ids, String status) {

        Example example = new Example(Item.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("goodsId",ids);
        criteria.andEqualTo("status",status);

        return itemMapper.selectByExample(example);
    }
}
