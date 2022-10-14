package com.ctgu.autoreport.controller;

import com.ctgu.autoreport.service.core.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Elm Forest
 * @date 13/10/2022 下午8:31
 */
@RestController
public class TestController {
    @Autowired
    private Report report;

    @RequestMapping("/test")
    public String test() {
        report.report();
        return "reporting~";
    }
}
