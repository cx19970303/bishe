package com.cx.demo.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cx.demo.entity.User;
import com.cx.demo.service.UserService;
import com.cx.demo.uitl.Util;

@Controller
@RequestMapping("/user")
@EnableAutoConfiguration
public class UserController {
	/*
	 * onenet平台接入原始参数及代码
	 */
	private static String token = "1212";// 用户自定义token和OneNet第三方平台配置里的token一致
	@SuppressWarnings("unused")
	private static String aeskey = "c7gjPry2I/XoQ41P35NBtDt+HsCPxIga1+j8FtxCLDs=";// aeskey和OneNet第三方平台配置里的token一致
	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService userService;

	/**
	 * 功能描述：第三方平台数据接收。
	 * <p>
	 * <ul>
	 * 注:
	 * <li>1.OneNet平台为了保证数据不丢失，有重发机制，如果重复数据对业务有影响，数据接收端需要对重复数据进行排除重复处理。</li>
	 * <li>2.OneNet每一次post数据请求后，等待客户端的响应都设有时限，在规定时限内没有收到响应会认为发送失败。
	 * 接收程序接收到数据时，尽量先缓存起来，再做业务逻辑处理。</li>
	 * </ul>
	 * 
	 * @param body
	 *            数据消息
	 * @return 任意字符串。OneNet平台接收到http 200的响应，才会认为数据推送成功，否则会重发。
	 */
	@RequestMapping(value = "/receive", method = RequestMethod.POST)
	@ResponseBody
	public String receive(@RequestBody String body)
			throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {

		logger.info("data receive:  body String --- " + body);
		/************************************************
		 * 解析数据推送请求，非加密模式。 如果是明文模式使用以下代码
		 **************************************************/
		/************* 明文模式 start ****************/
		Util.BodyObj obj = Util.resolveBody(body, false);
		logger.info("data receive:  body Object --- " + obj);
		if (obj != null) {
			boolean dataRight = Util.checkSignature(obj, token);
			if (dataRight) {
				logger.info("data receive: content" + obj.toString());
			} else {
				logger.info("data receive: signature error");
			}

		} else {
			logger.info("data receive: body empty error");
		}
		/************* 明文模式 end ****************/
		return "ok";
	}

	/**
	 * 功能说明： URL&Token验证接口。如果验证成功返回msg的值，否则返回其他值。
	 * 
	 * @param msg
	 *            验证消息
	 * @param nonce
	 *            随机串
	 * @param signature
	 *            签名
	 * @return msg值
	 */
	@RequestMapping(value = "/receive", method = RequestMethod.GET)
	@ResponseBody
	public String check(@RequestParam(value = "msg") String msg, @RequestParam(value = "nonce") String nonce,
			@RequestParam(value = "signature") String signature) throws UnsupportedEncodingException {

		logger.info("url&token check: msg:{} nonce:{} signature:{}", msg, nonce, signature);
		if (Util.checkToken(msg, nonce, signature, token)) {
			return msg;
		} else {
			return "error";
		}
		// return msg;
	}

	/*
	 * 登陆界面路径
	 */
	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	/*
	 * 登陆后跳转界面路径
	 */
	@RequestMapping("/welcome")
	public String welcome() {
		return "welcome";
	}

	/*
	 * 登陆验证方法
	 */
	@RequestMapping(value = "/dealLogin", method = RequestMethod.POST)
	@ResponseBody
	public String dealLogin(String name, String password, HttpSession session) {
		List<User> list = new ArrayList<User>();
		list = userService.dealLogin(name, password);
		if (list != null) {
			session.setAttribute("user", list.get(0).getName().toString());
			return "success";
		} else
			return "erro";
	}

	/*
	 * 获取登陆人员信息
	 */
	@RequestMapping(value = "/getLoginUser", method = RequestMethod.POST)
	@ResponseBody
	public String getLoginUser(HttpSession session) {
		String name = (String) session.getAttribute("user");
		return name;
	}
}
