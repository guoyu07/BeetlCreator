package liyf.beetl.util;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.management.InstanceAlreadyExistsException;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.ResourceLoader;
import org.beetl.core.exception.ScriptEvalError;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.servlet.ServletGroupTemplate;

public class Creator {
	private static Creator instance = null;

	private Random ra = null;
	private static List<String> randomStringList = null;
	private GroupTemplate groupTemplate = null;

	public GroupTemplate getGroupTemplate() {
		return groupTemplate;
	}

	public void setGroupTemplate(GroupTemplate groupTemplate) {
		this.groupTemplate = groupTemplate;
	}

	private Creator() {
		// 生成默认全局模板
		if (groupTemplate == null) {
			ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader();
			Configuration cfg = null;
			try {
				cfg = Configuration.defaultConfiguration();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			cfg.setDirectByteOutput(true);
			groupTemplate = new GroupTemplate(resourceLoader, cfg);
		}
		ra = new Random();
	}

	public static Creator newInstance() {
		if (instance == null) {
			instance = new Creator();
		}
		return instance;
	}

	public static BufferedReader readFileAsBufferedReader(String pathname) {
		File file = new File(pathname);
		BufferedReader br = null;
		try {
			if (!file.exists() || file.isDirectory())
				throw new FileNotFoundException();
			br = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return br;
	}

	private static List<String> RandomStringList() {
		if (randomStringList != null) {
			return randomStringList;
		}
		String pathname = Toolkit.getDefaultToolkit().getClass()
				.getResource("/RandomString.dic").getFile();
		BufferedReader br = readFileAsBufferedReader(pathname);
		List<String> list = new ArrayList<String>();
		String tmp = null;

		try {
			while ((tmp = br.readLine()) != null) {
				list.add(tmp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return randomStringList = list;
	}

	private <T> T randomValue(Class<T> clazz) {
		Object obj = null;
		if (clazz == boolean.class || clazz == Boolean.class) {
			obj = ra.nextBoolean();
		} else if (clazz == byte.class || clazz == Byte.class) {
			obj = (byte) (ra.nextInt(Byte.MAX_VALUE) + Byte.MIN_VALUE);
		} else if (clazz == char.class || clazz == Character.class) {
			obj = (char) ra.nextInt(Character.MAX_VALUE);
		} else if (clazz == short.class || clazz == Short.class) {
			obj = (short) (ra.nextInt(Short.MAX_VALUE) + Short.MIN_VALUE);
		} else if (clazz == int.class || clazz == Integer.class) {
			obj = ra.nextInt();
		} else if (clazz == long.class || clazz == Long.class) {
			obj = ra.nextLong();
		} else if (clazz == float.class || clazz == Float.class) {
			obj = ra.nextFloat();
		} else if (clazz == double.class || clazz == Double.class) {
			obj = ra.nextDouble();
		} else if (clazz == String.class) {
			// Unicode:0x4E00 – 0x9FFF
			// 生成5到10的字符串
			/*
			 * int len = ra.nextInt(5) + 5; StringBuffer sb = new
			 * StringBuffer(); for (int i = 0; i < len; i++) {
			 * sb.append("\\u").append( Integer.toHexString(ra.nextInt(0x4000) +
			 * 0x4e00)); } obj = decodeUnicode(sb.toString());
			 */
			int i = ra.nextInt(RandomStringList().size() - 1);
			obj = RandomStringList().get(i);
		} else if (clazz == void.class || clazz == Void.class) {

		} else if (clazz.isArray()) {

			Class<?> componentType = clazz.getComponentType();
			// 随机数组长度，0到5
			int len = ra.nextInt(5);
			T arr = (T) Array.newInstance(componentType, len);
			System.out.println(arr);
			for (int i = 0; i < len; i++) {
				Array.set(arr, i, randomValue(componentType));
			}
			obj = arr;

			// }else if (clazz == Map.class) {
			// Map<String, Object> map = new HashMap<String, Object>();
			// for(int i=0,len = ra.nextInt(3)+2;i<len; i++){
			// map.put("key"+ i, ra.nextInt());
			// }
			// obj = map;
			// }else if (clazz == List.class){
		} else if (clazz == Date.class) {
			obj = new Date(ra.nextLong());
		} else {
			System.out.println("sorry，" + clazz + "暂不支持，请反馈beetl官方论坛");
		}
		return (T) obj;
	}
	
	private void setValue(Field field, Object object,Object cfgObj){
		Class cfgClass = cfgObj.getClass();
		Class objClass = field.getType();
		if(objClass == cfgClass){
			//类型一致
			try {
				field.set(object, cfgObj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}else{
			//类型不一致
			try {
				if(cfgClass == ArrayList.class && objClass.isArray()){
					ArrayList list = (ArrayList)cfgObj;
					field.set(object, list.toArray(listToArray(list, objClass)));
				}else{
					//Integer对象放入int等
					field.set(object, cfgObj);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/***
	 * 传入对象的字段，并根据字段类型生成随机值
	 * 
	 * @param field
	 * @param object
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws UnsupportedEncodingException
	 */
	private void setValue(Field field, Object object)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> fClazz = field.getType();
		field.set(object, randomValue(fClazz));
	}
	
	/**
	 * 生成javabean的单一实例并且赋随机值
	 * 
	 * @param classType
	 * @return
	 */
	public <T> T single(Class<T> classType) {
		T obj = null;
		try {
			obj = classType.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		// 外层的for循环是为了把该javabean的父类找出来
		for (Class<?> clazz = classType; clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field f : fields) {
				f.setAccessible(true);
				String fieldName = f.toString().substring(
						f.toString().lastIndexOf(".") + 1);
				try {
					setValue(f, obj);
					System.out.println(fieldName + ':' + f.get(obj));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				f.setAccessible(false);
			}
		}
		return obj;
	}

	public <T>T singleByCfg(String path) {
		String cfgPath = "/"+path.replace(".", "/")+".var";
		Map commonData = null, data = null;
		String commonFile = cfgPath;
		System.out.println("---------------");
		try {
			commonData = groupTemplate.runScript(commonFile, null, null);
		} catch (ScriptEvalError e) {
			throw new RuntimeException("伪模型脚本有错！");
		}
		
		Class c = null;
		try {
			c = Class.forName(path);
			// class文件存在
			System.out.println("class found!");
			Object obj = null;
			try {
				obj = c.newInstance();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
			// 外层的for循环是为了把该javabean的父类找出来
			for (Class<?> clazz = c; clazz != Object.class; clazz = clazz
					.getSuperclass()) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field f : fields) {
					f.setAccessible(true);
					String fieldName = f.toString().substring(
					f.toString().lastIndexOf(".") + 1);
					try {
						setValue(f,obj,commonData.get(fieldName));
						System.out.println(fieldName + ':' + f.get(obj));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					f.setAccessible(false);
				}
			}
			return (T)obj;
		} catch (ClassNotFoundException e) {
			// class文件不存在
			System.out.println("class not found!");
			return (T)commonData;
		}
	}
	/***
	 * 随机生成多个实例
	 * @param classType
	 * @param size
	 * @return
	 */
	public <T> T[] list(Class<T> classType, int size) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < size; i++) {
			list.add(single(classType));
		}
		T[] arr = (T[]) Array.newInstance(classType, size);
		T[] listArr = list.toArray(arr);
		return listArr;
	}

	/***
	 * 随机生成多个实例
	 * @param path
	 * @param size
	 * @return
	 */
	public <T> T[] listByCfg(String path,int size) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < size; i++) {
			list.add((T) singleByCfg(path));
		}
		T[] arr = (T[]) Array.newInstance(list.get(0).getClass(), size);
		T[] listArr = list.toArray(arr);
		return listArr;
	}
	
	/***
	 * unicode码表转成 字符串
	 * 
	 * @param unicodeStr
	 * @return
	 */
	public static String decodeUnicode(String unicodeStr) {
		if (unicodeStr == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int maxLoop = unicodeStr.length();
		for (int i = 0; i < maxLoop; i++) {
			if (unicodeStr.charAt(i) == '\\') {
				if ((i < maxLoop - 5)
						&& ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr
								.charAt(i + 1) == 'U')))
					try {
						sb.append((char) Integer.parseInt(
								unicodeStr.substring(i + 2, i + 6), 16));
						i += 5;
					} catch (NumberFormatException localNumberFormatException) {
						sb.append(unicodeStr.charAt(i));
					}
				else
					sb.append(unicodeStr.charAt(i));
			} else {
				sb.append(unicodeStr.charAt(i));
			}
		}
		return sb.toString();
	}

	/***
	 * 把list转成数组array
	 * @param list list对象
	 * @param arrType 数组类型
	 * @return
	 */
	private <T> T[] listToArray(List list,Class<T> arrType){
		Class<?> componentType = arrType.getComponentType();
		T[] arr = (T[]) Array.newInstance(componentType, list.size());
		System.out.println(arr);
		for (int i = 0; i < list.size(); i++) {
			Array.set(arr, i, randomValue(componentType));
		}
		return arr;
	}
	public static void main(String args[]) throws IOException {
		 UserBean userBean = Creator.newInstance().single(UserBean.class);
		 UserBean[] userBeanArr = Creator.newInstance().list(UserBean.class, 3);
		 //根据配置生成对象，路径格式与class的路径相同，找到了生成对象，找不到生成map，配置文件名为相应的 类名.var  例如 User.var
		 User user = Creator.newInstance().singleByCfg("liyf.beetl.util.User");
		 Map userMap = Creator.newInstance().singleByCfg("liyf.beetl.util.UserNoClass");
		 System.out.println(userMap);
		 
		 User[] users = Creator.newInstance().listByCfg("liyf.beetl.util.User", 3);
		 Map[] userMaps = Creator.newInstance().listByCfg("liyf.beetl.util.UserNoClass", 2);
		 System.out.println(userMaps[0]);
	}
}
