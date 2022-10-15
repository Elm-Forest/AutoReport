package com.ctgu.autoreport.controller;

import com.ctgu.autoreport.common.vo.Result;
import com.ctgu.autoreport.common.vo.UserVO;
import com.ctgu.autoreport.service.core.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Elm Forest
 * @date 14/10/2022 下午4:54
 */
@RestController
@RequestMapping("api")
public class RegisterController {
    @Autowired
    private RegisterService register;

    @PostMapping("register")
    public Result<?> register(@RequestBody UserVO user) {
        return register.register(user);
    }

    @DeleteMapping("delete")
    public Result<?> delete(UserVO user) {
        return register.delete(user);
    }
}
