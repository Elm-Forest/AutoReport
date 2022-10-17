package com.ctgu.autoreport.service.core;

import com.ctgu.autoreport.common.dto.ServiceDTO;
import com.ctgu.autoreport.entity.User;

/**
 * ReportService
 *
 * @author Elm Forest
 * @date 13/10/2022 下午6:48
 */
public interface ReportService {
    /**
     * 上报服务
     */
    void report();

    /**
     * 上报服务核心
     *
     * @param user 用户实体
     * @return ServiceDTO 服务传输载体
     */
    ServiceDTO reportCore(User user);

    /**
     * 登录服务核心
     *
     * @param user 用户实体
     * @return ServiceDTO 服务传输载体
     */
    ServiceDTO login(User user);
}
