/**
 * 存放主要交互逻辑js代码
 * javascript 模块化（分包）: js是动态语言，很容易写得非常凌乱，所以建议分包
 * 在Java中典型的模块化就是分包，分包可更加规范程序
 * 在JavaScript中没有package的概念，但有json表示对象的方式
 * seckill.detail.init(params); -> 类比成 seckill包detail类init方法
 * 在下面几个方法的回调函数中，都用if(result && result['success'])来判断，前后端保持一致，代码规范
 * 秒杀按钮用one()来实现事件绑定，防止多次无效请求增加服务器负担（重复秒杀一直到底层数据库报错才会发现并回滚）
 *
 */
var seckill = {
    //封装秒杀相关的ajax的url,将和后端通信的url 与交互代码分开，方便维护修改
    URL:{
        now:function () {
            return '/seckill/time/now';
        },

        exposer:function (seckillId) {
            return '/seckill/'+seckillId+'/exposer';
        },

        execution:function (seckillId,md5) {
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },

    //处理秒杀逻辑
    handleSeckillKill: function (seckillId,node) {
        //获取秒杀地址，控制显示逻辑，执行秒杀
        node.hide()//建议所有内容节点的操作，在操作前都先隐藏一下
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId),{},function (result) {//因为Controller里的方法设计为只接受post方式，所以这里用post
            //在回调函数中，执行交互流程
            if(result && result['success']){
                var exposer = result['data'];
                if(exposer['exposed']){
                    //开启秒杀，显示执行按钮
                    //获取秒杀地址：
                    var md5 = exposer['md5']
                    var killUrl = seckill.URL.execution(seckillId,md5);
                    console.log('killUrl:'+killUrl);//debug
                    //给之前写的button用one()来绑定秒杀事件，因为click()是一直绑定，one()是只绑定一次点击事件，防止连续点击
                    $('#killBtn').one('click',function () {//告诉这个function的事件名字叫"click"
                        //执行秒杀请求
                        //1:先禁用按钮
                        $(this).addClass('disabled') //这里$(this)==$('#killBtn')，后者会又运行一次jQuery的选择器，效率较低

                        //2：发送秒杀请求
                        $.post(killUrl,{},function (result) {
                            if(result && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];

                                //3：显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>');
                                console.log('stateInfo:'+stateInfo);//debug
                            }
                        });
                    });
                    //绑定完事件后，显示node
                    node.show();

                }else{
                    //秒杀未开启（当用户长时间等待，client和server可能会出现时间偏差）
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新计算计时
                    seckill.countDown(seckillId,now,start,end);
                }
            }else{
                console.log('result'+result);
            }
        });
    },

    //验证方法，可能有多个地方都需要验证，所以建议提取到比较上层的逻辑
    validatePhone:function (phone) {
        //js中直接传入phone对象，会判断phone是否为空(undefined);isNaN()判断是否为非数字
        if (phone && phone.length >= 4 && !isNaN(phone)) {
            return true;
        }else{
            return false;
        }
    },
    
    //倒计时效果
    countDown:function (seckillId,nowTime,startTime,endTime) {
        var seckillBox = $('#seckill-box');
        //时间判断
        if(nowTime>endTime){
            //秒杀结束
            seckillBox.html('秒杀结束！');
        }else if (nowTime<startTime) {
            //秒杀未开始，计时
            var killTime = new Date(startTime + 1000);//加1秒防止客户端计时服务出生偏移，可不加
            console.log('killTime:'+ killTime)
            //seckillBox自带的countdown事件，在每一次触发时间变化时都会调用回调函数做输出
            seckillBox.countdown(killTime,function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown',function () { //倒计时完成后回调事件
                //倒计时完成，秒杀开始
                seckill.handleSeckillKill(seckillId,seckillBox);
            })
        }else{
            //秒杀已经开始
            seckill.handleSeckillKill(seckillId,seckillBox);
        }
    },

    //详情页秒杀逻辑
    detail:{
        //详情页初始化：init方法不宜过长，所以将validatePhone、countdown方法剥离出来，同时后面这个方法也能够得到重用
        init:function (params) {
            //用户手机验证和登陆，计时交互
            //规划交互流程，先想清楚，写好注释，再写实现代码

            //在cookie中查找
            var killPhone = $.cookie('killPhone');

            //登陆验证，如果没通过，则交互输入手机
            if(!seckill.validatePhone(killPhone)) {
                //绑定phone
                //控制输出
                var killPhoneModal = $('#killPhoneModal');
                //killPhoneModal是modal(bootstrap的一个组件)，可调接口
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘时间
                });
                $('#killPhoneBtn').click(function () {//js因为是动态语言，函数本身也是一个对象
                    var inputPhone = $('#killPhoneKey').val();//取值
                    //console.log('inputPhone = ' + inputPhone);//todo console
                    if (seckill.validatePhone(inputPhone)) {
                        //写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});//cookie有效路径只写需要用到的地方
                        //验证通过，刷新页面
                        window.location.reload();
                    } else {
                        //验证没通过
                        //先隐藏，填充内容，再延迟显示(动态效果好看一些)
                        //这里的html()处理有些粗糙，文案应该统一弄个前端字典，不应该写死
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误</label>').show(300);
                    }
                });
            }
            //验证通过，已经登陆过了
            //计时交互
            //js访问json的方式
            var startTime = params['startTime']
            var endTime = params['endTime']
            var seckillId = params['seckillId']
            $.get(seckill.URL.now(), {}, function (result) {//get(地址,参数,回掉函数)
                if(result && result['success']){//result存在 且得到的SeckillResult对象的success 为true
                    var nowTime = result['data'];
                    console.log('nowTime:'+nowTime);
                    //计时时间判断
                    seckill.countDown(seckillId,nowTime,startTime,endTime);
                }else{
                    console.log('result:'+result);
                }
            })
        }
    }

}