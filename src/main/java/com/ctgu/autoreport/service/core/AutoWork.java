package com.ctgu.autoreport.service.core;

import com.ctgu.autoreport.dao.UserMapper;
import com.ctgu.autoreport.entity.User;
import com.ctgu.autoreport.service.common.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ctgu.autoreport.common.constant.CommonConst.REPORTED_NUMS;
import static com.ctgu.autoreport.common.constant.CommonConst.REPORTED_SUCCESS;

/**
 * @author Elm Forest
 * @date 13/10/2022 下午6:47
 */
@Service
@EnableScheduling
public class AutoWork {
    @Autowired
    private ReportService report;

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserMapper userMapper;

    @Scheduled(cron = "0 1-15 8 * * ?")
    void autoReport() {
        report.reportByApi();
    }

    @Scheduled(cron = "0 0-5 9-10 * * ?")
    void autoReportV2() {
        report.reportByApi();
    }

    @Scheduled(cron = "0 0 11-16 * * ?")
    void autoReportCheck() {
        report.reportByApi();
    }

    @Scheduled(cron = "0 0 3 * * ?")
    void autoDelete() {
        List<User> users = userMapper.selectList(null);
        int num = 0;
        for (User user : users) {
            num += 1;
            redisService.del(REPORTED_SUCCESS + user.getUsername());
        }
        redisService.set(REPORTED_NUMS, num);
    }
}
