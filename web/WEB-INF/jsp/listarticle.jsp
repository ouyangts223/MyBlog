<%--
  Created by IntelliJ IDEA.
  User: 宇强
  Date: 2016/3/13 0013
  Time: 21:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
    <title>Coselding博客</title>
    <link rel="stylesheet" type="text/css" media="all" href="${pageContext.request.contextPath}/style.css" />
    <link rel="stylesheet" type="text/css" media="all" href="${pageContext.request.contextPath}/style/css/font-awesome.min.css" />
    <link href="http://fonts.googleapis.com/css?family=Open+Sans:400,400italic,300italic,300,700,700italic|Open+Sans+Condensed:300,700" rel="stylesheet" type="text/css">
    <!--[if IE 7]>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style/css/font-awesome-ie7.min.css"/>
    <![endif]-->
    <!--[if IE 8]>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style/css/ie8.css" media="all" />
    <![endif]-->
    <!--[if IE 9]>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style/css/ie9.css" media="all" />
    <![endif]-->
    <script type="text/javascript" src="${pageContext.request.contextPath}/style/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/style/js/ddsmoothmenu.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/style/js/retina.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/style/js/selectnav.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/style/js/jquery.backstretch.min.js"></script>
    <script type="text/javascript">
        $.backstretch("${pageContext.request.contextPath}/style/images/bg/1.jpg");
    </script>
</head>
<body>
<input id="cid" type="hidden" name="cid" value="${cid}">
<input id="gid" type="hidden" name="gid" value="${gid}">
<input id="key" type="hidden" name="key" value="${key}">
<%@include file="/public/header.jsp"%>
<div class="wrapper">
    <div class="content box">
        <h1 class="title article-title">文章列表</h1>
        <ul class="all-article">
            <c:forEach items="${page.list}" var="art">
                <li class="clearfix">
                    <i class="icon-circle"></i>
                    <h4>${art.top==1?'[顶置]':''}<a href="${pageContext.request.contextPath}${art.staticURL}.html">${art.title}</a></h4>
                    <div class="article-info">
                        <h6><i class="icon-user"></i> ${art.author}</h6>
                        <h6><i class="icon-time"></i> ${fn:substring(art.time, 0, 11).trim()}</h6>
                    </div>
                </li>
            </c:forEach>
        </ul>
        <div class="record">
            <%@include file="/public/page.jsp"%>
        </div>
    </div>

    <div class="sidebar box">
        <div class="sidebox widget">
            <h3 class="widget-title">最近更新</h3>
            <ul class="post-list">
                <c:forEach items="${params.lastArticlesList}" var="art">
                    <li>
                        <div class="meta">
                            <h5><a href="${pageContext.request.contextPath}${art.staticURL}.html">${art.title}</a></h5>
                            <em>${fn:substring(art.time,0,16)}</em>
                        </div>
                    </li>
                </c:forEach>
                <li class="more"><a href="${pageContext.request.contextPath}/listArticle.action">more</a></li>
            </ul>
        </div>

        <div class="sidebox widget">
            <h3 class="widget-title"><i class="icon-search icon"></i></h3>
            <form class="searchform" method="post" action="${pageContext.request.contextPath}/search.action">
                <input type="text" name="key" value="输入关键字搜索博客..." onFocus="this.value=''" onBlur="this.value='输入关键字搜索博客...'"/>
            </form>
        </div>

        <div class="sidebox widget">
            <h3 class="widget-title categories">分类</h3>
            <ul class="categories">
                <c:forEach items="${params.categories}" var="cate">
                    <li><a href="${pageContext.request.contextPath}/listArticle.action?cid=${cate.cid}">${cate.cname}</a></li>
                </c:forEach>
            </ul>
        </div>
    </div>
    <div class="clear"></div>
</div>
<%@include file="/public/footer.jsp"%>
</body>
</html>