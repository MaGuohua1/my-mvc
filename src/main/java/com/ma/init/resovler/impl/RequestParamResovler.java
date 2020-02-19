package com.ma.init.resovler.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ma.anno.MyComponent;
import com.ma.anno.MyRequestParam;
import com.ma.init.resovler.ArgumentResovler;
import com.ma.utils.CastUtils;

@MyComponent
public class RequestParamResovler implements ArgumentResovler {

	@Override
	public boolean support(Class<?> type, int index, Method method) {
		Annotation[] annotations  = method.getParameterAnnotations()[index];
		for (Annotation anno : annotations) {
			return MyRequestParam.class.isAssignableFrom(anno.getClass());
		}
		return false;
	}

	@Override
	public Object resovle(HttpServletRequest req, HttpServletResponse resp, Class<?> type, int index, Method method) {
		String param = null;
		Annotation[] annotations  = method.getParameterAnnotations()[index];
		for (Annotation anno : annotations) {
			if(MyRequestParam.class.isAssignableFrom(anno.getClass())) {
				String value = ((MyRequestParam) anno).value();
				if (value == null || value.isEmpty()) {
					value = method.getParameters()[index].getName();
				}
				param = req.getParameter(value);
			}
		}
		if (param!= null && !param.isEmpty()) {
			return CastUtils.cast(param, type);
		}
		//外部传过来的值为空时
		return CastUtils.getDefaultValueMap().get(type);
	}

}
