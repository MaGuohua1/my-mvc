package com.ma.init.handle;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ma.init.vo.MyInstence;

public interface HandleTools {

	Object[] handle(HttpServletRequest req, HttpServletResponse resp,
			Method method, Map<String, MyInstence> beans);
}
