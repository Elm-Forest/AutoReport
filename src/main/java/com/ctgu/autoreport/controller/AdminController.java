package com.ctgu.autoreport.controller;

import com.ctgu.autoreport.common.vo.Result;
import com.ctgu.autoreport.service.core.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhang Jinming
 * @date 6/12/2022 下午2:22
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @RequestMapping("/day")
    public Result<?> setHistory(Integer day) {
        return adminService.setHistory(day);
    }

    @RequestMapping("/work")
    public Result<?> setWork(boolean flag) {
        return adminService.setWork(flag);
    }

    @RequestMapping("/login/limit")
    public Result<?> clearLoginLimit() {
        return adminService.clearLoginLimit();
    }
}
