/*
 * 将登陆人员名字显示在页面头部
 */
$(function() {
	$.ajax({
		type : "post",
		url : "/user/getLoginUser",
		success : function(data) {
			// console.log(data);
			$("#sessionName").html(data);
		}
	})
})

/*
 * 注销当前登陆用户
 */
function cancel() {
	alert("注销成功");
	window.parent.location.replace("/user/login");
}

