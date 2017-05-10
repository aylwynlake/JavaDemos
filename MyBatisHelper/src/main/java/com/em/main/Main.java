package com.em.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.em.utils.JdbcUtil;
import com.em.utils.StringUtil;
import com.em.utils.Mapping;

public class Main {

	// 配置项：换行符
	@SuppressWarnings("restriction")
	static String lineSeparator = (String) java.security.AccessController
			.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

	// 配置项：输出目录
        static String outputPath = "d:\\mybatishelper\\output";
        // 配置项：生成文件的字符集
        static String charSet = "UTF-8";

        /*
         * 配置项：数据库连接配置
         */

        /*
         * oracle JDBC 链接
         */
        static String driver = "oracle.jdbc.driver.OracleDriver";
        static String url = "jdbc:oracle:thin:@172.31.217.104:1521/BDCFG";
        static String user = "wbank";
        static String password = "Wbank0413";
        
	// 配置项：生成DAO java文件的包路径
	// static String packageStr = "com.spdbccc.service.iface.dao";
	static String packageStr = "com.embd.text";

	// 配置项：生成DAO java文件的包路径(EM后修改为相关业务分类)
	static String packageStrPojoEx = "domain.words";
	static String packageStrDaoEx = "mapper.words";
	static String packageStrServiceEx = "service.words";
	static String packageStrServiceImplEx = "service.words";
	
	// 配置项：查询语句
        static String sql = "select * from WD_BANK_BASEINFO";

        static String sqlCount = "select count(*) from WD_BANK_BASEINFO";
        // 配置项：模式名，如果为空，则表名前将不带上模式名称
        static String schemaName = "";
        // 配置项：对应的实体对象名称
        static String beanName = "WDBankBaseInfo";
        // 配置项：表名
        static String tabName = "WD_BANK_BASEINFO".toUpperCase();

        // 配置项：主键字段名称
        static String pkName = "EID".toUpperCase();
        static String[] pks = pkName.split(",");

        // 配置项：其他可以作为删除条件的主键，如果为空的话，则生成对应的删除语句
        static String otherDeleteKey = "".toUpperCase();

	// 配置项：DAO基类的完整类路径
	// static String baseDaoFull = "com.spdbccc.opas.common.dao.AbstractDao";
	// static String baseDaoFull = "com.em.dao.base.AbstractDao";
	// 配置项：DAO基类的类名
	// static String baseDao = StringUtils.substringAfterLast(baseDaoFull, ".");

	// 配置项：是否需要生成插入方法
	static boolean needInsert = true;
	// 配置项：是否需要生成删除方法
	static boolean needDelete = true;
	// 配置项：是否需要生成更新方法
	static boolean needUpdate = true;
	// 配置项：是否需要生成查询方法
	static boolean needSelect = true;

	// 配置项：是否需要生成全部查询数据方法，慎用，一定要开发组提需求，并且是参数表才能放开该功能
	static boolean needSelectAll = true;

	// 配置项：是否需要生成全部条数、分页用
	static boolean needSelectCount = true;

	// 配置项：是否需要生成列表插入方法
	static boolean needListInsert = true;
	// 配置项：是否需要生成列表更新方法
	static boolean needListUpdate = true;

	

	// 配置项：查询成列表的条件列表
	static List<String> queryConditionsList = new ArrayList<String>();
	static {
		// queryConditionsList.add("QUEUE_NO");
		// queryConditionsList.add("OWNER_NO,STATUS");
	}

	

	// static String driver = "com.mysql.jdbc.Driver";
	// static String url = "jdbc:mysql://localhost:3306/localdb";
	// static String user = "root";
	// static String password = "root";

	static List<Mapping> orMappingList = null;
	static Map<String, Mapping> orMappingMap = null;

	static boolean hasBigDecimal = false;
	static boolean hasDate = false;

	private static String getFullTabName() {
		return StringUtil.isBlank(schemaName) ? tabName : schemaName + "." + tabName;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		/**
		 * 开始数据库处理
		 */
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;

		try {
			// 创建连接
			conn = JdbcUtil.createConnection(driver, url, user, password);
			stat = conn.createStatement();

			rs = stat.executeQuery(sql);

			orMappingList = JdbcUtil.getORMappingListOracle(rs);

			for (Mapping mapping : orMappingList) {

				String javaType = mapping.getJavaType();

				if ("BigDecimal".equalsIgnoreCase(javaType)) {
					hasBigDecimal = true;
				} else if ("Date".equalsIgnoreCase(javaType)) {
					hasDate = true;
				}
			}

			orMappingMap = JdbcUtil.getORMappingMapOracle(rs);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stat);
			JdbcUtil.close(conn);
		}

		// 输出JAVABEAN文件
		writeJavaBean();

		writeDaoJavaInterface();

		writeServiceJavaInterface();

		writeServiceJavaInterfaceImpl();

		writeSqlMap();

		// writeDaoTrans_xml();

		// writeDubboConfig_xml();

