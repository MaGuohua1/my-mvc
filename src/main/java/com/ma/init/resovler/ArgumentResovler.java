package com.ma.init.resovler;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ArgumentResovler {

	boolean support(Class<?> type, int index, Method method);
	Object resovle(HttpServletRequest req, HttpServletResponse resp, Class<?> type, int index, Method method);
}
