package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	//select one
	public BoardListDomain boardSelectOne(HashMap<String, Object> map);
	//select one file
	public List<BoardFileDomain> boardSelectOneFile(HashMap<String, Object> map);
	
	//delete content
	public void bdContentRemove(HashMap<String, Object> map);
	//delete file
	public void bdFileRemove(BoardFileDomain boardFileDomain);
	
	
	// 하나 수정
	public void bdContentUpdate(BoardContentDomain boardContentDomain);
	// 하나 수정
	public void bdFileUpdate(BoardFileDomain boardFileDomain);

}
