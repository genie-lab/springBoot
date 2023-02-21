package com.co.kr.service;

import java.io.File;
import java.io.IOException;
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

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
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
import com.co.kr.domain.BoardContentDomain;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.exception.InternalException;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.UploadMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UploadServiceImpl implements UploadService {

	@Autowired
	UploadMapper uploadMapper;
	
	@Override
	public void fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request) {
			
				//session 생성
				HttpSession session = request.getSession();
				
				//content domain 생성 
				BoardContentDomain boardContentDomain = BoardContentDomain.builder()
						.table(Table.BOARD.getTable())
						.mbId(session.getAttribute("id").toString())
						.bdTitle(fileListVO.getTitle())
						.bdContent(fileListVO.getContent())
						.build();
				// db 저장
				uploadMapper.contentUpload(boardContentDomain);
		
				
				int bdSeq = boardContentDomain.getBdSeq();
				String mbId = boardContentDomain.getMbId();
				System.out.println("getBdSeq=====>>>>>>"+bdSeq);
				
				
				List<MultipartFile> multipartFiles = request.getFiles("files");
				
				//파일 존재하지 않을 경우
				if(CollectionUtils.isEmpty(multipartFiles)) {
					throw RequestException.fire(Code.E404, "잘못된 업로드 파일", HttpStatus.NOT_FOUND);
				}

				Path rootPath = Paths.get(new File("C://").toString(),"upload", File.separator).toAbsolutePath().normalize();			
				File pathCheck = new File(rootPath.toString());
				
				// folder chcek
				if(!pathCheck.exists()) pathCheck.mkdirs();
				
				List list = new ArrayList();
				
				for(MultipartFile multipartFile : multipartFiles) {
					
					//확장자 추출
					String originalFileExtension;
					String contentType = multipartFile.getContentType();
		            String origFilename = multipartFile.getOriginalFilename();

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
					System.out.println("targetPath=====> "+targetPath);
					
					File file = new File(targetPath.toString());
	
					try {
						//파일복사저장
						multipartFile.transferTo(file);
		                // 파일 권한 설정(쓰기, 읽기)
		                file.setWritable(true);
		                file.setReadable(true);
		                
		                
						//파일 domain 생성 
						BoardFileDomain boardFileDomain = BoardFileDomain.builder()
								.table(Table.FILES.getTable())
								.bdSeq(bdSeq)
								.mbId(mbId)
								.upOriginalFileName(origFilename)
								.upNewFileName(newFileName)
								.upFilePath(targetPath.toString())
								.upFileSize(Long.valueOf(multipartFile.getSize()).intValue())
								.build();
						
						// db 저장
						uploadMapper.fileUpload(boardFileDomain);
		                
					} catch (IOException e) {
						throw RequestException.fire(Code.E404, "잘못된 업로드 파일", HttpStatus.NOT_FOUND);
					}
					
					System.out.println("upload done");
				}

	}
	
	

	@Override
	public List<BoardListDomain> boardList() {
		return uploadMapper.boardList();
	}

}
