package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.model.User;
import com.pinyougou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
* @Description 用户注册
* @Author  guanx
* @Date   2020/3/1 18:39
* @Param
* @Return
* @Exception
*
*/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
    * @Description 增加用户信息
    * @Author  guanx
    * @Date   2020/3/1 18:53
    * @Param
    * @Return
    * @Exception
    *
    */
    @Override
    public int add(User user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        return userMapper.insertSelective(user);
    }
}