		// commonColumnsList.add("CRT_USER");
		// commonColumnsList.add("CRT_DATE");
		// commonColumnsList.add("CRT_TIME");
		// commonColumnsList.add("LST_UPD_USER");
		// commonColumnsList.add("LST_UPD_TIME");
		// commonColumnsList.add("LST_UPD_DATE");
		// commonColumnsList.add("REC_STATUS");
		// commonColumnsList.add("SCR_LEVEL");
		System.out.println("****************END****************");
	}

	/**
	 * 
	 * @throws IOException
	 */
	public static void writeJavaBean() throws IOException {
		Writer out = null;

		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputPath + File.separator + beanName + ".java"), charSet), 8 * 1024);

			// package com.em.pojo;

			// out.write("package " + packageStr + "." + beanName.toLowerCase()
			// + ";" + lineSeparator);
			out.write("package " + packageStr + "." + packageStrPojoEx + ";" + lineSeparator);
			out.write(lineSeparator);
			out.write("import java.io.Serializable;" + lineSeparator);
			if (hasBigDecimal) {
				// import java.math.*;
				out.write("import java.math.BigDecimal;" + lineSeparator);
			}
			if (hasDate) {
				// import java.sql.*;
				out.write("import java.util.Date;" + lineSeparator);
			}

			out.write(lineSeparator);
			// @SuppressWarnings("serial")
			out.write("@SuppressWarnings(\"serial\")" + lineSeparator);
			// public class Bean {
			out.write("public class " + beanName + " implements Serializable {" + lineSeparator);
			out.write(lineSeparator);

			// 先输出
			String propertyName = null;
			String javaType = null;

			// 先输出全部属性
			for (Mapping mapping : orMappingList) {

				propertyName = mapping.getPropertyName(); // 属性名称
				javaType = mapping.getJavaType(); // JAVA类型

				// String custNo;
				out.write("\tprivate " + javaType + " " + propertyName + ";" + lineSeparator);
			}

			out.write(lineSeparator);

			// 再输出全部get和set方法
			for (Mapping mapping : orMappingList) {

				propertyName = mapping.getPropertyName(); // 属性名称
				javaType = mapping.getJavaType(); // JAVA类型

				// public BigDecimal getAmt() {
				out.write("\tpublic " + javaType + " get" + StringUtil.getMethodPropertyName(propertyName) + "() {"
						+ lineSeparator);
				// return amt;
				out.write("\t\treturn " + propertyName + ";" + lineSeparator);
				// }
				out.write("\t}" + lineSeparator);

				// public void setAmt(BigDecimal amt) {
				out.write("\tpublic void set" + StringUtil.getMethodPropertyName(propertyName) + "(" + javaType + " "
						+ propertyName + ") {" + lineSeparator);

				if ("String".equals(javaType)) {
					// this.username = (username == null ? null :
					// username.trim());
					out.write("\t\tthis." + propertyName + " = (" + propertyName + " == null ? null : " + propertyName
							+ ".trim());" + lineSeparator);
				} else {
					// this.amt = amt;
					out.write("\t\tthis." + propertyName + " = " + propertyName + ";" + lineSeparator);
				}
				// }
				out.write("\t}" + lineSeparator);
			}

			out.write(lineSeparator);

			out.write("}" + lineSeparator);

			out.flush();

		} finally {
			// 关闭输出流
			out.close();
		}
	}

	private static List<String> converArrays(String[] strs) {
		List<String> list = new ArrayList<String>();
		for (String string : strs) {
			list.add(StringUtil.trimToEmpty(string));
		}
		return list;
	}

	private static String getMethodByColnames(List<String> colList) {
		String str = "";

		String colName = null;
		for (int i = 0; i < colList.size(); i++) {
			colName = colList.get(i);

			Mapping mapping = orMappingMap.get(colName);

			str += StringUtil.getMethodPropertyName(mapping.getPropertyName());

			if (i != colList.size() - 1) {
				str += "And";
			}
		}
		return str;
	}

	private static String getMethodParamByColnames(List<String> colList) {
		String str = "";

		String colName = null;
		for (int i = 0; i < colList.size(); i++) {
			colName = colList.get(i);

			Mapping mapping = orMappingMap.get(colName);

			str += mapping.getJavaType() + " " + mapping.getPropertyName();

			if (i != colList.size() - 1) {
				str += ",";
			}
		}
		return str;
	}

	private static String getBeanSetString(List<String> colList) {
		String str = "";

		String objectName = StringUtil.getMethodPropertyName2(beanName);

		str += ("\t\t" + beanName + " " + objectName + " = new " + beanName + "();" + lineSeparator);

		String colName = null;
		for (int i = 0; i < colList.size(); i++) {
			colName = colList.get(i);

			Mapping mapping = orMappingMap.get(colName);

			str += ("\t\t" + objectName + "." + mapping.getSetMethodName() + "(" + mapping.getPropertyName() + ");"
					+ lineSeparator);
		}
		return str;
	}

	private static String getJavaFullType(List<String> colList) {
		String str = null;
		if (colList.size() == 1) {

			Mapping mapping = orMappingMap.get(colList.get(0));

			str = JdbcUtil.convertJdbcType2JavaTypeFull(mapping.getColumnType());
		} else {

			str = packageStr + "." + beanName.toLowerCase() + "." + beanName;
		}

		return str;
	}

	private static String getWhereConditionString(List<String> colList) {
		String whereCondition = "";

		String colName = null;
		for (int i = 0; i < colList.size(); i++) {
			colName = colList.get(i);

			Mapping mapping = orMappingMap.get(colName);

			whereCondition += (i > 0 ? "\t\t  and " : "") + mapping.getColumnName() + " = " + getColValueString(mapping)
					+ lineSeparator;

		}
		return whereCondition;
	}

	public static void writeDaoJavaInterface() throws IOException {
		Writer out = null;

		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputPath + File.separator + beanName + "Mapper.java"), charSet), 8 * 1024);

			// package com.huateng.dao;
			out.write("package " + packageStr + "." + packageStrDaoEx + ";" + lineSeparator);

			out.write(lineSeparator);

			out.write("import " + packageStr + "." + packageStrPojoEx + "." + beanName + ";" + lineSeparator);
			// out.write("import java.io.Serializable;"+lineSeparator);

			if (hasBigDecimal) {
				// import java.math.*;
				out.write("import java.math.BigDecimal;" + lineSeparator);
			}
			// if (hasDate) {
			// // import java.sql.*;
			// out.write("import java.util.Date;" + lineSeparator);
			// }
			out.write("import java.util.List;" + lineSeparator);

			// out.write("import com.huateng.neofp.core.CoreException;" +
			// lineSeparator);

			out.write(lineSeparator);
			//
			// public interface TbUserService {
			out.write("public interface " + beanName + "Mapper {" + lineSeparator);
			out.write(lineSeparator);

			// 获取对象参数名称
			String objectName = StringUtil.getMethodPropertyName2(beanName);

			if (needInsert) {
				// /*
				// * 增 insert
				// */
				// public int insertTbUser(TbUser tbUser) ;
				// public int insertTbUserSelective(TbUser tbUser) throws
				// CoreException;
				out.write("\tpublic int insert(" + beanName + " " + objectName + ");" + lineSeparator);
				out.write("\tpublic int insertSelective(" + beanName + " " + objectName + ") ;"
						+ lineSeparator);

			}

			if (needListInsert) {
				// public int insertTestQueueList(List<TestQueue> list) throws
				// CoreException;
				// public int insertTestQueueListSelective(List<TestQueue> list)
				// ;

				out.write("\tpublic int insertList(List<" + beanName + "> list) ;" + lineSeparator);
				out.write("\tpublic int insertListSelective(List<" + beanName + "> list); "
						+ lineSeparator);

			}

			// Mapping pkMapping = orMappingMap.get(pkName);

			List<String> pksList = converArrays(pks);

			if (needDelete) {

				// 设有主键
				if (pksList.size() > 1) {
					// /*
					// * 删 delete
					// */
					// public int deleteTbUserById(Integer id) throws
					// CoreException;
					// out.write("\tpublic int delete" + beanName + "By" +
					// getMethodByColnames(pksList) + "("
					// + getMethodParamByColnames(pksList) + ") ;" +
					// lineSeparator);

					out.write("\tpublic int deleteBy" + getMethodByColnames(pksList) + "(" + beanName
							+ " " + objectName + ") ;" + lineSeparator);

				} else {
					out.write("\tpublic int deleteBy" + getMethodByColnames(pksList) + "("
							+ getMethodParamByColnames(pksList) + ") ;" + lineSeparator);
				}

				Mapping otherMapping = orMappingMap.get(otherDeleteKey);
				if (otherMapping != null) {
					// /*
					// * 删 delete
					// */
					// public int deleteTbUserById(Integer id) throws
					// CoreException;
					out.write("\tpublic int deleteBy"
							+ StringUtil.getMethodPropertyName(otherMapping.getPropertyName()) + "("
							+ otherMapping.getJavaType() + " " + otherMapping.getPropertyName() + ") ;"
							+ lineSeparator);

				}
			}

			if (needUpdate) {
				// /*
				// * 改 update
				// */
				// public int updateTbUser(TbUser tbUser) ;
				// public int updateTbUserSelective(TbUser tbUser) throws
				// CoreException;
				out.write("\tpublic int update(" + beanName + " " + objectName + ") ;" + lineSeparator);
				out.write("\tpublic int updateSelective(" + beanName + " " + objectName + ") ;"
						+ lineSeparator);

			}

			if (needListUpdate) {

				// public int insertTestQueueList(List<TestQueue> list) throws
				// CoreException;
				// public int insertTestQueueListSelective(List<TestQueue> list)
				// ;

				out.write("\tpublic int updateList(List<" + beanName + "> list) ;" + lineSeparator);
				out.write("\tpublic int updateListSelective(List<" + beanName + "> list) ;"
						+ lineSeparator);
			}

			if (needSelect) {
				// /*
				// * 改 update
				// */
				// public TbUser queryTbUserByPrimaryKey(Integer id) throws
				// CoreException;
				if (pksList.size() > 1) {
					out.write("\tpublic " + beanName + " queryBy" + getMethodByColnames(pksList) + "("
							+ beanName + " " + objectName + ") ;" + lineSeparator);

				} else {
					out.write("\tpublic " + beanName + " queryBy" + getMethodByColnames(pksList) + "("
							+ getMethodParamByColnames(pksList) + ") ;" + lineSeparator);
				}

			}

			if (needSelectAll) {

				// /*
				// * 改 查询全部数据
				// */
				// public TbUser queryTbUserByPrimaryKey(Integer id) throws
				// CoreException;
				out.write("\tpublic List<" + beanName + "> queryList() ;" + lineSeparator);
			}
			if (needSelectCount) {

				// /*
				// * 改 查询全部数据
				// */
				// public TbUser queryTbUserByPrimaryKey(Integer id) throws
				// CoreException;
				out.write("\tpublic Long count() ;" + lineSeparator);
			}

			if (!queryConditionsList.isEmpty()) {

				// public List<TbUser> queryByPrimaryKeyForList(Integer id)
				// ;

				for (String condition : queryConditionsList) {

					String[] cols = condition.split(",");
					List<String> colList = new ArrayList<String>();

					// 处理字段
					for (String string : cols) {

						if (string.indexOf(".") >= 0) {
							colList.add(StringUtils.substringAfter(string, ".").toUpperCase());
						} else {
							colList.add(string);
						}

					}

					// System.out.println(StringUtil.toJson(colList));

					String str = "\tpublic List<" + beanName + "> queryListBy";

					String colName = null;
					for (int i = 0; i < colList.size(); i++) {
						colName = colList.get(i);

						Mapping mapping = orMappingMap.get(colName);

						str += StringUtil.getMethodPropertyName(mapping.getPropertyName());

						if (i != colList.size() - 1) {
							str += "And";
						}
					}

					str += "(";
					for (int i = 0; i < colList.size(); i++) {
						colName = colList.get(i);

						Mapping mapping = orMappingMap.get(colName);

						str += mapping.getJavaType() + " " + mapping.getPropertyName();

						if (i != colList.size() - 1) {
							str += ",";
						}
					}

					str += ") ;" + lineSeparator;

					out.write(str);

				}
			}

			out.write(lineSeparator);

			out.write("}" + lineSeparator);

			out.flush();

		} finally {
			// 关闭输出流
			out.close();
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	public static void writeServiceJavaInterface() throws IOException {
		Writer out = null;
		try {
			out = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(outputPath + File.separator + beanName + "Service.java"), charSet),
					8 * 1024);

			// package com.huateng.dao;
			out.write("package " + packageStr + "." + packageStrServiceEx + ";" + lineSeparator);

			out.write(lineSeparator);

			// out.write("import java.io.Serializable;"+lineSeparator);

			if (hasBigDecimal) {
				// import java.math.*;
				out.write("import java.math.BigDecimal;" + lineSeparator);
			}
			// if (hasDate) {
			// // import java.sql.*;
			// out.write("import java.util.Date;" + lineSeparator);
			// }
			out.write("import java.util.List;" + lineSeparator);
			out.write("import " + packageStr + "." + packageStrPojoEx + "." + beanName + ";" + lineSeparator);

			// out.write("import com.huateng.neofp.core.CoreException;" +
			// lineSeparator);

			out.write(lineSeparator);
			//
			// public interface TbUserService {
			out.write("public interface " + beanName + "Service {" + lineSeparator);
			out.write(lineSeparator);

			// 获取对象参数名称
			String objectName = StringUtil.getMethodPropertyName2(beanName);

			if (needInsert) {
				// /*
				// * 增 insert
				// */
				// public int insertTbUser(TbUser tbUser) ;
				// public int insertTbUserSelective(TbUser tbUser) throws
				// CoreException;
				out.write("\tpublic int insert(" + beanName + " " + objectName + ");" + lineSeparator);
				out.write("\tpublic int insertSelective(" + beanName + " " + objectName + ") ;"
						+ lineSeparator);

			}

			if (needListInsert) {
				// public int insertTestQueueList(List<TestQueue> list) throws
				// CoreException;
				// public int insertTestQueueListSelective(List<TestQueue> list)
				// ;

				out.write("\tpublic int insertList(List<" + beanName + "> list) ;" + lineSeparator);
				out.write("\tpublic int insertListSelective(List<" + beanName + "> list); "
						+ lineSeparator);

			}

			// Mapping pkMapping = orMappingMap.get(pkName);

			List<String> pksList = converArrays(pks);

			if (needDelete) {

				// 设有主键
				if (pksList.size() > 0) {
					// /*
					// * 删 delete
					// */
					// public int deleteTbUserById(Integer id) throws
					// CoreException;
					out.write("\tpublic int deleteBy" + getMethodByColnames(pksList) + "("
							+ getMethodParamByColnames(pksList) + ") ;" + lineSeparator);
				}

				Mapping otherMapping = orMappingMap.get(otherDeleteKey);
				if (otherMapping != null) {
					// /*
					// * 删 delete
					// */
					// public int deleteTbUserById(Integer id) throws
					// CoreException;
					out.write("\tpublic int deleteBy"
							+ StringUtil.getMethodPropertyName(otherMapping.getPropertyName()) + "("
							+ otherMapping.getJavaType() + " " + otherMapping.getPropertyName() + ") ;"
							+ lineSeparator);
				}
			}

			if (needUpdate) {
				// /*
				// * 改 update
				// */
				// public int updateTbUser(TbUser tbUser) ;
				// public int updateTbUserSelective(TbUser tbUser) throws
				// CoreException;
				out.write("\tpublic int update(" + beanName + " " + objectName + ") ;" + lineSeparator);
				out.write("\tpublic int updateSelective(" + beanName + " " + objectName + ") ;"
						+ lineSeparator);

			}

			if (needListUpdate) {

				// public int insertTestQueueList(List<TestQueue> list) throws
				// CoreException;
				// public int insertTestQueueListSelective(List<TestQueue> list)
				// ;

				out.write("\tpublic int updateList(List<" + beanName + "> list) ;" + lineSeparator);
				out.write("\tpublic int updateListSelective(List<" + beanName + "> list) ;"
						+ lineSeparator);
			}

			if (needSelect) {
				// /*
				// * 改 update
				// */
				// public TbUser queryTbUserByPrimaryKey(Integer id) throws
				// CoreException;
				out.write("\tpublic " + beanName + " queryBy" + getMethodByColnames(pksList) + "("
						+ getMethodParamByColnames(pksList) + ") ;" + lineSeparator);
			}

			if (needSelectAll) {

				// /*
				// * 改 查询全部数据
				// */
				// public TbUser queryTbUserByPrimaryKey(Integer id) throws
				// CoreException;
				out.write("\tpublic List<" + beanName + "> queryList() ;" + lineSeparator);
			}

			if (needSelectCount) {
				out.write("\tpublic Long count() ;" + lineSeparator);
			}

			if (!queryConditionsList.isEmpty()) {

				// public List<TbUser> queryByPrimaryKeyForList(Integer id)
				// ;

				for (String condition : queryConditionsList) {

					String[] cols = condition.split(",");
					List<String> colList = new ArrayList<String>();

					// 处理字段
					for (String string : cols) {

						if (string.indexOf(".") >= 0) {
							colList.add(StringUtils.substringAfter(string, ".").toUpperCase());
						} else {
							colList.add(string);
						}

					}

					// System.out.println(StringUtil.toJson(colList));

					String str = "\tpublic List<" + beanName + "> queryListBy";

					String colName = null;
					for (int i = 0; i < colList.size(); i++) {
						colName = colList.get(i);

						Mapping mapping = orMappingMap.get(colName);

						str += StringUtil.getMethodPropertyName(mapping.getPropertyName());

						if (i != colList.size() - 1) {
							str += "And";
						}
					}

					str += "(";
					for (int i = 0; i < colList.size(); i++) {
						colName = colList.get(i);

						Mapping mapping = orMappingMap.get(colName);

						str += mapping.getJavaType() + " " + mapping.getPropertyName();

						if (i != colList.size() - 1) {
							str += ",";
						}
					}

					str += ") ;" + lineSeparator;

					out.write(str);

				}
			}

			out.write(lineSeparator);

			out.write("}" + lineSeparator);

			out.flush();

		} finally {
			// 关闭输出流
			out.close();
		}
	}

	/**
	 * 首字母小写
	 * 
	 * @param str
	 * @return
	 */
	public static String getFNmae(String str) {
		char[] chars = new char[1];
		chars[0] = str.charAt(0);
		String temp = new String(chars);
		if (chars[0] >= 'A' && chars[0] <= 'Z') {
			return (str.replaceFirst(temp, temp.toLowerCase()));
		}
		return null;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public static void writeServiceJavaInterfaceImpl() throws IOException {
		Writer out = null;

		try {
			out = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(outputPath + File.separator + beanName + "ServiceImpl.java"), charSet),
					8 * 1024);
			// package com.em.service.impl.sys;
			out.write("package " + packageStr + "." + packageStrServiceImplEx + ";" + lineSeparator);
			out.write(lineSeparator);

			// out.write("import java.io.Serializable;"+lineSeparator);

			if (hasBigDecimal) {
				// import java.math.*;
				out.write("import java.math.BigDecimal;" + lineSeparator);
			}
			// if (hasDate) {
			// // import java.sql.*;
			// out.write("import java.util.Date;" + lineSeparator);
			// }
			out.write("import java.util.List;" + lineSeparator);

			// import javax.annotation.Resource;
			out.write("import javax.annotation.Resource;" + lineSeparator);
			// import org.springframework.stereotype.Service;

			out.write("import org.springframework.stereotype.Service;" + lineSeparator);
			// import com.em.dao.sys.UserInfoDao;
			out.write("import " + packageStr + "." + packageStrDaoEx + "." + beanName + "Mapper;" + lineSeparator);
			// import com.em.pojo.sys.UserInfo;
			out.write("import " + packageStr + "." + packageStrPojoEx + "." + beanName + ";" + lineSeparator);
			// import com.em.service.sys.UserInfoService;
			out.write("import " + packageStr + "." + packageStrServiceEx + "." + beanName + "Service;" + lineSeparator);
			// out.write("import com.huateng.neofp.core.CoreException;" +
			// lineSeparator);

			out.write(lineSeparator);

			// @Service("userInfoService")

			out.write(String.format("@Service(\"%s\")", getFNmae(beanName + "Service")) + lineSeparator);

			// public interface TbUserService {
			out.write("public class " + beanName + "ServiceImpl  " + " implements " + beanName + "Service {"
					+ lineSeparator);
			out.write(lineSeparator);

			String resJavaDao = beanName + "Mapper";
			String resJaveDaoLower = getFNmae(resJavaDao);

			// @Resource
			out.write("\t@Resource" + lineSeparator);

			// CreditinfoRoleResourceDao roleResourceDao;
			out.write("\t" + resJavaDao + " " + resJaveDaoLower + ";" + lineSeparator);
			out.write(lineSeparator);

			// private static final String NAMESPACES = "TbUser.";
			// out.write("\tprivate static final String NAMESPACES = \"" +
			// beanName + ".\";" + lineSeparator);
			// out.write(lineSeparator);

			// 获取对象参数名称
			String objectName = StringUtil.getMethodPropertyName2(beanName);

			if (needInsert) {
				// /*
				// * 增 insert
				// */
				// public int insertTbUser(TbUser tbUser) ;
				// public int insertTbUserSelective(TbUser tbUser) throws
				// CoreException;
				out.write("\t@Override" + lineSeparator);
				out.write(
						"\tpublic int insert(" + beanName + " " + objectName + ")  {" + lineSeparator);

				// return getSqlMap().insert(NAMESPACES +
				// "insertSelective",tbUser);

				// return roleResourceDao.deleteByPrimaryKey(key)
				out.write("\t\treturn  " + resJaveDaoLower + ".insert(" + objectName + ");"
						+ lineSeparator);

				// System.out.println("return " + resJaveDaoLower + ".insert" +
				// beanName + "(" + objectName + ");");
				// out.write("\t\treturn getSqlMap().insert(NAMESPACES +
				// \"insert" + beanName + "\", " + objectName + ");"
				// + lineSeparator);

				out.write("\t}" + lineSeparator);
				out.write(lineSeparator);

				out.write("\t@Override" + lineSeparator);
				out.write("\tpublic int insertSelective(" + beanName + " " + objectName + ")  {"
						+ lineSeparator);

				out.write("\t\treturn  " + resJaveDaoLower + ".insertSelective(" + objectName + ");"
						+ lineSeparator);

				out.write("\t}" + lineSeparator);
				out.write(lineSeparator);

			}

			if (needListInsert) {

				// @Override
				// public int insertTestQueueList(List<TestQueue> list) throws
				// CoreException {
				// int returnValue = 0;
				// for (TestQueue testQueue : list) {
				// returnValue+=insertTestQueue(testQueue);
				// }
				// return returnValue;
				// }

				out.write("\t@Override" + lineSeparator);
				out.write("\tpublic int insertList(List<" + beanName + "> list)  {" + lineSeparator);
				out.write("\t\tint returnValue = 0;" + lineSeparator);
				out.write("\t\tfor (" + beanName + " " + objectName + " : list) {" + lineSeparator);
				out.write("\t\t\treturnValue+=insert(" + objectName + ");" + lineSeparator);
				out.write("\t\t}" + lineSeparator);
				out.write("\t\treturn returnValue;" + lineSeparator);
				out.write("\t}" + lineSeparator);

				out.write(lineSeparator);

				out.write("\t@Override" + lineSeparator);
				out.write("\tpublic int insertListSelective(List<" + beanName + "> list)   {"
						+ lineSeparator);
				out.write("\t\tint returnValue = 0;" + lineSeparator);
				out.write("\t\tfor (" + beanName + " " + objectName + " : list) {" + lineSeparator);
				out.write("\t\t\treturnValue+=insertSelective(" + objectName + ");" + lineSeparator);
				out.write("\t\t}" + lineSeparator);
				out.write("\t\treturn returnValue;" + lineSeparator);
				out.write("\t}" + lineSeparator);

				out.write(lineSeparator);

			}

			// Mapping pkMapping = orMappingMap.get(pkName);
			List<String> pksList = converArrays(pks);

			if (needDelete) {

				// 设有主键
				if (pksList.size() > 0) {
					// /*
					// * 删 delete
					// */
					// public int deleteTbUserById(Integer id) throws
					// CoreException;

					out.write("\t@Override" + lineSeparator);
					out.write("\tpublic int deleteBy" + getMethodByColnames(pksList) + "("
							+ getMethodParamByColnames(pksList) + ")   {" + lineSeparator);

					if (pksList.size() > 1) {
						// 多个字段

						out.write(getBeanSetString(pksList));

						out.write("\t\treturn " + resJaveDaoLower + ".deleteBy"
								+ getMethodByColnames(pksList) + "(" + objectName + ");" + lineSeparator);

					} else {
						// 单个字段查询条件

						Mapping mapping = orMappingMap.get(pksList.get(0));

						// return (TbUser)getSqlMap().queryForObject(NAMESPACES
						// + "queryByPrimaryKey",id);
						// out.write("\t\treturn
						// (List<"+beanName+">)(getSqlMap().queryForList(NAMESPACES
						// + \"query"+beanName+"ListBy"+colNameAll+"\", "+
						// mapping.getPropertyName()+"));"+lineSeparator);

						out.write("\t\treturn " + resJaveDaoLower + ".deleteBy"
								+ getMethodByColnames(pksList) + "(" + mapping.getPropertyName() + ");"
								+ lineSeparator);
					}

					out.write("\t}" + lineSeparator);
					out.write(lineSeparator);
				}

				Mapping otherMapping = orMappingMap.get(otherDeleteKey);
				if (otherMapping != null) {
					// /*
					// * 删 delete
					// */
					// public int deleteTbUserById(Integer id) throws
					// CoreException;
					out.write("\t@Override" + lineSeparator);
					out.write("\tpublic int deleteBy"
							+ StringUtil.getMethodPropertyName(otherMapping.getPropertyName()) + "("
							+ otherMapping.getJavaType() + " " + otherMapping.getPropertyName() + ")   {"
							+ lineSeparator);

					out.write("\t\treturn " + resJaveDaoLower + ".deleteBy"
							+ StringUtil.getMethodPropertyName(otherMapping.getPropertyName()) + "\", "
							+ otherMapping.getPropertyName() + ");" + lineSeparator);

					out.write("\t}" + lineSeparator);
					out.write(lineSeparator);
				}
			}

			if (needUpdate) {
				// /*
				// * 改 update
				// */
				// public int updateTbUser(TbUser tbUser) ;
				// public int updateTbUserSelective(TbUser tbUser) throws
				// CoreException;

				out.write("\t@Override" + lineSeparator);
				out.write(
						"\tpublic int update(" + beanName + " " + objectName + ")   {" + lineSeparator);

				out.write("\t\treturn " + resJaveDaoLower + ".update(" + objectName + ");"
						+ lineSeparator);

				out.write("\t}" + lineSeparator);
				out.write(lineSeparator);

				out.write("\t@Override" + lineSeparator);
				out.write("\tpublic int updateSelective(" + beanName + " " + objectName + ")   {"
						+ lineSeparator);

				out.write("\t\treturn " + resJaveDaoLower + ".updateSelective(" + objectName + ");"
						+ lineSeparator);

				out.write("\t}" + lineSeparator);
				out.write(lineSeparator);

			}
			if (needListUpdate) {

				// @Override
				// public int insertTestQueueList(List<TestQueue> list) throws
				// CoreException {
				// int returnValue = 0;
				// for (TestQueue testQueue : list) {
				// returnValue+=insertTestQueue(testQueue);
				// }
				// return returnValue;
				// }

				out.write("\t@Override" + lineSeparator);
				out.write("\tpublic int updateList(List<" + beanName + "> list)   {" + lineSeparator);
				out.write("\t\tint returnValue = 0;" + lineSeparator);
				out.write("\t\tfor (" + beanName + " " + objectName + " : list) {" + lineSeparator);
				out.write("\t\t\treturnValue+=update(" + objectName + ");" + lineSeparator);
				out.write("\t\t}" + lineSeparator);
				out.write("\t\treturn returnValue;" + lineSeparator);
				out.write("\t}" + lineSeparator);

				out.write(lineSeparator);

				out.write("\t@Override" + lineSeparator);
				out.write("\tpublic int updateListSelective(List<" + beanName + "> list)   {"
						+ lineSeparator);
				out.write("\t\tint returnValue = 0;" + lineSeparator);
				out.write("\t\tfor (" + beanName + " " + objectName + " : list) {" + lineSeparator);
				out.write("\t\t\treturnValue+=updateSelective(" + objectName + ");" + lineSeparator);
				out.write("\t\t}" + lineSeparator);
				out.write("\t\treturn returnValue;" + lineSeparator);
				out.write("\t}" + lineSeparator);

				out.write(lineSeparator);

			}

			if (needSelect) {
				// /*
				// * 改 update
				// */
				// public TbUser queryTbUserByPrimaryKey(Integer id) throws
				// CoreException;
				out.write("\t@Override" + lineSeparator);
				out.write("\tpublic " + beanName + " queryBy" + getMethodByColnames(pksList) + "("
						+ getMethodParamByColnames(pksList) + ")   {" + lineSeparator);

				if (pksList.size() > 1) {
					// 多个字段

					out.write(getBeanSetString(pksList));

					out.write("\t\treturn (" + beanName + ")" + resJaveDaoLower + ".queryBy"
							+ getMethodByColnames(pksList) + "(" + objectName + ");" + lineSeparator);
				} else {
					// 单个字段查询条件

					Mapping mapping = orMappingMap.get(pksList.get(0));

					// return (TbUser)getSqlMap().queryForObject(NAMESPACES +
					// "queryByPrimaryKey",id);
					// out.write("\t\treturn
					// (List<"+beanName+">)(getSqlMap().queryForList(NAMESPACES
					// + \"query"+beanName+"ListBy"+colNameAll+"\", "+
					// mapping.getPropertyName()+"));"+lineSeparator);

					out.write("\t\treturn (" + beanName + ")" + resJaveDaoLower + ".queryBy"
							+ getMethodByColnames(pksList) + "(" + mapping.getPropertyName() + ");" + lineSeparator);
				}

				// return (TbUser)getSqlMap().queryForObject(NAMESPACES +
				// "queryByPrimaryKey",id);

				out.write("\t}" + lineSeparator);
				out.write(lineSeparator);

			}

			if (needSelectAll) {

				// /*
				// * 改 查询全部数据
				// */
				// public TbUser queryTbUserByPrimaryKey(Integer id) throws
				// CoreException;
				out.write("\t@Override" + lineSeparator);
				out.write("\tpublic List<" + beanName + "> queryList()   {" + lineSeparator);

				out.write("\t\treturn " + resJaveDaoLower + ".queryList();" + lineSeparator);

				out.write("\t}" + lineSeparator);
				out.write(lineSeparator);

			}

			if (needSelectCount) {

				// /*
				// * 改 查询全部数据
				// */
				// public TbUser queryTbUserByPrimaryKey(Integer id) throws
				// CoreException;
				out.write("\t@Override" + lineSeparator);
				out.write("\tpublic  Long count()   {" + lineSeparator);

				out.write("\t\treturn " + resJaveDaoLower + ".count();" + lineSeparator);

				out.write("\t}" + lineSeparator);
				out.write(lineSeparator);

			}

			if (!queryConditionsList.isEmpty()) {

				// public List<TbUser> queryByPrimaryKeyForList(Integer id)
				// ;

				// 循环全部的查询条件
				for (String condition : queryConditionsList) {

					out.write("\t@Override" + lineSeparator);

					String[] cols = condition.split(",");
					List<String> colList = new ArrayList<String>();

					// 处理字段
					for (String string : cols) {

						if (string.indexOf(".") >= 0) {
							colList.add(StringUtils.substringAfter(string, ".").toUpperCase());
						} else {
							colList.add(string);
						}

					}

					// System.out.println(StringUtil.toJson(colList));

					String str = "\tpublic List<" + beanName + "> queryListBy";

					String colNameAll = getMethodByColnames(colList);

					str += colNameAll;

					str += "(";

					str += getMethodParamByColnames(colList);

					str += ")   {" + lineSeparator;

					out.write(str);

					if (colList.size() > 1) {
						// 多个字段查询条件

						out.write(getBeanSetString(colList));

						// return (TbUser)getSqlMap().queryForObject(NAMESPACES
						// + "queryByPrimaryKey",id);
						// out.write("\t\treturn
						// (List<"+beanName+">)(getSqlMap().queryForList(NAMESPACES
						// + \"query"+beanName+"ListBy"+colNameAll+"\", "+
						// objectName+"));"+lineSeparator);
						out.write("\t\treturn " + resJaveDaoLower + ".queryListBy" + colNameAll + "\", "
								+ objectName + ");" + lineSeparator);
					} else {
						// 单个字段查询条件

						Mapping mapping = orMappingMap.get(colList.get(0));

						// return (TbUser)getSqlMap().queryForObject(NAMESPACES
						// + "queryByPrimaryKey",id);
						// out.write("\t\treturn
						// (List<"+beanName+">)(getSqlMap().queryForList(NAMESPACES
						// + \"query"+beanName+"ListBy"+colNameAll+"\", "+
						// mapping.getPropertyName()+"));"+lineSeparator);

						out.write("\t\treturn " + resJaveDaoLower + ".queryListBy" + colNameAll + "\", "
								+ mapping.getPropertyName() + ");" + lineSeparator);
					}

					out.write("\t}" + lineSeparator);
					out.write(lineSeparator);

				}
			}

			out.write(lineSeparator);

			out.write("}" + lineSeparator);

			out.flush();

		} finally {
			// 关闭输出流
			out.close();
		}
	}

	private static String getColValueString(Mapping mapping) {

		// commonColumnsList.add("CRT_USER");
		// commonColumnsList.add("CRT_DATE");
		// commonColumnsList.add("CRT_TIME");
		// commonColumnsList.add("LST_UPD_USER");
		// commonColumnsList.add("LST_UPD_TIME");
		// commonColumnsList.add("LST_UPD_DATE");
		// commonColumnsList.add("REC_STATUS");
		// commonColumnsList.add("SCR_LEVEL");

		if ("CRT_DATE".equals(mapping.getColumnName())) {
			return "CURRENT_DATE";
		} else if ("CRT_TIME".equals(mapping.getColumnName())) {
			return "CURRENT_TIMESTAMP";
		} else if ("LST_UPD_DATE".equals(mapping.getColumnName())) {
			return "CURRENT_DATE";
		} else if ("LST_UPD_TIME".equals(mapping.getColumnName())) {
			return "CURRENT_TIMESTAMP";
		} else if ("REC_STATUS".equals(mapping.getColumnName())) {
			return "'0'";
		} else if ("SCR_LEVEL".equals(mapping.getColumnName())) {
			return "'00'";
		} else {
			// #{id,jdbcType=DECIMAL}
			return "#{" + mapping.getPropertyName() + ",jdbcType="
					+ JdbcUtil.convertJdbcType2SqlMapTypeOracle(mapping.getColumnType()) + "}";
		}

	}

	private static String getColValueStringSelective(Mapping mapping) {

		// commonColumnsList.add("CRT_USER");
		// commonColumnsList.add("CRT_DATE");
		// commonColumnsList.add("CRT_TIME");
		// commonColumnsList.add("LST_UPD_USER");
		// commonColumnsList.add("LST_UPD_TIME");
		// commonColumnsList.add("LST_UPD_DATE");
		// commonColumnsList.add("REC_STATUS");
		// commonColumnsList.add("SCR_LEVEL");

		if ("CRT_DATE".equals(mapping.getColumnName())) {
			return "CURRENT_DATE,";
		} else if ("CRT_TIME".equals(mapping.getColumnName())) {
			return "CURRENT_TIMESTAMP,";
		} else if ("LST_UPD_DATE".equals(mapping.getColumnName())) {
			return "CURRENT_DATE,";
		} else if ("LST_UPD_TIME".equals(mapping.getColumnName())) {
			return "CURRENT_TIMESTAMP,";
		} else if ("REC_STATUS".equals(mapping.getColumnName())) {
			return "'0',";
		} else if ("SCR_LEVEL".equals(mapping.getColumnName())) {
			return "'00',";
		} else {
			// <if test="id != null">#{id,jdbcType=DECIMAL},</if>
			return "<if test=\"" + mapping.getPropertyName() + " != null\">#{" + mapping.getPropertyName()
					+ ",jdbcType=" + JdbcUtil.convertJdbcType2SqlMapTypeOracle(mapping.getColumnType()) + "},</if>";
		}

	}

	private static String getColValueStringSelective4update(Mapping mapping) {

		// commonColumnsList.add("CRT_USER");
		// commonColumnsList.add("CRT_DATE");
		// commonColumnsList.add("CRT_TIME");
		// commonColumnsList.add("LST_UPD_USER");
		// commonColumnsList.add("LST_UPD_TIME");
		// commonColumnsList.add("LST_UPD_DATE");
		// commonColumnsList.add("REC_STATUS");
		// commonColumnsList.add("SCR_LEVEL");

		if ("CRT_DATE".equals(mapping.getColumnName())) {
			return "CRT_DATE = CURRENT_DATE,";
		} else if ("CRT_TIME".equals(mapping.getColumnName())) {
			return "CRT_TIME = CURRENT_TIMESTAMP,";
		} else if ("LST_UPD_DATE".equals(mapping.getColumnName())) {
			return "LST_UPD_DATE = CURRENT_DATE,";
		} else if ("LST_UPD_TIME".equals(mapping.getColumnName())) {
			return "LST_UPD_TIME = CURRENT_TIMESTAMP,";
		} else if ("REC_STATUS".equals(mapping.getColumnName())) {
			return "REC_STATUS = '0',";
		} else if ("SCR_LEVEL".equals(mapping.getColumnName())) {
			return "SCR_LEVEL = '00',";
		} else {
			// <if test="id != null">#{id,jdbcType=DECIMAL},</if>
			return "<if test=\"" + mapping.getPropertyName() + " != null\">" + mapping.getColumnName() + " = #{"
					+ mapping.getPropertyName() + ",jdbcType="
					+ JdbcUtil.convertJdbcType2SqlMapTypeOracle(mapping.getColumnType()) + "},</if>";
		}

	}

	private static String getColNameStringSelective(Mapping mapping) {

		// commonColumnsList.add("CRT_USER");
		// commonColumnsList.add("CRT_DATE");
		// commonColumnsList.add("CRT_TIME");
		// commonColumnsList.add("LST_UPD_USER");
		// commonColumnsList.add("LST_UPD_TIME");
		// commonColumnsList.add("LST_UPD_DATE");
		// commonColumnsList.add("REC_STATUS");
		// commonColumnsList.add("SCR_LEVEL");

		if ("CRT_DATE".equals(mapping.getColumnName())) {
			return "CRT_DATE,";
		} else if ("CRT_TIME".equals(mapping.getColumnName())) {
			return "CRT_TIME,";
		} else if ("LST_UPD_DATE".equals(mapping.getColumnName())) {
			return "LST_UPD_DATE,";
		} else if ("LST_UPD_TIME".equals(mapping.getColumnName())) {
			return "LST_UPD_TIME,";
		} else if ("REC_STATUS".equals(mapping.getColumnName())) {
			return "REC_STATUS,";
		} else if ("SCR_LEVEL".equals(mapping.getColumnName())) {
			return "SCR_LEVEL,";
		} else {
			// <if test="id != null">ID,</if>
			return "<if test=\"" + mapping.getPropertyName() + " != null\">" + mapping.getColumnName() + ",</if>";
		}

	}

	private static boolean isColNameNeedUpdate(Mapping mapping) {

		// commonColumnsList.add("CRT_USER");
		// commonColumnsList.add("CRT_DATE");
		// commonColumnsList.add("CRT_TIME");
		// commonColumnsList.add("LST_UPD_USER");
		// commonColumnsList.add("LST_UPD_TIME");
		// commonColumnsList.add("LST_UPD_DATE");
		// commonColumnsList.add("REC_STATUS");
		// commonColumnsList.add("SCR_LEVEL");

		if ("CRT_DATE".equals(mapping.getColumnName())) {
			return false;
		} else if ("CRT_TIME".equals(mapping.getColumnName())) {
			return false;
		} else if ("CRT_USER".equals(mapping.getColumnName())) {
			return false;
		} else if ("REC_STATUS".equals(mapping.getColumnName())) {
			return false;
		} else if ("SCR_LEVEL".equals(mapping.getColumnName())) {
			return false;
		} else if (pkName.equals(mapping.getColumnName())) {
			return false;
		} else {
			return true;
		}

	}

	private static List<Mapping> getNeedUpdateColList() {
		List<Mapping> list = new ArrayList<Mapping>();

		Mapping mapping = null;
		for (int i = 0; i < orMappingList.size(); i++) {
			mapping = orMappingList.get(i);

			if (isColNameNeedUpdate(mapping)) {
				list.add(mapping);
			}

		}
		return list;

	}

	/**
	 * 
	 * @throws IOException
	 */
	public static void writeSqlMap() throws IOException {
		Writer out = null;

		try {
			out = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(outputPath + File.separator + beanName + "Mapper.xml"), charSet),
					8 * 1024);

			// <?xml version="1.0" encoding="UTF-8" ?>
			out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + lineSeparator);
			// <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
			// "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >

			// out.write("<!DOCTYPE mapper " + " PUBLIC
			// \"-//ibatis.apache.org//DTD Mapper 3.0//EN\" " +"
			// \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
			// <!DOCTYPE mapper
			out.write("<!DOCTYPE mapper " + lineSeparator);
			// PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
			out.write("  PUBLIC \"-//ibatis.apache.org//DTD Mapper 3.0//EN\" " + lineSeparator);
			// "http://ibatis.apache.org/dtd/ibatis-3-mapper.dtd">
			out.write("  \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">" + lineSeparator);

			out.write(lineSeparator);
			// String javaBeanFullString = packageStr + "." +
			// beanName.toLowerCase() + "." + beanName;
			String javaBeanFullString = packageStr + "." + packageStrPojoEx + "." + beanName;
			String javaDaoFullString = packageStr + "." + packageStrDaoEx + "." + beanName + "Mapper";
			// <mapper namespace="TbUser">
			out.write("<mapper namespace=\"" + javaDaoFullString + "\">" + lineSeparator);
			out.write(lineSeparator);

			String resultMapId = beanName + "ResultMap";

			// <resultMap id="TbUserResultMap" type="">
			// <result column="" property="" jdbcType="" />
			// </resultMap>

			out.write("\t<resultMap id=\"" + resultMapId + "\" type=\"" + javaBeanFullString + "\">" + lineSeparator);

			Mapping mapping = null;
			for (int i = 0; i < orMappingList.size(); i++) {
				mapping = orMappingList.get(i);

				out.write("\t\t<result column=\"" + mapping.getColumnName() + "\" property=\""
						+ mapping.getPropertyName() + "\" jdbcType=\""
						+ JdbcUtil.convertJdbcType2SqlMapTypeOracle(mapping.getColumnType()) + "\"/>" + lineSeparator);
			}
			out.write("\t</resultMap>" + lineSeparator);
			out.write(lineSeparator);

			if (needInsert) {
				// /*
				// * 增 insert
				// */
				// insertTbUser
				// <insert id="insertTbUser"
				// parameterType="com.spdbccc.service.iface.dao.tbuser.TbUser">
				out.write("\t<insert id=\"insert\" parameterType=\"" + javaBeanFullString + "\">"
						+ lineSeparator);
				//
				// insert into TB_USER (ID, USERNAME)
				out.write("\t\tinsert into " + getFullTabName() + " (");

				String allColStr = "";
				for (int i = 0; i < orMappingList.size(); i++) {
					mapping = orMappingList.get(i);

					allColStr += mapping.getColumnName();

					if (i != orMappingList.size() - 1) {
						allColStr += ",";
					}
				}
				out.write(allColStr + ") " + lineSeparator);

				out.write("\t\tvalues (" + lineSeparator);

				for (int i = 0; i < orMappingList.size(); i++) {
					mapping = orMappingList.get(i);

					out.write("\t\t\t" + getColValueString(mapping));

					if (i != orMappingList.size() - 1) {
						out.write(",");
					}
					out.write(lineSeparator);
				}

				out.write("\t\t)" + lineSeparator);

				out.write("\t</insert>" + lineSeparator);
				//
				out.write(lineSeparator);

				// insertTbUser=============================================
				out.write("\t<insert id=\"insertSelective\" parameterType=\"" + javaBeanFullString
						+ "\">" + lineSeparator);

				out.write("\t\tinsert into " + getFullTabName() + lineSeparator);

				// <trim prefix="(" suffix=")" suffixOverrides=",">
				out.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">" + lineSeparator);

				for (int i = 0; i < orMappingList.size(); i++) {
					mapping = orMappingList.get(i);

					String colStr = getColNameStringSelective(mapping);

					if (i == orMappingList.size() - 1) {
						colStr = colStr.replace(",", "");
					}

					out.write("\t\t\t" + colStr + lineSeparator);
				}

				out.write("\t\t</trim>" + lineSeparator);

				// <trim prefix="values (" suffix=")" suffixOverrides=",">
				out.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">" + lineSeparator);

				for (int i = 0; i < orMappingList.size(); i++) {
					mapping = orMappingList.get(i);

					String colStr = getColValueStringSelective(mapping);

					if (i == orMappingList.size() - 1) {
						colStr = colStr.replace(",", "");
					}

					out.write("\t\t\t" + colStr + lineSeparator);
				}
				out.write("\t\t</trim>" + lineSeparator);

				out.write("\t</insert>" + lineSeparator);
				//
				out.write(lineSeparator);

			}

			// Mapping pkMapping = orMappingMap.get(pkName);
			List<String> pksList = converArrays(pks);

			if (needDelete) {

				// 设有主键
				if (pksList.size() > 1) {
					// /*
					// * 删 delete
					// */

					// <delete id="deleteByPrimaryKey"
					// parameterType="java.lang.Integer">
					// delete from TB_USER
					// where ID = #{id,jdbcType=DECIMAL}
					// </delete>

					out.write("\t<delete id=\"deleteBy" + getMethodByColnames(pksList)
							+ "\" parameterType=\"" + javaBeanFullString + "\">" + lineSeparator);

					out.write("\t\tdelete from " + getFullTabName() + lineSeparator);
					out.write("\t\twhere " + getWhereConditionString(pksList));

					out.write("\t</delete>" + lineSeparator);
					//
					out.write(lineSeparator);

				} else {

					out.write("\t<delete id=\"deleteBy" + getMethodByColnames(pksList)
							+ "\" parameterType=\"" + getJavaFullType(pksList) + "\">" + lineSeparator);

					out.write("\t\tdelete from " + getFullTabName() + lineSeparator);
					out.write("\t\twhere " + getWhereConditionString(pksList));

					out.write("\t</delete>" + lineSeparator);
					//
					out.write(lineSeparator);
				}

				Mapping otherMapping = orMappingMap.get(otherDeleteKey);
				if (otherMapping != null) {
					// /*
					// * 删 delete
					// */

					out.write("\t<delete id=\"deleteBy"
							+ StringUtil.getMethodPropertyName(otherMapping.getPropertyName()) + "\" parameterType=\""
							+ JdbcUtil.convertJdbcType2JavaTypeFull(otherMapping.getColumnType()) + "\">"
							+ lineSeparator);

					out.write("\t\tdelete from " + getFullTabName() + lineSeparator);
					out.write("\t\twhere " + otherMapping.getColumnName() + " = " + getColValueString(otherMapping)
							+ lineSeparator);

					out.write("\t</delete>" + lineSeparator);
					//
					out.write(lineSeparator);

				}
			}

			if (needUpdate) {
				// /*
				// * 改 update
				// */

				// insertTbUser
				// <insert id="insertTbUser"
				// parameterType="com.spdbccc.service.iface.dao.tbuser.TbUser">
				out.write("\t<update id=\"update\" parameterType=\"" + javaBeanFullString + "\">"
						+ lineSeparator);
				//
				// insert into TB_USER (ID, USERNAME)
				out.write("\t\tupdate " + getFullTabName() + lineSeparator);

				out.write("\t\t<set>" + lineSeparator);

				List<Mapping> needUpdateColList = getNeedUpdateColList();

				for (int i = 0; i < needUpdateColList.size(); i++) {
					mapping = needUpdateColList.get(i);

					out.write("\t\t\t" + mapping.getColumnName() + " = " + getColValueString(mapping));

					if (i != needUpdateColList.size() - 1) {
						out.write(",");
					}
					out.write(lineSeparator);

				}
				out.write("\t\t</set>" + lineSeparator);

				out.write("\t\twhere " + getWhereConditionString(pksList) + lineSeparator);

				out.write("\t</update>" + lineSeparator);
				//
				out.write(lineSeparator);

				// ===================================================
				// <update id="updateByPrimaryKeySelective"
				// parameterType="com.spdbccc.service.iface.dao.tbuser.TbUser">
				//
				// update TB_USER
				// <set>
				// <if test="username != null">USERNAME =
				// #{username,jdbcType=VARCHAR},</if>
				// <if test="password != null">
				// PASSWORD = #{password,jdbcType=VARCHAR},
				// </if>
				// <if test="photo != null">
				// PHOTO = #{photo,jdbcType=CLOB},
				// </if>
				// LST_UPD_DATE = CURRENT_DATE,
				// LST_UPD_TIME = CURRENT_TIMESTAMP,
				// LST_UPD_USER = 'system'
				// </set>
				// where ID = #{id,jdbcType=DECIMAL}
				//
				// </update>

				out.write("\t<update id=\"updateSelective\" parameterType=\"" + javaBeanFullString
						+ "\">" + lineSeparator);
				//
				// insert into TB_USER (ID, USERNAME)
				out.write("\t\tupdate " + getFullTabName() + lineSeparator);

				out.write("\t\t<set>" + lineSeparator);

				for (int i = 0; i < needUpdateColList.size(); i++) {
					mapping = needUpdateColList.get(i);

					String str = "\t\t\t" + getColValueStringSelective4update(mapping);

					if (i == needUpdateColList.size() - 1) {
						str = str.replaceAll("(,)(.*?)(\\1)(.*?)", "$1$2");
					}
					out.write(str + lineSeparator);

				}
				out.write("\t\t</set>" + lineSeparator);

				out.write("\t\twhere " + getWhereConditionString(pksList) + lineSeparator);

				out.write("\t</update>" + lineSeparator);
				//
				out.write(lineSeparator);
			}

			if (needSelect) {
				// /*
				// * 查 select
				// */

				// <select id="queryTbUserByPrimaryKey"
				// parameterType="java.lang.Integer"
				// resultType="com.spdbccc.service.iface.dao.tbuser.TbUser">
				//
				// select
				// ID, USERNAME, PASSWORD
				// from TB_USER
				// where ID = #{id,jdbcType=DECIMAL}
				// </select>
				if (pksList.size() > 1) {

					out.write("\t<select id=\"queryBy" + getMethodByColnames(pksList)
							+ "\" parameterType=\"" + javaBeanFullString + "\" resultMap=\"" + resultMapId + "\">"
							+ lineSeparator);

					out.write("\t\t" + sql + lineSeparator);

					out.write("\t\twhere " + getWhereConditionString(pksList) + lineSeparator);

					out.write("\t</select>" + lineSeparator);

					out.write(lineSeparator);

				} else {
					out.write("\t<select id=\"queryBy" + getMethodByColnames(pksList)
							+ "\" parameterType=\"" + getJavaFullType(pksList) + "\" resultMap=\"" + resultMapId + "\">"
							+ lineSeparator);

					out.write("\t\t" + sql + lineSeparator);

					out.write("\t\twhere " + getWhereConditionString(pksList) + lineSeparator);

					out.write("\t</select>" + lineSeparator);

					out.write(lineSeparator);
				}

			}

			if (needSelectAll) {

				out.write("\t<select id=\"queryList\" parameterType=\"java.lang.String\" resultMap=\""
						+ resultMapId + "\">" + lineSeparator);

				out.write("\t\t" + sql + lineSeparator);

				out.write("\t</select>" + lineSeparator);

				out.write(lineSeparator);

			}
			if (needSelectCount) {

				out.write("\t<select id=\"count\" resultType=\"java.lang.Long\" " + ">"
						+ lineSeparator);

				out.write("\t\t" + sqlCount + lineSeparator);

				out.write("\t</select>" + lineSeparator);

				out.write(lineSeparator);

			}

			if (!queryConditionsList.isEmpty()) {

				// public List<TbUser> queryByPrimaryKeyForList(Integer id)
				// throws CoreException;

				// 循环全部的查询条件
				for (String condition : queryConditionsList) {

					String[] cols = condition.split(",");
					List<String> colList = new ArrayList<String>();

					// 处理字段
					for (String string : cols) {

						if (string.indexOf(".") >= 0) {
							colList.add(StringUtils.substringAfter(string, ".").toUpperCase());
						} else {
							colList.add(string);
						}

					}

					// System.out.println(StringUtil.toJson(colList));
					String colNameAll = getMethodByColnames(colList);

					out.write("\t<select id=\"queryListBy" + colNameAll + "\" parameterType=\""
							+ getJavaFullType(colList) + "\" resultMap=\"" + resultMapId + "\">" + lineSeparator);

					out.write("\t\t" + sql + lineSeparator);

					out.write((StringUtils.containsIgnoreCase(sql, " where ") ? "\t\tand " : "\t\twhere ")
							+ getWhereConditionString(colList));

					out.write("\t</select>" + lineSeparator);

					out.write(lineSeparator);

				}
			}

			out.write(lineSeparator);

			out.write("</mapper>" + lineSeparator);

			out.flush();

		} finally {
			// 关闭输出流
			out.close();
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	public static void writeDaoTrans_xml() throws IOException {
		Writer out = null;

		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputPath + File.separator + "dao-trans.xml"), charSet), 8 * 1024);

			// 获取对象参数名称
			String objectName = StringUtil.getMethodPropertyName2(beanName);

			// package com.huateng.dao;
			// <bean id="tbUserService"
			// class="com.spdbccc.service.iface.dao.tbuser.TbUserServiceDao" />
			out.write("\t<bean id=\"" + objectName + "Service\" class=\"" + packageStr + "." + beanName.toLowerCase()
					+ "." + beanName + "ServiceDao\" />" + lineSeparator);

			out.write(lineSeparator);

			out.flush();

		} finally {
			// 关闭输出流
			out.close();
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	public static void writeDubboConfig_xml() throws IOException {
		Writer out = null;

		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputPath + File.separator + "dubboConfig.xml"), charSet), 8 * 1024);

			// 获取对象参数名称
			String objectName = StringUtil.getMethodPropertyName2(beanName);
			out.write("服务端配置如下：" + lineSeparator);
			// <dubbo:service
			// interface="com.spdbccc.service.iface.dao.tbuser.TbUserService"
			// ref="tbUserService" />
			out.write("\t<dubbo:service interface=\"" + packageStr + "." + beanName.toLowerCase() + "." + beanName
					+ "Service\" ref=\"" + objectName + "Service\" />" + lineSeparator);

			out.write(lineSeparator);

			out.write("客户端配置如下：" + lineSeparator);
			// <dubbo:reference id="tbUserService"
			// interface="com.spdbccc.service.iface.dao.tbuser.TbUserService" />
			out.write("\t<dubbo:reference id=\"" + objectName + "Service\" interface=\"" + packageStr + "."
					+ beanName.toLowerCase() + "." + beanName + "Service\" />" + lineSeparator);
			out.write(lineSeparator);

			out.flush();

		} finally {
			// 关闭输出流
			out.close();
		}
	}

}
