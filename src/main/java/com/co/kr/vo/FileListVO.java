package com.co.kr.vo;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileListVO {
//
//	@Value("${upload.file.path}")
//	private String savePath;
//	
	private String isEdit;
	private String seq;
	private String title;
	private String content;
//	private List<MultipartFile> files;
	
}
