package com.co.kr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.BoardContentDomain;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;

@Mapper
public interface UploadMapper {
	
	//content upload
	public void contentUpload(BoardContentDomain boardContentDomain);
	//file upload
	public void fileUpload(BoardFileDomain boardFileDomain);
	//list
	public List<BoardListDomain> boardList();
	
}
