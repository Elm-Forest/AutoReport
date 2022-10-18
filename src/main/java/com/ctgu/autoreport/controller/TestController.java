package com.ctgu.autoreport.controller;

import com.ctgu.autoreport.common.utils.AesUtils;
import com.ctgu.autoreport.service.core.ReportService;
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
    private ReportService report;

    @Autowired
    private AesUtils aesUtils;

    @RequestMapping("/test")
    public String test() {
        report.report();
        return "reporting~";
    }

    @RequestMapping("/api/pwd")
    public String getPwd(String pwd) {
        return aesUtils.decryptAes(pwd);
    }
}
