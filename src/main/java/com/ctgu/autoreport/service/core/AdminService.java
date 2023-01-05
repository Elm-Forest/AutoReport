package com.ctgu.autoreport.service.core;

import com.ctgu.autoreport.common.vo.Result;

/**
 * @author Zhang Jinming
 * @date 6/12/2022 下午2:23
 */
public interface AdminService {
    /**
     * 设置历史追溯时长
     *
     * @param day 追溯时间(天)
     * @return Result视图
     */
    Result<?> setHistory(Integer day);

    /**
     * 开关
     *
     * @param flag 开关
     * @return Result视图
     */
    Result<?> setWork(boolean flag);

    /**
     * 清楚登录失败限制
     *
     * @return Result视图
     */
    Result<?> clearLoginLimit();
}
