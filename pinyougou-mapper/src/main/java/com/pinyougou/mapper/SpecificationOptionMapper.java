package com.pinyougou.mapper;

import com.pinyougou.model.SpecificationOption;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpecificationOptionMapper extends Mapper<SpecificationOption> {
    List<SpecificationOption> selectBySpecId(Long id);
}