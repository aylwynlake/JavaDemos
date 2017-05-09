package com.em.utils;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;

/**
 * �����ʹ����
 * @author pccc
 */
@SuppressWarnings("rawtypes")
public class ClassUtil {
	// java.lang.Boolean -> Boolean.TYPE
	private static Map<String, String> abbreviationMap = new HashMap<String, String>(8);
	
	public static final String ARRAY_SUFFIX = "[]";
	// Boolean.class -> Boolean.TYPE
	private static final Map<String, Class> primitiveTypeNameMap = new HashMap<String, Class>(8);
	// Boolean.class -> Boolean.TYPE
	
	private static final Map<Class, Class> primitiveWrapperTypeMap = new HashMap<Class, Class>(8);
	
	static {
		/*
		 * ��̬�����
		 */
		
		// ������д
		abbreviationMap.put("int", "I");
		abbreviationMap.put("boolean", "Z");
		abbreviationMap.put("float", "F");
		abbreviationMap.put("long", "J");
		abbreviationMap.put("short", "S");
		abbreviationMap.put("byte", "B");
		abbreviationMap.put("double", "D");
		abbreviationMap.put("char", "C");
		
		// ԭ��װ����
		primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
		primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
		primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
		primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
		primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
		primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
		primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
		primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
		
		// ���� ������ �� ��Ķ�Ӧ��ϵ
		for (Iterator iterator = primitiveWrapperTypeMap.values().iterator(); iterator.hasNext();) {
			Class primitiveClass = (Class) iterator.next();
			primitiveTypeNameMap.put(primitiveClass.getName(), primitiveClass);
		}
	}
	
	/**
	 * �����Դ·������·����
	 * @param clazz
	 * @param resourceName
	 * @return
	 */
	public static String addResourcePathToPackagePath(Class clazz, String resourceName) {
		if (!resourceName.startsWith("/")) {
			return classPackageAsResourcePath(clazz) + "/" + resourceName;
		}
		return classPackageAsResourcePath(clazz) + resourceName;
	}
	
	/**
	 * ����To�ַ�
	 * @param args ����
	 * @return
	 */
	public static String argumentTypesToString(Object[] args) {
		return "(" + Arrays.toString(args) + ")";
	}
	
	/**
	 * ���ת��Ϊ�ļ���·��
	 * @param clazz
	 * @return
	 */
	public static String classPackageAsResourcePath(Class clazz) {
		if (clazz == null || clazz.getPackage() == null) {
			return "";
		}
		return clazz.getPackage().getName().replace('.', '/');
	}
	
	/**
	 * ��������
	 * @param sourceObject ԭ����
	 * @param targetObject Ŀ�����
	 */
	public static void copyFields(Object sourceObject, Object targetObject) {
		if (
			null == sourceObject 
			|| null == targetObject
		) {
			throw new NullPointerException("both of the objects should not be null.");
		}
		Map<String, Field> fieldMap = new HashMap<String, Field>();
		
		// ��ȡԭ����ȫ������
		Field[] sourceFields = getAllFields(sourceObject.getClass(), 24);
		for (int i = 0; i < sourceFields.length; i++) {
			Field field = sourceFields[i];
			fieldMap.put(field.getName(), field);
		}
		
		// ��ȡĿ�����ȫ������
		Field[] targetFields = getAllFields(targetObject.getClass(), 24);
		for (int i = 0; i < targetFields.length; i++) {
			Field targetField = targetFields[i];
			
			// ���ԭ�����в����ڸ����ԣ������
			Field sourceField = (Field) fieldMap.get(targetField.getName());

			if (null == sourceField)
				continue;
			try {
				sourceField.setAccessible(true);
				targetField.setAccessible(true);
				
				Object value = sourceField.get(sourceObject);
				targetField.set(targetObject, value);
				
			} catch (Exception ignore) {
				// �����쳣
			}
		}
	}
	
	/**
	 * ��ȡȫ������
	 * @param clazz ��
	 * @param filter ������
	 * @return
	 */
	public static Field[] getAllFields(Class clazz, int filter) {
		List<Field> list = new ArrayList<Field>();

		while (null != clazz) {
			CollectionUtils.addAll(list, getFields(clazz, filter));
			clazz = clazz.getSuperclass();
		}

		Field[] resultFields = new Field[list.size()];
		list.toArray(resultFields);

		return resultFields;
	}
	
