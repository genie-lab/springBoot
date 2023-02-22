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
	@GetMapping("mbList")
	public ModelAndView mbList() {
		ModelAndView mav = new ModelAndView();
		mav = mbListCall();  //리스트만 가져오기
		mav.setViewName("admin/adminList.html");
		return mav; 
	}
	
	//대시보드 리스트 보여주기
	@GetMapping("mbEditList")
	public ModelAndView mbListEdit(@RequestParam("mbSeq") String mbSeq) {
		ModelAndView mav = new ModelAndView();
		mav = mbListCall();  //리스트만 가져오기
		Map map = new HashMap<String, String>();
		map.put("mbSeq", mbSeq);
		LoginDomain loginDomain = userService.mbSelectList(map);
		System.out.println("loginD=========="+loginDomain.getMbId());
		mav.addObject("item",loginDomain);
		mav.setViewName("admin/adminEditList.html");
		return mav; 
	}
	
	//리스트 가져오기 따로 함수뺌
	public ModelAndView mbListCall() {
		ModelAndView mav = new ModelAndView();
		List<LoginDomain> loginDomain = userService.mbAllList();
		boolean itemIsEmpty;
		if(loginDomain.isEmpty()) {
			itemIsEmpty = false;
		}else {
			itemIsEmpty = true;
		}
		mav.addObject("itemsIsEmpty", itemIsEmpty);
		mav.addObject("items", loginDomain);
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
    public ModelAndView mbRemove(@PathVariable("mbSeq") String mbSeq) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		Map map = new HashMap<String, String>();
		map.put("mbSeq", mbSeq);
		userService.mbRemove(map);
		
		mav.setViewName("redirect:/mbList");
		return mav;
	}
	
	//수정페이지 이동
	@GetMapping("/modify/{mbSeq}")
    public ModelAndView mbModify(@PathVariable("mbSeq") String mbSeq, RedirectAttributes re) throws IOException {
		System.out.println("mbSeq"+mbSeq);
		ModelAndView mav = new ModelAndView();
		re.addAttribute("mbSeq", mbSeq);
		mav.setViewName("redirect:/mbEditList");
		return mav;
	}
	
	//수정업데이트
	@RequestMapping("/update")
	public ModelAndView mbModify(LoginVO loginDTO, HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView mav = new ModelAndView();
		System.out.println("loginDTO"+ loginDTO);
		
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
