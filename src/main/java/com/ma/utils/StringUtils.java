package com.ma.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtils {
	
	private static final String REGEX = "/";
	
	public static String addStart(String str, String start) {
		return str.startsWith(start) ? str : start + str;
	}
	
	public static String lowerFirstCase(String name) {
		return name.substring(0, 1).toLowerCase()+name.substring(1);
	}

	public static List<String> matchMethod(String template, String target) {
		String[] keys = template.split("/");
		String[] uris = target.split("/");
		if (keys.length != uris.length) {
			return null;
		}
		List<String> list = new ArrayList<>();
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].equals(uris[i])) {
				continue;
			}
			String str = uris[i];
			String start = keys[i].substring(0, keys[i].indexOf('{'));
			String end = keys[i].substring(keys[i].indexOf('}') + 1);
			if (str.startsWith(start)) {
				str = str.substring(start.length());
			}
			if (str.endsWith(end)) {
				str = str.substring(0, str.length()-end.length());
			}
			list.add(str);
		}
		return list;
	}

	public static Map<String, String> getUrlMap(String key, String uri) {
		Map<String, String> map = new HashMap<String, String>();
		
		String[] keys = key.split(REGEX);
		String[] uris = uri.split(REGEX);
		if (keys.length != uris.length) {
			return null;
		}
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].equals(uris[i])) {
				continue;
			}
			
			Map<String, String> paramMap = getParamMap(keys[i],uris[i]);
			if (paramMap.size() == 0) {
				return null;
			}
			map.putAll(paramMap);
		}
		return map;
	}
	
	public static Map<String, String> getParamMap(String key, String value) {
		if (!key.contains("{") || !key.contains("}")) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		
		List<String> list = new ArrayList<>();
		List<String> params = new ArrayList<>();
		for (String str : key.split("\\}")) {
			String[] split = str.split("\\{");
			list.add(split[0]);
			if (split.length > 1) {
				params.add(split[1]);
			}
		}
		int count = list.size();
		
		if (list.get(0)!= null && value.startsWith(list.get(0))) {
			value = value.substring(list.get(0).length());
			list.remove(0);
		}
		if (list.size() > 0) {
			String last = list.get(list.size()-1);
			if (count > params.size() && value.endsWith(last)) {
				value = value.substring(0, value.length() - last.length());
				list.remove(list.size()-1);
			}
		}
		
		int length = 0;
		for (String str : list) {
			length += str.length();
		}
		if (value.length() < length) {
			System.out.println("URI路径匹配长度太小");
			return null;
		}
		StringBuilder str = new StringBuilder(value);
		for (int i = list.size() - 1; i >= 0; i--) {
			int start = str.lastIndexOf(list.get(i));
			int end = start + list.get(i).length();
			str.replace(start, end, REGEX);
		}
		String[] values = str.toString().split(REGEX);
		if (values.length <= params.size()) {
			for (int i = 0; i < values.length; i++) {
				map.put(params.get(i), values[i]);
			}
			for (int i = values.length; i < params.size(); i++) {
				map.put(params.get(i), "");
			}
		}
		return map;
	}
	
}
