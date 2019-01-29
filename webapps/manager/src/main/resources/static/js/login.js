$(document).ready(function () {
    $("#login").click(function (event) {
        var name = $("#username").val();
        var pwd = $("#password").val();
        if (name === "") {
            alert("用户名不能为空！");
        } else if (pwd === "") {
            alert("密码不能为空！");
        } else {
            var param = {};
            param.name = name;
            param.password = pwd;
            XW_GLOBAL.FUN.getData({
                url: "/ajaxlogin.do",
                param: param,
                callBackSuccess: function (msg) {
                    var status = msg.status,
                        data = msg.data;
                    if (status === 0) {
                        alert("登录成功");
                    } else {
                        alert("登录失败")
                    }
                }
            });
        }
    });

    $("#reset").click(function () {
        $("#username").val("");
        $("#password").val("");
    });
});