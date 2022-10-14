package com.ctgu.autoreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Elm Forest
 */
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
public class AutoReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutoReportApplication.class, args);
    }

}
