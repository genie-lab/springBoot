package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class FileListDomain {

	
	private String table;
	
	private String mbId;
	
	private String upOriginalFileName;
	private String upNewFileName; //동일 이름 업로드 될 경우
	private String upFilePath;
	private Integer upFileSize;
	
	private String upTitle;
	private String upContent;
	
	
	
	
}
