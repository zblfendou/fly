package zbl.fly.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zbl.fly.api.remote.UserService;

import javax.inject.Inject;

@RestController
public class TestController {
    @Inject
    private UserService userService;

    @RequestMapping("/test.do")
    public String test() {
        System.out.println(userService);
        return "test dubbo ok";

    }
}
