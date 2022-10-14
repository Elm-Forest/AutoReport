package com.ctgu.autoreport.service.common;

import com.ctgu.autoreport.service.core.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Elm Forest
 * @date 13/10/2022 下午6:47
 */
@Service
@EnableScheduling
public class AutoWork {
    @Autowired
    private Report report;

    @Scheduled(cron = "0 0 8,9,10,11,12,13 * * ?")
    void autoWork() {
        report.report();
    }
}
