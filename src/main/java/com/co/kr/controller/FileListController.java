package com.co.kr.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.code.Code;
import com.co.kr.code.Table;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.LoginDomain;
import com.co.kr.exception.InternalException;
import com.co.kr.service.UploadService;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;
import com.fasterxml.jackson.databind.util.JSONPObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class FileListController {
	
	@Autowired
	private UploadService uploadService;

	
	@PostMapping(value = "/upload")
	public ModelAndView upload(@ModelAttribute("fileListVO") FileListVO fileListVO, MultipartHttpServletRequest request) throws IOException, ParseException {
		
		System.out.println(fileListVO.getTitle() + fileListVO.getContent());
		System.out.println(request.getFiles("files"));
		System.out.println("============================");
		ModelAndView mav = new ModelAndView();
		uploadService.fileProcess(fileListVO, request) ;
		
//		mav.addObject("fileList",list);
		mav = boardListCall(fileListVO);
		mav.setViewName("board/boardList.html");
		return mav;
		
	}
	
	
	//리스트 가져오기 따로 함수뺌
	public ModelAndView boardListCall(@ModelAttribute("fileListVO") FileListVO fileListVO) {
		ModelAndView mav = new ModelAndView();
		List<BoardListDomain> items = uploadService.boardList();
		System.out.println("items ==> "+ items);
		mav.addObject("items", items);
		return mav;
	}
	
	
	
	
	
	
}
