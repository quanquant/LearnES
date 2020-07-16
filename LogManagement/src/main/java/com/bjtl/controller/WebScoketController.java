package com.bjtl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description: webscoket控制层
 * @Author: leitianquan
 * @Date: 2020/07/11
 **/
@Controller
public class WebScoketController {
    @GetMapping("/")
    public String toIndex() {
        return "index";
    }
}
