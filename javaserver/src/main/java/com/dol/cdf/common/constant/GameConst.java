package com.dol.cdf.common.constant;


public class GameConst {
	
	private int id;
	
	private ConstTypeEnum type;
	
	private Object value;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ConstTypeEnum getType() {
		return type;
	}

	public void setType(ConstTypeEnum type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(String value) {
		if(type != null){
			try {
				this.value = type.getValue(value);
			} catch (Exception e) {
				throw new RuntimeException("转换GameConst的value错误，id=" + id + " value=" + value + "type=" + type.name());
			}
			
		}else{
			throw new RuntimeException("设置GameConst的value失败，没有定义type，value=" + value);
		}
	}



	
}
