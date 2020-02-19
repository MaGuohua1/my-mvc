package com.ma.init.listener;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ma.anno.MyAutowired;
import com.ma.anno.MyComponent;
import com.ma.anno.MyComponentScan;
import com.ma.anno.MyController;
import com.ma.anno.MyQualifier;
import com.ma.anno.MyRequestMapping;
import com.ma.anno.MyService;
import com.ma.init.AnnotationConfigInitializer;
import com.ma.init.vo.MyInstence;
import com.ma.utils.StringUtils;

public class MyListener implements ServletContextListener {
	
	private static Map<String, MyInstence> beans = new ConcurrentHashMap<>();//beans
	
	public static Map<String, MyInstence> getInstanceObjects() {
		return beans;
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		beans.clear();
	}
	
	public void contextInitialized(ServletContextEvent sce) {
		List<String> list = new ArrayList<>();
		// 获取配置类注解信息
		String[] filePaths = getConfigAnnoInfo();
		// 扫描获取类信息
		scanAll(filePaths,list);
		// 实例化
		getInstance(list);
		//依赖注入
		iocDI();
		//路径映射
		handleMapping();
	}

	//路径映射
	private void handleMapping() {
		if (beans.isEmpty()) return;
		
		for (Entry<String, MyInstence> entry : beans.entrySet()) {
			//为controller添加映射
			MyInstence instence = entry.getValue();
			Map<String, Method> mapping = instence.getMethodMapping();
			Class<?> cls = instence.getInstence().getClass();
			if (cls.isAnnotationPresent(MyController.class)) {
				for (Method method : cls.getDeclaredMethods()) {
					MyRequestMapping methodMapping = method.getAnnotation(MyRequestMapping.class);
					if (methodMapping!= null) {
						String clsMapping = StringUtils.addStart(instence.getMapping(), "/");
						String value = StringUtils.addStart(methodMapping.value(), "/");
						mapping.put(clsMapping+value,method);
					} 
				}
			}
			
		}
	}

	//依赖注入
	private void iocDI() {
		if (beans.isEmpty()) return;
		
		for (Entry<String, MyInstence> entry : beans.entrySet()) {
			Object instence = entry.getValue().getInstence();
			Field[] fields = instence.getClass().getDeclaredFields();
			for (Field field : fields) {
				inject(instence, field);//注入到instence的field
				
			}
		}
	}

