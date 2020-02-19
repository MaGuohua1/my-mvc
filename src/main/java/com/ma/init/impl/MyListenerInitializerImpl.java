package com.ma.init.impl;

import javax.servlet.ServletContext;

import com.ma.init.MyInitializer;
import com.ma.init.listener.MyListener;

public class MyListenerInitializerImpl implements MyInitializer {

	public void onStartup(ServletContext context) {
		context.addListener(MyListener.class);
	}

}
