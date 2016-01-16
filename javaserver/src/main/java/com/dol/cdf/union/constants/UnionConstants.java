package com.dol.cdf.union.constants;

public interface UnionConstants {
	/** 请求入帮的最大记录数*/
	public final static int MAX_REQUEST_JOIN_NUM=100;
	
	public enum MemberStatus{
		JOINED,NOT_REQUEST,REQUESTED,
		/** 其他帮会成员*/
		OTHER;
	}
}
