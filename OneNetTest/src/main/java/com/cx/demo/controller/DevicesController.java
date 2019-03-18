package com.cx.demo.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.iot.onenet.javasdk.api.device.AddDevicesApi;
import cmcc.iot.onenet.javasdk.api.device.DeleteDeviceApi;
import cmcc.iot.onenet.javasdk.api.device.FindDevicesListApi;
import cmcc.iot.onenet.javasdk.api.device.GetDeviceApi;
import cmcc.iot.onenet.javasdk.api.device.ModifyDevicesApi;
import cmcc.iot.onenet.javasdk.model.Location;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.device.DeviceList;
import cmcc.iot.onenet.javasdk.response.device.DeviceResponse;
import cmcc.iot.onenet.javasdk.response.device.NewDeviceResponse;

@Controller
@RequestMapping("/devices")
public class DevicesController {
	// 产品的masterkey
	private String key = "3uTT4Z9lvGi3Mzr2cOW0S1kUZP8=";

	/**
	 * 模糊查询设备 参数顺序与构造函数顺序一致
	 * 
	 * @param keywords:匹配关键字（可选，从id和title字段中左匹配）,String
	 * @param authinfo:鉴权信息（可选，对应注册时的sn参数，唯一设备编号）,Object
	 * @param devid:
	 *            指定设备ID，多个用逗号分隔，最多100个（可选）,String
	 * @param begin:起始时间，包括当天（可选）,Date
	 * @param end:结束时间，包括当天（可选）,Date
	 * @param tags:标签（可选）
	 * @param isPrivate：
	 *            设备私密性，Boolean类型
	 * @param page:指定页码，最大页数为10000（可选）,Integer
	 * @param perpage:指定每页输出设备个数，默认30，最多100（可选）,Integer
	 * @param isOnline:在线状态（可选）
	 * @param title:设备名称关键字，字符串，左匹配，大小写不敏感（可选）
	 * @param imei:设备imei关键字，字符串，左匹配，大小写不敏感（可选）
	 * @param key:masterkey
	 * @throws ParseException
	 */
	@RequestMapping(value = "/findDevicesList", method = RequestMethod.POST)
	@ResponseBody
	public DeviceList findDevicesList(String keywords, String begin, String end, String isOnline, int page)
			throws ParseException {
		boolean isOnline2;
		FindDevicesListApi api;

		// 将yyyy-mm-dd的String的日期转化为yyyy-mm-dd的Date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 小写的mm表示的是分钟
		java.sql.Date begin2 = null;
		java.sql.Date end2 = null;
		if (!begin.isEmpty()) {
			java.util.Date creatDate1 = sdf.parse(begin);
			begin2 = new java.sql.Date(creatDate1.getTime());
		}
		if (!end.isEmpty()) {
			java.util.Date creatDate2 = sdf.parse(end);
			end2 = new java.sql.Date(creatDate2.getTime());
		}

		if (!isOnline.equals("all")) {
			isOnline2 = Boolean.parseBoolean(isOnline);
			api = new FindDevicesListApi(keywords, null, null, begin2, end2, null, null, page, 10, isOnline2, null,
					null, key);
		} else {
			api = new FindDevicesListApi(keywords, null, null, begin2, end2, null, null, page, 10, null, null, null,
					key);
		}

		BasicResponse<DeviceList> response = api.executeApi();
		// System.out.println("errno:" + response.errno + " error:" + response.error);
		// System.out.println(response.getJson());
		return response.data;
	}

