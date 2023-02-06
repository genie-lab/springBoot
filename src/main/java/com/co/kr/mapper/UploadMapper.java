package com.co.kr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.FileListDomain;
import com.co.kr.vo.LoginDTO;

@Mapper
public interface UploadMapper {
	
	//upload
	public void fileUpload(FileListDomain fileListDomain);
	public List<FileListDomain> list();
	
}
