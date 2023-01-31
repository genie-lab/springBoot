package com.co.kr.service;

import java.util.List;

import com.co.kr.vo.LoginDTO;

public interface UserService {
	// 전체 리스트 조회
    List<LoginDTO> list();
}
