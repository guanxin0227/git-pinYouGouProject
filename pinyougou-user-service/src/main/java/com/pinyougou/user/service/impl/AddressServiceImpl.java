package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.model.Address;
import com.pinyougou.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 *  @author: guanx
 *  @Date: 2020/3/9 12:13
 *  @Description: 用户地址查询
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * @Description 用户地址查询
     * @Author  guanx
     * @Date   2020/3/9 12:14
     * @Param
     * @Return
     * @Exception
     *
     */
    @Override
    public List<Address> getAddressListByUserId(String userId) {

        Address address = new Address();

        address.setUserId(userId);
        return addressMapper.select(address);

    }
}