	/****
	 * 设备新增 参数顺序与构造函数顺序一致
	 * 
	 * @param title：
	 *            设备名，String
	 * @param protocol：
	 *            接入协议（可选，默认为HTTP）,String
	 * @param desc：
	 *            设备描述（可选）,String
	 * @param tags：
	 *            设备标签（可选，可为一个或多个）,List<String>
	 * @param location：
	 *            设备位置{"纬度", "经度", "高度"}（可选）,Location类型
	 * @param isPrivate：
	 *            设备私密性,Boolean类型
	 * @param authInfo：
	 *            设备唯一编号 ,String或json
	 * @param other：
	 *            其他信息,Map<String, Object>
	 * @param interval：
	 *            MODBUS设备 下发命令周期,Integer类型
	 * @param key：
	 *            masterkey,String
	 * @return
	 */
	@RequestMapping(value = "/addDevices", method = RequestMethod.POST)
	@ResponseBody
	public BasicResponse<NewDeviceResponse> addDevices(String title, String auth_info, String isPrivate, String desc,
			Float longitude, Float latitude, String tags) {
		boolean isPrivate2 = Boolean.parseBoolean(isPrivate);
		String protocol = "HTTP";// 接入协议
		Location location = new Location(longitude, latitude, 0);// 设备位置{"纬度", "经度", "高度"}（可选）
		List<String> tagsList = new ArrayList<String>();
		String[] test = tags.split(",");
		for (int i = 0; i < test.length; i++) {
			tagsList.add(test[i]);
		}
		AddDevicesApi api = new AddDevicesApi(title, protocol, desc, tagsList, location, isPrivate2, auth_info, null,
				null, key);
		BasicResponse<NewDeviceResponse> response = api.executeApi();
		// System.out.println("errno:" + response.errno + " error:" + response.error);
		// System.out.println(response.getJson());
		return response;
	}

	/**
	 * 精确查询单个设备 参数顺序与构造函数顺序一致
	 * 
	 * @param devid:设备名，String
	 * @param key:masterkey
	 *            或者 设备apikey,String
	 * @return
	 */
	@RequestMapping(value = "/getDevice", method = RequestMethod.POST)
	@ResponseBody
	public BasicResponse<DeviceResponse> getDevice(String devId) {
		GetDeviceApi api = new GetDeviceApi(devId, key);
		BasicResponse<DeviceResponse> response = api.executeApi();
		// System.out.println("errno:" + response.errno + " error:" + response.error);
		// System.out.println(response.getJson());
		return response;
	}

	/***
	 * 设备更新 参数顺序与构造函数顺序一致
	 * 
	 * @param devId：
	 *            设备ID,String
	 * @param title：
	 *            设备名，String
	 * @param protocol：
	 *            接入协议（可选，默认为HTTP），String
	 * @param desc：
	 *            设备描述（可选），String
	 * @param tags：
	 *            设备标签（可选，可为一个或多个），List<String>
	 * @param location：
	 *            设备位置{"纬度", "精度", "高度"}（可选）,Location类型
	 * @param isPrivate：
	 *            设备私密性，Boolean类型
	 * @param authInfo：
	 *            设备唯一编号 ，String
	 * @param other：
	 *            其他信息，Map<String, Object>
	 * @param interval：
	 *            MODBUS设备 下发命令周期,Integer类型
	 * @param key
	 *            ：masterkey 或者 设备apikey
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/modifyDevices", method = RequestMethod.POST)
	@ResponseBody
	public BasicResponse<Void> modifyDevices(String devId, String title, String auth_info, String isPrivate, String desc,
			Float longitude, Float latitude, String tags) {
		String protocol = "HTTP";
		Location location = new Location(longitude, latitude, 0);// 设备位置{"纬度", "经度", "高度"}（可选）
		boolean isPrivate2 = Boolean.parseBoolean(isPrivate);
		List<String> tagsList = new ArrayList<String>();
		String[] test = tags.split(",");
		for (int i = 0; i < test.length; i++) {
			tagsList.add(test[i]);
		}
		ModifyDevicesApi api = new ModifyDevicesApi(devId, title, protocol, desc, tagsList, location, isPrivate2,
				auth_info, null, null, key);
		BasicResponse<Void> response = api.executeApi();
//		System.out.println("errno:" + response.errno + " error:" + response.error);
		return response;
	}

	/**
	 * 设备删除 参数顺序与构造函数顺序一致
	 * 
	 * @param devid:
	 *            设备ID,String
	 * @param key:
	 *            masterkey
	 * @return
	 */
	@RequestMapping(value = "/removeDevice", method = RequestMethod.POST)
	@ResponseBody
	public BasicResponse<Void> removeDevice(String devId) {
		DeleteDeviceApi api = new DeleteDeviceApi(devId, key);
		BasicResponse<Void> response = api.executeApi();
		// System.out.println("errno:" + response.errno + " error:" + response.error);
		return response;
	}
}
