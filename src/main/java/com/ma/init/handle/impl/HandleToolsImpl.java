package com.ma.init.handle.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ma.anno.MyComponent;
import com.ma.init.handle.HandleTools;
import com.ma.init.resovler.ArgumentResovler;
import com.ma.init.vo.MyInstence;

@MyComponent
public class HandleToolsImpl implements HandleTools {

	@Override
	public Object[] handle(HttpServletRequest req, HttpServletResponse resp, Method method,
			Map<String, MyInstence> beans) {
		Class<?>[] types = method.getParameterTypes();
		Object[] args = new Object[types.length];
		ArgumentResovler resovler = null;
		Map<String, MyInstence> map = getInstancesType(beans,ArgumentResovler.class);
		for (int i = 0; i < types.length; i++) {
			for (Entry<String, MyInstence> entry : map.entrySet()) {
				Object instence = entry.getValue().getInstence();
				if (ArgumentResovler.class.isAssignableFrom(instence.getClass())) {
					resovler = (ArgumentResovler) instence;
					if(resovler.support(types[i], i, method)) {
						args[i] = resovler.resovle(req, resp, types[i], i, method);
						break;
					}
				}
			}
		}
		return args;
	}

	private Map<String, MyInstence> getInstancesType(Map<String, MyInstence> beans, Class<ArgumentResovler> class1) {
		Map<String, MyInstence> map = new HashMap<>();
		for (Entry<String, MyInstence> entry : beans.entrySet()) {
			if (ArgumentResovler.class.isAssignableFrom(entry.getValue().getInstence().getClass())) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return map;
	}

}
