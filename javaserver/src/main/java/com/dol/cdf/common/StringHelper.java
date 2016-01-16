package com.dol.cdf.common;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

import com.dol.cdf.common.collect.Pair;

public final class StringHelper {

	private final static Logger logger = Logger.getLogger(StringHelper.class);

	public static final String ANY = "any";

	public static final String EMPTY = "";

	public static final String COMMA = ",";

	public static int[] getIntList(String csv) {
		String[] strs = csv.split(COMMA);
		return getIntList(strs);
	}

	public static int[] getIntList(String[] strs) {
		int[] rets = new int[strs.length];
		for (int i = 0; i < strs.length; i++) {
			try {
				rets[i] = Integer.parseInt(strs[i]);
			} catch (Exception e) {
				if (!"".equals(strs[i])) {
					logger.error("", e);
				}
				rets[i] = 0;
			}

		}
		return rets;
	}

	public static float[] getFloatList(String[] strs) {
		float[] rets = new float[strs.length];
		for (int i = 0; i < strs.length; i++) {
			try {
				rets[i] = Float.parseFloat(strs[i]);
			} catch (Exception e) {
				logger.error("", e);
				rets[i] = 0;
			}

		}
		return rets;
	}

	public static String getGenericName(Class<?> clazz) {
		Type type = null;
		if (clazz.getGenericInterfaces().length == 0) {
			type = clazz.getGenericSuperclass();
		} else {
			type = clazz.getGenericInterfaces()[0];
		}
		String genericFullName = substringBetween(type.toString(), "<", ">");
		try {
			Class<?> forName = Class.forName(genericFullName);
			return forName.getSimpleName();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String substringBetween(String str, String open, String close) {
		if (str == null || open == null || close == null) {
			return null;
		}
		int start = str.indexOf(open);
		if (start != -1) {
			int end = str.indexOf(close, start + open.length());
			if (end != -1) {
				return str.substring(start + open.length(), end);
			}
		}
		return null;
	}

	public static int asciiLength(String str) {
		if (str == null || str.length() == 0) {
			return 0;
		}
		int len = 0;
		char[] chars = str.toCharArray();
		for (char ch : chars) {
			if (ch < 128) {
				len += 1;
			} else {
				len += 2;
			}
		}
		return len;
	}

	public static boolean containsNone(String str, String invalidChars) {
		if (str == null || invalidChars == null) {
			return true;
		}
		return containsNone(str, invalidChars.toCharArray());
	}

	public static boolean contains(String[] strs, String str) {
		if (strs == null || str == null) {
			return false;
		}
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].equals(str)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsNone(String str, char[] invalidChars) {
		if (str == null || invalidChars == null) {
			return true;
		}
		int strSize = str.length();
		int validSize = invalidChars.length;
		for (int i = 0; i < strSize; i++) {
			char ch = str.charAt(i);
			for (int j = 0; j < validSize; j++) {
				if (invalidChars[j] == ch) {
					return false;
				}
			}
		}
		return true;
	}

	public static String describeMap(Map<Object, Object> map) {
		StringBuilder sb = new StringBuilder();

		sb.append("[");
		boolean first = true;
		for (Object key : map.keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}

			sb.append(key);
			sb.append("==");
			sb.append(map.get(key));
		}
		sb.append("]");

		return sb.toString();
	}

	public static String formatString(String str, Object... params) {
		try {
			return MessageFormat.format(str, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 用来拆分具有<..><..><..>形式的字符串正则表达式
	 */
	public static final String GROUP_SPLITER = "(?<=>)(?=<)";
	/**
	 * 用来拆分具有v1|v2|v3形式的字符串表达式
	 */
	public static final String ELEMENT_SPLITER = "\\|";

	public static List<Pair<String, Integer>> toStrIntTupleList(String str) {
		List<Pair<String, Integer>> tuples = new ArrayList<Pair<String, Integer>>();
		if (!str.isEmpty()) {
			for (String pair : str.split(GROUP_SPLITER)) {
				tuples.add(toStrIntTuple(pair));
			}
		}
		return tuples;
	}

	/**
	 * 将<key|value>形式的字符串转为Pair<String,Integer>
	 * 
	 * @param pair
	 * @return
	 */
	public static Pair<String, Integer> toStrIntTuple(String str) {
		if (str.isEmpty()) {
			return null;
		}
		String[] strs = StringHelper.substringBetween(str, "<", ">").split(ELEMENT_SPLITER);
		String key = strs[0];
		int value = Integer.parseInt(strs[1]);
		return new Pair<String, Integer>(key, value);

	}

	public static int getValueByKey(String str, String key) {
		List<Pair<String, Integer>> list = toStrIntTupleList(str);
		for (Pair<String, Integer> p : list) {
			if (p.getFirst().equals(key)) {
				return p.getSecond();
			}
		}
		return 0;
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing
	 * the provided list of elements.
	 * </p>
	 * 
	 * <p>
	 * No delimiter is added before or after the list. A <code>null</code>
	 * separator is the same as an empty String (""). Null objects or empty
	 * strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *)                = null
	 * StringUtils.join([], *)                  = ""
	 * StringUtils.join([null], *)              = ""
	 * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
	 * StringUtils.join(["a", "b", "c"], null)  = "abc"
	 * StringUtils.join(["a", "b", "c"], "")    = "abc"
	 * StringUtils.join([null, "", "a"], ',')   = ",,a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @return the joined String, <code>null</code> if null array input
	 */
	public static String join(Object[] array, String separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing
	 * the provided list of elements.
	 * </p>
	 * 
	 * <p>
	 * No delimiter is added before or after the list. A <code>null</code>
	 * separator is the same as an empty String (""). Null objects or empty
	 * strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *)                = null
	 * StringUtils.join([], *)                  = ""
	 * StringUtils.join([null], *)              = ""
	 * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
	 * StringUtils.join(["a", "b", "c"], null)  = "abc"
	 * StringUtils.join(["a", "b", "c"], "")    = "abc"
	 * StringUtils.join([null, "", "a"], ',')   = ",,a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass
	 *            in an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to
	 *            pass in an end index past the end of the array
	 * @return the joined String, <code>null</code> if null array input
	 */
	public static String join(Object[] array, String separator, int startIndex, int endIndex) {
		if (array == null) {
			return null;
		}
		if (separator == null) {
			separator = EMPTY;
		}

		// endIndex - startIndex > 0: Len = NofStrings *(len(firstString) +
		// len(separator))
		// (Assuming that all Strings are roughly equally long)
		int bufSize = (endIndex - startIndex);
		if (bufSize <= 0) {
			return EMPTY;
		}

		bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + separator.length());

		StringBuffer buf = new StringBuffer(bufSize);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	static String[] a(String... strings) {
		return strings;
	}

	public static String obj2String(Object obj, Map<String, Boolean> excludes) {
		BaseReflectionToStringBuilder builder = new BaseReflectionToStringBuilder(obj, ToStringStyle.SHORT_PREFIX_STYLE, excludes);
		return builder.toString();
	}

	private static class BaseReflectionToStringBuilder extends ReflectionToStringBuilder {
		private final Map<String, Boolean> exclues;

		public BaseReflectionToStringBuilder(Object object, ToStringStyle style, Map<String, Boolean> exclues) {
			super(object);
			this.exclues = exclues;
		}

		@Override
		protected boolean accept(Field field) {
			boolean accepted = true;
			if (this.exclues != null) {
				accepted = this.exclues.get(field.getName()) == null;
			}
			return super.accept(field) && accepted;
		}
	}

	public static void main(String[] args) {

		System.out.println(join(a(), "|"));
	}
}
