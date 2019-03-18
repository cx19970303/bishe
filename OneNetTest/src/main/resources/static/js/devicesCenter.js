/*
 * 模糊查询设备信息
 */
function findDevicesList(page) {
	var keywords = $("#keywords").val();
	var begin = $("#begin").val();
	var end = $("#end").val();
	var isOnline = $("#isOnline").val();
	$
			.ajax({
				type : "post",
				url : "/devices/findDevicesList",
				data : {
					"keywords" : keywords,
					"begin" : begin,
					"end" : end,
					"isOnline" : isOnline,
					"page":page,
				},
				success : function(data) {
// console.log(data);
					// 动态显示上一页与下一页之间页码数
					$(".pageDiv").remove();
					$("#setPage")
							.append(
									"<ul class='pageDiv am-pagination am-pagination-centered'><li onclick='lastPage()'><a>上一页</a></li>"
											+ "<li onclick='nextPage()'><a>下一页</a></li>"
											+ "<li>第<input type='text' id='page' style='width:38px'/>页</li><li onclick='choosePage()'><a>跳转</a></li>"
											+ "共<span id='countpages'></span>页</ul>");
					// 动态显示表格
					$(".tr").remove();// 清除上次检索的结果
					for (var i = 0; i < data.devices.length; i++) {
						var lable = "<tr class='tr'><td>" + data.devices[i].id
								+ "</td><td>"+data.devices[i].auth_info+"</td><td>" + data.devices[i].title + "</td>";
						if ($("#isOnline").val() == "all") {
							if (!data.devices[i].online) {
								lable = lable + "<td>离线</td>";
							} else {
								lable = lable + "<td>在线</td>";
							}
						} else {
							if ($("#isOnline").val() == "true") {
								lable = lable + "<td>在线</td>";
							} else {
								lable = lable + "<td>离线</td>";
							}
						}
						lable+="<td><div class='am-btn-group'>" 
							+"<button class='am-btn am-btn-primary am-round am-btn-sm' data-am-modal='{target: \"#displalyDeviceModal\", closeViaDimmer: 0, width: 1000, height: 530}' onclick='displayDevice("+data.devices[i].id+")'>详情</button>"
							+"<button class='am-btn am-btn-warning am-round am-btn-sm' data-am-modal='{target: \"#modifyDeviceModal\", closeViaDimmer: 0, width: 1000, height:600}' onclick='disModifyDevices("+data.devices[i].id+"),initTagValue()'>修改</button>" 
							+"<button class='am-btn am-btn-danger am-round am-btn-sm' onclick='removeDevice("+data.devices[i].id+")'>删除</button></div></td></tr>"
						$("#tbody").append(lable);
					}
					$("#countpages").html(Math.ceil(data.total_count/data.per_page));// 一共多少页
					$("#page").val(data.page);// 当前第几页
					$("#findDevicesSidebar").offCanvas('close');// 关闭查询设备侧边栏
				}
			})
}

/*
 * 上一页点击按钮事件
 */
function lastPage(){
	if(Number($("#page").val())-1==0){
		findDevicesList(1);
	}else{
		findDevicesList(Number($("#page").val())-1);
	}
	
}
/*
 * 下一页点击按钮事件
 */
function nextPage(){
	if(Number($("#page").val())+1>$("#countpages").html()){
		findDevicesList(Number($("#countpages").html()));
	}else{
		findDevicesList(Number($("#page").val())+1);
	}
}
/*
 * 跳转按钮点击事件
 */
function choosePage(){
	if(Number($("#page").val())>$("#countpages").html()){
		findDevicesList(Number($("#countpages").html()));
	}else if(Number($("#page").val())<1){
			findDevicesList(1);
	}else{
		findDevicesList(Number($("#page").val()));
	}
	
}
/*
 * 新增设备时候创建百度地图实例
 */
function createMap(){
	$("input[name='isPrivate']").eq(0).click();// 默认数据私密性为私密
	var map = new BMap.Map("container");// 创建地图实例
	var point = new BMap.Point(115.834546, 28.655575);// 创建点坐标
	map.centerAndZoom(point, 15);// 初始化地图，设置中心点坐标和地图级别
	map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放

	// 单击获取点击的经纬度
	map.addEventListener("click", function(e) {
		map.clearOverlays();
		$("#longitude").val(e.point.lng);
		$("#latitude").val(e.point.lat);
		var new_point = new BMap.Point(e.point.lng, e.point.lat);
		var marker = new BMap.Marker(new_point); // 创建标注
		map.addOverlay(marker); // 将标注添加到地图中
		map.panTo(new_point);
	});
}

