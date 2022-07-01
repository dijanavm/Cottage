package com.isa.cottages.Controller;

import com.isa.cottages.Service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/profile")
public class ProfileController {

    private UserServiceImpl userService;

    @Autowired
    public ProfileController(UserServiceImpl userService){
        this.userService = userService;
    }
}
