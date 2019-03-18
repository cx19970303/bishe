/*
 * 匹配登陆信息
 */
function dealLogin() {
	$.ajax({
		type : "post",
		url : "/user/dealLogin",
		data : {
			"name" : $("#user").val(),
			"password" : $("#password").val()
		},
		success : function(data) {
			console.log(data);
			if (data == "success") {
				$(location).attr("href", "/user/welcome");
			} else {
				alert("登陆用户或密码错误");
			}
		}
	});
}