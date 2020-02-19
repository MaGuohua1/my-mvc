package com.ma.init.resovler.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ma.anno.MyComponent;
import com.ma.anno.MyPathVariable;
import com.ma.anno.MyRequestMapping;
import com.ma.init.resovler.ArgumentResovler;
import com.ma.utils.CastUtils;
import com.ma.utils.StringUtils;

@MyComponent
public class PathVariableResovler implements ArgumentResovler {

	@Override
	public boolean support(Class<?> type, int index, Method method) {
		Annotation[] annotations = method.getParameterAnnotations()[index];
		for (Annotation annotation : annotations) {
			return MyPathVariable.class.isAssignableFrom(annotation.getClass());
		}
		return false;
	}

	@Override
	public Object resovle(HttpServletRequest req, HttpServletResponse resp, Class<?> type, int index, Method method) {
		String param = null;
		String uri = req.getRequestURI().replace(req.getContextPath(), "");
		
		String mapping = method.getDeclaringClass().getAnnotation(MyRequestMapping.class).value();
		mapping = mapping.startsWith("/") ? mapping : "/".concat(mapping);
		String str = method.getAnnotation(MyRequestMapping.class).value();
		str = str.startsWith("/") ? str : "/".concat(str);
		mapping = mapping.concat(str);
		
		Map<String, String> paramMap = StringUtils.getUrlMap(mapping, uri);
		Annotation[] annotations = method.getParameterAnnotations()[index];
		for (Annotation annotation : annotations) {
			if (MyPathVariable.class.isAssignableFrom(annotation.getClass())) {
				String name = method.getParameters()[index].getName();
				param = paramMap.get(name);
			}
		}
		
		if (param!= null && !param.isEmpty()) {
			return CastUtils.cast(param, type);
		} else {
			
		}
		return CastUtils.getDefaultValueMap().get(type);
	}

}
