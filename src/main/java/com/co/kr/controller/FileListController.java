package com.co.kr.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.co.kr.code.Code;
import com.co.kr.code.Table;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.LoginDomain;
import com.co.kr.exception.InternalException;
import com.co.kr.exception.RequestException;
import com.co.kr.service.UploadService;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class FileListController {
	
	@Autowired
	private UploadService uploadService;

	
	@PostMapping(value = "upload")
	public ModelAndView bdUpload(@ModelAttribute("fileListVO") FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException, ParseException {
		
		System.out.println(fileListVO.getTitle() + fileListVO.getContent()+fileListVO.getSeq());
		System.out.println(request.getFiles("files"));
		System.out.println("============================");
		ModelAndView mav = new ModelAndView();
		int bdSeq = uploadService.fileProcess(fileListVO, request, httpReq);
		fileListVO.setContent(""); //?????????
		fileListVO.setTitle(""); //?????????
		
		// ???????????? ??????????????? bdSeq String?????? string?????? ???????????? ?????????
		mav = bdSelectOneCall(String.valueOf(bdSeq),request);
		mav.setViewName("board/boardList.html");
		return mav;
		
	}
	
	
	//????????? ???????????? ?????? ?????????
	public ModelAndView bdListCall() {
		ModelAndView mav = new ModelAndView();
		List<BoardListDomain> items = uploadService.boardList();
		System.out.println("items ==> "+ items);
		mav.addObject("items", items);
		return mav;
	}
	
	//????????? ?????? ???????????? ?????? ?????????
	public ModelAndView bdSelectOneCall(String bdSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		System.out.println("bdSeq"+bdSeq);
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		
		map.put("bdSeq", Integer.parseInt(bdSeq));
		BoardListDomain boardListDomain =uploadService.boardSelectOne(map);
		List<BoardFileDomain> fileList =  uploadService.boardSelectOneFile(map);
		
		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);

		//????????? ????????? ??????
		session.setAttribute("files", fileList);
		return mav;
	}
	 
	//detail
	@GetMapping("detail")
    public ModelAndView bdDetail(@ModelAttribute("fileListVO") FileListVO fileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		//???????????? ????????????
		mav = bdSelectOneCall(bdSeq,request);
		mav.setViewName("board/boardList.html");
		return mav;
	}
	
	//??????
	@GetMapping("remove")
	public ModelAndView mbRemove(@ModelAttribute("fileListVO") FileListVO fileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<BoardFileDomain> fileList = null;
		if(session.getAttribute("files") != null) {						
			fileList = (List<BoardFileDomain>) session.getAttribute("files");
		}

		map.put("bdSeq", Integer.parseInt(bdSeq));
		
		//????????????
		uploadService.bdContentRemove(map);
		System.out.println("bdSeq===111="+map.get("bdSeq"));

		for (BoardFileDomain list : fileList) {
			System.out.println(list);
			list.getUpFilePath();
			Path filePath = Paths.get(list.getUpFilePath());
	 
	        try {
	        	
	            // ?????? ??????
	            Files.deleteIfExists(filePath); // notfound??? exception ??????????????? false ??????
	            //?????? 
				uploadService.bdFileRemove(list);
				
	        } catch (DirectoryNotEmptyException e) {
				throw RequestException.fire(Code.E404, "??????????????? ???????????? ????????????", HttpStatus.NOT_FOUND);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		
		session.removeAttribute("files"); // ??????
		mav = bdListCall();
		mav.setViewName("board/boardList.html");
		
		return mav;
	}
	
	@GetMapping("edit")
	public ModelAndView edit(@ModelAttribute("fileListVO") FileListVO fileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		System.out.println("bdSeq===="+bdSeq);

		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		
		map.put("bdSeq", Integer.parseInt(bdSeq));
		BoardListDomain boardListDomain =uploadService.boardSelectOne(map);
		List<BoardFileDomain> fileList =  uploadService.boardSelectOneFile(map);
		
		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		System.out.println("boardListDomain.getBdSeq()===="+boardListDomain.getBdSeq());

//		mav.addObject("detail", boardListDomain);
		fileListVO.setSeq(boardListDomain.getBdSeq());
		fileListVO.setContent(boardListDomain.getBdContent());
		fileListVO.setTitle(boardListDomain.getBdTitle());
		fileListVO.setIsEdit("edit");  // upload ????????????????????????
		
	
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);
		mav.addObject("fileLen",fileList.size());
		
//		mav = bdListCall();
//		mav = bdSelectOneCall(bdSeq,request);
		mav.setViewName("board/boardEditList.html");
		return mav;
	}
	
	@PostMapping("editSave")
	public ModelAndView editSave(@ModelAttribute("fileListVO") FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		//??????
		uploadService.fileProcess(fileListVO, request, httpReq);
		
		mav = bdSelectOneCall(fileListVO.getSeq(),request);
		fileListVO.setContent(""); //?????????
		fileListVO.setTitle(""); //?????????
		mav.setViewName("board/boardList.html");
		return mav;
	}
	
	
}
