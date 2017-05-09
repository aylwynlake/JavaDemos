package com.em.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.em.utils.Index;

/**
 * �ַ�ʵ����
 * 
 * @author pccc
 */
public class StringUtil {

	public static final String SPACE = " ";
	public static final String EMPTY = "";
	public static final String LF = "\n";
	public static final String CR = "\r";
	public static final String CHARSET_UTF8 = "UTF-8";
	public static final String CHARSET_GB2312 = "GB2312";
	public static final String CHARSET_GBK = "GBK";
	public static final String CHARSET_GB18030 = "GB18030";

	public static void main(String[] args) throws UnsupportedEncodingException {

		// String[] str = StringUtils.substringsBetween(" a.test>'${DATE}' and
		// b.x='${XXX}'", "${", "}");
		// String a =
		// "3c7265733a726573756c747320786d6c6e733a7265733d22687474703a2f2f726573756c742e6f757465726372656469742e70636363223e3c7265733a726573756c74206964547970653d22302220696456616c75653d22313138313632313938303033303731393532222071756572794e616d653d22cea4d0a1b1a6222072656649643d2222207461726765743d223022207461736b49643d2222207374617475733d2230223e3c7265733a646174613e3c7265733a6e636969735f726f6f742f3e3c7265733a6564755f726f6f742f3e3c7265733a6e63696973636d705f726f6f742f3e3c7265733a70625f726f6f742f3e3c7265733a736f6369616c5f726f6f742f3e3c7265733a636172696e666f5f726f6f742f3e3c7265733a636e73735f726f6f742f3e3c2f7265733a646174613e3c2f7265733a726573756c743e3c2f7265733a726573756c74733e";
		// System.out.println(hex2String(a));

		// String filename = "sdfsf. sdfdasda ";

		// System.out.println(getFileSuffix(filename));

		// System.out.println(getJavaPropertyName("cust_no_PAR__AMT_"));
		//
		// System.out.println(toJson(null));
		// System.out.println(gb2312("�ϴ��㷨��������"));
		// System.out.println(gbk("�ϴ��㷨��������"));
		// System.out.println(gb18030("�ϴ��㷨��������"));
		// System.out.println(utf8("�ϴ��㷨��������"));

		String uuid = randomUUID();
		System.out.println(uuid.length() + " = " + uuid);

		// List<String> list = new ArrayList<String>();
		//
		// list.add("1111");
		// list.add("2222");
		// list.add("3333");
		// list.add("4444");
		// list.add("5555");
		//
		// System.out.println(getInCondition(list));
	}

	/**
	 * ��ȡһ���µ�UUIDֵ UUID ��ʾһ�� 128 λ��ֵ��
	 * 
	 * @return
	 */
	public static String randomUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * ��ȡSQL������In�������Ӿ�
	 * 
	 * @param list
	 *            �����б�
	 * @return
	 */
	public static String getInCondition(List<String> list) {
		StringBuilder sb = new StringBuilder(512);
		sb.append("(");
		for (String string : list) {
			sb.append("'").append(string).append("',");
		}
		int lenth = sb.length();
		sb.replace(lenth - 1, lenth, ")");
		return sb.toString();
	}

	/**
	 * ��ȡUTF-8���ַ�
	 * 
	 * @param str
	 *            �ַ�
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String utf8(String str) throws UnsupportedEncodingException {
		return getEncodingString(str, CHARSET_UTF8);
	}

	/**
	 * ��ȡGBK���ַ�
	 * 
	 * @param str
	 *            �ַ�
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String gbk(String str) throws UnsupportedEncodingException {
		return getEncodingString(str, CHARSET_GBK);
	}

	/**
	 * ��ȡGB2312���ַ�
	 * 
	 * @param str
	 *            �ַ�
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String gb2312(String str) throws UnsupportedEncodingException {
		return getEncodingString(str, CHARSET_GB2312);
	}

	/**
	 * ��ȡGB18030���ַ�
	 * 
	 * @param str
	 *            �ַ�
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String gb18030(String str) throws UnsupportedEncodingException {
		return getEncodingString(str, CHARSET_GB18030);
	}

	/**
	 * ��ȡָ�������ʽ���ַ�
	 * 
	 * @param str
	 *            �ַ�
	 * @param charset
	 *            �ַ�
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getEncodingString(String str, String charset) throws UnsupportedEncodingException {
		if (str == null) {
			return str;
		}
		return new String(str.getBytes(charset));
	}

	/**
	 * ��ԭ�ַ���ɾ��ָ���ַ�
	 * 
	 * @param str
	 *            ԭ�ַ�
	 * @param remove
	 *            ��Ҫɾ����ַ�
	 * @return
	 */
	public static String remove(String str, String remove) {
		if (StringUtils.isEmpty(str) || StringUtils.isEmpty(remove)) {
			return str;
		}
		return StringUtils.replace(str, remove, "", -1);
	}

