package com.cx.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cx.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
	//匹配登陆人员账号和密码
	@Query(value="select a from User a where a.name=?1 and a.password=?2")
	public List<User> dealLogin(String name,String password);
	
}
