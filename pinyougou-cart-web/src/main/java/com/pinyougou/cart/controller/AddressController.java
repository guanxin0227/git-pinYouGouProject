package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.model.Address;
import com.pinyougou.user.service.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  @author: guanx
 *  @Date: 2020/3/9 12:41
 *  @Description: 用户地址查询
 */
@RestController
@RequestMapping(value = "/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    /**
    * @Description 根据userId查询用户地址
    * @Author  guanx
    * @Date   2020/3/9 12:46
    * @Param
    * @Return
    * @Exception
    *
    */
    @RequestMapping(value = "/user/list")
    public List<Address> getAddressListByUserId(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.getAddressListByUserId(userId);
    }
}