	/**
	 * ��ȡ����ȫ���ӿڵ�����
	 * @param object ����
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Class[] getAllInterfaces(Object object) {
		Set interfaces = getAllInterfacesAsSet(object);
		return (Class[]) (Class[]) interfaces.toArray(new Class[interfaces.size()]);
	}
	
	/**
	 * ��ȡ����ȫ���ӿڵ�����
	 * @param object ����
	 * @return
	 */
	public static Set getAllInterfacesAsSet(Object object) {
		return getAllInterfacesForClassAsSet(object.getClass());
	}
	
	/**
	 * ��ȡ����ȫ���ӿڵ�����
	 * @param clazz ��
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Class[] getAllInterfacesForClass(Class clazz) {
		Set interfaces = getAllInterfacesForClassAsSet(clazz);
		return (Class[]) (Class[]) interfaces.toArray(new Class[interfaces.size()]);
	}
	
	/**
	 * ��ȡ����ȫ���ӿڵ�����
	 * @param clazz ��
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Set getAllInterfacesForClassAsSet(Class clazz) {
		return new HashSet(ClassUtils.getAllInterfaces(clazz));
	}

	/**
	 * ��ȡClass
	 * @param classLoader �������
	 * @param className ����
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class getClass(ClassLoader classLoader, String className)
			throws ClassNotFoundException {
		return getClass(classLoader, className, true);
	}

	/**
	 * ��ȡClass����
	 * @param classLoader �������
	 * @param className ����
	 * @param initialize whether the class must be initialized
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static Class getClass(ClassLoader classLoader, String className,
			boolean initialize) throws ClassNotFoundException {
		Class clazz;
		if (abbreviationMap.containsKey(className)) {
			String clsName = "[" + abbreviationMap.get(className);
			clazz = Class.forName(clsName, initialize, classLoader)
					.getComponentType();
		} else {
			clazz = Class.forName(toProperClassName(className), initialize,
					classLoader);
		}
		return clazz;
	}

	/**
	 * ��ȡ�ඨ��
	 * @param args ��Ҫ��ȡ�Ķ����б�
	 * @return
	 */
	public static Class[] getClasses(Object[] args) {
		if (ArrayUtils.isEmpty(args)) {
			return new Class[0];
		}

		Class[] classes = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			Object object = args[i];
			if (null == object) {
				throw new IllegalArgumentException(
						"the object array should not contains null");
			}

			classes[i] = object.getClass();
			if (primitiveWrapperTypeMap.containsKey(classes[i])) {
				classes[i] = ((Class) primitiveWrapperTypeMap.get(classes[i]));
			}

		}

