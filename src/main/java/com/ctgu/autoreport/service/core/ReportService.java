package com.ctgu.autoreport.service.core;

import com.ctgu.autoreport.common.dto.ServiceDTO;
import com.ctgu.autoreport.entity.User;

/**
 * @author Elm Forest
 * @date 13/10/2022 下午6:48
 */
public interface ReportService {
    void report();

    ServiceDTO reportCore(User user);

    ServiceDTO login(User user);
}
