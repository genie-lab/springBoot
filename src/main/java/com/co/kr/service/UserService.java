package com.co.kr.service;

import java.util.List;
import java.util.Map;

import com.co.kr.domain.LoginDomain;
import com.co.kr.vo.LoginVO;


public interface UserService {
	// selectId
    public LoginDomain mbSelectList(Map<String, String> map);
    
    // selectAll
    public List<LoginDomain> mbAllList();
    
    //신규
    public void mbCreate(LoginDomain loginDomain);
    
    //중복체크 
    public LoginDomain mbDuplicationCheck(Map<String, String> map);
    
    //update 
    public void mbUpdate(LoginDomain loginDomain); 
    
    //delete 
    public void mbRemove(Map<String, String> map); 
    
}
