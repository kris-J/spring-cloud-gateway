package org.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description: IndexController
 * date: 2021/10/8 11:07
 * author: fangjie24
 */
@RestController
//@RequestMapping("/index")
public class IndexController {

//    @RequestMapping("/index")
    public String index() {
        return "Hello index";
    }
}
