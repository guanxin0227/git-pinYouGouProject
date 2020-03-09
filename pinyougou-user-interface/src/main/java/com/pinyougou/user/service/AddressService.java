package com.pinyougou.user.service;

import com.pinyougou.model.Address;

import java.util.List;

/**
 *  @author: guanx
 *  @Date: 2020/3/9 12:13
 *  @Description: 用户地址查询
 */
public interface AddressService {

    /**
    * @Description 用户地址查询
    * @Author  guanx
    * @Date   2020/3/9 12:14
    * @Param
    * @Return
    * @Exception
    *
    */
    List<Address> getAddressListByUserId(String userId);
}
