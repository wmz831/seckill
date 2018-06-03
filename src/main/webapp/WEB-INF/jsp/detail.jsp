<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀详情页</title>
    <%@include file="common/head.jsp"%>

</head>
<body>
    <div class="container">
        <div class="panel panel-default text-center">
            <div class="panel-heading">
                <h1>${seckill.name}</h1>
            </div>
            <div class="panel-body">
                <h2 class="text-danger">
                    <!-- 显示time图标 -->
                    <span class="glyphicon glyphicon-time"/>
                    <!-- 显示倒计时 -->
                    <span class="glyphicon" id="seckill-box"/>
                </h2>
            </div>
        </div>
    </div>
    <!-- 登陆弹出层，输入电话 -->
    <div id="killPhoneModal" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title text-center">
                        <span class="glyphicon glyphicon-phone"></span>
                    </h3>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-xs-8 col-xs-offset-2">
                            <input type="text" name="killPhone" id="killPhoneKey"
                                    placeholder="填手机号^o^" class="form-control">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <!-- 验证信息 -->
                    <span id="killPhoneMessage" class="glyphicon"></span>
                    <button type="button" id="killPhoneBtn" class="btn btn-success">
                        <span class="glyphicon glyphicon-phone"></span>
                        Submit
                    </button>
                </div>
            </div>
        </div>
    </div>
</body>

<!-- 使用CDN 获取公共js http://www.bootcdn.cn/
     使用CDN的好处：
     1、不用去官网下载
     2、服务上线后，一些稳定可靠的cdn比直接发布到服务器上更有效
     3、cdn也是web重要的加速功能点
-->
<!-- jquery-cookie 插件 -->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<!-- jquery-countdown 插件 -->
<script src="https://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>
<!-- 交互逻辑 -->
<script src="/resources/script/seckill.js" type="text/javascript"></script> <!-- 这里注意：如果写成"/>"那么后面的script就不会加载，所以最好写成"></script>" -->

<script type="text/javascript">
    $(function () {
        //使用jsp的EL表达式传入参数给js
        seckill.detail.init({//json格式参数
            seckillId:${seckill.seckillId},
            startTime:${seckill.startTime.time},//毫秒long方便js直接做解析
            endTime:${seckill.endTime.time}
        });
    });
</script>
</html>
