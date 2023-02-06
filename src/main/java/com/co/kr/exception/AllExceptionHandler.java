package com.co.kr.exception;

import java.io.IOException;
import java.util.HashSet;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class AllExceptionHandler {
	
	
	// request error
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public HttpEntity<ErrorResponse> handlerBindingResultException(RequestException exception){
		
		// catch exception
		if(exception.getException() != null) {
			Exception ex = exception.getException();
			StackTraceElement [] steArr = ex.getStackTrace();
			for(StackTraceElement ste : steArr) {
				System.out.println(ste.toString());
			}
		}
		
		// response 담기
		ErrorResponse errRes = ErrorResponse.builder()
				.result(exception.getCode().getResult())
				.resultDesc(exception.getCode().getResultDesc())
				.resDate(CommonUtils.currentTime())
				.reqNo(exception.getReqNo())
				.httpStatus(exception.getHttpStatus())
				.build();
		
		return new ResponseEntity<ErrorResponse>(errRes, errRes.getHttpStatus());
	}
	
	
	//db error
	@ExceptionHandler(InternalServerError.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public HttpEntity<ErrorResponse> handelerInternalServerError(InternalException exception) {
		System.out.println("=========Internal Error=========" + exception.getMessage());
		ErrorResponse errRes = ErrorResponse.builder()
				.result(exception.getCode().getResult())
				.resultDesc(exception.getCode().getResultDesc())
				.resDate(CommonUtils.currentTime())
				.reqNo(CommonUtils.currentTime())
				.build();
		return new ResponseEntity<ErrorResponse>(errRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	// error page
	@ExceptionHandler(Exception.class)
	public ModelAndView commonException(Exception e) {
		e.getStackTrace();
		ModelAndView mv = new ModelAndView();
		mv.addObject("exception", e.getStackTrace());
		mv.setViewName("commons/commonErr.html");
		return mv;
	}
	
	
	//multipart error
//	public  ResponseEntity<?> handle(org.springframework.web.multipart.MultipartException exception) {
//		log.error("handle->MultipartException" + exception.getMessage(),exception);
//		// general exception
//		if (exception.getCause() instanceof IOException && exception.getCause().getMessage().startsWith("The temporary upload location"))
//		{
//			String pathToRecreate = exception.getMessage().substring(exception.getMessage().indexOf("[")+1,exception.getMessage().indexOf("]"));
//			Set<PosixFilePermission> perms = new HashSet<>();
//		    // add permission as rw-r--r-- 644
//		    perms.add(PosixFilePermission.OWNER_WRITE);
//		    perms.add(PosixFilePermission.OWNER_READ);
//		    perms.add(PosixFilePermission.OWNER_EXECUTE);	    
//		    perms.add(PosixFilePermission.GROUP_READ);
//		    perms.add(PosixFilePermission.GROUP_WRITE);
//		    perms.add(PosixFilePermission.GROUP_EXECUTE);
//		    FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(perms);
//			try {			
//				Files.createDirectories(FileSystems.getDefault().getPath(pathToRecreate), fileAttributes);
//			} catch (IOException e) {
//				LOG.error(e.getMessage(),e);
//				return ResponseUtils.sendError("Unable to recreate deleted temp directories. Please check  "+ pathToRecreate);
//			}	
//			return ResponseUtils.sendError("Recovered from temporary error by recreating temporary directory. Please try to upload logo again.");		
//		}
//		return ResponseUtils.sendError("Unable to process this request.");
//	}
	
	
}