var tagNum=0;// 记录标签个数
var tagsList=new Array();// 存储标签的数组
/*
 * 重置标签变量
 */
function initTagValue(){
	tagNum=0;
	tagsList.splice(0,tagsList.length);
}
/*
 * 添加标签按钮点击事件
 */
function addTags(tags,inputTag){
	if(tagNum==0){
		$("#"+tags).append("<span id="+$('#'+inputTag).val()+" class='"+tags+" am-badge am-badge-secondary am-round'>"+$('#'+inputTag).val()+"<i onclick='removeTag(\""+$('#'+inputTag).val()+"\")' class='am-close'>&times;</i></span>");
		tagsList.push($('#'+inputTag).val());// 将新标签加入数组
		tagNum++;
	}else if(tagNum>=5){
		tagNum=5;
		alert("标签个数不能超过5个");
	}else{
		// 遍历标签,查看是否有重复的
		var key=true;
		$("#"+tags+" span").each(function(){
			if($(this).attr("id")==$('#'+inputTag).val()){
				alert("标签不能重复");
				key=false;
				return false;
			}
			
		});
		if(key){
			$("#"+tags).append("<span id="+$('#'+inputTag).val()+" class='"+tags+" am-badge am-badge-secondary am-round'>"+$('#'+inputTag).val()+"<i onclick='removeTag(\""+$('#'+inputTag).val()+"\")' class='am-close'>&times;</i></span>");
			tagsList.push($('#'+inputTag).val());// 将新标签加入数组
			tagNum++;
		}
	}
// console.log(tagsList);
}
/*
 * 标签的x点击事件
 */
function removeTag(spanTagId){
	$("#"+spanTagId).remove();
	tagsList.splice(tagsList.indexOf(spanTagId),1);
// console.log(tagsList);
	tagNum--;
}

/*
 * 新增设备按钮点击事件
 */
function addDevices(){
	var title=$("#title").val();// 设备名
	var auth_info=$("#auth_info").val();// 设备编号
	var isPrivate=$("input[name='isPrivate']:checked").val();// 数据私密性
	var desc=$("#desc").val();// 设备描述
	var longitude=$("#longitude").val();// 经度
	var latitude=$("#latitude").val();// 纬度
	var tags="";// String
	for(var a=0;a<tagsList.length;a++){
		tags+=tagsList[a]+",";
	}
	$.ajax({
		type:"post",
		url:"/devices/addDevices",
		data:{
			"title":title,
			"auth_info":auth_info,
			"isPrivate":isPrivate,
			"desc":desc,
			"longitude":longitude,
			"latitude":latitude,
			"tags":tags,
		},
		success:function(data){
// console.log(data);
			if(data.error=="succ"){
				findDevicesList(1);// 新增成功刷新查询列表
				$("#addDevicesModal").modal("close");
				alert("成功新增设备！");
			}else{
				alert(data.error);
			}
		}
	})
}

/*
 * 显示某个设备详情
 */
function displayDevice(devId){
	$.ajax({
		type:"post",
		url:"/devices/getDevice",
		data:{
			"devId":devId,
		},
		success:function(data){
// console.log(data);
			$("#displayID").val(data.data.id);
			$("#displayTitle").val(data.data.title);
			$("#displayAuthInfo").val(data.data.auth_info);
			$("#displayDesc").val(data.data.desc);
			$("#displayProtocol").val(data.data.protocol);
			$("#displayCreateTime").val(data.data.create_time);
			if(data.data.private){
				$("#displayPrivate").val("私密");
			}else{
				$("#displayPrivate").val("公开");
			}
			$(".displayTags").remove();
			for(var i=0;i<data.data.tags.length;i++){
				$("#displayTags").append("<span class='displayTags am-badge am-badge-primary am-margin-right'>"+data.data.tags[i]+"</span>");
			}
			
			var map = new BMap.Map("displayMap");// 创建地图实例
			var new_point = new BMap.Point(data.data.location.lon,data.data.location.lat);
			map.centerAndZoom(new_point, 15);// 初始化地图，设置中心点坐标和地图级别
			map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放
			var marker = new BMap.Marker(new_point); // 创建标注
			map.addOverlay(marker); // 将标注添加到地图中
		}
	})
}
/*
 * 显示更新设备前的设备信息
 */
