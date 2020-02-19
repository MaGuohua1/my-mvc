package com.ma.init;

import javax.servlet.ServletContext;

public interface MyInitializer {

	void onStartup(ServletContext context);
}
