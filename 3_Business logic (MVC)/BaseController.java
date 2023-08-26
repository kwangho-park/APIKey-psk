package kr.com.dreamsecurity.hotp.controller;

import java.util.Hashtable;

public abstract class BaseController {
	
	protected void putErrorResult(Exception e, Hashtable<String, Object> result){
		result.put("result", "failed");
		result.put("message", e.getMessage());
	}
	
	protected void putErrorResult(Exception e, Hashtable<String, Object> result, int errorCode){
		result.put("result", "failed");
		String s = e.getMessage();
		if(s != null)
			result.put("message",  e.getMessage());
		else {
			result.put("message",  "처리중 예상하지 못한 에러가 발생 했습니다.");
		}
	}
}
