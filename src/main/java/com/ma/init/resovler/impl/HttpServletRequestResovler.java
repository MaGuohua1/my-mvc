package com.ma.init.resovler.impl;

import java.lang.reflect.Method;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ma.anno.MyComponent;
import com.ma.init.resovler.ArgumentResovler;

@MyComponent
public class HttpServletRequestResovler implements ArgumentResovler {

	@Override
	public boolean support(Class<?> type, int index, Method method) {
		return ServletRequest.class.isAssignableFrom(type);
	}

	@Override
	public Object resovle(HttpServletRequest req, HttpServletResponse resp, Class<?> type, int index, Method method) {
		return req;
	}

}
