package com.co.kr.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMethodMappingNamingStrategy;

import com.co.kr.code.Code;
import com.co.kr.code.Table;
import com.co.kr.domain.FileListDomain;
import com.co.kr.exception.InternalException;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.UploadMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UploadServiceImpl implements UploadService {

	@Autowired
	UploadMapper uploadMapper;
	
	@Override
	public ModelAndView fileProcess(FileListDTO fileListDTO, MultipartHttpServletRequest request) {
		
		
		request.getFiles("files");
		ModelAndView mav = new ModelAndView();
//		try {
				List<MultipartFile> multipartFiles = request.getFiles("files");
				
				//파일 존재하지 않을 경우
				if(CollectionUtils.isEmpty(multipartFiles)) {
					throw RequestException.fire(Code.E404, "잘못된 업로드 파일", HttpStatus.NOT_FOUND);
				}

				
				//root path, new File("") == root path
				Path basicPath = Paths.get(new File("C://").toString(),"upload").toAbsolutePath().normalize();
				System.out.println(basicPath);
				
				String strPath = basicPath.toString() + File.separator; // separator 추가
				Path rootPath = Paths.get(strPath).toAbsolutePath().normalize(); // 절대경로
				
				File pathCheck = new File(rootPath.toString());
				

				
				// folder chcek
				if(!pathCheck.exists()) pathCheck.mkdirs();
				
				List list = new ArrayList();
				///////////////////////////////////////////////////////
				

				for(MultipartFile multipartFile : multipartFiles) {
					
					
					//확장자 추출
					String originalFileExtension;
					String contentType = multipartFile.getContentType();
					

					//확장자 조재안을경우
					if(ObjectUtils.isEmpty(contentType)){
						break;
					}else { // 확장자가 jpeg, png인 파일들만 받아서 처리
						if(contentType.contains("image/jpeg")) {
							originalFileExtension = ".jpg";
						}else if(contentType.contains("image/png")) {
							originalFileExtension = ".png";
						}else {
							break;
						}
					}
					
					//파일명을 업로드한 날짜로 변환하여 저장
					String uuid = UUID.randomUUID().toString();
					String current = CommonUtils.currentTime();
					//System.out.println(current);
					String newFileName = uuid + current + originalFileExtension;
					
					System.out.println(newFileName);
					
					
					//경로에 파일저장
				
					Path targetPath = rootPath.resolve(newFileName);
					
					System.out.println(targetPath);
					
//					multipartFile.transferTo(targetPath);

					System.out.println(newFileName);
					
					File file = new File(targetPath.toString());
					//파일복사저장  // 파일/ 패스 /옵션
					try {
						Files.copy(multipartFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// inputstream 닫기;
					
					file.setWritable(true);
					file.setReadable(true);
					
					System.out.println("sdfsdf");

					
					//파일 domain 생성 
					FileListDomain fileListDomain = FileListDomain.builder()
							.table(Table.UPLOAD.getTable())
							.mbId(fileListDTO.getMbId())
							.upOriginalFileName(multipartFile.getOriginalFilename())
							.upNewFileName(newFileName)
							.upFilePath(targetPath.toString())
							.upFileSize(Long.valueOf(multipartFile.getSize()).intValue())
							.upTitle(fileListDTO.getTitle())
							.upContent(fileListDTO.getContent())
							.build();
					
					try {
						multipartFile.getInputStream().close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					System.getProperty("java.io.tmpdir");
					
//					FileListDomain fileListDomain = FileListDomain.builder()
//							.table(Table.UPLOAD.getTable())
//							.mbId(fileListDTO.getMbId())
//							.upOriginalFileName("c")
//							.upNewFileName("c")
//							.upFilePath("c")
//							.upFileSize(Long.valueOf(1L).intValue())
//							.upTitle(fileListDTO.getTitle())
//							.upContent(fileListDTO.getContent())
//							.build();
					

					// db 저장
					uploadMapper.fileUpload(fileListDomain);
					
//					List<FileListDomain> data =  list();
					list.add(fileListDomain);

					System.out.println("fileListDomain" +list);
				}
					mav.addObject("fileList",list);
					mav.setViewName("board.html");
			
//		} catch (Exception e) {
//			// TODO: handle exception
//			log.info("[uplaodAPI ] DB error");
//			throw RequestException.fire(Code.E400); //디비연동실페 
//			
//		}
		
		return mav;
		
	}

	@Override
	public List<FileListDomain> list() {
		
		return uploadMapper.list();
	}

}
