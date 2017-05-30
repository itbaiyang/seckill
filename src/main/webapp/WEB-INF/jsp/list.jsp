<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--引入jstl--%>
<%@include file="common/tag.jsp" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>秒杀列表页</title>
    <%@include file="common/head.jsp" %>
</head>
<body>

<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading text-center">
            <h2>秒杀列表</h2>
        </div>
        <div class="panel-body">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th>名称</th>
                    <th>库存</th>
                    <th>开始时间</th>
                    <th>结束时间</th>
                    <th>创建时间</th>
                    <th>详情页</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="sk" items="${list}">
                    <tr id="${sk.seckillId}">
                        <td>${sk.name}</td>
                        <td>${sk.number}</td>
                        <td>
                            <fmt:formatDate value="${sk.startTime}" pattern="yyy-MM-dd HH:mm:ss" />
                        </td>
                        <td>
                            <fmt:formatDate value="${sk.endTime}" pattern="yyy-MM-dd HH:mm:ss" />
                        </td>
                        <td>
                            <fmt:formatDate value="${sk.createTime}" pattern="yyy-MM-dd HH:mm:ss" />
                        </td>
                        <td>
                            <%--<a class="btn btn-info" href="/seckill/${sk.seckillId}/detail" target="_blank">link</a>--%>
                            <button class="btn btn-info delete" data-id="${sk.seckillId}">删除</button>
                            <button class="btn btn-info update" data-id="${sk}">修改</button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <button class="btn btn-info" id="insert" style="margin: 10px">新建秒杀</button>
    </div>
</div>

<div class="modal fade" id="seckillModal" tabindex="-1" role="dialog" aria-labelledby="seckillModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h3 class="modal-title text-center" id="seckillModalLabel">
                    <span class="glyphicon glyphicon-add"></span>
                    <span id="modal-title"></span>
                </h3>
            </div>
            <div class="modal-body clearfix">
                <form class="col-xs-10 col-xs-offset-1">
                    <div class="form-group">
                        <input type="text" class="form-control" name="name" id="name" placeholder="填写秒杀名称^o^" >
                    </div>
                </form>
                <form class="col-xs-10 col-xs-offset-1">
                    <div class="form-group">
                        <input type="number" class="form-control" name="number" id="number" placeholder="填写秒杀数量^o^" >
                    </div>
                </form>
                <form class="col-xs-10 col-xs-offset-1">
                    <div class="form-group">
                        <input type="date" class="form-control" name="startTime" id="startTime" placeholder="填写秒杀开始时间^o^" >
                    </div>
                </form>
                <form class="col-xs-10 col-xs-offset-1">
                    <div class="form-group">
                        <input type="date" class="form-control" name="endTime" id="endTime" placeholder="填写秒杀结束时间^o^" >
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" id="addSeckill" class="btn btn-success">
                    <span class="glyphicon glyphicon-plus"></span>保存
                </button>
                <button type="button" id="updateSeckill" class="btn btn-success">
                    <span class="glyphicon glyphicon-edit"></span>修改
                </button>
            </div>
        </div>
    </div>
</div>


<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://cdn.bootcss.com/jquery/2.1.2/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"
        integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
        crossorigin="anonymous"></script>

<script src="/resources/script/seckill.js" type="text/javascript"></script>
<script type="text/javascript">
    $(function(){
        //使用el表达式传入参数
        seckill.insertSeckill.init({
        })
    })

</script>
</body>
</html>
