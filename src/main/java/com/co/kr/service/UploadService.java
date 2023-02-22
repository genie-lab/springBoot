package com.co.kr.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.domain.BoardContentDomain;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.LoginDomain;
import com.co.kr.vo.FileListVO;
import com.co.kr.vo.LoginVO;

public interface UploadService {
	
	//인서트
	public void fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
	
	// 전체 리스트 조회
	public List<BoardListDomain> boardList();
	// 하나 리스트 조회
	public BoardListDomain boardSelectOne(HashMap<String, Object> map);
	// 하나 파일 리스트 조회
	public List<BoardFileDomain> boardSelectOneFile(HashMap<String, Object> map);
	// 하나 삭제
	public void bdContentRemove(HashMap<String, Object> map);
	// 하나 삭제
	public void bdFileRemove(BoardFileDomain boardFileDomain);
	
}
