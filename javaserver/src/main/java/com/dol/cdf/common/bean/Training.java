package com.dol.cdf.common.bean;

import com.dol.cdf.common.StringHelper;

public class Training {
	
	private Integer quality;//忍者品质
	private String atkReduce;//攻击降低数值
	private String atkRaise;//攻击提高数值
	private String defReduce;//防御降低数值
	private String defRaise;//防御提高数值
	private String hpReduce;//HP降低数值
	private String hpRaise;//HP提高数值
	private String dexReduce;//敏捷降低数值
	private String dexRaise;//敏捷提高数值
	private String atkChange;//攻击变化几率
	private String defChange;//防御变化几率
	private String hpChange;//HP变化几率
	private String dexChange;//敏捷变化几率
	private Integer atkLvUp;//攻击单级提升
	private Integer defLvUp;//防御单级提升
	private Integer hpLvUp;//HP单级提升
	private Integer dexLvUp;//敏捷单级提升
	private Integer trainLv;//培养成长级别
	private Integer trainItem;//培养道具数量
	private Integer trainSilver;//银币消耗
	public Integer getQuality() {
		return quality;
	}
	public void setQuality(Integer quality) {
		this.quality = quality;
	}
	public String getAtkReduce() {
		return atkReduce;
	}
	public void setAtkReduce(String atkReduce) {
		this.atkReduce = atkReduce;
	}
	public String getAtkRaise() {
		return atkRaise;
	}
	public void setAtkRaise(String atkRaise) {
		this.atkRaise = atkRaise;
	}
	public String getDefReduce() {
		return defReduce;
	}
	public void setDefReduce(String defReduce) {
		this.defReduce = defReduce;
	}
	public String getDefRaise() {
		return defRaise;
	}
	public void setDefRaise(String defRaise) {
		this.defRaise = defRaise;
	}
	public String getHpReduce() {
		return hpReduce;
	}
	public void setHpReduce(String hpReduce) {
		this.hpReduce = hpReduce;
	}
	public String getHpRaise() {
		return hpRaise;
	}
	public void setHpRaise(String hpRaise) {
		this.hpRaise = hpRaise;
	}
	public String getDexReduce() {
		return dexReduce;
	}
	public void setDexReduce(String dexReduce) {
		this.dexReduce = dexReduce;
	}
	public String getDexRaise() {
		return dexRaise;
	}
	public void setDexRaise(String dexRaise) {
		this.dexRaise = dexRaise;
	}
	public String getAtkChange() {
		return atkChange;
	}
	public void setAtkChange(String atkChange) {
		this.atkChange = atkChange;
	}
	public String getDefChange() {
		return defChange;
	}
	public void setDefChange(String defChange) {
		this.defChange = defChange;
	}
	public String getHpChange() {
		return hpChange;
	}
	public void setHpChange(String hpChange) {
		this.hpChange = hpChange;
	}
	public String getDexChange() {
		return dexChange;
	}
	public void setDexChange(String dexChange) {
		this.dexChange = dexChange;
	}
	public Integer getAtkLvUp() {
		return atkLvUp;
	}
	public void setAtkLvUp(Integer atkLvUp) {
		this.atkLvUp = atkLvUp;
	}
	public Integer getDefLvUp() {
		return defLvUp;
	}
	public void setDefLvUp(Integer defLvUp) {
		this.defLvUp = defLvUp;
	}
	public Integer getHpLvUp() {
		return hpLvUp;
	}
	public void setHpLvUp(Integer hpLvUp) {
		this.hpLvUp = hpLvUp;
	}
	public Integer getDexLvUp() {
		return dexLvUp;
	}
	public void setDexLvUp(Integer dexLvUp) {
		this.dexLvUp = dexLvUp;
	}
	public Integer getTrainLv() {
		return trainLv;
	}
	public void setTrainLv(Integer trainLv) {
		this.trainLv = trainLv;
	}
	public Integer getTrainItem() {
		return trainItem;
	}
	public void setTrainItem(Integer trainItem) {
		this.trainItem = trainItem;
	}
	public Integer getTrainSilver() {
		return trainSilver;
	}
	public void setTrainSilver(Integer trainSilver) {
		this.trainSilver = trainSilver;
	}
	
	
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
	
	
	
}