	/**
	 * ��ԭ�ַ���ɾ��ָ���ַ�
	 * 
	 * @param str
	 *            ԭ�ַ�
	 * @param remove
	 *            ��Ҫɾ����ַ�
	 * @return
	 */
	public static String remove(String str, char remove) {
		if (StringUtils.isEmpty(str) || str.indexOf(remove) == -1) {
			return str;
		}
		char[] chars = str.toCharArray();
		int pos = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != remove) {
				chars[(pos++)] = chars[i];
			}
		}
		return new String(chars, 0, pos);
	}

	/**
	 * �ж�
	 * 
	 * @param source
	 * @param with
	 * @return
	 */
	public static boolean isBeginWith(String source, String with) {
		if (source == null || with == null) {
			return false;
		}
		return source.indexOf(with) == 0;
	}
	
	public static String genUUID() {
		UUID uuid = UUID.randomUUID();
		String s = uuid.toString();//
		int p = 0;
		int j = 0;
		char[] buf = new char[32];
		while (p < s.length()) {
			char c = s.charAt(p);
			p += 1;
			if (c == '-')
				continue;
			buf[j] = c;
			j += 1;
		}
		return new String(buf);
	}


	/**
	 * �����ַ�Ƚϣ����Դ�Сд
	 * 
	 * @param str1
	 *            �ַ�1
	 * @param str2
	 *            �ַ�2
	 * @return
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		return StringUtils.equalsIgnoreCase(str1, str2);
	}

	/**
	 * �����ַ�֮��Ƚ�
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean equals(String str1, String str2) {
		if (str1 == str2) {
			// ���������ͬһ������ֱ�ӷ���true
			return true;
		}
		if (str1 == null || str2 == null) {
			// �������һ��Ϊnull����ֱ�ӷ���false
			return false;
		}
		return str1.equals(str2);
	}

	/**
	 * byte���תΪ16�����ַ�
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		return CryptoUtil.byte2hex(b);
	}

	/**
	 * ɾ��հ��ַ�
	 * 
	 * @param className
	 * @return
	 */
	public static String deleteWhiteSpace(String className) {
		return className.replace(SPACE, EMPTY);
	}

	/**
	 * ��ȡ�ļ���׺��
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileSuffix(String filename) {
		String returnStr = null;
		if (StringUtils.isNotBlank(filename)) {

			returnStr = StringUtils.substringAfterLast(StringUtils.trimToEmpty(filename), ".");

		}
		return returnStr;
	}

	public static String getFileExtension(String filename) {
		return getFileSuffix(filename);
	}

	/**
	 * ��ȡ��ݿ��ֶε��������
	 * 
	 * @param columnName
	 *            ��ݿ��ֶ����
	 * @return
	 */
	public static String getJavaPropertyName(String columnName) {
		if (columnName == null) {
			return null;
		}

		// ��תΪСд
		columnName = columnName.trim().toLowerCase();

		StringBuilder sb = new StringBuilder(columnName.length());

		boolean isBeforeUnderline = false;
		String underline = "_";
		String str = null;
		for (int i = 0; i < columnName.length(); i++) {
			str = columnName.substring(i, i + 1);

			if (underline.equals(str)) {
				// ������»���
				isBeforeUnderline = true;
			} else if (isBeforeUnderline) {
				// ���ǰһ�����»���
				sb.append(str.toUpperCase());

				isBeforeUnderline = false;
			} else {
				sb.append(str);
			}
		}
		return sb.toString();
	}

	/**
	 * ��ȡ�������ϵ��������
	 * 
	 * @param javaPropertyName
	 * @return
	 */
	public static String getMethodPropertyName(String javaPropertyName) {
		if (javaPropertyName == null) {
			return null;
		}

		// ��תΪСд
		javaPropertyName = javaPropertyName.trim();

		return javaPropertyName.substring(0, 1).toUpperCase() + javaPropertyName.substring(1);
	}

	public static String getMethodPropertyName2(String javaPropertyName) {
		if (javaPropertyName == null) {
			return null;
		}

		// ��תΪСд
		javaPropertyName = javaPropertyName.trim();

		return javaPropertyName.substring(0, 1).toLowerCase() + javaPropertyName.substring(1);
	}

	/**
	 * ͨ���ַ��ȡҳ����
	 * 
	 * @param charset
	 *            �ַ�
	 * @return
	 */
	public static String getPageCodeByCharset(String charset) {
		String returnStr = null;

		if ("UTF-8".equalsIgnoreCase(charset)) {
			returnStr = "1208";
		} else if ("GBK".equalsIgnoreCase(charset)) {
			returnStr = "1386";
		} else if ("GB18030".equalsIgnoreCase(charset)) {
			returnStr = "1392";
		} else if ("GB2312".equalsIgnoreCase(charset)) {
			returnStr = "1381";
		} else if ("UTF-16".equalsIgnoreCase(charset)) {
			returnStr = "1204";
		} else if ("UTF-32".equalsIgnoreCase(charset)) {
			returnStr = "1236";
		} else if ("ISO-8859-1".equalsIgnoreCase(charset)) {
			returnStr = "819";
		} else {
			throw new UnsupportedCharsetException(charset);
		}
		return returnStr;
	}

	/**
	 * 16�����ַ�תΪbyte����
	 * 
	 * @param hexStr
	 *            16�����ַ�
	 * @return
	 */
	public static byte[] hex2byte(String hexStr) {
		int length = hexStr.length() / 2;
		byte b[] = new byte[length];

		String subStr = null;
		for (int i = 0; i < length; i++) {
			subStr = StringUtils.substring(hexStr, i * 2, i * 2 + 2);
			b[i] = Integer.decode("0x" + subStr).byteValue();
		}
		return b;
	}

	/**
	 * 16�����ַ�תΪ�ַ�����Ĭ��ϵͳ����
	 * 
	 * @param hexStr
	 *            16�����ַ�ת
	 * @return
	 */
	public static String hex2String(String hexStr) {
		return new String(hex2byte(hexStr));
	}

	/**
	 * �ж��ַ�Ϊ��
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return StringUtils.isBlank(str);
	}

	/**
	 * �ж��ַ�Ϊ��
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		return StringUtils.isNotBlank(str);
	}

	/**
	 * �����滻
	 * 
	 * @param str
	 * @param paramStart
	 * @param paramEnd
	 * @param valueMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String replaceAllParam(String str, String paramStart, String paramEnd, Map valueMap) {
		String returnStr = null;
		if (str != null) {
			// keys �п����ǿյ�
			String[] keys = StringUtils.substringsBetween(str, paramStart, paramEnd);

			returnStr = str;

			if (keys != null) {
				for (String key : keys) {
					returnStr = StringUtils.replace(returnStr, paramStart + key + paramEnd,
							String.valueOf(valueMap.get(key)));
				}
			}
		}

		return returnStr;
	}

	/**
	 * �滻�ַ��еĻ��з�
	 * 
	 * @param inStr
	 * @return
	 */
	public static String replaceNewline(String inStr, String str) {
		String returnStr = inStr;
		if (returnStr != null) {
			returnStr = StringUtils.replace(returnStr, LF, str);
			returnStr = StringUtils.replace(returnStr, CR, str);
		}
		return returnStr;
	}

	/**
	 * ����Ĭ���ַ�ת��Ϊ16�����ַ�
	 * 
	 * @param str
	 * @return
	 */
	public static String string2hex(String str) {
		return CryptoUtil.byte2hex(str.getBytes());
	}

	public static String toJson(Object obj) {
		return new Gson().toJson(obj);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object fromJson(String json, Class clazz) {
		return new Gson().fromJson(json, clazz);
	}

	/**
	 * ȥ�����˿ո�����������null�����ػ���null
	 * 
	 * @param str
	 * @return
	 */
	public static String trim(String str) {
		if (str == null) {
			return null;
		}
		return str.trim();
	}

	/**
	 * ȥ�����˿ո�����������null��������""�����ַ�
	 * 
	 * @param str
	 * @return
	 */
	public static String trimToEmpty(String str) {
		if (str == null) {
			return EMPTY;
		}
		return str.trim();
	}

	/**
	 * ��buff�д�index.offsetλ�ÿ�ʼȡstrLen�ĳ��ȵ��ַ�
	 * 
	 * @param bytes
	 *            �����byte����
	 * @param cutLength
	 *            ��Ҫ��ȡ��byte��
	 * @param index
	 *            �������
	 * @return ȡ�����ַ�
	 * @throws UnsupportedEncodingException
	 */
	public static String mqMsg2Str(byte[] bytes, int cutLength, Index index) throws UnsupportedEncodingException {
		return mqMsg2Str(bytes, cutLength, index, null);
	}

	/**
	 * ��buff�д�index.offsetλ�ÿ�ʼȡstrLen�ĳ��ȵ��ַ�
	 * 
	 * @param buff
	 *            �����byte����
	 * @param cutLength
	 *            ��Ҫ��ȡ��byte��
	 * @param index
	 *            �������
	 * @param charset
	 *            �ַ����Ϊ�ջ���null�������ϵͳ��ǰĬ���ַ�
	 * @return ȡ�����ַ�
	 * @throws UnsupportedEncodingException
	 */
	public static String mqMsg2Str(byte[] buff, int cutLength, Index index, String charset)
			throws UnsupportedEncodingException {
		byte[] returnByteData = new byte[cutLength];
		int buffIndex = 0, returnDataIndex = 0;
		// int validLen = cutLength;
		byte bChar = 0;
		buffIndex = index.offset;

		// ���ȿ���
		if (buffIndex >= buff.length) {
			return null;
		}

		bChar = buff[buffIndex++];
		while (true) {
			// ����Ӣ���ֽ�
			if (0 <= bChar && bChar < 128) {
				returnByteData[returnDataIndex++] = bChar;
				if (returnDataIndex >= cutLength)
					break;

				// ���ȿ���
				if (buffIndex >= buff.length) {
					break;
				}

				bChar = buff[buffIndex++];

			} else {

				// ���������ֽ�
				while (bChar < 0 || bChar > 128) {
					/* ����һ������ */
					// ���ֵ�һ���ֽ�
					returnByteData[returnDataIndex++] = bChar;
					if (returnDataIndex >= cutLength)
						break;
					// ���ֵڶ����ֽ�

					// ���ȿ���
					if (buffIndex >= buff.length) {
						break;
					}

					returnByteData[returnDataIndex++] = buff[buffIndex++];
					if (returnDataIndex >= cutLength)
						break;

					// ���ȿ���
					if (buffIndex >= buff.length) {
						break;
					}
					// ȡ��һ���ֽ�
					bChar = buff[buffIndex++];
				}
				// validLen -= 2;
			}
			if (returnDataIndex >= cutLength)
				break;
		}
		// ����offset
		index.offset += cutLength;

		// �����Ƿ�����Ż�������
		// byte[] rb = new byte[validLen];
		// for (i = 0; i < validLen; i++)
		// rb[i] = rData[i];

		String rtnString = null;
		if (isBlank(charset)) {
			rtnString = new String(returnByteData);
		} else {
			rtnString = new String(returnByteData, charset);
		}

		// System.out.println("[" + rtnString + "]");

		return rtnString;
	}

}
