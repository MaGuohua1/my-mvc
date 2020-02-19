package com.ma.config;

import com.ma.init.AnnotationConfigInitializer;

public class MyAnnotationConfigInitializer implements AnnotationConfigInitializer {

	public Class<?>[] getConfigClasses() {
		return new Class<?>[] {AppConfig.class};
	}

}
