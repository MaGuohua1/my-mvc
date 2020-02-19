package com.ma.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CastUtils {
	
	private static final Map<String, Class<?>> MAPPER  = new HashMap<>();
	private static final List<String> POSSIBLE_PATTERNS = new ArrayList<>();
	private static final Map<Class<?>, Object> DEFAULT_VALUE_MAP = new HashMap<>();
	
	static {
		MAPPER.put("boolean", Boolean.class);
		MAPPER.put("byte", Byte.class);
		MAPPER.put("short", Short.class);
		MAPPER.put("int", Integer.class);
		MAPPER.put("long", Long.class);
		MAPPER.put("float", Float.class);
		MAPPER.put("double", Double.class);
		MAPPER.put("char", Character.class);
		
		POSSIBLE_PATTERNS.add("yyyy-MM-dd");
		POSSIBLE_PATTERNS.add("yyyy-MM-dd HH:mm:ss");
		POSSIBLE_PATTERNS.add("yyyyMMdd");
		POSSIBLE_PATTERNS.add("yyyy/MM/dd");
		POSSIBLE_PATTERNS.add("yyyy/MM/dd HH:mm:ss");
		POSSIBLE_PATTERNS.add("yyyy年MM月dd日");
		POSSIBLE_PATTERNS.add("yyyy MM dd");
		
		DEFAULT_VALUE_MAP.put(boolean.class, false);
		DEFAULT_VALUE_MAP.put(byte.class, (byte) 0);
		DEFAULT_VALUE_MAP.put(short.class, (short) 0);
		DEFAULT_VALUE_MAP.put(int.class, 0);
		DEFAULT_VALUE_MAP.put(long.class, 0L);
		DEFAULT_VALUE_MAP.put(float.class, 0.0f);
		DEFAULT_VALUE_MAP.put(double.class, 0.0d);
		DEFAULT_VALUE_MAP.put(char.class, null);
	}

	
	
	public static Map<Class<?>, Object> getDefaultValueMap() {
		return DEFAULT_VALUE_MAP;
	}

	public static Object cast(String param, Class<?> type) {
		if (type == String.class) {
			return param;
		}
		if (type == Date.class) {
			return castToDate(param);
		}
		Class<?> cls = type;
		if (cls.isPrimitive()) {
			cls = MAPPER.get(cls.getSimpleName());
		}
		if (cls.getName().indexOf("java.lang") == 0) {
			try {
				Method method = cls.getMethod("valueOf", String.class);
				if (method !=  null) {
					Object object = method.invoke(null, param);
					return object;
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException 
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	private static Date castToDate(String param) {
		SimpleDateFormat format = new SimpleDateFormat();
		ParsePosition position = new ParsePosition(0);
		
		Date date = null;
		for (String string : POSSIBLE_PATTERNS) {
			format.applyPattern(string);
			format.setLenient(false);
			Date parse = format.parse(param,position);
			position.setIndex(0);
			if (parse != null) {
				date = parse;
			}
		}
		return date;
	}
}
