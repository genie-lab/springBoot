package com.co.kr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.co.kr.mapper.UserMapper;
import com.co.kr.vo.LoginDTO;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper userMapper;

	@Override
	public List<LoginDTO> list() {
		// TODO Auto-generated method stub
		return userMapper.list();
	}
}
