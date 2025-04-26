package com.fourclover.clobee.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/user")
public class UserController {

    @GetMapping("/getUserEmail")
    public void getUserEmail() {
        System.out.println("getUserEmail");
    }
}
