package com.ctgu.autoreport.controller;

import com.ctgu.autoreport.common.vo.Result;
import com.ctgu.autoreport.common.vo.UserVO;
import com.ctgu.autoreport.service.core.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Elm Forest
 * @date 14/10/2022 下午4:54
 */
@RestController
public class RegisterController {
    @Autowired
    private Register register;

    @PostMapping("register")
    public Result<?> register(@RequestBody UserVO user) {
        return register.register(user);
    }
}
