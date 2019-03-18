package com.cx.demo.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cx.demo.dao.UserRepository;
import com.cx.demo.entity.User;

@Service
public class UserService {
	@Autowired
	UserRepository userRepository;

	/*
	 * 匹配登陆信息
	 */
	public List<User> dealLogin(String name, String password) {
		List<User> list = new ArrayList<User>();
		list = userRepository.dealLogin(name, password);
		if (!list.isEmpty()) {
			return list;
		}
		return null;
	}
}
