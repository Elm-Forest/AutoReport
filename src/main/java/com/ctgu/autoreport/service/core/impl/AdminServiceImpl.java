package com.ctgu.autoreport.service.core.impl;

import com.ctgu.autoreport.common.exception.BizException;
import com.ctgu.autoreport.common.vo.Result;
import com.ctgu.autoreport.service.common.RedisService;
import com.ctgu.autoreport.service.core.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ctgu.autoreport.common.constant.CommonConst.*;

/**
 * @author Zhang Jinming
 * @date 6/12/2022 下午2:23
 */
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private RedisService redisService;

    @Override
    public Result<?> setHistory(Integer day) {
        int minDay = 1;
        int maxDay = 3;
        try {
            if (day < minDay) {
                throw new BizException("上报参考日期应当大于" + minDay);
            } else if (day > maxDay) {
                throw new BizException("上报参考日期应当小于等于" + maxDay);
            } else {
                redisService.set(REPORTED_HISTORY_DAY, day - 1);
            }
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        return Result.ok();
    }

    @Override
    public Result<?> setWork(boolean flag) {
        redisService.set(IS_WORKING, true);
        return Result.ok();
    }

    @Override
    public Result<?> clearLoginLimit() {
        redisService.set(REDIS_LOGIN_FAILED, 0);
        return Result.ok();
    }
}
