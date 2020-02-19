package com.ma.init.impl;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;

import com.ma.init.MyInitializer;
import com.ma.init.servlet.MyServlet;

public class MyContextInitializerImpl implements MyInitializer {

	public void onStartup(ServletContext context) {
		Dynamic servlet = context.addServlet("servlet", MyServlet.class);
		servlet.addMapping("/");
		servlet.setLoadOnStartup(0);
	}

}
