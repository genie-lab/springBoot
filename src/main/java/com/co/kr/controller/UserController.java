package com.co.kr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.co.kr.service.UserService;

@Controller
@RequestMapping(value = "/")
public class UserController {
	
	@Autowired
	private UserService userService;

	/**
	 * 전체리스트
	 * */
	@GetMapping(value = "/list")
	public ResponseEntity<?> list(){
		return ResponseEntity.ok().body(userService.list());
	}
}
