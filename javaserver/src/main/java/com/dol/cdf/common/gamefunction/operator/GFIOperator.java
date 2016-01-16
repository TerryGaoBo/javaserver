package com.dol.cdf.common.gamefunction.operator;

import java.util.HashMap;
import java.util.Map;

public abstract class GFIOperator {
	
	
	abstract boolean eval(Object left, Object right);
	
	// 相等
	static GFIOperator equalOperator = new GFIOperator() {
		
		@Override
		public boolean eval(Object left, Object right) {
			Number leftNum = (Number)left;
			Number rightNum = (Number)right;
			return leftNum.floatValue() == rightNum.floatValue();
		}
	};
	
	// 不相等
	static GFIOperator notEqualOperator = new GFIOperator() {
		
		@Override
		public boolean eval(Object left, Object right) {
			Number leftNum = (Number)left;
			Number rightNum = (Number)right;
			return leftNum.floatValue() != rightNum.floatValue();
		}
	};
	
	// 大于
	static GFIOperator greaterThenOperator = new GFIOperator() {
		
		@Override
		public boolean eval(Object left, Object right) {
			Number leftNum = (Number)left;
			Number rightNum = (Number)right;
			return leftNum.floatValue() > rightNum.floatValue();
		}
	};
	
	// 小于
	static GFIOperator lessThenOperator = new GFIOperator() {
		
		@Override
		public boolean eval(Object left, Object right) {
			Number leftNum = (Number)left;
			Number rightNum = (Number)right;
			return leftNum.floatValue() < rightNum.floatValue();
		}
	};
	
	// 不大于
	static GFIOperator notGreaterThenOperator = new GFIOperator() {
		
		@Override
		public boolean eval(Object left, Object right) {
			Number leftNum = (Number)left;
			Number rightNum = (Number)right;
			return leftNum.floatValue() <= rightNum.floatValue();
		}
	};
	
	// 不小于
	static GFIOperator notLessThenOperator = new GFIOperator() {
		
		@Override
		public boolean eval(Object left, Object right) {
			Number leftNum = (Number)left;
			Number rightNum = (Number)right;
			return leftNum.floatValue() >= rightNum.floatValue();
		}
	};
	
	private static final Map<String,GFIOperator> OPERATOR_MAP = new HashMap<String,GFIOperator>();
	
	static{
		OPERATOR_MAP.put("=", equalOperator);
		OPERATOR_MAP.put("!=", notEqualOperator);
		OPERATOR_MAP.put(">",greaterThenOperator);
		OPERATOR_MAP.put("<", lessThenOperator);
		OPERATOR_MAP.put("<=", notGreaterThenOperator);
		OPERATOR_MAP.put(">=", notLessThenOperator);
	}
	
	
	public static GFIOperator createGfiOperator(String symbol){
		GFIOperator operator = OPERATOR_MAP.get(symbol);
		if(operator == null)
		{
			throw new RuntimeException("创建GFI符号对象出错：错误的符号" + symbol);
		}
		return operator;
	}
	
	
	
	
}
