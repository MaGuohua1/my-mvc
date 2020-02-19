package com.ma.init.vo;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyInstence {

	private Object instence;
	private	String mapping;
	private Map<String, Method> methodMapping = new ConcurrentHashMap<>();
	
	public Object getInstence() {
		return instence;
	}
	
	public void setInstence(Object instence) {
		this.instence = instence;
	}
	
	public String getMapping() {
		return mapping;
	}
	
	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public Map<String, Method> getMethodMapping() {
		return methodMapping;
	}

}
