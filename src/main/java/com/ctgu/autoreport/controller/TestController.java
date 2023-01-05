package com.ctgu.autoreport.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ctgu.autoreport.common.dto.EmailDTO;
import com.ctgu.autoreport.common.utils.AesUtils;
import com.ctgu.autoreport.common.vo.Result;
import com.ctgu.autoreport.dao.UserMapper;
import com.ctgu.autoreport.entity.User;
import com.ctgu.autoreport.service.common.MailService;
import com.ctgu.autoreport.service.common.RedisService;
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

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisService redisService;

    public static final String MAIL_R = "mail_r:";

    public static final String FLAG = "flag";

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

    @RequestMapping("/api/user/email")
    public String sendMail() {
        List<User> users = userMapper.selectList(null);
        for (User user : users) {
            Object o = redisService.get(MAIL_R + user.getUsername());
            if (o != null) {
                System.out.println(user.getUsername());
                continue;
            }
            try {
                mailService.sendMail(EmailDTO.builder()
                        .email(user.getEmail())
                        .subject("自动安全上报网址变更")
                        .content("你好！" + user.getUsername() + "</br>" +
                                "由于服务器已到期，目前自动安全上报网址发生变更，临时使用以下地址:</br>" +
                                "http://101.132.249.251:6633/ </br>" +
                                "这并不会持续太久，目前正在购买新的服务器，并且注册新域名，未来将使用" +
                                "<br>http://yiqing.studygether.com" +
                                "<br>作为本站访问地址")
                        .build());
                redisService.set(MAIL_R + user.getUsername(), FLAG);
                System.out.println(user.getUsername() + "已发送");
                Thread.sleep(5000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return "hello";
    }
}
