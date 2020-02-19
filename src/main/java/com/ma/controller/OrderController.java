package com.ma.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ma.anno.MyAutowired;
import com.ma.anno.MyController;
import com.ma.anno.MyPathVariable;
import com.ma.anno.MyRequestMapping;
import com.ma.anno.MyRequestParam;
import com.ma.service.OrderService;

@MyController("order")
@MyRequestMapping("order")
public class OrderController {
	
	@MyAutowired
	private OrderService service;
	
	@MyRequestMapping("insert/{name}")
	public void insert(HttpServletRequest req, HttpServletResponse resp, @MyPathVariable String name,
			@MyRequestParam int age, @MyRequestParam Date date) {
		String string = service.insert(name,age);
		try {
			resp.getWriter().write(string);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public OrderService getService() {
		return service;
	}

	
}
