/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Adventure {
	/**id*/
	private Integer id;
	/**type*/
	private Integer type;
	/**chapter*/
	private Integer chapter;
	/**stage*/
	private Integer stage;
	/**preStage*/
	private Integer preStage;
	/**commonStage*/
	private Integer commonStage;
	/**mainThread*/
	private Integer mainThread;
	/**stageLabel*/
	private Integer stageLabel;
	/**energy*/
	private Integer energy;
	/**bg*/
	private String bg;
	/**desc*/
	private String desc;
	/**enemy*/
	private int[] enemy;
	/**level*/
	private int[] level;
	/**village*/
	private String village;
	/**beforeBattle*/
	private String beforeBattle;
	/**afterBattle*/
	private String afterBattle;
	/**exp*/
	private Integer exp;
	/**rxp*/
	private Integer rxp;
	/**silver*/
	private Integer silver;
	/**firstItem*/
	private VariousItemEntry[] firstItem;
	/**itemGroup*/
	private Integer itemGroup;
	/**guildEXP*/
	private Integer guildEXP;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getType(){
		 return this.type;
	}
	public  void setType(Integer type){
		this.type = type ;
	}
	public Integer getChapter(){
		 return this.chapter;
	}
	public  void setChapter(Integer chapter){
		this.chapter = chapter ;
	}
	public Integer getStage(){
		 return this.stage;
	}
	public  void setStage(Integer stage){
		this.stage = stage ;
	}
	public Integer getPreStage(){
		 return this.preStage;
	}
	public  void setPreStage(Integer preStage){
		this.preStage = preStage ;
	}
	public Integer getCommonStage(){
		 return this.commonStage;
	}
	public  void setCommonStage(Integer commonStage){
		this.commonStage = commonStage ;
	}
	public Integer getMainThread(){
		 return this.mainThread;
	}
	public  void setMainThread(Integer mainThread){
		this.mainThread = mainThread ;
	}
	public Integer getStageLabel(){
		 return this.stageLabel;
	}
	public  void setStageLabel(Integer stageLabel){
		this.stageLabel = stageLabel ;
	}
	public Integer getEnergy(){
		 return this.energy;
	}
	public  void setEnergy(Integer energy){
		this.energy = energy ;
	}
	public String getBg(){
		 return this.bg;
	}
	public  void setBg(String bg){
		this.bg = bg ;
	}
	public String getDesc(){
		 return this.desc;
	}
	public  void setDesc(String desc){
		this.desc = desc ;
	}
	public int[] getEnemy(){
		 return this.enemy;
	}
	public  void setEnemy(int[] enemy){
		this.enemy = enemy ;
	}
	public int[] getLevel(){
		 return this.level;
	}
	public  void setLevel(int[] level){
		this.level = level ;
	}
	public String getVillage(){
		 return this.village;
	}
	public  void setVillage(String village){
		this.village = village ;
	}
	public String getBeforeBattle(){
		 return this.beforeBattle;
	}
	public  void setBeforeBattle(String beforeBattle){
		this.beforeBattle = beforeBattle ;
	}
	public String getAfterBattle(){
		 return this.afterBattle;
	}
	public  void setAfterBattle(String afterBattle){
		this.afterBattle = afterBattle ;
	}
	public Integer getExp(){
		 return this.exp;
	}
	public  void setExp(Integer exp){
		this.exp = exp ;
	}
	public Integer getRxp(){
		 return this.rxp;
	}
	public  void setRxp(Integer rxp){
		this.rxp = rxp ;
	}
	public Integer getSilver(){
		 return this.silver;
	}
	public  void setSilver(Integer silver){
		this.silver = silver ;
	}
	public VariousItemEntry[] getFirstItem(){
		 return this.firstItem;
	}
	public  void setFirstItem(VariousItemEntry[] firstItem){
		this.firstItem = firstItem ;
	}
	public Integer getItemGroup(){
		 return this.itemGroup;
	}
	public  void setItemGroup(Integer itemGroup){
		this.itemGroup = itemGroup ;
	}
	public Integer getGuildEXP(){
		 return this.guildEXP;
	}
	public  void setGuildEXP(Integer guildEXP){
		this.guildEXP = guildEXP ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
