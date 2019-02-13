package zbl.fly.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zbl.fly.api.remote.ManagerService;
import zbl.fly.base.vos.AjaxResult;

import javax.inject.Inject;

@Api(value = "账号管理")
@RestController
@RequestMapping("/manager")
public class ManagerController {
    @Inject
    private ManagerService service;

    @ApiOperation(value = "修改密码", notes = "修改密码")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "id", dataType = "long", paramType = "form", required = true),
            @ApiImplicitParam(name = "oldPwd", value = "旧密码", dataType = "string", paramType = "form", required = true),
            @ApiImplicitParam(name = "newPwd", value = "新密码", dataType = "string", paramType = "form", required = true),
    })
    @RequestMapping("/modifyManagerPwd.do")
    public AjaxResult modifyManagerPwd(@RequestParam("id") long id,
                                       @RequestParam("oldPwd") String oldPwd,
                                       @RequestParam("newPwd") String newPwd) {
        service.modifyManagerPwd(id, oldPwd, newPwd);
        return AjaxResult.success();
    }
}
