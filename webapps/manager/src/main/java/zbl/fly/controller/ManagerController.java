package zbl.fly.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zbl.fly.api.remote.ManagerService;
import zbl.fly.base.vos.AjaxResult;

import javax.inject.Inject;

@RestController
@RequestMapping("/manager")
public class ManagerController {
    @Inject
    private ManagerService service;

    @RequestMapping("/modifyManagerPwd.do")
    public AjaxResult modifyManagerPwd(@RequestParam("id") long id,
                                       @RequestParam("oldPwd") String oldPwd,
                                       @RequestParam("newPwd") String newPwd) {
        service.modifyManagerPwd(id, oldPwd, newPwd);
        return AjaxResult.success();
    }
}
