package com.ctgu.autoreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Elm Forest
 */

@EnableTransactionManagement
@SpringBootApplication
public class AutoReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutoReportApplication.class, args);
    }

}
