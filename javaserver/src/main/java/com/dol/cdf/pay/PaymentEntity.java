package com.dol.cdf.pay;

public class PaymentEntity {
	public String qid; // 平台用户的标示
	public String guid; //角色id
	public String nickname; // 角色名
	public String sid; // 服务器id
	public String oid; // 订单id
	public int amount; // 骨币数量
	public long finishTime; //完成时间
	public String rmb;		//人民币数量
	public String params;	//其他参数
	
	public PaymentEntity() {
		super();
	}
	
	public PaymentEntity(String qid, String guid, String sid, String oid, String rmb, int amount, long finishTime, String nickname, String params) {
		super();
		this.qid = qid;
		this.guid = guid;
		this.nickname = nickname;
		this.sid = sid;
		this.oid = oid;
		this.amount = amount;
		this.finishTime = finishTime;
		this.rmb = rmb;
		this.params = params;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public String getRmb() {
		return rmb;
	}

	public void setRmb(String rmb) {
		this.rmb = rmb;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String toString() {
		return "qid=" + qid + " sid=" + sid + " oid=" + oid + " amount=" + amount 
				+ " finishTime=" + finishTime + " rmb=" + rmb + " params=" + params + " nickname=" + nickname;
	}
}
