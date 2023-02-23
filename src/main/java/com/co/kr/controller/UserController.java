package com.co.kr.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.LoginDomain;
import com.co.kr.service.UploadService;
import com.co.kr.service.UserService;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;
import com.co.kr.vo.LoginVO;
import com.co.kr.vo.SigninVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping(value = "/")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UploadService uploadService;

	private FileListController fileListController;
	
	/**
	 * 전체리스트
	 * */
//	@GetMapping(value = "/list")
//	public ResponseEntity<?> list(){
//		ModelAndView mv = new ModelAndView();
//        
//        List<String> listTest = new ArrayList<String>();
//         
//        listTest.add("test1");
//        listTest.add("test2");
//        listTest.add("test3");
//         
//        mv.addObject("listTest",listTest);      // jstl로 호출
//        mv.addObject("ObjectTest","테스트입니다."); // jstl로 호출
//        mv.setViewName("index.html");         // 실제 호출될 /WEB-INF/jsp/model/testMv.jsp
////        return mv; 
//		return ResponseEntity.ok().body(userService.list());
//	}
	

	
	// 진입점
	@GetMapping("/")
	public String index(HttpServletRequest request) throws IOException {
		HttpSession session = request.getSession();
		System.out.println(session.getId());
		System.out.println(session.getAttribute("id"));
//		session.getAttribute("id");
		
		System.out.println("request.getSession().getId()");
		if(session.getAttribute("id") != null) {
			session.invalidate();
		}
		
		return "index.html";
	}
	
	// 회원가입 화면
	@GetMapping("signin")
    public ModelAndView signIn() throws IOException {
		ModelAndView mav = new ModelAndView();
        mav.setViewName("signin/signin.html"); 
        return mav;
    }
	
	// 회원가입
	@PostMapping("create")
	public ModelAndView create(LoginVO loginDTO, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		ModelAndView mav = new ModelAndView();
		
		//session 처리 
		HttpSession session = request.getSession();
//		RedirectView red = new RedirectView();
		System.out.println(loginDTO);
		
		// 중복체크
		Map<String, String> map = new HashMap();
		map.put("mbId", loginDTO.getId());
		map.put("mbPw", loginDTO.getPw());
		
		
		// 중복체크
		int dupleCheck = userService.mbDuplicationCheck(map);
		System.out.println(dupleCheck);

		if(dupleCheck > 0) { // 가입되있으면  
			String alertText = "중복이거나 유효하지 않은 접근입니다";
			String redirectPath = "/main";
			System.out.println(loginDTO.getAdmin());
			if(loginDTO.getAdmin() != null) {
				redirectPath = "/main/mbList";
			}
			CommonUtils.redirect(alertText, redirectPath, response);
		}else {
			
			//현재아이피 추출
			String IP = CommonUtils.getClientIP(request);
			
			
			LoginDomain loginDomain = null; //초기화
			loginDomain = LoginDomain.builder()
					.mbId(loginDTO.getId())
					.mbPw(loginDTO.getPw())
					.mbLevel("2")
					.mbIp(IP)
					.mbUse("Y")
					.build();
			
			// 저장
			userService.mbCreate(loginDomain);
			
			System.out.println(loginDomain.getMbId());
			if(loginDTO.getAdmin() == null) { // 'admin'들어있을때는 alert 스킵이다
				// session 저장 
				session.setAttribute("ip",IP);
				session.setAttribute("id", loginDomain.getMbId());
				session.setAttribute("mbLevel", "2"); 
				mav.setViewName("redirect:/bdList");
			}else { // admin일때
				mav.setViewName("redirect:/mbList");
			}
		}
		
		return mav;

	}
	
	
	//대시보드 리스트 보여주기
	@GetMapping("mbList") // required=false null 일때 받기 에러금지 // querystring == @RequestParam
	public ModelAndView mbList(@RequestParam(value="page") String page,
			 HttpServletRequest request
			) {
		
		//page 초기화
		HttpSession session = request.getSession();
//		if(session.getAttribute("page") !=null) {			
//			page = (String) session.getAttribute("page");
//			System.out.println("clickPage0="+page); 
//		}else if(page == null) {
//			page = "1"; 
//			System.out.println("clickPage1="+page); 
//		}
		
		session.setAttribute("page", page);
		System.out.println("clickPage3="+page);
		ModelAndView mav = new ModelAndView();
		
		mav = mbListCall(page, request);  //리스트만 가져오기
		//페이지네이션
		
		mav.setViewName("admin/adminList.html");
		return mav; 
	}
	
	//대시보드 리스트 보여주기
	@GetMapping("mbEditList")
	public ModelAndView mbListEdit(@RequestParam("mbSeq") String mbSeq, 
			@RequestParam(value="page", required = false) String page,
			HttpServletRequest request
			) {
		
		//page 초기화
		HttpSession session = request.getSession();
		if(session.getAttribute("page") !=null) {			
			page = (String) session.getAttribute("page");
		}else if(page == null) {
			page = "1"; 
		}
		ModelAndView mav = new ModelAndView();
		mav = mbListCall(page, request);  //리스트만 가져오기
		Map map = new HashMap<String, String>();
		map.put("mbSeq", mbSeq);
		LoginDomain loginDomain = userService.mbSelectList(map);
		mav.addObject("item",loginDomain);
		mav.setViewName("admin/adminEditList.html");
		return mav; 
	}
	
	//리스트 가져오기 따로 함수뺌
    public ModelAndView mbListCall(String clickPage, HttpServletRequest request) { //클릭페이지 널이면 
		ModelAndView mav = new ModelAndView();
		//페이지네이션 추가  SELECT * FROM jsp.member order by mb_update_at limit 1, 5; {offset}{limit}
		// 전체갯수
		//pagenation
		
		// 페이지 저장
		System.out.println("pageBlock====>"+clickPage);
		HttpSession session = request.getSession();
		session.setAttribute("page", clickPage);
		Integer pageBlock = Integer.parseInt(clickPage);
		
		
		//전체 갯수
		int totalcount = userService.mbGetAll();
		//전체 페이지수  totalpage 10개면 for문으로 1-10까지 만든다.
		int contentnum = 10;
		int totalpage = totalcount / contentnum;
        if(totalcount % contentnum > 0){
            totalpage++;
        }
        
//        startPage = pageBlock == 1 ? 0 : pageBlock; // 음수일때는 0
        int offset;
        if(pageBlock == 1) {
        	offset = 0; //1이면 offset 0 
        }else {        	
        	offset = ((pageBlock)*contentnum)-contentnum; //페이지 2클릭시 11부터 시작
        }
//        startPage = startPage < 0 ? 0 : startPage; // 음수일때는 0
		System.out.println("offset"+offset);
        Map map = new HashMap<String, Integer>();
        map.put("offset",offset);
        map.put("contentnum",contentnum);
		
		List<LoginDomain> loginDomain = userService.mbAllList(map);
		System.out.println(loginDomain.size());
		System.out.println("loginDomain"+loginDomain);
		System.out.println("offset"+offset);
		System.out.println("totalpage"+totalpage);
		System.out.println("totalcount"+totalcount);
		
		boolean itemIsEmpty;
		if(loginDomain.isEmpty()) {
			itemIsEmpty = false;
		}else {
			itemIsEmpty = true;
		}
		
		// 타임리프에서 for문 돌리려면 
		List<Integer> list = new ArrayList();
		for(int i=0; i < totalpage; i++) {
			list.add(i);
		}
		
		mav.addObject("itemsIsEmpty", itemIsEmpty);
		mav.addObject("items", loginDomain);
		mav.addObject("totalpage", list);
		return mav;
	}
	
	@RequestMapping(value = "bdList")
	public ModelAndView bdList(@ModelAttribute("fileListVO") FileListVO fileListVO) { 
		//BindingResult nor plain target object for bean name 'fileListVO' available as request attribute
		ModelAndView mav = new ModelAndView();
		List<BoardListDomain> items = uploadService.boardList();
		System.out.println("items ==> "+ items);
		mav.addObject("items", items);
//		List<LoginDomain> loginDomain = userService.mbAllList();
//		mav.addObject("items", loginDomain);
		mav.setViewName("board/boardList.html");
		return mav; 
	}
	
	//삭제
	@GetMapping("/remove/{mbSeq}")
    public ModelAndView mbRemove(
    		@PathVariable("mbSeq") String mbSeq,
    		RedirectAttributes re,
    		HttpServletRequest request
    		) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		Map map = new HashMap<String, String>();
		map.put("mbSeq", mbSeq);
		userService.mbRemove(map);
		
		//page 초기화
		HttpSession session = request.getSession();
		String page;  // 삭제시에는 보던 리스트로 가야하기때문에 세션이용한다.
		if(session.getAttribute("page") !=null) {			
			 page = (String) session.getAttribute("page");
		}else {
			page = "1"; 
		}
		re.addAttribute("page", page);
		mav.setViewName("redirect:/mbList");
		return mav;
	}
	
	//수정페이지 이동
	@GetMapping("/modify/{mbSeq}")
    public ModelAndView mbModify(@PathVariable("mbSeq") String mbSeq, RedirectAttributes re) throws IOException {
		ModelAndView mav = new ModelAndView();
		re.addAttribute("mbSeq", mbSeq);
		mav.setViewName("redirect:/mbEditList");
		return mav;
	}
	
	//수정업데이트
	@RequestMapping("/update")
	public ModelAndView mbModify(
			LoginVO loginDTO, 
			HttpServletRequest request, 
			RedirectAttributes re
			) throws IOException {
		
		
		
		
		ModelAndView mav = new ModelAndView();
		System.out.println("loginDTO"+ loginDTO);
		
//		//page 초기화
		HttpSession session = request.getSession();
		String page = "1"; // 업데이트 되면 가장 첫화면으로 가야한다. 
		
		
		LoginDomain loginDomain = null; //초기화
		String IP = CommonUtils.getClientIP(request);
		loginDomain = LoginDomain.builder()
				.mbSeq(Integer.parseInt(loginDTO.getSeq()))
				.mbId(loginDTO.getId())
				.mbPw(loginDTO.getPw())
				.mbLevel("2")
				.mbIp(IP)
				.mbUse("Y")
				.build();
		userService.mbUpdate(loginDomain);
		re.addAttribute("page",page);
		mav.setViewName("redirect:/mbList");
		return mav;
	}

	@RequestMapping(value = "board")
	public ModelAndView login(@ModelAttribute("fileListVO") FileListVO fileListVO, LoginVO loginDTO, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//session 처리 
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();
		// 중복체크
		Map<String, String> map = new HashMap();
		map.put("mbId", loginDTO.getId());
		map.put("mbPw", loginDTO.getPw());
		
		System.out.println("dupleCheck0");
		System.out.println("map"+ map.get("mbId"));

		// 중복체크
		int dupleCheck = userService.mbDuplicationCheck(map);
		LoginDomain loginDomain = userService.mbGetId(map);
		System.out.println("dupleCheck01"+dupleCheck);
		
		if(dupleCheck == 0) {  
			String alertText = "없는 아이디이거나 패스워드가 잘못되었습니다. 가입해주세요";
			String redirectPath = "/main/signin";
			CommonUtils.redirect(alertText, redirectPath, response);
			return mav;
		}

		System.out.println("dupleCheck1");

		//현재아이피 추출
		String IP = CommonUtils.getClientIP(request);
		
		//session 저장
		session.setAttribute("ip",IP);
		session.setAttribute("id", loginDomain.getMbId());
		session.setAttribute("mbLevel", loginDomain.getMbLevel());
		
		System.out.println("dupleCheck2");
		
		List<BoardListDomain> items = uploadService.boardList();
		System.out.println("items ==> "+ items);
		mav.addObject("items", items);
		
		mav.setViewName("board/boardList.html"); 
		
		return mav;
	}
	
	@RequestMapping("logout")
	public ModelAndView logout(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		session.invalidate(); // 전체삭제
		mav.setViewName("index.html");
		return mav;
	}
	
	@GetMapping("message")
	@RequestMapping(value = "message", method = RequestMethod.GET)
	public ModelAndView messages() {
		ModelAndView mav = new ModelAndView("message/list");
		
		List<String> listTest = new ArrayList<String>();
		
		listTest.add("test1");
		listTest.add("test2");
		listTest.add("test3");
		
//		mav.addObject("messages", userService.list());
		mav.setViewName("index");
		mav.addObject("listTest",listTest);      // jstl로 호출
		mav.addObject("ObjectTest","테스트입니다."); // jstl로 호출
		mav.setViewName("index.html"); 
		
		System.out.println(mav);
		return mav;
	}
	
	
	
//	@GetMapping(value = "/list")
//	public ResponseEntity<?> list(){
//		
//		return ResponseEntity.ok().body(userService.list());
//	}
	
	
}
