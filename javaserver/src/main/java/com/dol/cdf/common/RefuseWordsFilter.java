package com.dol.cdf.common;

import java.io.InputStream;

/**
 * 违禁字过滤器
 **/
public class RefuseWordsFilter extends KeyWordsFilter {
	
	private static RefuseWordsFilter rwf = null;
	
	private static final String FILTER_WORD_FILE_NAME = "nadron/props/refuseWords.txt";
	
	private KeyWordsFilter filter;
	
	private RefuseWordsFilter(String[] keyWords, char[] ignores, char substitute) {
		super(keyWords, ignores, substitute);
	}
	
	public static RefuseWordsFilter getInstance() {
		if (rwf == null) {
			try {
				InputStream in = ClassLoader.getSystemResourceAsStream(FILTER_WORD_FILE_NAME);
				int size = in.available();
				byte[] bytes = new byte[size];
				in.read(bytes);
				String s = new String(bytes, "utf8");
				String[] keys = s.split("\r\n");
				char[] ignores = { ' ', '\t', '-', ',', '.', '，', '。', '*' };
				rwf = new RefuseWordsFilter(keys, ignores, '*');	
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return rwf;
	}
}
