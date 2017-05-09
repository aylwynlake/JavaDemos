package com.em.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.em.utils.Mapping;

/**
 * JDBCʵ����
 * 
 */
public class JdbcUtil {

	/**
	 * ����JDBC���ӣ�ע�⣺��������������Ҫʹ�ø÷���
	 * 
	 * @param driver
	 *            ��
	 * @param url
	 *            ��ݿ�URL
	 * @param user
	 *            �û���
	 * @param password
	 *            ����
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection createConnection(String driver, String url, String user, String password)
			throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	/**
	 * �ر���ݿ�����
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public static void revertConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	/**
	 * ��ȡ��ѯ����
	 * 
	 * @param stat
	 *            ������
	 * @param sql
	 *            ��ѯSQL
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet getResultSet(Statement stat, String sql) throws SQLException {
		return stat.executeQuery(sql);
	}

	/**
	 * �رս����Դ
	 * 
	 * @param rs
	 *            ����
	 * @throws IOException
	 */
	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				// �رյ��쳣���Ժ���
				e.printStackTrace();
			}
		}
	}

	/**
	 * �ر���������Դ
	 * 
	 * @param stat
	 *            ������
	 * @throws IOException
	 */
	public static void close(Statement stat) {
		if (stat != null) {
			try {
				stat.close();
			} catch (Exception e) {
				// �رյ��쳣���Ժ���
				e.printStackTrace();
			}
		}
	}

	/**
	 * �ر���ݿ�������Դ����������ӳصĻ������ǻ������ӳأ�
	 * 
	 * @param conn
	 *            ��ݿ�����
	 * @throws IOException
	 */
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				// �رյ��쳣���Ժ���
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ѯ��������
	 * 
	 * @param conn
	 *            ��ݿ�����
	 * @param sql
	 *            ��ѯ���
	 * @param clazz
	 *            ��Ҫת��Ϊ�Ķ�����
	 * @return
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("rawtypes")
	public static Object queryBeanFetchFirstRowOnly(Connection conn, String sql, Class clazz) throws SQLException,
			IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Object obj = null;

		Statement stat = null;
		ResultSet rs = null;
		try {
			stat = conn.createStatement();
			rs = stat.executeQuery(sql + " fetch first 1 row only");

			if (rs.next()) {
				// ת������
				obj = createBeanFromResultSet(rs, getORMappingList(rs), clazz);
			}
		} finally {
			close(rs);
			close(stat);
		}
		return obj;
	}

	/**
	 * ����������
	 * 
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public Statement createStatement(Connection conn) throws SQLException {
		return conn.createStatement();
	}

	/**
	 * ����Ԥ�����������
	 * 
	 * @param conn
	 *            ��ݿ�����
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement createPreparedStatement(Connection conn, String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}

	/**
	 * ��ѯ��������
	 * 
	 * @param conn
	 *            ��ݿ�����
	 * @param sql
	 *            ��ѯSQL
	 * @return
	 * @throws SQLException
	 */
	public static int queryIntFromDB(Connection conn, String sql) throws SQLException {
		int returnValue = 0;

		Statement stat = null;
		ResultSet rs = null;
		try {
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			if (rs.next()) {
				returnValue = rs.getInt(1);
			}
		} finally {
			close(rs);
			close(stat);
		}

		return returnValue;
	}

	/**
	 * ��ѯ�������
	 * 
	 * @param conn
	 *            ��ݿ�����
	 * @param sql
	 *            ��ѯSQL
	 * @return
	 * @throws SQLException
	 */
	public static BigDecimal queryBigDecimalFromDB(Connection conn, String sql) throws SQLException {
		BigDecimal returnValue = null;

		Statement stat = null;
		ResultSet rs = null;
		try {
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			if (rs.next()) {
				returnValue = rs.getBigDecimal(1);
			}
		} finally {
			close(rs);
			close(stat);
		}

		return returnValue;
	}

	/**
	 * ��ѯ����������
	 * 
	 * @param conn
	 *            ��ݿ�����
	 * @param sql
	 *            ��ѯSQL
	 * @return
	 * @throws SQLException
	 */
	public static long queryLongFromDB(Connection conn, String sql) throws SQLException {
		long returnValue = 0;

		Statement stat = null;
		ResultSet rs = null;
		try {
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			if (rs.next()) {
				returnValue = rs.getLong(1);
			}
		} finally {
			close(rs);
			close(stat);
		}

		return returnValue;
	}

	/**
	 * ��ȡ���ϵ�OR-MAPPING����
	 * 
	 * @param rs
	 *            ����
	 * @return
	 * @throws SQLException
	 */
	public static List<Mapping> getORMappingList(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		List<Mapping> orMappingList = new ArrayList<Mapping>(meta.getColumnCount());
		/*
		 * ѭ������ȡ�������������
		 */
		Mapping mapping = null;
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			mapping = new Mapping();

			mapping.setColumnName(meta.getColumnName(i)); // �ֶ����
			mapping.setColumnType(meta.getColumnType(i)); // �ֶ�����JDBC
			mapping.setPropertyName(StringUtil.getJavaPropertyName(mapping.getColumnName())); // JAVA�������
			mapping.setSetMethodName("set" + StringUtil.getMethodPropertyName(mapping.getPropertyName())); // ����set���������

			mapping.setJavaType(convertJdbcType2JavaType(mapping.getColumnType())); // JAVA�ֶ����

			orMappingList.add(mapping);
		}

		return orMappingList;
	}

	public static List<Mapping> getORMappingListOracle(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		List<Mapping> orMappingList = new ArrayList<Mapping>(meta.getColumnCount());
		/*
		 * ѭ������ȡ�������������
		 */
		Mapping mapping = null;
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			mapping = new Mapping();

			mapping.setColumnName(meta.getColumnName(i)); // �ֶ����
			mapping.setColumnType(meta.getColumnType(i)); // �ֶ�����JDBC
			mapping.setPropertyName(StringUtil.getJavaPropertyName(mapping.getColumnName())); // JAVA�������
			mapping.setSetMethodName("set" + StringUtil.getMethodPropertyName(mapping.getPropertyName())); // ����set���������

			mapping.setJavaType(
					convertJdbcType2JavaTypeOracle(mapping.getColumnType(), meta.getPrecision(i), meta.getScale(i))); // JAVA�ֶ����

			orMappingList.add(mapping);
		}

		return orMappingList;
	}

	/**
	 * ��ȡ���ϵ�OR-MAPPING���� K:�ֶ���V:�ֶ�ӳ���ϵ
	 * 
	 * @param rs
	 *            ����
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, Mapping> getORMappingMap(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		Map<String, Mapping> orMappingMap = new HashMap<String, Mapping>(meta.getColumnCount());
		/*
		 * ѭ������ȡ�������������
		 */
		Mapping mapping = null;
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			mapping = new Mapping();

			mapping.setColumnName(meta.getColumnName(i)); // �ֶ����
			mapping.setColumnType(meta.getColumnType(i)); // �ֶ�����JDBC
			mapping.setPropertyName(StringUtil.getJavaPropertyName(mapping.getColumnName())); // JAVA�������
			mapping.setSetMethodName("set" + StringUtil.getMethodPropertyName(mapping.getPropertyName())); // ����set���������

			mapping.setJavaType(convertJdbcType2JavaType(mapping.getColumnType())); // JAVA�ֶ����

			orMappingMap.put(mapping.getColumnName(), mapping);
		}

		return orMappingMap;
	}

	public static Map<String, Mapping> getORMappingMapOracle(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		Map<String, Mapping> orMappingMap = new HashMap<String, Mapping>(meta.getColumnCount());
		/*
		 * ѭ������ȡ�������������
		 */
		Mapping mapping = null;
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			mapping = new Mapping();

			mapping.setColumnName(meta.getColumnName(i)); // �ֶ����
			mapping.setColumnType(meta.getColumnType(i)); // �ֶ�����JDBC
			mapping.setPropertyName(StringUtil.getJavaPropertyName(mapping.getColumnName())); // JAVA�������
			mapping.setSetMethodName("set" + StringUtil.getMethodPropertyName(mapping.getPropertyName())); // ����set���������

			mapping.setJavaType(
					convertJdbcType2JavaTypeOracle(mapping.getColumnType(), meta.getPrecision(i), meta.getScale(i))); // JAVA�ֶ����

			orMappingMap.put(mapping.getColumnName(), mapping);
		}

		return orMappingMap;
	}

	/**
	 * �������ʵ�����
	 * 
	 * @param rs
	 *            ����
	 * @param orMappingList
	 *            ӳ���ϵ�б�
	 * @param clazz
	 *            ��Ҫ�����Ķ�����
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 */
	@SuppressWarnings("rawtypes")
	public static Object createBeanFromResultSet(ResultSet rs, List<Mapping> orMappingList, Class clazz)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			SQLException {
		if (rs == null) {
			throw new IllegalArgumentException("Parameter resultSet is null !");
		}
		if (orMappingList == null) {
			throw new IllegalArgumentException("Parameter orMappingList is null !");
		}
		if (clazz == null) {
			throw new IllegalArgumentException("Parameter class is null !");
		}
		// �ȳ�ʼ������
		Object obj = clazz.newInstance();
		Object value = null;
		for (Mapping mapping : orMappingList) {

			// ��ȡ�ֶζ�Ӧ�����
			value = getObjectFromResultSet(rs, // ���
					mapping.getColumnType(), // �ֶ�����JDBC
					mapping.getColumnName() // �ֶ����
			);

			if (value == null) {
				// ������Ϊ�գ���ֱ����һ���ֶ�
				continue;
			}

			// ���÷���
			ClassUtil.invokeMethod(obj, // bean����
					mapping.getSetMethodName(), // �������
					new Object[] { value } // ��������Ҫ�Ĳ���
			);

		}
		return obj;
	}

	/**
	 * �ӽ����л�ȡ��Ӧ�����
	 * 
	 * @param rs
	 *            �����
	 * @param jdbcType
	 *            �������
	 * @param columnLabel
	 *            �ֶ���
	 * @return
	 * @throws SQLException
	 */
	public static Object getObjectFromResultSet(ResultSet rs, int jdbcType, String columnLabel) throws SQLException {

		Object returnObj = null;
		if (jdbcType == Types.CHAR || jdbcType == Types.VARCHAR || jdbcType == Types.CLOB || jdbcType == Types.NCHAR
				|| jdbcType == Types.NVARCHAR || jdbcType == Types.NCLOB || jdbcType == Types.LONGVARCHAR
				|| jdbcType == Types.LONGNVARCHAR) {
			// �ַ�����
			returnObj = rs.getString(columnLabel);
		} else if (jdbcType == Types.DATE || jdbcType == Types.TIMESTAMP || jdbcType == Types.TIME) {
			// ʱ������
			returnObj = rs.getDate(columnLabel);

		} else if (jdbcType == Types.DECIMAL || jdbcType == Types.DOUBLE || jdbcType == Types.FLOAT
				|| jdbcType == Types.NUMERIC) {
			// ���ֽ������
			returnObj = rs.getBigDecimal(columnLabel);

		} else if (jdbcType == Types.INTEGER || jdbcType == Types.SMALLINT || jdbcType == Types.TINYINT) {
			// ��������
			returnObj = rs.getInt(columnLabel);

		} else if (jdbcType == Types.BIGINT || jdbcType == Types.SMALLINT || jdbcType == Types.TINYINT) {
			// ����������
			returnObj = rs.getLong(columnLabel);
		} else {
			throw new RuntimeException(String.format("jdbcType=%s, columnLabel=%s", jdbcType, columnLabel));
		}
		return returnObj;
	}

	/**
	 * �ӽ���л��ָ�����ַ�
	 * 
	 * @param rs
	 *            ���
	 * @param columnName
	 *            �ֶ���
	 * @param jdbcType
	 *            �ֶ�����
	 * @param charset
	 *            �ַ�
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public static String getString(ResultSet rs, String columnName, int jdbcType, String charset)
			throws UnsupportedEncodingException, SQLException {
		String returnValue = null;
		if (jdbcType == Types.BLOB) {
			Blob blob = rs.getBlob(columnName);
			if (blob != null) {
				returnValue = new String(blob.getBytes(1, (int) blob.length()), charset);
			}
		} else {
			returnValue = rs.getString(columnName);
			if (returnValue != null) {
				returnValue = new String(returnValue.getBytes(charset));
			}
		}
		return returnValue;
	}

	/**
	 * �ӽ���л��ָ�����ַ�
	 * 
	 * @param rs
	 *            ���
	 * @param columnName
	 *            �ֶ���
	 * @param jdbcType
	 *            �ֶ�����
	 * @return
	 * @throws SQLException
	 */
	public static String getString(ResultSet rs, String columnName, int jdbcType) throws SQLException {
		String returnValue = null;
		if (jdbcType == Types.BLOB) {
			Blob blob = rs.getBlob(columnName);
			if (blob != null) {
				returnValue = new String(blob.getBytes(1, (int) blob.length()));
			}
		} else {
			returnValue = rs.getString(columnName);
		}
		return returnValue;
	}

	/**
	 * �ӽ���л��ָ�����ַ�
	 * 
	 * @param rs
	 *            ���
	 * @param column
	 *            �ֶ�����
	 * @param charset
	 *            �ַ�
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public static String getString(ResultSet rs, int column, String charset)
			throws UnsupportedEncodingException, SQLException {
		int jdbcType = rs.getMetaData().getColumnType(column);

		String returnValue = null;
		if (jdbcType == Types.BLOB) {
			// BLOB���⴦��һ��
			Blob blob = rs.getBlob(column);
			if (blob != null) {
				returnValue = new String(blob.getBytes(1, (int) blob.length()), charset);
			}
		} else {
			returnValue = rs.getString(column);
			if (returnValue != null) {
				returnValue = new String(returnValue.getBytes(charset));
			}
		}
		return returnValue;
	}

	/**
	 * �ӽ���л��ָ�����ַ�
	 * 
	 * @param rs
	 *            ���
	 * @param column
	 *            �ֶ�����
	 * @return
	 * @throws SQLException
	 */
	public static String getString(ResultSet rs, int column) throws SQLException {
		int jdbcType = rs.getMetaData().getColumnType(column);

		String returnValue = null;
		if (jdbcType == Types.BLOB) {
			// BLOB���⴦��һ��
			Blob blob = rs.getBlob(column);
			if (blob != null) {
				returnValue = new String(blob.getBytes(1, (int) blob.length()));
			}
		} else {
			returnValue = rs.getString(column);
		}
		return returnValue;
	}

	/**
	 * ����ݿ�����ת��ΪJAVA����
	 * 
	 * @param jdbcType
	 * @return
	 */
	public static String convertJdbcType2JavaType(int jdbcType) {
		// java.sql.Types
		// public static final int ARRAY 2003
		// public static final int BINARY -2
		// public static final int BIT -7
		// public static final int BLOB 2004
		// public static final int BOOLEAN 16
		// public static final int DATALINK 70
		// public static final int DISTINCT 2001
		// public static final int JAVA_OBJECT 2000
		// public static final int LONGVARBINARY -4
		// public static final int NULL 0
		// public static final int OTHER 1111
		// public static final int REAL 7
		// public static final int REF 2006
		// public static final int ROWID -8
		// public static final int SQLXML 2009
		// public static final int STRUCT 2002
		// public static final int VARBINARY -3

		/*
		 * �������������ʱ��֧��
		 */
		String returnType = null;
		if (jdbcType == Types.CHAR || jdbcType == Types.VARCHAR || jdbcType == Types.CLOB || jdbcType == Types.NCHAR
				|| jdbcType == Types.NVARCHAR || jdbcType == Types.NCLOB || jdbcType == Types.LONGVARCHAR
				|| jdbcType == Types.LONGNVARCHAR) {
			// �ַ�����
			returnType = "String";
		} else if (jdbcType == Types.DATE || jdbcType == Types.TIMESTAMP || jdbcType == Types.TIME) {
			// ʱ������
			returnType = "Date";

		} else if (jdbcType == Types.DECIMAL || jdbcType == Types.DOUBLE || jdbcType == Types.FLOAT
				|| jdbcType == Types.NUMERIC) {
			// ���ֽ������
			returnType = "BigDecimal";

		} else if (jdbcType == Types.INTEGER || jdbcType == Types.SMALLINT || jdbcType == Types.TINYINT) {
			// ��������
			returnType = "Integer";

		} else if (jdbcType == Types.BIGINT) {
			// ����������
			returnType = "Long";
		} else {
			throw new RuntimeException("jdbcType=" + jdbcType);
		}

		return returnType;

	}

	/**
	 * ����ݿ�����ת��ΪJAVA����
	 * 
	 * @param jdbcType
	 * @return
	 */
	public static String convertJdbcType2JavaTypeOracle(int jdbcType, int precision, int scale) {
		// java.sql.Types
		// public static final int ARRAY 2003
		// public static final int BINARY -2
		// public static final int BIT -7
		// public static final int BLOB 2004
		// public static final int BOOLEAN 16
		// public static final int DATALINK 70
		// public static final int DISTINCT 2001
		// public static final int JAVA_OBJECT 2000
		// public static final int LONGVARBINARY -4
		// public static final int NULL 0
		// public static final int OTHER 1111
		// public static final int REAL 7
		// public static final int REF 2006
		// public static final int ROWID -8
		// public static final int SQLXML 2009
		// public static final int STRUCT 2002
		// public static final int VARBINARY -3

		/*
		 * �������������ʱ��֧��
		 */
		String returnType = null;
		if (jdbcType == Types.CHAR || jdbcType == Types.VARCHAR || jdbcType == Types.CLOB || jdbcType == Types.NCHAR
				|| jdbcType == Types.NVARCHAR || jdbcType == Types.NCLOB || jdbcType == Types.LONGVARCHAR
				|| jdbcType == Types.LONGNVARCHAR) {
			// �ַ�����
			returnType = "String";
		} else if (jdbcType == Types.DATE || jdbcType == Types.TIMESTAMP || jdbcType == Types.TIME) {
			// ʱ������
			returnType = "Date";

		} else if (jdbcType == Types.DECIMAL || jdbcType == Types.DOUBLE || jdbcType == Types.FLOAT
				|| jdbcType == Types.NUMERIC) {

			if (scale == 0) {
				// ������0
				if (precision <= 8) {
					returnType = "Integer";
				} else {
					returnType = "Long";
				}
			} else {
				returnType = "BigDecimal";

			}

		} else if (jdbcType == Types.INTEGER || jdbcType == Types.SMALLINT || jdbcType == Types.TINYINT) {
			// ��������
			returnType = "Integer";

		} else if (jdbcType == Types.BIGINT) {
			// ����������
			returnType = "Long";
		} else {
			throw new RuntimeException("jdbcType=" + jdbcType);
		}

		return returnType;

	}

	public static String convertJdbcType2JavaTypeFull(int jdbcType) {
		// java.sql.Types
		// public static final int ARRAY 2003
		// public static final int BINARY -2
		// public static final int BIT -7
		// public static final int BLOB 2004
		// public static final int BOOLEAN 16
		// public static final int DATALINK 70
		// public static final int DISTINCT 2001
		// public static final int JAVA_OBJECT 2000
		// public static final int LONGVARBINARY -4
		// public static final int NULL 0
		// public static final int OTHER 1111
		// public static final int REAL 7
		// public static final int REF 2006
		// public static final int ROWID -8
		// public static final int SQLXML 2009
		// public static final int STRUCT 2002
		// public static final int VARBINARY -3

		/*
		 * �������������ʱ��֧��
		 */
		String returnType = null;
		if (jdbcType == Types.CHAR || jdbcType == Types.VARCHAR || jdbcType == Types.CLOB || jdbcType == Types.NCHAR
				|| jdbcType == Types.NVARCHAR || jdbcType == Types.NCLOB || jdbcType == Types.LONGVARCHAR
				|| jdbcType == Types.LONGNVARCHAR) {
			// �ַ�����
			returnType = "java.lang.String";
		} else if (jdbcType == Types.DATE || jdbcType == Types.TIMESTAMP || jdbcType == Types.TIME) {
			// ʱ������
			returnType = "java.util.Date";

		} else if (jdbcType == Types.DECIMAL || jdbcType == Types.DOUBLE || jdbcType == Types.FLOAT
				|| jdbcType == Types.NUMERIC) {
			// ���ֽ������
			returnType = "java.math.BigDecimal";

		} else if (jdbcType == Types.INTEGER || jdbcType == Types.SMALLINT || jdbcType == Types.TINYINT) {
			// ��������
			returnType = "java.lang.Integer";

		} else if (jdbcType == Types.BIGINT) {
			// ����������
			returnType = "java.lang.Long";
		} else {
			throw new RuntimeException("jdbcType=" + jdbcType);
		}

		return returnType;

	}

	public static String convertJdbcType2SqlMapTypeOracle(int jdbcType) {
		// java.sql.Types
		// public static final int ARRAY 2003
		// public static final int BINARY -2
		// public static final int BIT -7
		// public static final int BLOB 2004
		// public static final int BOOLEAN 16
		// public static final int DATALINK 70
		// public static final int DISTINCT 2001
		// public static final int JAVA_OBJECT 2000
		// public static final int LONGVARBINARY -4
		// public static final int NULL 0
		// public static final int OTHER 1111
		// public static final int REAL 7
		// public static final int REF 2006
		// public static final int ROWID -8
		// public static final int SQLXML 2009
		// public static final int STRUCT 2002
		// public static final int VARBINARY -3

		/*
		 * �������������ʱ��֧��
		 */
		String returnType = null;
		if (jdbcType == Types.CHAR || jdbcType == Types.NCHAR) {
			// �ַ�����
			returnType = "CHAR";
		} else if (jdbcType == Types.VARCHAR || jdbcType == Types.NVARCHAR || jdbcType == Types.LONGVARCHAR
				|| jdbcType == Types.LONGNVARCHAR) {
			// ʱ������
			returnType = "VARCHAR";
		} else if (jdbcType == Types.CLOB || jdbcType == Types.NCLOB) {
			// �ַ�����
			returnType = "CLOB";
		} else if (jdbcType == Types.DATE) {
			// ʱ������
			returnType = "DATE";
		} else if (jdbcType == Types.TIMESTAMP) {
			// ʱ������
			returnType = "TIMESTAMP";

		} else if (jdbcType == Types.TIME) {
			// ʱ������
			returnType = "TIME";

		} else if (jdbcType == Types.DECIMAL || jdbcType == Types.DOUBLE || jdbcType == Types.FLOAT
				|| jdbcType == Types.NUMERIC) {
			// ���ֽ������
			returnType = "NUMERIC";

		} else if (jdbcType == Types.INTEGER || jdbcType == Types.SMALLINT || jdbcType == Types.TINYINT) {
			// ��������
			returnType = "INTEGER";

		} else if (jdbcType == Types.BIGINT) {
			// ����������
			returnType = "BIGINT";
		} else {
			throw new RuntimeException("jdbcType=" + jdbcType);
		}

		return returnType;

	}

}
