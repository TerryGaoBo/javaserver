package com.jelly.player;

/**
 * 英雄的评级
 * @author zhoulei
 *
 */
public enum GradeType {
	D(1),
	C(2),
	B(3),
	A(4),
	S(5),
	SS(6),
	SSS(7);
	public static GradeType parse(int idx) {
		for (GradeType e : GradeType.values()) {
			if (e.ordinal() == idx)
				return e;
		}
		return null;
	}
	
	int id;

	private GradeType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	
}
