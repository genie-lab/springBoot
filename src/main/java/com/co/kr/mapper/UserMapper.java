package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.co.kr.domain.LoginDomain;
import com.co.kr.vo.LoginDTO;

@Mapper
public interface UserMapper {
	
	//전체 리스트 조회
    public LoginDomain mbSelectList(Map<String, String> map);
    
    //sign up 저장
    public void mbCreate(LoginDomain loginDomain);
    
    //전체데이터
    public List<LoginDomain> mbAllList();
    
    //login duplicationCheck
    public LoginDomain mbDuplicationCheck(Map<String, String> map);
    
    //update
    public void mbUpdate(LoginDomain loginDomain);
    
    //delete
    public void mbRemove(Map<String, String> map);
    
}
