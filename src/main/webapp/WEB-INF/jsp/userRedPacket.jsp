<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>演示页面</title>
</head>
<body>
<div class="main-container">
	演示页面<br/>
	<button id="demo1">超发数量，无控制</button><br/>
	<button id="demo2">数据强一致，数据库加锁(悲观锁)</button><br/>
	<button id="demo3">数据强一致，代码加锁(悲观锁)</button><br/>
	<button id="demo4">数据强一致，数据库引入version解决ABA问题(乐观锁))</button><br/>
	<button id="demo5">数据强一致，数据库引入version+重入机制(乐观锁))</button><br/>
	<button id="demo6">数据强一致，redis</button><br/>
</div>
	
<script src="${pageContext.request.contextPath }/html/js/jquery-1.11.3.min.js"></script> 
<script>
$(document).ready(function(){
	$("#demo1").bind("click", function(){
		for (var i = 1; i <= 5000; i++) {
			$.get("getRedPacket/1/" + i, function(data){
				console.log(data);
			});
		}
		alert("get ok");
	});
	$("#demo2").bind("click", function(){
		for (var i = 1; i <= 5000; i++) {
			$.get("getRedPacket2/2/" + i, function(data){
				console.log(data);
			});
		}
		alert("get ok");
	});
	$("#demo3").bind("click", function(){
		for (var i = 1; i <= 5000; i++) {
			$.get("getRedPacket3/3/" + i, function(data){
				console.log(data);
			});
		}
		alert("get ok");
	});
	$("#demo4").bind("click", function(){
		for (var i = 1; i <= 5000; i++) {
			$.get("getRedPacket4/4/" + i, function(data){
				console.log(data);
			});
		}
		alert("get ok");
	});
	$("#demo5").bind("click", function(){
		for (var i = 1; i <= 5000; i++) {
			$.get("getRedPacket5/5/" + i, function(data){
				console.log(data);
			});
		}
		alert("get ok");
	});
	$("#demo6").bind("click", function(){
		for (var i = 1; i <= 5000; i++) {
			$.get("getRedPacket6/6/" + i, function(data){
				console.log(data);
			});
		}
		alert("get ok");
	});
});
</script> 

</body>
</html>