package com.co.kr.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class LoginDTO {
	private String id;
	private String pw;
	private String mb_use;
//	private String mb_create_at;
//	private String mb_create_at;
	
}
