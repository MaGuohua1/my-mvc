package com.ma.init.servlet;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ma.anno.MyPathVariable;
import com.ma.init.handle.HandleTools;
import com.ma.init.listener.MyListener;
import com.ma.init.vo.MyInstence;
import com.ma.utils.CastUtils;
import com.ma.utils.StringUtils;


public class MyServlet extends HttpServlet{

	private static final long serialVersionUID = 6656465808883829822L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyInstence instence = null;
		HandleTools tool = null;
		Method method = null;
		
		// 问号前面的 uri=/my-mvc/order/insert, contextPath=/my-mvc
		String uri = req.getRequestURI().replace(req.getContextPath(), "");
		String classUri = "/" + uri.split("/")[1];
		String methodUri = uri.substring(classUri.length());
		
		//匹配类
		Map<String, MyInstence> instanceObjects = MyListener.getInstanceObjects();
		for (Entry<String, MyInstence> entry : instanceObjects.entrySet()) {
			MyInstence value = entry.getValue();
			if (classUri.equals(value.getMapping())) {
				instence = value;
			}
			if (HandleTools.class.isAssignableFrom(value.getInstence().getClass())) {
				tool = (HandleTools) value.getInstence();
			}
		}
		if (instence == null) {
			resp.getWriter().write("URL地址" + classUri + "错误");
			return;
		}
		
		//匹配方法
		for (Entry<String, Method> en : instence.getMethodMapping().entrySet()) {
			String key = en.getKey();
			Method value = en.getValue();
			if (key.equals(uri)) {
				method = value;
				break;
			}
			
			//对带有参数的uri匹配方法
			method = getMethodWithParams(uri, en);
			if (method != null) {
				break;
			}
		}
		if (method == null) {
			resp.getWriter().write("URL地址" + methodUri + "错误");
			return;
		}
		
		
		Object[] args = tool.handle(req, resp, method, instanceObjects);//获取参数
		
		try {
			method.invoke(instence.getInstence(), args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private Method getMethodWithParams(String uri, Entry<String, Method> en) {
		String key = en.getKey();
		Method value = en.getValue();
		
		//按照uri获取参数key-value
		Map<String, String> params = StringUtils.getUrlMap(key, uri);
		if (params == null || params.isEmpty()) {
			return null;
		}
		
		//获取方法中匹配注解类型的参数map
		Map<String, Class<?>> typeMap = getTypeMap(value,MyPathVariable.class);
		
		//校验方法参数名称与uri名称是否匹配
		//校验值是否符合匹配的类型
		boolean flag = checkNameAndValueMatch(typeMap, params);
		if (flag) 
			return null;
		
		return value;
	}

	private boolean checkNameAndValueMatch(Map<String, Class<?>> typeMap, Map<String, String> params) {
		boolean flag = false;
		for (Entry<String, Class<?>> name : typeMap.entrySet()) {
			for (Entry<String, String> param : params.entrySet()) {
				if (!name.getKey().equals(param.getKey())) {
					System.out.println("没有匹配的参数[" + name + "]");
					flag = true;
				}
				if (name.getKey().equals(param.getKey())) {
					Object cast = CastUtils.cast(param.getValue(), name.getValue());
					if (cast == null) {
						flag = true;
						throw new ClassCastException("参数+[" + param.getKey() + "]的类型不能匹配类[" + name.getValue().getName() + "]。");
					}
				}
			}
		}
		return flag;
	}

	private Map<String, Class<?>> getTypeMap(Method method, Class<?> cls) {
		Map<String,Class<?>> typeMap = new HashMap<>();
		for (int i = 0; i < method.getParameterAnnotations().length; i++) {
			Annotation[] annos = method.getParameterAnnotations()[i];
			for (int j = 0; j < annos.length; j++) {
				if (cls.isAssignableFrom(annos[j].getClass())) {
					String name = method.getParameters()[i].getName();
					Class<?> type = method.getParameterTypes()[i];
					typeMap.put(name, type);
				}
			}
		}
		return typeMap;
	}

}