		return classes;
	}
	
	/**
	 * ��ȡĬ���������
	 * @return
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader classLoader = null;
		try {
			classLoader = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ignore) {
			ignore.printStackTrace();
		}
		if (classLoader == null) {
			classLoader = ClassUtil.class.getClassLoader();
		}
		return classLoader;
	}

	/**
	 * ��ȡ����
	 * @param clazz ��
	 * @param fieldName �������
	 * @return
	 */
	public static Field getField(Class clazz, String fieldName) {
		Field[] fields = clazz.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}
		fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}
		return null;
	}

	/**
	 * ��ȡ����
	 * @param clazz
	 * @param filter
	 * @return
	 */
	public static Field[] getFields(Class clazz, int filter) {
		List<Field> list = new ArrayList<Field>();

		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];

			if ((field.getModifiers() & filter) == 0) {
				list.add(field);
			}
		}

		Field[] resultFields = new Field[list.size()];
		list.toArray(resultFields);

		return resultFields;
	}
	
	/**
	 * ��ȡ��������ֵ
	 * @param object ����
	 * @param fieldName �������
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static Object getFieldValue(Object object, String fieldName)
	throws NoSuchFieldException {
		try {
			if (null == object) {
				return null;
			}

			Field field = object.getClass().getDeclaredField(fieldName);

			if (null == field) {
				field = object.getClass().getField(fieldName);
			}

			if (null != field) {
				field.setAccessible(true);
				return field.get(object);
			}

			return null;
		} catch (Exception e) {
			if (e instanceof NoSuchFieldException) {
				try {
					return getNotAccessibleFieldValue(object, fieldName);
				} catch (Exception ex1) {
				}
			}
		}
		throw new NoSuchFieldException("The field of " + fieldName + "not founded.");
	}

	/**
	 * ��ȡ����
	 * @param clazz ��
	 * @param methodName �������
	 * @param parameterTypes ��������
	 * @return
	 */
	public static Method getMethod(Class clazz, String methodName,
			Class[] parameterTypes) {
		return getMethod(clazz, methodName, parameterTypes, true);
	}

	/**
	 * ��ȡ����
	 * @param clazz ��
	 * @param methodName �������
	 * @param parameterTypes ��������
	 * @param allowSuperType �Ƿ��鸸�෽��
	 * @return
	 */
	public static Method getMethod(Class clazz, String methodName,
			Class[] parameterTypes, boolean allowSuperType) {
		
		// ��ͨ����
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if ((method.getName().equals(methodName))
					&& (isParameterEqual(method, parameterTypes, allowSuperType))) {
				return method;
			}
		}
		
		// ����ʹ�õķ���
		methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if ((method.getName().equals(methodName))
					&& (isParameterEqual(method, parameterTypes, allowSuperType))) {
				return method;
			}
		}

		return null;
	}

	/**
	 * ��ȡ����
	 * @param clazz ��
	 * @param methodName �������
	 * @param args ��������
	 * @return
	 */
	public static Method getMethod(Class clazz, String methodName, Object[] args) {
		return getMethod(clazz, methodName, args, true);
	}

	/**
	 * ��ȡ����
	 * @param clazz ��
	 * @param methodName �������
	 * @param args ��������
	 * @param allowSuperType
	 * @return
	 */
	public static Method getMethod(Class clazz, String methodName, Object[] args,
			boolean allowSuperType) {
		Class[] classes = getClasses(args);
		return getMethod(clazz, methodName, classes, allowSuperType);
	}

	public static int getMethodCountForName(Class clazz, String methodName) {
		int count = 0;
		for (int i = 0; i < clazz.getDeclaredMethods().length; i++) {
			Method method = clazz.getDeclaredMethods()[i];
			if (methodName.equals(method.getName())) {
				count++;
			}
		}
		Class[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			count += getMethodCountForName(interfaces[i], methodName);
		}
		if (clazz.getSuperclass() != null) {
			count += getMethodCountForName(clazz.getSuperclass(), methodName);
		}
		return count;
	}

	/**
	 * 
	 * @param method
	 * @return
	 */
	public static String getMethodSingnature(Method method) {
		StringBuffer buffer = new StringBuffer();
		String methodName = method.getName();
		buffer.append(methodName);
		buffer.append("(");
		Class[] argumentTypes = method.getParameterTypes();
		for (int i = 0; i < argumentTypes.length; i++) {
			buffer.append(argumentTypes[i].getSimpleName());
			if (i < argumentTypes.length - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

	/**
	 * ��ȡ�޷����������ֵ
	 * @param object ����
	 * @param fieldName ����
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static Object getNotAccessibleFieldValue(Object object, String fieldName) 
	throws IllegalArgumentException, IllegalAccessException {
		Field field = null;
		Class superClass = object.getClass().getSuperclass();
		while (true) {
			if (superClass == null)
				return null;
			try {
				field = superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException noSuchFieldException1) {
				superClass = superClass.getSuperclass();
			}
			
			if (null != field) {
				field.setAccessible(true);
				return field.get(object);
			}
		}
	}

	public static String getQualifiedMethodName(Method method) {
		return method.getDeclaringClass().getName() + "." + method.getName();
	}

	public static String getQualifiedName(Class clazz) {
		if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		}

		return clazz.getName();
	}

	private static String getQualifiedNameForArray(Class clazz) {
		StringBuffer buffer = new StringBuffer();
		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			buffer.append(ARRAY_SUFFIX);
		}
		buffer.insert(0, clazz.getName());
		return buffer.toString();
	}

	public static Class getRealComponentType(Class clazz) {
		if (clazz.isArray()) {
			Class newClazz = clazz.getComponentType();
			return getRealComponentType(newClazz);
		}

		return clazz;
	}

	/**
	 * ��ȡ���Զ����
	 * @param clazz
	 * @return
	 */
	public static String getShortNameAsProperty(Class clazz) {
		return Introspector.decapitalize(ClassUtils.getShortClassName(clazz));
	}

	/**
	 * ��ȡ��̬����
	 * @param clazz ��
	 * @param methodName ������
	 * @param args 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Method getStaticMethod(Class clazz, String methodName, Class[] args) {
		try {
			Method method = clazz.getDeclaredMethod(methodName, args);
			if ((method.getModifiers() & 0x8) != 0)
				return method;
		} catch (NoSuchMethodException ex) {
		}
		return null;
	}
	
	/**
	 * �쳣���?��
	 * @param ex
	 */
	private static void handleInvocationTargetException(
			InvocationTargetException ex) {
		if (ex.getTargetException() instanceof RuntimeException) {
			throw ((RuntimeException) ex.getTargetException());
		}
		if (ex.getTargetException() instanceof Error) {
			throw ((Error) ex.getTargetException());
		}
		throw new IllegalStateException(
				"Unexpected exception thrown by method - "
						+ ex.getTargetException().getClass().getName() + ": "
						+ ex.getTargetException().getMessage());
	}
	
	/**
	 * �쳣���?��
	 * @param ex �쳣
	 */
	private static void handleReflectionException(Exception ex) {
		if ((ex instanceof NoSuchMethodException)) {
			throw new IllegalStateException("Method not found: " + ex.getMessage());
		}
		if ((ex instanceof IllegalAccessException)) {
			throw new IllegalStateException("Could not access method: " + ex.getMessage());
		}
		if ((ex instanceof InvocationTargetException)) {
			handleInvocationTargetException((InvocationTargetException) ex);
		}
		throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
	}
	
	/**
	 * �ж����Ƿ������Ӧ����
	 * @param clazz ��
	 * @param methodName 
	 * @return
	 */
	public static boolean hasMethod(Class clazz, String methodName) {
		for (int i = 0; i < clazz.getDeclaredMethods().length; i++) {
			Method method = clazz.getDeclaredMethods()[i];
			if (method.getName().equals(methodName)) {
				return true;
			}
		}
		Class[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			if (hasMethod(interfaces[i], methodName)) {
				return true;
			}
		}
		if (clazz.getSuperclass() != null) {
			return hasMethod(clazz.getSuperclass(), methodName);
		}
		return false;
	}
	
	public static boolean hasMethod(Class clazz, String methodName,
			Class[] argumentTypes) {
		return getMethod(clazz, methodName, argumentTypes) != null;
	}
	
	/**
	 * ���÷���
	 * @param clazz ��
	 * @param methodName ������
	 * @param args ��������Ҫ�Ĳ���
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokeMethod(Class clazz, String methodName,
			Object[] args) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Object target = clazz.newInstance();
		return getMethod(clazz, methodName, args).invoke(target, args);
	}
	
	/**
	 * ���÷���
	 * @param clazz ��
	 * @param methodName ������
	 * @param args ��������Ҫ�Ĳ���
	 * @param allowSuperType
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokeMethod(Class clazz, String methodName,
			Object[] args, boolean allowSuperType)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Object target = clazz.newInstance();
		return getMethod(clazz, methodName, args).invoke(target, args);
	}
	
	/**
	 * ����Ŀ����󷽷�
	 * @param method ����
	 * @param target ����
	 * @return
	 */
	public static Object invokeMethod(Method method, Object target) {
		return invokeMethod(method, target, null);
	}
	
	/**
	 * ����Ŀ����󷽷�
	 * @param method ����
	 * @param target ����
	 * @param args ����
	 * @return
	 */
	public static Object invokeMethod(Method method, Object target, Object[] args) {
		try {
			
			return method.invoke(target, args);
			
		} catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		} catch (InvocationTargetException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
		
	}
	
	/**
	 * ���÷���
	 * @param object ����
	 * @param methodName ������
	 * @param types ����
	 * @param args ��������Ҫ�Ĳ���
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokeMethod(Object object, String methodName, Class[] types, Object[] args) 
	throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return getMethod(object.getClass(), methodName, types).invoke(object, args);
	}
	
	/**
	 * ���÷���
	 * @param object ����
	 * @param methodName �������
	 * @param args ����
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokeMethod(Object object, String methodName, Object[] args) 
	throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return getMethod(object.getClass(), methodName, args).invoke(object, args);
	}
	
	/**
	 * ���÷���
	 * @param clazzName �����
	 * @param methodName ������
	 * @param args ��������Ҫ�Ĳ���
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws Throwable
	 */
	public static Object invokeMethod(String clazzName, String methodName, Object[] args) 
	throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException {
		Class clazz = getClass(getDefaultClassLoader(), clazzName);
		Object target = clazz.newInstance();
		return getMethod(clazz, methodName, args).invoke(target, args);
	}
	
	/**
	 * �ж��� Class �������ʾ�����ӿ���ָ���� Class �������ʾ�����ӿ��Ƿ���ͬ�����Ƿ����䳬��򳬽ӿڡ�
	 * @param targetType Ŀ������
	 * @param valueType ֵ����
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isAssignable(Class targetType, Class valueType) {
		return targetType.isAssignableFrom(valueType)
				|| (targetType.equals(primitiveWrapperTypeMap.get(valueType)));
	}
	
	/**
	 * 
	 * @param type ����
	 * @param value ֵ
	 * @return
	 */
	public static boolean isAssignableValue(Class type, Object value) {
		return !type.isPrimitive() ? true : value != null ? isAssignable(type, value.getClass()) : false;
	}
	
	/**
	 * �ж����������Ƿ������ͬ������
	 * @param sourceObject ԭ����
	 * @param targetObject Ŀ�����
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static boolean isEqualOnFields(Object sourceObject, Object targetObject) 
	throws IllegalArgumentException, IllegalAccessException {
		if (null == sourceObject && null == targetObject) {
			return true;
		}

		if (null == sourceObject || null == targetObject) {
			return false;
		}

		if (sourceObject.getClass() != targetObject.getClass()) {
			return false;
		}

		if (sourceObject instanceof Comparable) {
			return 0 == ((Comparable) sourceObject).compareTo(targetObject);
		}

		Field[] fields = getAllFields(sourceObject.getClass(), 24);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];

			field.setAccessible(true);

			Object sourceValue = field.get(sourceObject);
			Object targetValue = field.get(targetObject);

			if (field.getType().isArray()) {
				if (!ArrayUtils.isEquals(sourceValue, targetValue)) {
					return false;
				}

			} else if (!ObjectUtils.equals(sourceValue, targetValue)) {
				return false;
			}

		}

		return true;
	}
	
	/**
	 * �жϷ����Ƿ����Ҳ��һ�µ�
	 * @param method ����
	 * @param parameterTypes ��������
	 * @param allowSuperType �Ƿ��鸸�෽��
	 * @return
	 */
	public static boolean isParameterEqual(Method method,
			Class[] parameterTypes, boolean allowSuperType) {
		if (null == method) {
			return false;
		}
		Class[] methodParameterTypes = method.getParameterTypes();

		if ((ArrayUtils.isEmpty(parameterTypes))
				&& (ArrayUtils.isEmpty(methodParameterTypes))) {
			return true;
		}

		if (ArrayUtils.getLength(parameterTypes) != ArrayUtils
				.getLength(methodParameterTypes)) {
			return false;
		}

		if (allowSuperType) {
			return ClassUtils
					.isAssignable(parameterTypes, methodParameterTypes);
		}

		return Arrays.equals(parameterTypes, methodParameterTypes);
	}
	
	/**
	 * �ж����Ƿ����
	 * @param className ����
	 * @return
	 */
	public static boolean isPresent(String className) {
		try {
			getClass(getDefaultClassLoader(), className);
			return true;
		} catch (Throwable ex) {
		}
		return false;
	}
	
	/**
	 * �ж����Ƿ����
	 * @param className ����
	 * @param classLoader �������
	 * @return
	 */
	public static boolean isPresent(String className, ClassLoader classLoader) {
		try {
			getClass(classLoader, className);
			return true;
		} catch (Throwable ex) {
		}
		return false;
	}
	
	public static boolean isPrimitiveArray(Class clazz) {
		return (clazz.isArray()) && (getRealComponentType(clazz).isPrimitive());
	}
	
	public static boolean isPrimitiveOrWrapper(Class clazz) {
		return (clazz.isPrimitive()) || (isPrimitiveWrapper(clazz));
	}
	
	public static boolean isPrimitiveWrapper(Class clazz) {
		return primitiveWrapperTypeMap.containsKey(clazz);
	}
	
	public static boolean isPrimitiveWrapperArray(Class clazz) {
		return (clazz.isArray())
				&& (isPrimitiveWrapper(getRealComponentType(clazz)));
	}
	
	/**
	 * �ж��Ƿ�̬����
	 * @param targetClass ��
	 * @param methodName �������
	 * @param argumentTypes ��������
	 * @return
	 */
	public static boolean isStaticMethod(Class targetClass, String methodName, Class[] argumentTypes) {
		Method method = getMethod(targetClass, methodName, argumentTypes);
		return Modifier.isStatic(method.getModifiers());
	}
	
	/**
	 * �ж��Ƿ�̬����
	 * @param targetClass ��
	 * @param methodName �������
	 * @param args ����
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static boolean isStaticMethod(Class targetClass, String methodName, Object[] args) 
	throws IllegalArgumentException {
		Class[] classes = getClasses(args);
		return isStaticMethod(targetClass, methodName, classes);
	}
	
	/**
	 * �ж��Ƿ�̬����
	 * @param className �����
	 * @param methodName �������
	 * @param args ����
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static boolean isStaticMethod(String className, String methodName, Object[] args) 
	throws ClassNotFoundException {
		return isStaticMethod(Class.forName(className), methodName, args);
	}
	
	/**
	 * �������
	 * @param fileNames ����ļ�����
	 * @return
	 * @throws IOException
	 */
	public static URLClassLoader loadLibrary(String[] fileNames)
			throws IOException {
		File[] files = new File[fileNames.length];

		for (int i = 0; i < files.length; i++) {
			files[i] = new File(fileNames[i]);
		}

		URL[] urls = FileUtils.toURLs(files);
		return URLClassLoader.newInstance(urls);
	}
	
	/**
	 * �����µĶ���ʵ��
	 * @param className �����
	 * @param objects ����
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws LinkageError
	 */
	@SuppressWarnings("unchecked")
	public static Object newInstance(String className, Object[] objects)
	throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException, LinkageError {
		if (StringUtils.isBlank(className)) {
			return new IllegalArgumentException("the class name can't be null");
		}
		Class clazz = getClass(getDefaultClassLoader(), className);
		return ConstructorUtils.invokeConstructor(clazz, objects);
	}
	
	/**
	 * ��ȡԭʼ��
	 * @param name
	 * @return
	 */
	public static Class resolvePrimitiveClassName(String name) {
		Class result = null;

		if ((name != null) && (name.length() <= 8)) {
			result = (Class) primitiveTypeNameMap.get(name);
		}
		return result;
	}
	
	/**
	 * 
	 * @param className
	 * @return
	 */
	private static String toProperClassName(String className) {
		className = StringUtil.deleteWhiteSpace(className);
		if (className == null) {
			throw new IllegalArgumentException("the className can't be null");
		}
		if (className.endsWith(ARRAY_SUFFIX)) {
			StringBuffer classNameBuffer = new StringBuffer();
			for (; className.endsWith(ARRAY_SUFFIX); classNameBuffer.append("[")) {
				className = className.substring(0, className.length() - 2);
			}

			String abbreviation = (String) abbreviationMap.get(className);
			if (abbreviation != null) {
				classNameBuffer.append(abbreviation);
			} else {
				classNameBuffer.append("L").append(className).append(";");
			}
			className = classNameBuffer.toString();
		}
		return className;
	}

}
