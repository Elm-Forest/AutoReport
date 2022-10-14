package com.ctgu.autoreport.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ctgu.autoreport.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Elm Forest
 * @date 13/10/2022 下午6:46
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
