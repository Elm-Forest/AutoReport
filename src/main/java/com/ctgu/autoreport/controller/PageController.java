package com.ctgu.autoreport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Elm Forest
 * @date 14/10/2022 下午8:03
 */
@Controller
public class PageController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
