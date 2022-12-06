package com.ctgu.autoreport.service.core;

import com.ctgu.autoreport.common.vo.Result;

/**
 * @author Zhang Jinming
 * @date 6/12/2022 下午2:23
 */
public interface AdminService {
    Result<?> setHistory(Integer day);

    Result<?> setWork(boolean flag);

    Result<?> clearLoginLimit();
}