function disModifyDevices(devId){
	$.ajax({
		type:"post",
		url:"/devices/getDevice",
		data:{
			"devId":devId,
		},
		success:function(data){
// console.log(data);
			$("#modifyID").val(data.data.id);
			$("#modifyTitle").val(data.data.title);
			$("#modifyAuthInfo").val(data.data.auth_info);
			$("#modifyDesc").val(data.data.desc);
			if(data.data.private){
				$("input[name='modifyPrivate']").eq(0).click();
			}else{
				$("input[name='modifyPrivate']").eq(1).click();
			}
			
			$(".modifyTags").remove();
			for(var i=0;i<data.data.tags.length;i++){
				$("#modifyTags").append("<span class='modifyTags am-badge am-badge-secondary am-round' id="+data.data.tags[i]+">"+data.data.tags[i]+"<i onclick='removeTag(\""+data.data.tags[i]+"\")' class='am-close'>&times;</i></span>");
				tagsList.push(data.data.tags[i]);// 将新标签加入数组
				tagNum++;
			}
			
			var map = new BMap.Map("modifyMap");// 创建地图实例
			var point = new BMap.Point(data.data.location.lon,data.data.location.lat);
			$("#modifylongitude").val(data.data.location.lon);
			$("#modifylatitude").val(data.data.location.lat);
			map.centerAndZoom(point, 15);// 初始化地图，设置中心点坐标和地图级别
			map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放
			var marker1 = new BMap.Marker(point); // 创建标注
			map.addOverlay(marker1); // 将标注添加到地图中
			// 单击获取点击的经纬度
			map.addEventListener("click", function(e) {
				map.clearOverlays();
				$("#modifylongitude").val(e.point.lng);
				$("#modifylatitude").val(e.point.lat);
				var new_point = new BMap.Point(e.point.lng, e.point.lat);
				var marker2 = new BMap.Marker(new_point); // 创建标注
				map.addOverlay(marker2); // 将标注添加到地图中
				map.panTo(new_point);
			});
		}
	})
}
/*
 * 更新设备按钮点击事件
 */
function modifyDevices(){
	var devId=$("#modifyID").val();// 设备ID
	var title=$("#modifyTitle").val();// 设备名
	var desc=$("#modifyDesc").val();// 设备描述
	var auth_info=$("#modifyAuthInfo").val();// 设备编号
	var isPrivate=$("input[name='modifyPrivate']:checked").val();// 数据私密性
	var longitude=$("#modifylongitude").val();// 经度
	var latitude=$("#modifylatitude").val();// 纬度
	var tags="";// String
	for(var a=0;a<tagsList.length;a++){
		tags+=tagsList[a]+",";
	}
	$.ajax({
		type:"post",
		url:"/devices/modifyDevices",
		data:{
			"devId":devId,
			"title":title,
			"desc":desc,
			"auth_info":auth_info,
			"isPrivate":isPrivate,
			"longitude":longitude,
			"latitude":latitude,
			"tags":tags,
		},
		success:function(data){
// console.log(data);
			if(data.error=="succ"){
				findDevicesList(1);// 新增成功刷新查询列表
				$("#modifyDeviceModal").modal("close");
				alert("更新成功！");
			}else{
				alert(data.error);
			}
		}
	})
}
/*
 * 删除某个设备
 */
function removeDevice(devId){
	var isDel = window.confirm("确定删除吗？");
	if(isDel){
		$.ajax({
			type:"post",
			url:"/devices/removeDevice",
			data:{
				"devId":devId,
			},
			success:function(data){
// console.log(data);
				if(data.error=="succ"){
					findDevicesList(1);// 删除成功刷新查询列表
					alert("成功删除设备！");
				}else{
					alert("删除失败！");
				}
			}
		})
	}
}
