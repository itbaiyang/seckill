// 存放主要交互逻辑js代码
//javascript 模块化
var seckill = {
    //封装秒杀ajax的url
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        insert: function () {
            return '/seckill/insert';
        },
        delete: function (seckillId) {
            return '/seckill/' + seckillId + '/delete';
        },
        update: function (seckillId) {
            return '/seckill/' + seckillId + '/update';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    data:{
        id: ''
    },
    handleSeckillKill: function (seckillId, node) {
        //处理秒杀逻辑
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //开启秒杀
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log('killUrl:' + killUrl);
                    //绑定一次点击事件
                    $('#killBtn').one('click', function () {
                        //执行秒杀请求
                        //禁用按钮
                        $(this).addClass('disabled');
                        //发送秒杀请求
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>')
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckillId.countdown(seckillId, now, start, end);
                }
            } else {

            }
        })
    },
    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    countdown: function (seckillId, nowTime, startTime, endTime) {
        //秒杀结束
        var seckillBox = $('#seckill-box');
        //时间的判断
        console.log(seckillId, nowTime, startTime, endTime);
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束！');

        } else if (nowTime < startTime) {
            //秒杀未开始，计时
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (evnet) {
                var format = evnet.strftime('秒杀几时：%D天 %H时 %M 分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                //获取秒杀地址，控制显示逻辑，执行秒杀
                seckill.handleSeckillKill(seckillId, seckillBox);
            });

        } else {
            seckill.handleSeckillKill(seckillId, seckillBox);
        }
    },
    updateInit: function (dataId, id) {
        seckill.data.id = id;
        //去除空格
        String.prototype.trim = function () {
            return this.replace(/(^\s*)|(\s*$)/g, '');
        };

        //处理Seckill格式为json
        var eachArr = [];
        var item = {};
        var arr = dataId.substring(8, dataId.length - 1).split(',');
        arr.forEach(function (each) {
            eachArr = each.split('=');
            item[eachArr[0].trim()] = eachArr[1];
            $('#' + eachArr[0].trim()).val(eachArr[1]);
        });

        //格式化时间
        var format = function (time) {
            var year = new Date(time).getFullYear();
            var month = new Date(time).getMonth();
            var day = new Date(time).getDay();
            if (month < 10) {
                month = '0' + month;
            }
            if (day < 10) {
                day = '0' + day;
            }
            var result = [year, month, day].join('-');
            console.log(result);
            return result;
        };
        var startTime = format(item.startTime);
        var endTime = format(item.endTime);

        //修改弹出初始化赋值
        $('#name').val(item.name.substring(1, item.name.length - 1));
        $('#number').val(item.number);
        $('#startTime').val(startTime);
        $('#endTime').val(endTime);
        console.log(item);

        //新建和修改显示不同
        $("#addSeckill").hide();
        $("#updateSeckill").show();
        //模态框显示
        $('#modal-title').text('修改秒杀');
        var seckillModal = $('#seckillModal');
        seckillModal.modal({
            show: true
        });

    },
    delete: function (id) {
        $.get(seckill.URL.delete(id), function (result) {
            if (result && result['success']) {
                alert("删除成功");
                window.location.reload();
            } else {
                alert(result['error']);
            }
        })
    },
    //详情页秒杀逻辑
    detail: {
        //详情页面初始化
        init: function (params) {
            //手机验证和登录，计时交互
            //规划交互流程
            //在cookie中查找手机
            var killPhone = $.cookie('killPhone');
            //验证手机号
            if (!seckill.validatePhone(killPhone)) {
                //绑定phone
                //控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,
                    backdrop: 'static',
                    keyboard: false
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    console.log(inputPhone);
                    if (seckill.validatePhone(inputPhone)) {
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误</label>').show(300);

                    }
                });
            }
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    seckill.countdown(seckillId, nowTime, startTime, endTime);

                }
            })
        }
    },
    insertSeckill: {
        init: function () {
            $('table').on('click', 'tr', function (e) {
                var id = $(this).attr('id');
                var e = e || window.event;                    //处理兼容性
                var target = e.target || e.srcElement;
                if (target.nodeName.toLowerCase() == 'button') {
                    if (target.className.indexOf('delete') >= 0) {
                        seckill.delete(id);
                    } else if (target.className.indexOf('update') >= 0) {
                        var dataId = target.getAttribute("data-id");
                        seckill.updateInit(dataId, id);
                    } else {
                        console.log('other btn')
                    }
                } else {
                    window.open(window.location.origin + '/seckill/' + id + '/detail');
                    // window.location.href='/seckill/' + id + '/detail';
                }
            });
            $("#updateSeckill").one('click', function () {
                var params = {
                    name: $('#name').val(),
                    number: $('#number').val(),
                    startTime: Date.parse($('#startTime').val()),
                    endTime: Date.parse($('#endTime').val())
                };
                //发送秒杀请求
                $.post(seckill.URL.update(seckill.data.id), params, function (result) {
                    if (result && result['success']) {
                        var killResult = result['data'];
                        var state = killResult['state'];
                        var stateInfo = killResult['stateInfo'];
                    }
                    $('#seckillModal').modal({
                        show: false
                    });
                    window.location.reload();
                });
            });
            //绑定添加时间
            $('#insert').on('click', function () {
                $('#name').val('');
                $('#number').val('');
                $('#startTime').val('');
                $('#endTime').val('');
                $('#modal-title').text('新增秒杀');
                $("#updateSeckill").hide();
                $("#addSeckill").show();
                var seckillModal = $('#seckillModal');
                seckillModal.modal({
                    show: true
                });
                $("#addSeckill").show().one('click', function () {
                    var params = {
                        name: $('#name').val(),
                        number: $('#number').val(),
                        startTime: Date.parse($('#startTime').val()),
                        endTime: Date.parse($('#endTime').val())
                    };
                    //发送秒杀请求
                    $.post(seckill.URL.insert(), params, function (result) {
                        if (result && result['success']) {
                            var killResult = result['data'];
                            var state = killResult['state'];
                            var stateInfo = killResult['stateInfo'];
                        }
                        var seckillModal = $('#seckillModal');
                        seckillModal.modal({
                            show: false
                        });
                        window.location.reload();
                    });
                });
            });
        }
    }
};