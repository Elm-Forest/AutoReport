package com.ctgu.autoreport.service.core.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ctgu.autoreport.common.dto.ServiceDTO;
import com.ctgu.autoreport.common.enums.StatusCodeEnum;
import com.ctgu.autoreport.common.exception.BizException;
import com.ctgu.autoreport.common.vo.Result;
import com.ctgu.autoreport.common.vo.UserVO;
import com.ctgu.autoreport.dao.UserMapper;
import com.ctgu.autoreport.entity.User;
import com.ctgu.autoreport.service.common.RedisService;
import com.ctgu.autoreport.service.core.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ctgu.autoreport.common.constant.CommonConst.*;
import static com.ctgu.autoreport.common.enums.StatusCodeEnum.SYSTEM_ERROR;

/**
 * @author Elm Forest
 * @date 14/10/2022 下午3:05
 */
@Service
public class RegisterImpl implements Register {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<?> register(UserVO userVO) {
        if (redisService.get(AUTO_REPORT + userVO.getUsername()) != null) {
            return Result.fail("请勿重复提交！");
        }
        redisService.set(AUTO_REPORT + userVO.getUsername(), userVO.getUsername(), 60);
        Result<?> result = registerCore(userVO);
        redisService.del(AUTO_REPORT + userVO.getUsername());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    Result<?> registerCore(UserVO userVO) {
        if (!checkExists(userVO.getUsername())) {
            return Result.fail(StatusCodeEnum.EXISTED);
        }
        User user = User.builder()
                .username(userVO.getUsername())
                .password(userVO.getPassword())
                .email(userVO.getEmail())
                .build();
        System.out.println(user);
        ServiceDTO serviceDTO = checkLogin(user);
        if (!serviceDTO.getFlag()) {
            if (serviceDTO.getCode().equals(LOGIN_FAILED)) {
                return Result.fail(serviceDTO.getMessage());
            } else if (serviceDTO.getCode().equals(SERVICE_ERROR)) {
                try {
                    int insert = userMapper.insert(user);
                    if (insert <= 0) {
                        throw new BizException("注册异常");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return Result.fail(SYSTEM_ERROR);
                }
                return Result.fail(serviceDTO.getMessage());
            }
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

    ServiceDTO checkLogin(User user) {
        return new ReportImpl().login(user);
    }
}
