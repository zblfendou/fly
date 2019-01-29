XW_GLOBAL = {};
XW_GLOBAL.FUN = {};
XW_GLOBAL.FUN.getData = function (params) {
    var defaults = {
        url: "",
        contentType: "",
        dataType: "json",
        param: "",
        type:"POST",
        callBackSuccess: function () {
        },
        callbackError: function () {
        },
        //callBackFail: function () {},
        relogin: true

    };
    $.extend(defaults, params);

    if (defaults.contentType == "json") {
        var contentType = "application/json; charset=utf-8";
    } else if (defaults.contentType == "multipart") {
        var contentType = "multipart/form-data";
    } else {
        var contentType = "application/x-www-form-urlencoded; charset=UTF-8";
    }
    $.ajax({
        type: defaults.type,
        url: defaults.url,
        dataType: defaults.dataType,
        data: defaults.param,
        contentType: contentType,
        success: function (msg) {
            if (msg.status == -1) {

                if (defaults.relogin && typeof defaults.relogin != "function") {
                    XW_GLOBAL.FUN.xwBubbleTip.show({
                        msg: "登录超时，请重新登录",
                        icon: "<i class='font icon-font-cross'></i>",
                        coverShow: false,
                        times: 1500,
                        callback:function(){
                            location.href = "/index.html?url=" + encodeURIComponent(window.location.href);
                        }
                    });
                    // setTimeout(function () {
                    //     location.href = "/login.do?url=" + encodeURIComponent(window.location.href);
                    // }, 1000);
                } else if (typeof defaults.relogin == "function") {
                    defaults.relogin(msg);
                } else {

                }
            } else if (msg.status == -9) {
                location.href = "index.html";
            } else {
                if (defaults.callBackSuccess && typeof defaults.callBackSuccess == "function") {
                    defaults.callBackSuccess(msg);
                }
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            defaults.callbackError();//（只要报错  数据分析清掉遮罩）
            //console.log(XMLHttpRequest);
            //console.log(textStatus);
            //console.log(errorThrown);

            if (defaults.callBackFail && typeof defaults.callBackFail == "function") {
                defaults.callBackFail(XMLHttpRequest, textStatus, errorThrown);
            } else {
                XW_GLOBAL.FUN.xwBubbleTip.show({
                    msg: "出现小问题，请稍后再试",
                    icon: "<i class='font icon-font-cross'></i>",
                    coverShow: false,
                    times: 1500
                });
            }
        }
    });
};
/**
 * dialog信息弹框
 *
 * @param {object}      param               参数对象
 * @param {string}      param.url           页面地址
 * @param {string}      param.data          参数数据
 * @param {boolean}     param.coverShow     是否关闭遮罩层
 * @param {num}         param.closeTarget   关闭对象
 * @param {boolean}     param.closeCover    是否关闭遮罩层
 * @param {function}    param.callback      打开后回调方法
 *
 */























XW_GLOBAL.FUN.popDialog = {
    show: function (param) {
        // default param
        var params = {
            url: "",
            data: "",
            coverShow: true,
            closeTarget: 1,//0:全部窗口  1:本身
            closeCover: true,
            callbackEnd: function () {
            },
            callbackClickBtnClose: function () {
            }
        };
        // extend param
        $.extend(params, param);
        // create relate dom
        var $body = $("body"),
            $popBox = $('<div class="xwPopBox"></div>'),
            $popCover = $body.find(".xwPopCover");

        if (params.coverShow) {
            if (!$popCover.length) {
                var $popCover = $('<div class="xwPopCover" style="display: none"></div>');
                $body.append($popCover).append($popBox);
            } else {
                $body.append($popBox);
            }
        } else {
            $body.append($popBox);
        }
        $popBox.attr({
            "id": ("dialog" + new Date().getTime()),
            "data-closetarget": params.closeTarget,
            "data-closecover": params.closeCover
        });
        $popBox.load(params.url, params.data, params.callbackEnd);
        if (params.coverShow) {
            $popCover.show();
        }
        $popBox.show();
        $popBox.delegate('.btnClose,.btnNo', "click", function () {
            XW_GLOBAL.FUN.popDialog.close($(this).closest(".xwPopBox"), $(this));
            params.callbackClickBtnClose();
        });
        //禁止滚动方法所用 WinScrollEvent
        XW_GLOBAL.winScrollTop = $(document).scrollTop();
        $("body,html").css("overflow", "hidden");
    },
    close: function ($target, $clickTarget) {
        var $popBox = $(".xwPopBox"),
            $popCover = $(".xwPopCover"),
            closeCover = $target.attr("data-closecover");
        closeTarget = $target.attr("data-closetarget");
        if (typeof $clickTarget != "undefined" && ($clickTarget.hasClass("btnClose") || $clickTarget.hasClass("btnNo"))) {
            $popBox.remove();
            $popCover.hide();
        } else {
            if (closeTarget == 1) {
                if ($popBox.length == 1) {
                    $target.remove();
                    if (closeCover == "true") {
                        $popCover.hide();
                    }
                } else if ($target.length > 1) {
                    $target.remove();
                    $popCover.hide();
                } else if ($popBox.length > 1 && $target.length == 1) {
                    $target.remove();
                    if (closeCover == "true") {
                        $popCover.hide();
                    }
                }
            } else {
                $popBox.remove();
                $popCover.hide();
            }
        }
        $("body,html").removeAttr('style').css('display', 'block');
    }
};
/**
 * 模拟原生confirm信息弹框
 *
 * @param {object}   param                  参数对象
 * @param {string}   param.title            标题文字
 * @param {string}   param.info             说明文字
 * @param {boolean}  param.btnOkText        确定按钮文字
 * @param {boolean}  param.btnNoText        否定按钮文字
 * @param {string}   param.addClass         自定义样式类
 * @param {boolean}  param.coverShow        显示遮罩
 * @param {boolean}  param.closeCover       关闭遮罩
 * @param {num}      param.closeTarget      弹框关闭对象
 * @param {function} param.callbackYes      确定触发后回调方法
 * @param {function} param.callbackNo       否定触发后回调方法
 * @param {function} param.callbackEnd      弹框显示后回调方法
 *
 */
XW_GLOBAL.FUN.popConfirm = {
    show: function (param) {
        // default param
        var params = {
            title: "提示",
            info: "",
            btnOkText: "是",
            btnNoText: "否",
            btnOkTextDisplay: true,
            btnNoTextDisplay: true,
            btnCloseDisplay: true,
            addClass: "",
            coverShow: true,
            closeCover: true,
            closeTarget: 1,//0:全部窗口  1:本身
            callbackYes: function () {
            },
            callbackNo: function () {
            },
            callbackClose: function () {
            },
            callbackEnd: function () {
            }
        };
        // extend param
        $.extend(params, param);
        // create relate dom
        var $body = $("body"),
            $popCover = $body.find(".xwPopCover"),
            popBox = "";
        popBox += '<div class="xwPopBox">';
        popBox += '    <div class="xwDialog xwDialogConfirm ' + params.addClass + '">';
        if (params.btnCloseDisplay) {
            popBox += '        <a class="btnClose" href="javascript:">×</a>';
        }
        popBox += '        <h2 class="dialogT">' + params.title + '</h2>';
        popBox += '        <div class="dialogC">';
        popBox += '            <div class="info">' + params.info + '</div>';
        popBox += '        </div>';
        popBox += '        <div class="dialogB">';
        popBox += '            <div class="cont">';
        if (params.btnNoTextDisplay) {
            popBox += '                <a href="javascript:;" class="xwBtn btnWhite btnNo">' + params.btnNoText + '</a>';
        }
        if (params.btnOkTextDisplay) {
            popBox += '                <a href="javascript:;" class="xwBtn btnRed btnYes"><span class="text">' + params.btnOkText + '</span><span class="ing"></span></a>';
        }
        popBox += '            </div>';
        popBox += '        </div>';
        popBox += '    </div>';
        popBox += '</div>';
        $popBox = $(popBox);

        $popBox.attr({
            "id": ("confirm" + new Date().getTime()),
            "data-closetarget": params.closeTarget,
            "data-closecover": params.closeCover
        });

        if (params.coverShow) {
            if (!$popCover.length) {
                var $popCover = $('<div class="xwPopCover" style="display: none"></div>');
                $body.append($popCover).append($popBox);
            } else {
                $body.append($popBox);
            }
        } else {
            $body.append($popBox);
        }
        if (params.coverShow) {
            $popCover.show();
        }
        $popBox.show();
        // "btnYes" click event
        $popBox.delegate(".btnYes", "click", function () {
            $(this).addClass("xwLoading");
            params.callbackYes($(this).closest(".xwPopBox"));
        });
        // "popNo" click event
        $popBox.delegate(".btnNo", "click", function () {
            params.callbackNo($(this).closest(".xwPopBox"));
        });
        // "popClose" click event
        $popBox.delegate(".btnClose", "click", function () {
            XW_GLOBAL.FUN.popConfirm.close($(this).closest(".xwPopBox"));
            params.callbackClose();
        });
        // callbackEnd
        params.callbackEnd($popBox);
        XW_GLOBAL.winScrollTop = $(document).scrollTop();//禁止滚动方法所用 WinScrollEvent
        $("body,html").css("overflow","hidden");
    },
    close: function ($target) {
        var $popBox = $(".xwPopBox"),
            $popCover = $(".xwPopCover"),
            closeCover = $target.attr("data-closecover");
        closeTarget = $target.attr("data-closetarget");

        if (closeTarget == 1) {
            if ($popBox.length == 1) {
                $target.remove();
                if (closeCover == "true") {
                    $popCover.hide();
                }
            } else if ($target.length > 1) {
                $target.remove();
                $popCover.hide();
            } else if ($popBox.length > 1 && $target.length == 1) {
                $target.remove();
                if (closeCover == "true") {
                    $popCover.hide();
                }
            }
        } else {
            $popBox.remove();
            $popCover.hide();
        }
        $("body,html").removeAttr('style');
    }
};

/**
 * 冒泡提示信息
 *
 * @param {object}   param          参数对象
 * @param {string}   param.msg      提示信息文字
 * @param {string}   param.icon     显示的icon图标“class”名称
 * @param {number}   param.times    自动关闭时间
 * @param {function} param.callback 关闭后执行的回调方法
 *
 */
XW_GLOBAL.FUN.xwBubbleTip = {
    show: function (param) {
        // default param
        var params = {
            icon: "",
            msg: "",
            coverShow: false,
            times: 1000
        };
        $.extend(params, param);
        if (!params.msg) {
            return false;
        }
        // create relate dom
        var $body = $("body"),
            $popCover = $body.find(".xwPopCover"),
            pop = "";
        pop += '    <div class="xwBubbleTip">';
        pop += params.icon;
        pop += '      <span class="text">' + params.msg + '</span>';
        pop += '    </div>';
        $pop = $(pop);

        if (params.coverShow) {
            if (!$popCover.length) {
                var $popCover = $('<div class="xwPopCover"></div>');
                $body.append($popCover);
            }
        }
        $body.append($pop);

        $pop.css({
            "margin-top": "-" + ($pop.height() / 2) + "px",
            "margin-left": "-" + ($pop.width() / 2) + "px"
        }).css("display", "inline-block");
        XW_GLOBAL.winScrollTop = $(document).scrollTop();//禁止滚动方法所用 WinScrollEvent
        // close event
        XW_GLOBAL.FUN.xwBubbleTip.close(params);
    },
    close: function (params) {
        setTimeout(function () {
            $(".xwBubbleTip:not(.disabled)").remove();
            // default callback
            if (params.callback && typeof params.callback == "function") {
                params.callback($pop);
            }
        }, params.times)
    }
};

XW_GLOBAL.FUN.getVeriCode = {
    params: {
        seconds: 60,
        param: {},
        $target: ""
    },
    get: function (param) {
        XW_GLOBAL.FUN.getVeriCode.params.seconds = 60;
        $.extend(XW_GLOBAL.FUN.getVeriCode.params, param);
        XW_GLOBAL.FUN.getVeriCode.params.$target.addClass("xwLoading");
        XW_GLOBAL.FUN.getData({
            url: XW_GLOBAL.FUN.getVeriCode.params.url,
            param: XW_GLOBAL.FUN.getVeriCode.params.param,
            callBackSuccess: function (msg) {
                var status = msg.status,
                    data = msg.data;
                if (status == 0) {
                    XW_GLOBAL.FUN.getVeriCode.params.$target.addClass("disabled");
                    XW_GLOBAL.FUN.getVeriCode.params.$target.siblings("input").removeAttr("disabled");
                    XW_GLOBAL.FUN.getVeriCode.timedown();
                    XW_GLOBAL.FUN.getVeriCode.params.$target.find(".text").text(XW_GLOBAL.FUN.getVeriCode.params.seconds-- + "秒");
                } else {
                    XW_GLOBAL.FUN.xwBubbleTip.show({
                        msg: "加载异常",
                        icon: "<i class='font font-cross'>&#xe620;</i>",
                        times: 500,
                        coverShow: false
                    });
                    XW_GLOBAL.FUN.getVeriCode.params.$target.removeClass("xwLoading");
                }
                XW_GLOBAL.FUN.getVeriCode.params.$target.removeClass("xwLoading");
            }
        });
    },
    timedown: function () {
        if (typeof XW_GLOBAL.FUN.getVeriCode.timeid != "undefined") {
            clearInterval(XW_GLOBAL.FUN.getVeriCode.timeid);
        }
        XW_GLOBAL.FUN.getVeriCode.timeid = setInterval(function () {
            if (XW_GLOBAL.FUN.getVeriCode.params.seconds == 0) {
                clearInterval(XW_GLOBAL.FUN.getVeriCode.timeid);
                XW_GLOBAL.FUN.getVeriCode.params.$target.removeClass("disabled");
                XW_GLOBAL.FUN.getVeriCode.params.$target.removeClass("xwLoading");
                XW_GLOBAL.FUN.getVeriCode.params.$target.find(".text").text("重新获取");
                if (typeof XW_GLOBAL.FUN.getVeriCode.params.callbackEnd != "undefined" && typeof XW_GLOBAL.FUN.getVeriCode.params.callbackEnd == "function") {
                    XW_GLOBAL.FUN.getVeriCode.params.callbackEnd();
                }
                //XW_GLOBAL.FUN.getVeriCode.params.clear($this,params.callbackEnd)
            } else {
                XW_GLOBAL.FUN.getVeriCode.params.$target.find(".text").text(XW_GLOBAL.FUN.getVeriCode.params.seconds + "秒");
                if (XW_GLOBAL.FUN.getVeriCode.params.$target.siblings(".smsVeriCode").attr("disabled") == "disabled") {
                    XW_GLOBAL.FUN.getVeriCode.params.$target.siblings(".smsVeriCode").removeAttr("disabled");
                }
                XW_GLOBAL.FUN.getVeriCode.params.seconds--;
            }
        }, 1000);
    },
    clear: function (callback) {
        if (typeof XW_GLOBAL.FUN.getVeriCode.timeid != "undefined") {
            clearInterval(XW_GLOBAL.FUN.getVeriCode.timeid);
            XW_GLOBAL.FUN.getVeriCode.params.$target.removeClass("disabled");
            XW_GLOBAL.FUN.getVeriCode.params.$target.removeClass("xwLoading");
            XW_GLOBAL.FUN.getVeriCode.params.$target.find(".text").text("获取验证码");
            if (typeof callback != "undefined" && typeof callback == "function") {
                callback();
            }
        }
    }
};
//短信验证码
XW_GLOBAL.FUN.xwTimeOutDown = function (param) {
    var params = {
        seconds: 60,
        $target: "",
        textAfter: "",
        textBefore: "",
        callbackEnd: function () {
        }
    };
    $.extend(params, param);

    if (params.seconds == 0) {
        params.$target.text(params.textBefore + params.seconds + params.textAfter);
        params.callbackEnd();
        clearTimeout(window[params.$target.attr("data-timesid")]);
    } else {
        if (!params.$target.attr("data-timesid")) {
            params.$target.attr("data-timesid", ("td_" + new Date().getTime()));
        }
        params.$target.text(params.textBefore + params.seconds + params.textAfter);
        params.seconds--;
        var param = {
            seconds: params.seconds,
            $target: params.$target,
            textAfter: params.textAfter,
            textBefore: params.textBefore,
            callbackEnd: params.callbackEnd
        };
        window[params.$target.attr("data-timesid")] = setTimeout(function () {
            XW_GLOBAL.FUN.xwTimeOutDown(param)
        }, 1000);
    }
};
//登录密码
//1、密码必须由数字、字符、特殊字符三种中的两种组成；
//2、密码长度不能少于8个字符；
XW_GLOBAL.FUN.checkPwd = function (val) {
    var reg = /^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?![,\.#%'\+\*\-:;^_`]+$)[,\.#%'\+\*\-:;^_`0-9A-Za-z]{8,20}$/;
    return reg.test(val);
};
/* zcCheckTel */
XW_GLOBAL.FUN.checkTel = function (tel) {
    var reg = !!tel.match(/^(0|86|17951)?(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/);
    return reg;
};
/* zcCheckMail */
XW_GLOBAL.FUN.checkMail = function (mail) {
    var reg = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
    return reg.test(mail);
};
/* 获取地址栏参数值 */
XW_GLOBAL.FUN.getUrlParam = function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    //var r = window.location.search.substr(1).match(reg);
    var r = decodeURIComponent(window.location.search).substr(1).match(reg);
    if (r != null)return unescape(r[2]);
    return "";
};
//判断值是否为空,空(false)不为空(true)
XW_GLOBAL.FUN.isInvalid = function (val) {
    var exp = val;
    return exp == null || typeof(exp) == "undefined" || $.trim(exp).length == 0 || $.trim(exp) == "";
};
// 固定电话正则
XW_GLOBAL.FUN.regularFixedTel = function (tel) {
    var reg = tel.match(/^0\d{2,3}-?\d{7,8}$/);
    return reg;
};
// 查找数字
XW_GLOBAL.FUN.checkNumber = function (str) {
    var pattern = new RegExp(/\d+/);
    var reg = pattern.test(str);
    console.log(reg);
    return reg;
};
// 查找汉字
XW_GLOBAL.FUN.checkChinese = function (str) {
    var pattern = new RegExp("[\u4e00-\u9fa5]");
    var reg = pattern.test(str);
    console.log(reg);
    return reg;
};
//整数
XW_GLOBAL.FUN.checkIntegerNumber = function (str) {
    var pattern = new RegExp(/^-?\d+$/);
    var reg = pattern.test(str);
    return reg;
}
// 查找字母
XW_GLOBAL.FUN.checkEnglish = function (str) {
    var pattern = new RegExp("[A-Za-z]");
    var reg = pattern.test(str);
    console.log(reg);
    return reg;
};
// 2位大写字母母
XW_GLOBAL.FUN.checkBigTwoEnglish = function (str) {
    var pattern = new RegExp("[A-Z]");
    var reg = pattern.test(str);
    console.log(reg);
    return reg;
};
// 查找字符
XW_GLOBAL.FUN.checkSpecialChar = function (str) {
    var pattern = new RegExp("[`~!@#$^&*()_=|':;,\\[\\]\\+\\%\\-.<>/?~！￥……（）—{}【】‘；：”“。，、？]");
    var reg = pattern.test(str);
    console.log(reg);
    return reg;
};
// 检查色值
XW_GLOBAL.FUN.checkColorVal = function (str) {
    var pattern = new RegExp("^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$");
    var reg = pattern.test(str);
    return reg;
};
// 查找 非数字/字母/汉子 的字符
// 查找]用\\]
XW_GLOBAL.FUN.checkCharacter = function (str, strExtra) {
    var pattern = new RegExp("^[\u4e00-\u9fa5A-Za-z0-9" + strExtra + "]*$");
    var reg = pattern.test(str);
    console.log(!reg);
    return !reg;
};
// 身份证校验
XW_GLOBAL.FUN.checkCardId = function (socialNo) {
    if (socialNo == "") {
        //alert("输入身份证号码不能为空!");
        return (false);
    }
    //if (socialNo.length != 15 && socialNo.length != 18)
    if (socialNo.length != 18) {
        //alert("输入身份证号码格式不正确!");
        return (false);
    }

    var area = {
        11: "北京",
        12: "天津",
        13: "河北",
        14: "山西",
        15: "内蒙古",
        21: "辽宁",
        22: "吉林",
        23: "黑龙江",
        31: "上海",
        32: "江苏",
        33: "浙江",
        34: "安徽",
        35: "福建",
        36: "江西",
        37: "山东",
        41: "河南",
        42: "湖北",
        43: "湖南",
        44: "广东",
        45: "广西",
        46: "海南",
        50: "重庆",
        51: "四川",
        52: "贵州",
        53: "云南",
        54: "西藏",
        61: "陕西",
        62: "甘肃",
        63: "青海",
        64: "宁夏",
        65: "新疆",
        71: "台湾",
        81: "香港",
        82: "澳门",
        91: "国外"
    };

    if (area[parseInt(socialNo.substr(0, 2))] == null) {
        //alert("身份证号码不正确(地区非法)!");
        return (false);
    }

    if (socialNo.length == 15) {
        pattern = /^\d{15}$/;
        if (pattern.exec(socialNo) == null) {
            //alert("15位身份证号码必须为数字！");
            return (false);
        }
        var birth = parseInt("19" + socialNo.substr(6, 2));
        var month = socialNo.substr(8, 2);
        var day = parseInt(socialNo.substr(10, 2));
        switch (month) {
            case '01':
            case '03':
            case '05':
            case '07':
            case '08':
            case '10':
            case '12':
                if (day > 31) {
                    //alert('输入身份证号码不格式正确!');
                    return false;
                }
                break;
            case '04':
            case '06':
            case '09':
            case '11':
                if (day > 30) {
                    //alert('输入身份证号码不格式正确!');
                    return false;
                }
                break;
            case '02':
                if ((birth % 4 == 0 && birth % 100 != 0) || birth % 400 == 0) {
                    if (day > 29) {
                        //alert('输入身份证号码不格式正确!');
                        return false;
                    }
                } else {
                    if (day > 28) {
                        //alert('输入身份证号码不格式正确!');
                        return false;
                    }
                }
                break;
            default:
                //alert('输入身份证号码不格式正确!');
                return false;
        }
        var nowYear = new Date().getYear();
        if (nowYear - parseInt(birth) < 15 || nowYear - parseInt(birth) > 100) {
            //alert('输入身份证号码不格式正确!');
            return false;
        }
        return (true);
    }

    var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6,
        3, 7, 9, 10, 5, 8, 4, 2, 1];
    var lSum = 0;
    var nNum = 0;
    var nCheckSum = 0;

    for (i = 0; i < 17; ++i) {


        if (socialNo.charAt(i) < '0' || socialNo.charAt(i) > '9') {
            //alert("输入身份证号码格式不正确!");
            return (false);
        }
        else {
            nNum = socialNo.charAt(i) - '0';
        }
        lSum += nNum * Wi[i];
    }


    if (socialNo.charAt(17) == 'X' || socialNo.charAt(17) == 'x') {
        lSum += 10 * Wi[17];
    }
    else if (socialNo.charAt(17) < '0' || socialNo.charAt(17) > '9') {
        //alert("输入身份证号码格式不正确!");
        return (false);
    }
    else {
        lSum += ( socialNo.charAt(17) - '0' ) * Wi[17];
    }


    if ((lSum % 11) == 1) {
        return true;
    }
    else {
        //alert("输入身份证号码格式不正确!");
        return (false);
    }

};

// 银行卡校验
XW_GLOBAL.FUN.checkBankNum = function (bankno) {
    var lastNum = bankno.substr(bankno.length - 1, 1);//取出最后一位（与luhm进行比较）

    var first15Num = bankno.substr(0, bankno.length - 1);//前15或18位
    var newArr = [];
    for (var i = first15Num.length - 1; i > -1; i--) {    //前15或18位倒序存进数组
        newArr.push(first15Num.substr(i, 1));
    }
    var arrJiShu = [];  //奇数位*2的积 <9
    var arrJiShu2 = []; //奇数位*2的积 >9

    var arrOuShu = [];  //偶数位数组
    for (var j = 0; j < newArr.length; j++) {
        if ((j + 1) % 2 == 1) {//奇数位
            if (parseInt(newArr[j]) * 2 < 9)
                arrJiShu.push(parseInt(newArr[j]) * 2);
            else
                arrJiShu2.push(parseInt(newArr[j]) * 2);
        }
        else //偶数位
            arrOuShu.push(newArr[j]);
    }

    var jishu_child1 = [];//奇数位*2 >9 的分割之后的数组个位数
    var jishu_child2 = [];//奇数位*2 >9 的分割之后的数组十位数
    for (var h = 0; h < arrJiShu2.length; h++) {
        jishu_child1.push(parseInt(arrJiShu2[h]) % 10);
        jishu_child2.push(parseInt(arrJiShu2[h]) / 10);
    }

    var sumJiShu = 0; //奇数位*2 < 9 的数组之和
    var sumOuShu = 0; //偶数位数组之和
    var sumJiShuChild1 = 0; //奇数位*2 >9 的分割之后的数组个位数之和
    var sumJiShuChild2 = 0; //奇数位*2 >9 的分割之后的数组十位数之和
    var sumTotal = 0;
    for (var m = 0; m < arrJiShu.length; m++) {
        sumJiShu = sumJiShu + parseInt(arrJiShu[m]);
    }

    for (var n = 0; n < arrOuShu.length; n++) {
        sumOuShu = sumOuShu + parseInt(arrOuShu[n]);
    }

    for (var p = 0; p < jishu_child1.length; p++) {
        sumJiShuChild1 = sumJiShuChild1 + parseInt(jishu_child1[p]);
        sumJiShuChild2 = sumJiShuChild2 + parseInt(jishu_child2[p]);
    }
    //计算总和
    sumTotal = parseInt(sumJiShu) + parseInt(sumOuShu) + parseInt(sumJiShuChild1) + parseInt(sumJiShuChild2);

    //计算Luhm值
    var k = parseInt(sumTotal) % 10 == 0 ? 10 : parseInt(sumTotal) % 10;
    var luhm = 10 - k;

    if (lastNum == luhm) {

        return true;
    }
    else {

        return false;
    }

};
// 长度计算，中文一个字符长度，英文0.5个字符长度
XW_GLOBAL.FUN.cnlength = function (val) {
    return val.replace(/[^\x00-\xff]/g, 'xx').length / 2.0
};


XW_GLOBAL.FUN.xwSelect = function (param) {
    $("body").delegate(".xwSelect:not(.disabled)", "click", function (event) {
        //alert("sdfsdf")
        var $this = $(this),
            types = $(this).attr('data-type'),
            $selected = $this.find('.selected');
        thisWidth = $this.width();
        /* radio:单选;checkbox:复选*/
        if ($selected.text() != '') {
            $this.find('.selectLabel').html($selected.text()).removeClass('defaultColor');
            $this.find('.inputSelect').val($selected.attr('selectid'));
        } else {
            //$this.find('.selectLabel').html('请选择').addClass('defaultColor');
            //$this.find('.inputSelect').val('');
        }
        var ul = $this.find('ul');
        //alert(ul.is(":visible"))
        if (!ul.is(":visible")) {
            $(".xwSelect ul").hide();
            //console.log("show")
            ul.show();
        } else {
            $(".xwSelect ul").hide();
            //console.log("hide")
            ul.hide();
        }
        //event.stopPropagation();    //  阻止事件冒泡   
    });
    $("body").delegate('.xwSelect ul', "click", function (event) {
        event.stopPropagation();
    });
    $("body").delegate('.xwSelect li:not(.disabled)', "click", function (event) {
        var txt, value;
        var $this = $(this),
            $xwSelect = $this.closest(".xwSelect"),
            types = $xwSelect.attr('data-type');
        if (types == 'radio') {
            txt = $this.text();
            $this.addClass('selected').siblings('li').removeClass('selected');
            $xwSelect.find('.selectLabel').html(txt);
            value = $this.attr('selectid');
            $xwSelect.find('.inputSelect').attr('value', value);
            $xwSelect.find('ul').hide();
            if (value != '0') {
                $xwSelect.removeClass("error").find('.selectLabel').removeClass('defaultColor');
                $xwSelect.siblings('.verifyMsg').find(".msg").text("");
            } else {
                $xwSelect.find('.selectLabel').addClass('defaultColor');
            }
        } else {
            var tLst = '',
                valLst = '';
            $this.removeClass('selected');
            $xwSelect.find('input[type="checkbox"]:checked').each(function () {
                txt = $this.parent().text();
                value = $this.parents('li').attr('selectid');
                $this.parents('li').addClass('selected');
                tLst += txt + ',';
                valLst += value + ',';
            });
            $xwSelect.find('.selectLabel').html(tLst);
            $xwSelect.find('.inputSelect').attr('value', valLst);
            if (value != '0') {
                $xwSelect.removeClass("error").find('.selectLabel').removeClass('defaultColor');
            } else {
                $xwSelect.find('.selectLabel').addClass('defaultColor');
            }
        }
        event.stopPropagation();
    });
    $(document).click(function (e) {
        var $target = $(e.target);
        if (!(($target.hasClass("xwSelect") || $target.closest(".xwSelect").length))) {
            $(".xwSelect ul").hide();
        }
        e.stopPropagation();
    });
};
// 问题栏目 点击 预览大图
XW_GLOBAL.FUN.xwImgPreview = function (data, idx, appendTarget, callback) {
    var len = data.length,
        html = "";
    if (!$("#xwImgPreview").length) {
        html += '<div class="xwImgPreview" id="xwImgPreview">';
        html += '            <div class="hd">';
        html += '                <ul class="list">';
        html += '                </ul>';
        html += '                <span class="pageState"></span>';
        html += '            </div> ';
        html += '    <span class="close">×</span>';
        html += '    <a href="javascript:;" class="btn prev"><</a>';
        html += '    <div class="bd">';
        html += '        <ul class="list">';
        html += '        </ul>';
        html += '    </div>';
        html += '    <a href="javascript:;" class="btn next">></a>';
        html += '</div>';

        if (typeof appendTarget == "undefined") {
            $("body").append(html);
        } else {
            $(appendTarget).eq(0).append(html);
        }
        if (callback && typeof callback == "function") {
            callback();
        }
    }
    html = "";
    for (var i = 0; i < len; i++) {
        html += '<li class="imgWrap"><span data-x="0" data-y="0" class="drag"><img draggable="false" src="' + data[i] + '" /><div class="imgcover"></div></span></li>';
    }
    $("#xwImgPreview").find(".bd .list").empty().html(html);
    $("html,body").addClass("overflowHidden");
    $("#xwImgPreview").find(".bd,.imgWrap").width($(window).width()).height($(window).height()).css("line-height", $(window).height() + "px");
    $("#xwImgPreview").slide({
        titCell: ".hd ul li",
        mainCell: ".bd .list",
        autoPage: false,
        defaultIndex: idx,
        effect: "left",
        scroll: 1,
        vis: 1,
        pnLoop: false,
        trigger: "click"
    });
    $("#xwImgPreview").fadeIn(200);
    $("#xwImgPreview").find(".close").click(function () {
        $("#xwImgPreview").fadeOut(200);
        setTimeout(function () {
            $("html,body").removeClass("overflowHidden");
        }, 100);
        setTimeout(function () {
            $("#xwImgPreview").remove();
        }, 200);
    });
    // 鼠标mousedown
    $("#xwImgPreview").delegate(".drag", "mousedown", function (e) {
        var $this = $(this),
            dom = $this.parent().get(0);
        $this.addClass("cursorMouseKeydown");
        // $this.attr("data-move",false);
        $this.attr("data-down", true);
        $this.attr("data-x", e.clientX);
        $this.attr("data-y", e.clientY);
        $this.attr("data-top", dom.scrollTop);
        $this.attr("data-left", dom.scrollLeft);
        console.log("data-x=" + e.clientX + "data-y=" + e.clientY + "data-top=" + dom.scrollTop + "data-left=" + dom.scrollLeft)
    });
    // 鼠标mouseup
    $("#xwImgPreview").delegate(".drag", "mouseup", function (e) {
        var $this = $(this);
        $this.removeClass("cursorMouseKeydown").attr("data-down", false);
        e && e.preventDefault();
    });
    // 鼠标mousemove
    $("#xwImgPreview").delegate(".drag", "mousemove", function (e) {
        e && e.preventDefault();

        var $this = $(this);
        if ($this.attr("data-down") == "true") {
            var x = $this.attr("data-x") - e.clientX,
                y = $this.attr("data-y") - e.clientY,
                dom = $this.parent().get(0);
            dom.scrollLeft = (parseInt($this.attr("data-left")) + x);
            dom.scrollTop = (parseInt($this.attr("data-top")) + y);
        }
    });
};
// 文本/域字数限制
XW_GLOBAL.FUN.inputLimitFun = function () {
    $("body").delegate(".xwInputLimit input,.xwInputLimit textarea", "focusin focusout keyup paste", function (e) {
        var $thisInput = $(this),
            minsize = $thisInput.attr("data-minsize"),
            maxsize = $thisInput.attr("data-maxsize"),
            maxsizecan = $thisInput.attr("data-maxsizecan"),
            $output = $thisInput.siblings(".outputInfo");

        if (e.type == "focusin") {
            var len = $thisInput.val().length;
            $output.find("i").text(len);
            // if( (len < minsize && len!=0) || len > maxsize ){
            //     $output.addClass("error");
            //     $thisInput.addClass("error");
            // };
            $output.removeClass("error");
        } else if (e.type == "keyup" || e.type == "focusout" || e.type == "paste") {
            var val = $thisInput.val(),
                len = val.length;
            if(e.type == "paste"){
                setTimeout(function(){
                    // 可以超出
                    if (typeof maxsizecan != "undefined" && maxsizecan == "true") {
                        if (len < minsize || len > maxsize) {
                            if (len == 0) {
                                $output.removeClass("error");
                                $thisInput.removeClass("error");
                            } else {
                                $output.addClass("error");
                                $thisInput.addClass("error");
                            }
                        } else {
                            $output.removeClass("error");
                            $thisInput.removeClass("error");
                        }
                    } else {
                        if (len < minsize && len != 0) {
                            $output.addClass("error");
                            $thisInput.addClass("error");
                        } else if (len >= minsize) {
                            $output.removeClass("error");
                            $thisInput.removeClass("error");
                            if (len > maxsize) {
                                var trimmedtext = val.substring(0, maxsize);
                                $thisInput.val(trimmedtext);
                            }
                        }
                    }
                    $output.find("i").text($thisInput.val().length);
                },100);
            }else{
                // 可以超出
                if (typeof maxsizecan != "undefined" && maxsizecan == "true") {
                    if (len < minsize || len > maxsize) {
                        if (len == 0) {
                            $output.removeClass("error");
                            $thisInput.removeClass("error");
                        } else {
                            $output.addClass("error");
                            $thisInput.addClass("error");
                        }
                    } else {
                        $output.removeClass("error");
                        $thisInput.removeClass("error");
                    }
                } else {
                    if (len < minsize && len != 0) {
                        $output.addClass("error");
                        $thisInput.addClass("error");
                    } else if (len >= minsize) {
                        $output.removeClass("error");
                        $thisInput.removeClass("error");
                        if (len > maxsize) {
                            var trimmedtext = val.substring(0, maxsize);
                            $thisInput.val(trimmedtext);
                        }
                    }
                }
                $output.find("i").text($thisInput.val().length);
            };

        }
    });
};
// 获取json 对象 长度
XW_GLOBAL.FUN.getJsonLen = function (jsonObj) {
    var Length = 0;
    for (var item in jsonObj) {
        Length++;
    }
    return Length;
};

// 判断 日期 是否 是今天
XW_GLOBAL.FUN.isToday = function (date) {
    var today = new Date().toLocaleDateString();
    var date = new Date(date).toLocaleDateString();
    return today == date;
};
// 获取日期的星期
XW_GLOBAL.FUN.getWeekText = function (date) {
    var weekText = ["星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"];
    //alert(new Date('2016-02-03').getDay());
    return weekText[new Date(date).getDay() - 1];
};
//判断是否为JSON对象
XW_GLOBAL.FUN.isJson = function (obj) {
    var isjson = typeof(obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length;
    return isjson;
};
// 浏览器版本提示
XW_GLOBAL.FUN.browserVerIE = function (obj) {
    var useragent = window.navigator.userAgent.toLowerCase();
    var msie10 = $.browser.msie && /msie 10\.0/i.test(useragent);
    var msie9 = $.browser.msie && /msie 9\.0/i.test(useragent);
    var msie8 = $.browser.msie && /msie 8\.0/i.test(useragent);
    var msie7 = $.browser.msie && /msie 7\.0/i.test(useragent);
    var msie6 = !msie8 && !msie7 && $.browser.msie && /msie 6\.0/i.test(useragent);
    return {
        msie10: msie10,
        msie9: msie9,
        msie8: msie8,
        msie7: msie7,
        msie6: msie6
    }
};
// 滚动到指定位置
XW_GLOBAL.FUN.winScrollTo = function (param) {
    if (typeof param == "string") {
        var scrollTo = $(param).offset().top;
    } else if (typeof param == "number") {
        var scrollTo = param;
    }
    $("html,body").animate({scrollTop: scrollTo}, 300);
};
XW_GLOBAL.FUN.isImgType = function (fileType) {
    var reg = /(\.|\/)(jpe?g|png|gif)$/i;
    return reg.test(fileType);
};
// 间隔时间计算
XW_GLOBAL.FUN.getDateDiff = function (startDate, endDate) {
    var startTime = startDate;
    var endTime = new Date(endDate).getTime();
    var days = (endTime - startTime) / (1000 * 60 * 60 * 24);
    return Math.ceil(days);
};
// 判断是否为整数类型
XW_GLOBAL.FUN.isInteger = function(obj) {
 return parseInt(obj, 10) == obj
}
XW_GLOBAL.FUN.xwTimeSelect=function(){
    $("body").delegate('.xwTimeSelect input', 'click', function(event) {
        $input = $(this),
        $xwTimeSelect = $input.closest('.xwTimeSelect'),
        $timeList = $xwTimeSelect.find(".timeList"),
        $timeHour = $xwTimeSelect.find(".timeHour"),
        $timeMins = $xwTimeSelect.find(".timeMins"),
        minHour = parseInt($xwTimeSelect.attr("data-minhour")),
        minMins = parseInt($xwTimeSelect.attr("data-minmins")),
        maxHour = parseInt($xwTimeSelect.attr("data-maxhour")),
        maxMins = parseInt($xwTimeSelect.attr("data-maxmins")),
        curHour = parseInt($xwTimeSelect.attr("data-curhour")),
        curMins = parseInt($xwTimeSelect.attr("data-curmins"));


        if(!$timeList.is("visible")){
            $(".xwTimeSelect .timeList").hide();
            $timeList.fadeIn(100);
            $xwTimeSelect.find("li").removeClass("disabled");
        };
        resetTimeSelect($xwTimeSelect);
    });
    $("body").delegate('.xwTimeSelect .timeHour li:not(.disabled)', 'click', function(event) {
        var $this = $(this),
            $xwTimeSelect = $this.closest('.xwTimeSelect'),
            $curMins = $xwTimeSelect.attr("data-curmins"),
            $input = $xwTimeSelect.find("input"),
            maxTarget = $xwTimeSelect.attr("data-maxtarget"),
            minTarget = $xwTimeSelect.attr("data-mintarget");

        $xwTimeSelect.attr("data-curhour",$this.attr("data-num"));
        $this.addClass('on').siblings().removeClass("on");
        if(typeof $curMins!="undefined" && $curMins!="" && !isNaN($curMins)){
            $input.val($this.text()+":"+($curMins.length==1?("0"+$curMins):$curMins));
        };
        if(typeof maxTarget!="undefined"){
            $(maxTarget).attr("data-minhour",$this.text());
            resetTimeSelect($xwTimeSelect);
        };
        if(typeof minTarget!="undefined"){
            $(minTarget).attr("data-maxhour",$this.text());
            resetTimeSelect($xwTimeSelect);
        };

    });
    $("body").delegate('.xwTimeSelect .timeMins li:not(.disabled)', 'click', function(event) {
        var $this = $(this),
            $xwTimeSelect = $this.closest('.xwTimeSelect'),
            $curHour = $xwTimeSelect.attr("data-curhour"),
            $input = $xwTimeSelect.find("input"),
            maxTarget = $xwTimeSelect.attr("data-maxtarget"),
            minTarget = $xwTimeSelect.attr("data-mintarget");

        $xwTimeSelect.attr("data-curmins",$this.attr("data-num"));
        $this.addClass('on').siblings().removeClass("on");
        if(typeof $curHour!="undefined" && $curHour!="" && !isNaN($curHour)){
            $input.val(($curHour.length==1?("0"+$curHour):$curHour)+":"+$this.text());
        };
        if(typeof maxTarget!="undefined"){
            $(maxTarget).attr("data-minmins",$this.text());
        };
        if(typeof minTarget!="undefined"){
            $(minTarget).attr("data-maxmins",$this.text());
        };
    });
    $(document).click(function (e) {
        var $target = $(e.target);
        if (!(($target.hasClass("xwTimeSelect") || $target.closest(".xwTimeSelect").length))) {
            $(".xwTimeSelect .timeList").hide();
        }
        e.stopPropagation();
    });
    function resetTimeSelect($target){
        $timeList = $target.find(".timeList"),
        $timeHour = $target.find(".timeHour"),
        $timeMins = $target.find(".timeMins"),
        minHour = parseInt($target.attr("data-minhour")),
        minMins = parseInt($target.attr("data-minmins")),
        maxHour = parseInt($target.attr("data-maxhour")),
        maxMins = parseInt($target.attr("data-maxmins")),
        curHour = parseInt($target.attr("data-curhour")),
        curMins = parseInt($target.attr("data-curmins")),
        maxTarget = $target.attr("data-maxtarget"),
        minTarget = $target.attr("data-mintarget");
        // 当前小时
        if(typeof curHour!="undefined" && curHour!="" && !isNaN(curHour)){
            $timeHour.find("li[data-num='"+curHour+"']").addClass('on');
        };
        // 当前分钟
        if(typeof curMins!="undefined" && !isNaN(curMins)){
            $timeMins.find("li[data-num='"+curMins+"']").addClass('on');
        };
        // 最大值小时
        if(typeof maxHour!="undefined" && !isNaN(maxHour)){
            var _limit = maxHour;
            if(maxMins!=0){
                _limit=maxHour+1;
            };
            for (var i = _limit; i <= 24; i++) {
                $timeHour.find("li[data-num='"+i+"']").addClass('disabled');
            };
        };
        // 最大值分钟
        if(typeof maxMins!="undefined" && !isNaN(maxMins)){
            if((typeof curHour!="undefined" && curHour!="" && !isNaN(curHour)) && curHour!=maxHour){
                $timeMins.find("li").removeClass('disabled');
            }else{
                for (var i = maxMins; i <= 60 ; i++) {
                    $timeMins.find("li[data-num='"+i+"']").addClass('disabled');
                };
            };
        };
        // 最小值小时
        //alert(typeof minHour!="undefined" && minHour!="" && !isNaN(minHour))
        if(typeof minHour!="undefined" && minHour!="" && !isNaN(minHour)){
            var _limit = minHour;
            if(minMins!=59){
                _limit=minHour-1;
            };
            // if(minHour==1){
            //     $timeHour.find("li[data-num='0']").addClass('disabled');
            // }else{
            //     $timeHour.find("li[data-num='0']").removeClass('disabled');
            // };
            for (var i = 0; i <= _limit; i++) {
                $timeHour.find("li[data-num='"+i+"']").addClass('disabled');
            };
        };
        // 最小值分钟
        if(typeof minMins!="undefined" && !isNaN(minMins)){
            //alert((typeof curHour!="undefined" && curHour!="" && !isNaN(curHour)) && curHour!=minHour)
            if((typeof curHour!="undefined" && curHour!="" && !isNaN(curHour)) && curHour!=minHour){
                $timeMins.find("li").removeClass('disabled');
            }else{
                for (var i = 0; i <= minMins; i++) {
                    $timeMins.find("li[data-num='"+i+"']").addClass('disabled');
                };
            };
        };
    }
};
/* 原生 Array 方法扩展 */
// Array forEach方法
if (typeof Array.prototype.forEach != "function") {
  Array.prototype.forEach = function (fn, context) {
    for (var k = 0, length = this.length; k < length; k++) {
      if (typeof fn === "function" && Object.prototype.hasOwnProperty.call(this, k)) {
        fn.call(context, this[k], k, this);
      }
    }
  };
}
