package com.dol.cdf.union.model;

import com.dol.cdf.common.TimeUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UnionJoinRequestInfo {
	public UnionJoinRequestInfo(){}
	public UnionJoinRequestInfo(String guid,long power){
		this.guid=guid;
		this.request_time=TimeUtil.getCurrentTime();
		this.request_status=0;
		this.power=power;
	}
	
	/** 申请人*/
	@JsonProperty("i")
	private String guid;
	
	/**申请时间 */
	@JsonProperty("r")
	private int request_time;
	
	/** 申请信息状态   （0:未处理，1：已同意，2：已拒绝）*/
	@JsonProperty("s")
	private int request_status;
	
	/** 申请处理时间*/
	@JsonProperty("t")
	private int request_resolve_time;
	
	@JsonProperty("p")
	private long power;
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public int getRequest_time() {
		return request_time;
	}

	public void setRequest_time(int request_time) {
		this.request_time = request_time;
	}

	public int getRequest_status() {
		return request_status;
	}

	public void setRequest_status(int request_status) {
		this.request_status = request_status;
	}

	public int getRequest_resolve_time() {
		return request_resolve_time;
	}

	public void setRequest_resolve_time(int request_resolve_time) {
		this.request_resolve_time = request_resolve_time;
	}
	
	public long getPower() {
		return power;
	}
	public void setPower(long power) {
		this.power = power;
	}

	
}
