package com.pinyougou.sellergoods.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.model.SpecificationOption;
import com.pinyougou.model.TypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Autowired
    private RedisTemplate redisTemplate;

	/**
	 * 返回TypeTemplate全部列表
	 * @return
	 */
	@Override
    public List<TypeTemplate> getAll(){
        return typeTemplateMapper.selectAll();
    }


    /***
     * 分页返回TypeTemplate列表
     * @param pageNum
     * @param pageSize
     * @return
     */
	@Override
    public PageInfo<TypeTemplate> getAll(TypeTemplate typeTemplate,int pageNum, int pageSize) {
        //执行分页
        PageHelper.startPage(pageNum,pageSize);

        //将数据【品牌信息+规格信息】压入缓存
        modifyRedis();

        //执行查询
        List<TypeTemplate> all = typeTemplateMapper.select(typeTemplate);
        PageInfo<TypeTemplate> pageInfo = new PageInfo<TypeTemplate>(all);
        return pageInfo;
    }

    /***
     * 将数据【品牌信息+规格信息】压入缓存
     * @param
     * @return
     */
    private void modifyRedis() {

        //查询所有模板信息
        List<TypeTemplate> typeTemplateList = typeTemplateMapper.selectAll();
        
        //循环，将模板对应的信息存入redis
        for (TypeTemplate typeTemplate : typeTemplateList) {

            //数据转List
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);

            //将品牌存入redis  命名空间+key+value
            redisTemplate.boundHashOps("BrandList").put(typeTemplate.getId(),brandList);

            //将规格存入redis
            List<Map> specList = getOptionsByTypeId(typeTemplate.getId());

            redisTemplate.boundHashOps("SpecList").put(typeTemplate.getId(),specList);
        }

    }


    /***
     * 增加TypeTemplate信息
     * @param typeTemplate
     * @return
     */
    @Override
    public int add(TypeTemplate typeTemplate) {
        return typeTemplateMapper.insertSelective(typeTemplate);
    }


    /***
     * 根据ID查询TypeTemplate信息
     * @param id
     * @return
     */
    @Override
    public TypeTemplate getOneById(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }


    /***
     * 根据ID修改TypeTemplate信息
     * @param typeTemplate
     * @return
     */
    @Override
    public int updateTypeTemplateById(TypeTemplate typeTemplate) {
        return typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
    }


    /***
     * 根据ID批量删除TypeTemplate信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //创建Example，来构建根据ID删除数据
        Example example = new Example(TypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_typeTemplate where id in(1,2,5,6)
        criteria.andIn("id",ids);
        return typeTemplateMapper.deleteByExample(example);
    }
    /***
     * 根据模板id查询规格选项信息
     * @param id
     * @return
     */
    @Override
    public List<Map> getOptionsByTypeId(Long id) {

        //先查询出模板中的规格信息
        TypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);

        //通过FastJSON把字符串集合转换成集合数据，并循环
        List<Map> listMap = JSON.parseArray(typeTemplate.getSpecIds(),Map.class);

        for (Map map : listMap) {

            if(StringUtils.isNotEmpty(map.get("id").toString())){
                //根据spec_id JSON中的值的id去数据库查询规格选项
                long spceId = Long.parseLong(map.get("id").toString());
                SpecificationOption specificationOption = new SpecificationOption();
                specificationOption.setId(spceId);
                //List<SpecificationOption> options = specificationOptionMapper.select(specificationOption);
                List<SpecificationOption> options = specificationOptionMapper.selectBySpecId(spceId);

                //构建JSON数据options结构
                map.put("options",options);
            }

        }

        return listMap;
    }
}