	private void inject(Object instence, Field field) {
		if (instence == null || field == null) return;
		
		try {
			field.setAccessible(true);//private 允许设值
			
			//如果已经赋值,则跳过
			Object obj = field.get(instence);
			if (obj != null) {
				return;
			}
			
			//赋值操作
			if (field.isAnnotationPresent(MyAutowired.class)) {
				//存在MyQualifier注解时
				MyQualifier qualifier = field.getAnnotation(MyQualifier.class);
				if (qualifier != null) {
					String value = qualifier.value();
					if (beans.get(value)!= null) {
						field.set(instence, beans.get(value).getInstence());
						return;
					}
				}
				//不存在MyQualifier或未注入时
				//进行匹配,并放入map
				Map<String, Object> map = new HashMap<>();
				Object object = null;
				for (Entry<String, MyInstence> entry : beans.entrySet()) {
					object = entry.getValue().getInstence();
					Class<?> cls = object.getClass();
					boolean match = match(field.getType().getName(), cls);
					if (match) {
						map.put(StringUtils.lowerFirstCase(cls.getSimpleName()), object);
					}
				}
				//存在一个实例匹配
				if (map.size() == 1) {
					field.set(instence, object);
					map.clear();
					return;
				//存在多个匹配时,用属性名称匹配	
				} else if (map.size() > 1) {
					Object o = map.get(field.getName());
					if (o != null) {//属性名称匹配	
						field.set(instence, o);
						return;
					}
				} 
				//未匹配到
				throw new NullPointerException();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private boolean match(String name, Class<?> cls) {
		boolean flag = false;
		if (cls.isAnnotation()) {
			return false;
		}
		//匹配类型(本类)
		if (name.equals(cls.getName())) {
			return true;
		}
		//匹配类型(本类接口)
		if (cls.getInterfaces().length != 0) {
			for (Class<?> clazz : cls.getInterfaces()) {
				if (name.equals(clazz.getName())) {
					return true;
				}
				//接口继承递归
				flag = match(name, clazz);
			}
		}
		//继承递归
		if (cls.getSuperclass() != null && !cls.getSuperclass().equals(Object.class)) {
			flag = match(name, cls.getSuperclass());
		}
		return flag;
	}

	private void scanAll(String[] filePaths, List<String> list) {
		for (String string : filePaths) {
			scan(string,list);
		}
	}

	private void getInstance(List<String> list) {
		if (list.size() <= 0) return;
		
		List<Class<?>> clss = new ArrayList<Class<?>>();
		//过滤没必要生成实例的class和已经生成实例的class
		classFilter(list, clss);
		//生成实例
		for (Class<?> cls : clss) {
			getInstance(cls);
		}
	}

	private void classFilter(List<String> list, List<Class<?>> clss) {
		for (String str : list) {
			try {
				Class<?> cls = Class.forName(str);
				Annotation[] annotations = cls.getAnnotations();
				for (Annotation anno : annotations) {
					if (anno instanceof MyController || anno instanceof MyService || anno instanceof MyComponent) {
						clss.add(cls);
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Object getInstance(Class<?> cls) {
		if (cls == null) return null;
		
		//生成key值
		String key = StringUtils.lowerFirstCase(cls.getSimpleName());
		
		MyInstence instence = beans.get(key);
		if (instence != null) {
			return instence;
		}
		
		try {
			if (!cls.isInterface()) {
				instence = new MyInstence();
				Object obj = cls.newInstance();
				MyRequestMapping mapping = cls.getAnnotation(MyRequestMapping.class);
				instence.setMapping(mapping == null ? null : StringUtils.addStart(mapping.value(),"/"));
				instence.setInstence(obj);
				
				MyController controller = cls.getAnnotation(MyController.class);
				MyService service = cls.getAnnotation(MyService.class);
				MyComponent component = cls.getAnnotation(MyComponent.class);
				if (controller != null && !controller.value().isEmpty()) {
					key = controller.value();
				} else if(service != null && !service.value().isEmpty()) {
					key = service.value();
				} else if(component != null && !component.value().isEmpty()) {
					key = component.value();
				}
				
				beans.put(key, instence);
				return instence;
			}
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	@SuppressWarnings("unused")
	private Object getInstance(String str) {
		if (str == null || "".equals(str)) return null;
		
		try {
			Class<?> cls = Class.forName(str);
			return getInstance(cls);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	

	// 扫描获取类信息
	private void scan(String filePath, List<String> list) {
		if (filePath == null || filePath.isEmpty()) {
			filePath = "";
		}
		String url = getClass().getClassLoader()
				.getResource("/" + filePath.replaceAll("\\.", "/")).getFile();
		File file = new File(url);
		for (String path : file.list()) {
			File fi = new File(url+path);
			if (fi.isDirectory()) {
				String string = "".equals(filePath) ? path : (filePath + "." + path);
				scan(string, list);
			} else {
				if(fi.getName().endsWith(".class")) {
					list.add(filePath+"."+fi.getName().replace(".class", ""));
				}
			}
		}
	}

	// 获取配置类注解信息
	private String[] getConfigAnnoInfo() {
		Map<String, AnnotationConfigInitializer> map = new HashMap<>();
		List<String> list = new ArrayList<>();
		scan(null, list);
		int count = 0;
		for (String name : list) {
			try {
				Class<?> clazz = Class.forName(name);
				boolean flag = AnnotationConfigInitializer.class.isAssignableFrom(clazz);
				if (!clazz.isInterface() && flag) {
					AnnotationConfigInitializer initializer = (AnnotationConfigInitializer) clazz.newInstance();
					map.put(name, initializer);
					count++;
				}
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (count <= 0) {
			throw new NullPointerException("AnnotationConfigInitializer接口未被实例化");
		}
		for (Entry<String, AnnotationConfigInitializer> entry : map.entrySet()) {
			AnnotationConfigInitializer initializer = entry.getValue();
			Class<?>[] classes = initializer.getConfigClasses();
			for (Class<?> cls : classes) {
				for (Annotation anno : cls.getDeclaredAnnotations()) {
					if (anno instanceof MyComponentScan) {
						return ((MyComponentScan) anno).value();
					}
				}
			}
		}
		return null;
	}

}
