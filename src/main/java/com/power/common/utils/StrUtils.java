package com.power.common.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串的帮助类，提供静态方法，不可以实例化。
 * 
 * @author liufang
 * 
 */
public class StrUtils {
	
	private static final String emailstr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";//邮箱正则表达式
	private static final String mobilestr = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
	/**
	 * 禁止实例化
	 */
	private StrUtils() {
	}
	
	private static boolean isMatch(String regex, String orginal){
		if (orginal == null || orginal.trim().equals("")) {
            return false;
        }
		Pattern pattern = Pattern.compile(regex);
		Matcher isNum = pattern.matcher(orginal);
		return isNum.matches();
	}
	public static boolean isDecimal(String orginal){
		return isMatch("[-+]{0,1}\\d+\\.?\\d*|[-+]{0,1}\\d*\\.?\\d+", orginal);
	}
	
	/**
	 * 小数格式判断
	 * @param orginal
	 * @return BigDecimal 
	 */
	public static BigDecimal getDecimal(String orginal){
		return isDecimal(orginal)?new BigDecimal(orginal):BigDecimal.ZERO;
	}

	/**
	 * 处理url
	 * 
	 * url为null返回null，url为空串或以http://或https://开头，则加上http://，其他情况返回原参数。
	 * 
	 * @param url
	 * @return
	 */
	public static String handelUrl(String url) {
		if (url == null) {
			return null;
		}
		url = url.trim();
		if (url.equals("") || url.startsWith("http://")
				|| url.startsWith("https://")) {
			return url;
		} else {
			return "http://" + url.trim();
		}
	}

	/**
	 * 分割并且去除空格
	 * 
	 * @param str
	 *            待分割字符串
	 * @param sep
	 *            分割符
	 * @param sep2
	 *            第二个分隔符
	 * @return 如果str为空，则返回null。
	 */
	public static String[] splitAndTrim(String str, String sep, String sep2) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		if (!StringUtils.isBlank(sep2)) {
			str = StringUtils.replace(str, sep2, sep);
		}
		String[] arr = StringUtils.split(str, sep);
		// trim
		for (int i = 0, len = arr.length; i < len; i++) {
			arr[i] = arr[i].trim();
		}
		return arr;
	}

	/**
	 * 文本转html
	 * 
	 * @param txt
	 * @return
	 */
	public static String txt2htm(String txt) {
		if (StringUtils.isBlank(txt)) {
			return txt;
		}
		StringBuilder sb = new StringBuilder((int) (txt.length() * 1.2));
		char c;
		boolean doub = false;
		for (int i = 0; i < txt.length(); i++) {
			c = txt.charAt(i);
			if (c == ' ') {
				if (doub) {
					sb.append(' ');
					doub = false;
				} else {
					sb.append("&nbsp;");
					doub = true;
				}
			} else {
				doub = false;
				switch (c) {
				case '&':
					sb.append("&amp;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '\n':
					sb.append("<br/>");
					break;
				default:
					sb.append(c);
					break;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 剪切文本。如果进行了剪切，则在文本后加上"..."
	 * 
	 * @param s
	 *            剪切对象。
	 * @param len
	 *            编码小于256的作为一个字符，大于256的作为两个字符。
	 * @return
	 */
	public static String textCut(String s, int len, String append) {
		if (s == null) {
			return null;
		}
		int slen = s.length();
		if (slen <= len) {
			return s;
		}
		// 最大计数（如果全是英文）
		int maxCount = len * 2;
		int count = 0;
		int i = 0;
		for (; count < maxCount && i < slen; i++) {
			if (s.codePointAt(i) < 256) {
				count++;
			} else {
				count += 2;
			}
		}
		if (i < slen) {
			if (count > maxCount) {
				i--;
			}
			if (!StringUtils.isBlank(append)) {
				if (s.codePointAt(i - 1) < 256) {
					i -= 2;
				} else {
					i--;
				}
				return s.substring(0, i) + append;
			} else {
				return s.substring(0, i);
			}
		} else {
			return s;
		}
	}

	public static String htmlCut(String s, int len, String append) {
		String text = html2Text(s, len * 2);
		return textCut(text, len, append);
	}

	public static String html2Text(String html, int len) {
		return "";
//		try {
//			Lexer lexer = new Lexer(html);
//			Node node;
//			StringBuilder sb = new StringBuilder(html.length());
//			while ((node = lexer.nextNode()) != null) {
//				if (node instanceof TextNode) {
//					sb.append(node.toHtml());
//				}
//				if (sb.length() > len) {
//					break;
//				}
//			}
//			return sb.toString();
//		} catch (ParserException e) {
//			throw new RuntimeException(e);
//		}
	}

	/**
	 * 检查字符串中是否包含被搜索的字符串。被搜索的字符串可以使用通配符'*'。
	 * 
	 * @param str
	 * @param search
	 * @return
	 */
	public static boolean contains(String str, String search) {
		if (StringUtils.isBlank(str) || StringUtils.isBlank(search)) {
			return false;
		}
		String reg = StringUtils.replace(search, "*", ".*");
		Pattern p = Pattern.compile(reg);
		return p.matcher(str).matches();
	}
	
	
	/**
	 * 验证邮箱合法性
	 * @param email
	 * @return
	 */
	public static boolean validatEmail(String email) {
		Pattern regex = Pattern.compile(emailstr);
	    Matcher matcher = regex.matcher(email);
	    boolean ismatcher = matcher.matches();
		return ismatcher;
	}
	
	/**
	 * 验证电话号码
	 * @param mobile
	 * @return
	 */
	public static boolean validateMobile(String mobile) {
		Pattern regex = Pattern.compile(mobilestr);
		Matcher matcher = regex.matcher(mobile);
		boolean ismatcher = matcher.matches();
		return ismatcher;
	}
	
	/**
	 * 处理字符串
	 * @param message
	 * @return
	 */
	public static String alertMsg(String message) {
        StringBuffer sb = new StringBuffer("{\"error\":1,\"message\":\"");
        sb.append(message).append("\"}");
        return sb.toString();
    }
	
	public static void main(String args[]) {
//		System.out.println("email "+StrUtils.validatEmail("1223265333@qq.com"));
		
		System.out.println(StrUtils.isDecimal("aa"));
		System.out.println(StrUtils.isDecimal("0."));
		System.out.println(StrUtils.isDecimal(".2"));
		System.out.println(StrUtils.isDecimal("1.2"));
		System.out.println(StrUtils.isDecimal("1"));
		System.out.println(StrUtils.isDecimal("1.5.2"));
		System.out.println(StrUtils.isDecimal("-1"));
	}
}
