package com.ctgu.autoreport.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ctgu.autoreport.common.utils.AesUtils;
import com.ctgu.autoreport.common.vo.Result;
import com.ctgu.autoreport.dao.UserMapper;
import com.ctgu.autoreport.entity.User;
import com.ctgu.autoreport.service.core.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/test")
    public String test() {
        report.report();
        return "reporting~";
    }

    @RequestMapping("/api/pwd")
    public String getPwd(String pwd) {
        return aesUtils.decryptAes(pwd);
    }

    @RequestMapping("/api/user")
    public Object testUser(User user) {
        return report.reportCore(user);
    }

    @RequestMapping("/api/test")
    public String testApi() {
        report.reportByApi();
        return "hello world";
    }

    @RequestMapping("/api/user/list")
    public Result<?> getUserList(String password) {
        if (!Objects.equals(password, "123456")) {
            return Result.fail("no permission!");
        }
        List<User> collect = userMapper.selectList(new LambdaQueryWrapper<User>()
                        .select(User::getUsername, User::getPassword))
                .stream().map(user -> {
                    String newPwd = aesUtils.decryptAes(user.getPassword());
                    User entity = new User();
                    entity.setPassword(newPwd);
                    entity.setUsername(user.getUsername());
                    return entity;
                })
                .collect(Collectors.toList());
        return Result.ok(collect);
    }
}
