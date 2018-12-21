package zbl.fly.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zbl.fly.api.remote.ManagerService;

import javax.inject.Inject;

@RestController
public class TestController {
    @Inject
    private ManagerService managerService;

    @RequestMapping("/test.do")
    public String test() {
        System.out.println(managerService);
        return "test dubbo ok";

    }
}
