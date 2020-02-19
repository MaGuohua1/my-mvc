package com.ma.init;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

@HandlesTypes(MyInitializer.class)
public class MyServletContainerInitializer implements ServletContainerInitializer{

	public void onStartup(Set<Class<?>> arg0, ServletContext arg1) throws ServletException {
		if (arg0.size()==0) {
			return;
		}
		for (Class<?> cls : arg0) {
			try {
				MyInitializer instance = (MyInitializer) cls.newInstance();
				instance.onStartup(arg1);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}

}
