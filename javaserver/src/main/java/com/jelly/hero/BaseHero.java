package com.jelly.hero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.bean.Role;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.constant.GameConstId;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseHero implements IHero {

	private static final Logger logger = LoggerFactory.getLogger(BaseHero.class);
	
	@JsonProperty("l")
	int level = 1;

	@JsonProperty("r")
	int roleId;

	// 品质等级
	@JsonProperty("q")
	int qlv = 0;
	
	// 星级
	@JsonProperty("t")
	int starLv = 0;

	// 查克拉类型（后期学习的）
	@JsonProperty("c")
	int chakraLearn;

	Role roleConfig;
	
	
	public int getStarLv() {
		return starLv;
	}

	public void setStarLv(int starLv) {
		this.starLv = starLv;
	}

	public int getQlv() {
		return qlv;
	}

	public void setQlv(int qlv) {
		this.qlv = qlv;
	}

	public BaseHero(int roleId) {
		this.roleId = roleId;
		setRoleConfig();
	}

	public BaseHero() {
	}

	protected void setRoleConfig() {
		if (this.roleConfig == null) {
			this.roleConfig = AllGameConfig.getInstance().characterManager.getRoleById(roleId);
		}
	}

	@Override
	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	@Override
	public int getStrength() {
		setRoleConfig();
		return propValueFormula(roleConfig.getAtt());
	}

	@Override
	public int getDefence() {
		setRoleConfig();
		return propValueFormula(roleConfig.getDef());
	}

	@Override
	public int getHpMax() {
		setRoleConfig();
		return propValueFormula(roleConfig.getHp());
	}

	@Override
	public int getAgility() {
		setRoleConfig();
		return propValueFormula(roleConfig.getAgi());
	}

	@Override
	public String getName() {
		setRoleConfig();
		return roleConfig.getAlt();
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	private int propValueFormula(Float ratio) {
		Float qlvInc = (Float) AllGameConfig.getInstance().gconst.getConstant(GameConstId.PER_LV_INC);
		float value = (1 + level * 0.1f) * ratio * (1 + qlvInc * (qlv+starLv));
		if (logger.isDebugEnabled()) {
//			logger.debug("role level {}",level);
//			logger.debug("role ratio {}",ratio);
//			logger.debug("role qlvInc {}",qlvInc);
//			logger.debug("role qlv {}",qlv);
		}
		return Math.round(value);
	}

	@Override
	public int getTop() {
		setRoleConfig();
		return roleConfig.getTop();
	}

	@Override
	public int getLeft() {
		setRoleConfig();
		return roleConfig.getLeft();
	}

	@Override
	public int getRight() {
		setRoleConfig();
		return roleConfig.getRight();
	}

	@Override
	public int getBottom() {
		setRoleConfig();
		return roleConfig.getBottom();
	}

	@Override
	public int getQuality() {
		setRoleConfig();
		return roleConfig.getQuality();
	}

	@Override
	public int[] getEskill() {
		setRoleConfig();
		return roleConfig.getEskill();
	}

	@Override
	public int getPower() {
		int agi = getAgility();
		int att = getStrength();
		int def = getDefence();
		int hp = getHpMax() / 10;
		int power = agi + att + def + hp;
//		if (logger.isDebugEnabled()) {
//			logger.info("role agi {}",agi);
//			logger.info("role att {}",att);
//			logger.info("role def {}",def);
//			logger.info("role hp {}",hp);
//			logger.info("role power {}",power);
//		}
		
		return power;
	}
	@Override
	public int getUuid() {
		return 0;
	}
	
	@Override
	public int getHp() {
		return getHpMax();
	}
	
	@Override
	public int[] getChakra() {
		setRoleConfig();
		return new int []{roleConfig.getChakra()[0], this.chakraLearn};
	}

	public void setChakraLearn(int chakraLearn) {
		this.chakraLearn = chakraLearn;
	}
	@Override
	public int getDefineID(){
		return 0;
	}
	@Override
	public void setDefineID(int id){
		
	}
	
	@Override
	public String getDefineGuid()
	{
		return "";
	}
	@Override
	public void setDefineGuid(String guid)
	{
	}

}
