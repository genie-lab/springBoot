package com.co.kr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.vo.LoginDTO;

@Mapper
public interface UserMapper {
	//전체 리스트 조회
    public List<LoginDTO> list();
}
