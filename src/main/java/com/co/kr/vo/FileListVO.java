package com.co.kr.vo;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.Getter;

@Getter
public class FileListVO {
//
//	@Value("${upload.file.path}")
//	private String savePath;
//	
	private String mbId;
	private String title;
	private String content;
	private List<MultipartFile> files;
	
}
