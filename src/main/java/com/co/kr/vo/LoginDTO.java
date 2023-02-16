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
	private String seq;
	private String id;
	private String pw;
	private String admin;
}