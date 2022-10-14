package com.ctgu.autoreport.service.core.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ctgu.autoreport.common.enums.StatusCodeEnum;
import com.ctgu.autoreport.common.exception.BizException;
import com.ctgu.autoreport.common.vo.Result;
import com.ctgu.autoreport.common.vo.UserVO;
import com.ctgu.autoreport.dao.UserMapper;
import com.ctgu.autoreport.entity.User;
import com.ctgu.autoreport.service.core.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ctgu.autoreport.common.enums.StatusCodeEnum.SYSTEM_ERROR;

/**
 * @author Elm Forest
 * @date 14/10/2022 下午3:05
 */
@Service
public class RegisterImpl implements Register {
    @Autowired
    private UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<?> register(UserVO userVO) {
        if (!checkExists(userVO.getUsername())) {
            return Result.fail(StatusCodeEnum.EXISTED);
        }
        User user = User.builder()
                .username(userVO.getUsername())
                .password(userVO.getPassword())
                .email(userVO.getEmail())
                .build();
        boolean flag = checkLogin(user);
        if (!flag) {
            return Result.fail(StatusCodeEnum.LOGIN_FAILED);
        }
        try {
            int insert = userMapper.insert(user);
            if (insert <= 0) {
                throw new BizException("注册异常");
            }
        } catch (Exception e) {
            return Result.fail(SYSTEM_ERROR);
        }
        return Result.ok();
    }

    boolean checkExists(String username) {
        Object user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return user == null;
    }

    boolean checkLogin(User user) {
        return new ReportImpl().login(user);
    }
}
