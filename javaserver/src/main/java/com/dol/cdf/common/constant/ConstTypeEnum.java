package com.dol.cdf.common.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemRateEntry;
import com.dol.cdf.common.collect.Range;

public enum ConstTypeEnum {
	INT {
		@Override
		public Integer getValue(String value) {
			return Integer.parseInt(value);
		}
	},
	FLOAT {
		@Override
		public Float getValue(String value) {
			return Float.parseFloat(value);
		}
	},
	STRING {
		@Override
		public String getValue(String value) {
			return value;
		}
	},
	// 例如1-7
	RANGE_INT {
		@Override
		public Range<Integer> getValue(String value) {
			return parseRangeInt(value);
		}
	},
	// 例如a-z
	RANGE_STRING {
		@Override
		public Range<String> getValue(String value) {
			return parseRangeStr(value);
		}
	},
	// 例如1|7
	INT_LIST {
		@Override
		public int[] getValue(String value) {
			String[] strs = value.split(ELEMENT_SPLITER);
			return StringHelper.getIntList(strs);
		}
	},
	// 例如1|7
	FLOAT_LIST {
		@Override
		public float[] getValue(String value) {
			String[] strs = value.split(ELEMENT_SPLITER);
			return StringHelper.getFloatList(strs);
		}
	},
	// 例如 aa|bb|cc
	STRING_LIST {
		@Override
		public String[] getValue(String value) {
			return value.split(ELEMENT_SPLITER);
		}
	},
	// 例如1-7|8-10
	RANGE_INT_LIST {
		@Override
		public List<Range<Integer>> getValue(String value) {
			List<Range<Integer>> rangeList = new ArrayList<Range<Integer>>();
			String[] strs = value.split(ELEMENT_SPLITER);
			for (String string : strs) {
				Range<Integer> range = parseRangeInt(string);
				rangeList.add(range);
			}
			return rangeList;
		}
	},
	// 例如a-d|e-h
	RANGE_STRING_LIST {
		@Override
		public List<Range<String>> getValue(String value) {
			List<Range<String>> rangeList = new ArrayList<Range<String>>();
			String[] strs = value.split(ELEMENT_SPLITER);
			for (String string : strs) {
				String[] rangeStrs = string.split(RANGE_ELEMENT_SPLITER);
				Range<String> range = new Range<String>(rangeStrs[0], rangeStrs[1]);
				rangeList.add(range);
			}
			return rangeList;
		}
	},
	// 例如1=100|2=200
	INT_INT_MAP {
		@Override
		public Map<Integer, Integer> getValue(String value) {
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			String[] strs = value.split(ELEMENT_SPLITER);
			for (String string : strs) {
				String[] mapStrs = string.split(MAP_ELEMENT_SPLITER);
				Integer key = (Integer) INT.getValue(mapStrs[0]);
				int val = (Integer) INT.getValue(mapStrs[1]);
				map.put(key, val);
			}
			return map;
		}
	},
	// 例如a=100|b=200
	STRING_INT_MAP {
		@Override
		public Map<String, Integer> getValue(String value) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			String[] strs = value.split(ELEMENT_SPLITER);
			for (String string : strs) {
				String[] mapStrs = string.split(MAP_ELEMENT_SPLITER);
				String key = mapStrs[0];
				int val = (Integer) INT.getValue(mapStrs[1]);
				map.put(key, val);
			}
			return map;
		}
	},

	// 例如1-7=100|8-10=200
	RANGE_INT_MAP {
		@Override
		public Map<Range<Integer>, Integer> getValue(String value) {
			Map<Range<Integer>, Integer> rangeMap = new HashMap<Range<Integer>, Integer>();
			String[] strs = value.split(ELEMENT_SPLITER);
			for (String string : strs) {
				String[] mapStrs = string.split(MAP_ELEMENT_SPLITER);
				Range<Integer> range = parseRangeInt(mapStrs[0]);
				int val = (Integer) INT.getValue(mapStrs[1]);
				rangeMap.put(range, val);
			}
			return rangeMap;
		}
	},

	// 例如<gold|50><ore|20>
	VIENTRY_LIST {
		@Override
		public VariousItemEntry[] getValue(String value) {
			String[] stringArray = paireValueToStringArray(value);
			VariousItemEntry[] itemArray = new VariousItemEntry[stringArray.length];
			for (int i = 0; i < stringArray.length; i++) {
				String[] realItem = stringArray[i].split(ELEMENT_SPLITER);
				String type = realItem[0];
				int count = Integer.parseInt(realItem[1]);
				itemArray[i] = new VariousItemEntry(type, count);
			}
			return itemArray;
		}

	},
	// 例如<gold|50|1000><ore|20|10000>
	VIENTRY_RATE_LIST {
		@Override
		public VariousItemRateEntry[] getValue(String value) {
			String[] stringArray = paireValueToStringArray(value);
			VariousItemRateEntry[] itemArray = new VariousItemRateEntry[stringArray.length];
			for (int i = 0; i < stringArray.length; i++) {
				String[] realItem = stringArray[i].split(ELEMENT_SPLITER);
				String type = realItem[0];
				int count = Integer.parseInt(realItem[1]);
				int rate = Integer.parseInt(realItem[2]);
				itemArray[i] = new VariousItemRateEntry(type, count, rate);
			}
			return itemArray;
		}

	},
	// 例如<1|2|3><4|5|6>
	INT_INT_LIST {
		@Override
		public List[] getValue(String value) {
			String[] stringArray = value.split(ELEMENT_SPLITER);
			List[] list = new ArrayList[stringArray.length];
			for (int i = 0; i < stringArray.length; i++) {
				int[] realItem = StringHelper.getIntList(stringArray[i].split(";"));
				ArrayList l = new ArrayList();
				for (int num : realItem) {
					l.add(num);
				}
				list[i] = l;
			}
			return list;
		}

	},

	;

	private static String[] paireValueToStringArray(String value) {
		value = value.substring(1, value.length() - 1);
		String[] split = value.split("><");
		return split;
	}

	private static Range<Integer> parseRangeInt(String value) {
		String[] strs = value.split(RANGE_ELEMENT_SPLITER);
		return new Range<Integer>((Integer) INT.getValue(strs[0]), (Integer) INT.getValue(strs[1]));
	}

	private static Range<String> parseRangeStr(String value) {
		String[] strs = value.split(RANGE_ELEMENT_SPLITER);
		return new Range<String>((String) STRING.getValue(strs[0]), (String) STRING.getValue(strs[1]));
	}

	private static final String ELEMENT_SPLITER = "\\|";

	private static final String RANGE_ELEMENT_SPLITER = "-";

	private static final String MAP_ELEMENT_SPLITER = "=";

	abstract public Object getValue(String value);
}