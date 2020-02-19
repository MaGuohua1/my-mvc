package com.ma.service.impl;

import com.ma.anno.MyService;
import com.ma.service.OrderService;

@MyService
public class OrderServiceImpl implements OrderService {

	public String insert(String name, int age) {
		String str = "[ name = "+ name +", age = "+ age +"]";
		return str;
	}

}
