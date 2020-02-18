package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

	/**
	 * 返回Content全部列表
	 * @return
	 */
	@Override
    public List<Content> getAll(){
        return contentMapper.selectAll();
    }


    /***
     * 分页返回Content列表
     * @param pageNum
     * @param pageSize
     * @return
     */
	@Override
    public PageInfo<Content> getAll(Content content,int pageNum, int pageSize) {
        //执行分页
        PageHelper.startPage(pageNum,pageSize);
       
        //执行查询
        List<Content> all = contentMapper.select(content);
        PageInfo<Content> pageInfo = new PageInfo<Content>(all);
        return pageInfo;
    }



    /***
     * 增加Content信息
     * @param content
     * @return
     */
    @Override
    public int add(Content content) {
        return contentMapper.insertSelective(content);
    }


    /***
     * 根据ID查询Content信息
     * @param id
     * @return
     */
    @Override
    public Content getOneById(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }


    /***
     * 根据ID修改Content信息
     * @param content
     * @return
     */
    @Override
    public int updateContentById(Content content) {
        return contentMapper.updateByPrimaryKeySelective(content);
    }


    /***
     * 根据ID批量删除Content信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //创建Example，来构建根据ID删除数据
        Example example = new Example(Content.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_content where id in(1,2,5,6)
        criteria.andIn("id",ids);
        return contentMapper.deleteByExample(example);
    }

    /****
     * 根据分类查询广告信息
     * @param categoryId
     * @return
     */
    @Override
    public List<Content> findByCategoryId(long categoryId) {

        //第二次查询时，先查redis缓存，如果有数据则取缓存中数据，否则查询数据库
        Object content = redisTemplate.boundHashOps("Content").get(categoryId);

        if(null != content){
            return (List<Content>) content;
        }

        //创建Example，来构建根据ID删除数据
        Example example = new Example(Content.class);
        Example.Criteria criteria = example.createCriteria();

        //根据分类id查询
        criteria.andEqualTo("categoryId",categoryId);

        //根据状态筛选
        criteria.andEqualTo("status",1);

        //排序
        example.setOrderByClause("sort_order desc");

        List<Content> contents = contentMapper.selectByExample(example);

        //当第一次查询时，数据不为空，存入redis
        if(null != contents && contents.size()>0){
            redisTemplate.boundHashOps("Content").put(categoryId,contents);
        }

        return contents;
    }
}
