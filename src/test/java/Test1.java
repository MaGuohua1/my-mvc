import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class Test1 {

	private static final String REGEX = "/";

	@Test
	public void test() {
		String key = "{qqq}{aaa}";
		String string = "fffffff";
		Map<String, String> map = getParamMap(key, string);
		System.out.println(map);
//		aaa();
	}

	private Map<String, String> getParamMap(String key, String value) {
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
	
	public List<String> aaa() {
		String key = "/order/f{qqq}f{ss}f/{www}/{eee}";
		String uri = "/order/fffffff/gg/hhhhhhhhhhh";
		String[] keys = key.split(REGEX);
		String[] uris = uri.split(REGEX);
		if (keys.length != uris.length) {
			return null;
		}
		List<String> params = new ArrayList<>();
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].equals(uris[i])) {
				continue;
			}
			
			List<String> list = new ArrayList<>();
			for (String string : keys[i].split("\\}")) {
				String[] split = string.split("\\{");
				list.add(split[0]);
				if (split.length > 1) {
					params.add(split[1]);
				}
			}
			
			String str = uris[i];
			for (int j = list.size() - 1; j >= 0; j--) {
				str.lastIndexOf(list.get(j));
			}
			
			
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
		return null;
	}
}
