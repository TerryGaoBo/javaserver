package com.dol.cdf.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.Text;
import com.fasterxml.jackson.core.type.TypeReference;

public class TextConfigManager extends BaseConfigLoadManager {

	private static final String JSON_FILE_FORM = "Text.json";

	private Map<Integer, String> textMap;

	public String getText(int id) {
		String text = textMap.get(id);
		return text == null ? "" : text;
	}

	@Override
	public void loadConfig() {
		textMap = new HashMap<Integer, String>();
		List<Text> list = readConfigFile(JSON_FILE_FORM, new TypeReference<List<Text>>() {
		});
		for (Text text : list) {
			textMap.put(text.getId(), text.getText());
		}
	}

}
