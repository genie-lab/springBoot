package com.co.kr.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.domain.FileListDomain;
import com.co.kr.vo.FileListVO;
import com.co.kr.vo.LoginVO;

public interface UploadService {
	
	//인서트
	public ModelAndView fileProcess(FileListVO fileListDTO, MultipartHttpServletRequest request);
	
	// 전체 리스트 조회
	public List<FileListDomain> list();
}
