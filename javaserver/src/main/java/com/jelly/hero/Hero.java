package com.jelly.hero;

import io.nadron.app.Player;
import io.nadron.app.impl.OperResultType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.bean.Accessory;
import com.dol.cdf.common.bean.Level;
import com.dol.cdf.common.bean.QualityRef;
import com.dol.cdf.common.bean.Skill;
import com.dol.cdf.common.bean.Training;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.ItemConstant;
import com.dol.cdf.common.config.SkillConfigManager;
import com.dol.cdf.log.LogConst;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.player.ItemInstance;

public class Hero extends BaseHero implements Cloneable {

	private static final Logger logger = LoggerFactory.getLogger(Hero.class);

	public static final short NINJITSU_COUNT = 5;//old 3
	public static final short PSYCHIC_COUNT = 1;
//	public static final short BLOOD_COUNT = 2;
	public static final short BEAST_COUNT = 1;

	public static final short SKILL_TYPE_NINJITSU = 0;
	public static final short SKILL_TYPE_PSYCHIC = 1;
//	public static final short SKILL_TYPE_BLOOD = 2;
	public static final short SKILL_TYPE_BEAST = 3;

	@JsonProperty("x")
	int exp = 0;

	// 星级品质点
	@JsonProperty("o")
	int starPoint = 0;

	@JsonProperty("e")
	List<ItemInstance> equip;

	// 忍术
	@JsonProperty("n")
	List<Integer> ninjitsu;
	// 通灵
	@JsonProperty("p")
	List<Integer> psychic;
	// 血继
	@JsonProperty("b")
	List<Integer> blood;
	// 尾兽
	@JsonProperty("s")
	List<Integer> beast;
	// 属性
	@JsonProperty("props")
	List<Integer> props;
	//训练临时属性
	@JsonProperty("tmptrainprops")
	List<Integer> tmptrainprops;
	//技能星数，key （type - idx） value，星数
	@JsonProperty("k")
	Map<String, Integer> skillStars = Maps.newHashMap();
	
	enum PropType {
		ATK,
		DEF,
		HP,
		AGILE
	}
	
	
	boolean isLevelUp = false;
	
	public static final int starLength = AllGameConfig.getInstance().qref.getQualityStarUpLength();//忍者星数长度
	public static final int spointLength = AllGameConfig.getInstance().qref.getSpointUpLength();//技能星数长度
	
	
	// 忍者状态列表
	@JsonProperty("hstates")
	private List<Integer> hstates; 
	
	public Hero() {		
		props = Lists.newArrayList();
		tmptrainprops = Lists.newArrayList();
		hstates = Lists.newArrayList();
	}

	public Hero(int roleId) {
		super(roleId);
		equip = Lists.newArrayList(null, null, null, null);
		ninjitsu = Lists.newArrayList();
		psychic = Lists.newArrayList();
		//blood = Lists.newArrayList();
		beast = Lists.newArrayList();
		props = Lists.newArrayList();
		tmptrainprops = Lists.newArrayList();
		hstates = Lists.newArrayList();
		for (int sid : roleConfig.getSkill()) {
			// logger.info("skill Id {}", sid);
			Skill sconfig = config().getSkill(sid);
			List<Integer> skills = getSkillBytype(sconfig.getType());
			skills.add(sid);
		}
		for (int i = ninjitsu.size(); i < NINJITSU_COUNT; i++) {
			ninjitsu.add(null);
		}
		for (int i = psychic.size(); i < PSYCHIC_COUNT; i++) {
			psychic.add(null);
		}
//		for (int i = blood.size(); i < BLOOD_COUNT; i++) {
//			blood.add(null);
//		}
		for (int i = beast.size(); i < BEAST_COUNT; i++) {
			beast.add(null);
		}
	}
	
	/**
	 * 训练属性
	 **/
	public int train(Player player, int idx) {
		ItemInstance item = player.getInvenotry().getItemInstance(ItemConstant.CON_ITEM, idx);
		if (item == null) {
			logger.error("道具不存在");
			return 0;
		}
		if(item.getStackCount() <= 0) {
			List<ItemInstance> container = player.getInvenotry().getContainerById(ItemConstant.CON_ITEM);
			container.set(idx, null);
			player.getInvenotry().addChange(null, ItemConstant.CON_ITEM, idx, false);
			return 0;
		}
		if (props.isEmpty()) {
			for (int i = 0; i < PropType.values().length; ++i) {
				props.add(0);
			}
		}
		if (tmptrainprops.isEmpty()) {
			for (int i = 0; i < PropType.values().length; ++i) {
				tmptrainprops.add(0);
			}
		}
		//忍者训练配置文件
		int quality = AllGameConfig.getInstance().characterManager.getRoleById(roleId).getQuality();
		Training training = AllGameConfig.getInstance().characterManager.getTraining(quality);
		
		//判断道具类型 （1忍者心得  2无攻秘籍  3无防秘籍   4无生秘籍  5无敏秘籍）
		int addtype = -1;//属性增加类型（0攻击  1防御  2血量 3敏捷）
		int redtype = -1;//属性减少类型（0攻击  1防御  2血量 3敏捷）
		int add = 0;
		int red = 0;
		
		switch (item.getItemId()) {//判断减少属性
		case 5995:
			int index = getRedProp(training);
			redtype = index;
			break;
		case 5996:
			redtype = 0;
			break;
		case 5997:
			redtype = 1;
			break;
		case 5998:
			redtype = 2;
			break;
		case 5999:
			redtype = 3;
			break;
		default:
			break;
		}
		switch (redtype) {
		case 0:
			red = Rnd.get(Integer.parseInt(training.getAtkReduce().split("\\|")[0]), Integer.parseInt(training.getAtkReduce().split("\\|")[1]));
			int roleAtt = super.getStrength()+props.get(0);
			if(roleAtt<=1)
			{
				player.sendResult(OperResultType.TRAIN,MessageCode.HEROTRAIN_PROPENPUGH);
				return 0;
			}else{
				red = roleAtt-red>1 ? red:roleAtt-1;
			}
			break;
		case 1:
			red = Rnd.get(Integer.parseInt(training.getDefReduce().split("\\|")[0]), Integer.parseInt(training.getDefReduce().split("\\|")[1]));
			int roleDef = super.getDefence()+props.get(1);
			if(roleDef<=1)
			{
				player.sendResult(OperResultType.TRAIN,MessageCode.HEROTRAIN_PROPENPUGH);
				return 0;
			}else{
				red = roleDef-red>=1 ? red:roleDef-1;
			}
			break;
		case 2:
			red = Rnd.get(Integer.parseInt(training.getHpReduce().split("\\|")[0]), Integer.parseInt(training.getHpReduce().split("\\|")[1]));
			int roleHp = super.getHpMax()+props.get(2);
			if(roleHp<=1)
			{
				player.sendResult(OperResultType.TRAIN,MessageCode.HEROTRAIN_PROPENPUGH);
				return 0;
			}else{
				red = roleHp-red>1 ? red:roleHp-1;
			}
			break;
		case 3:
			red = Rnd.get(Integer.parseInt(training.getDexReduce().split("\\|")[0]), Integer.parseInt(training.getDexReduce().split("\\|")[1]));
			int roleDex = super.getAgility()+props.get(3);
			if(roleDex<=1){
				player.sendResult(OperResultType.TRAIN,MessageCode.HEROTRAIN_PROPENPUGH);
				return 0;
			}else{
				red = roleDex-red>1 ? red:roleDex-1;
			}
			break;
		default:
			break;
		}
		
		String addprops[] = getAddProp(training, redtype).split("_");
		addtype = Integer.parseInt(addprops[0]);
		int addmaxnum = Integer.parseInt(addprops[1]);
		if(addmaxnum==0){
			player.sendResult(OperResultType.TRAIN,MessageCode.HEROTRAIN_OTHERPROPISFULL);
			return 0;
		}
		switch (addtype) {
		case 0:
			add = Rnd.get(Integer.parseInt(training.getAtkRaise().split("\\|")[0]), Integer.parseInt(training.getAtkRaise().split("\\|")[1]));
			break;
		case 1:
			add = Rnd.get(Integer.parseInt(training.getDefRaise().split("\\|")[0]), Integer.parseInt(training.getDefRaise().split("\\|")[1]));
			break;
		case 2:
			add = Rnd.get(Integer.parseInt(training.getHpRaise().split("\\|")[0]), Integer.parseInt(training.getHpRaise().split("\\|")[1]));
			break;
		case 3:
			add = Rnd.get(Integer.parseInt(training.getDexRaise().split("\\|")[0]), Integer.parseInt(training.getDexRaise().split("\\|")[1]));
			break;
		default:
			break;
		}
		if(add>=addmaxnum)
		{
			add = addmaxnum;
		}
		VariousItemEntry tNeedItem = new VariousItemEntry(item.getItemId(), training.getTrainItem());
		int code = VariousItemUtil.doBonus(player, tNeedItem, LogConst.TRAINING_HERO, false);
		if (code != MessageCode.OK) {
			logger.error("训练所需道具不足");
			return 0;
		}
		VariousItemEntry tNeedSilver = new VariousItemEntry("silver", training.getTrainSilver());
		int code1 = VariousItemUtil.doBonus(player, tNeedSilver, LogConst.TRAINING_HERO, false);
		if (code1 != MessageCode.OK) {
			player.sendMiddleMessage(MessageCode.SILVER_NOT_ENUGH);
			return 0;
		}
		
		tmptrainprops.set(redtype, -red);
		tmptrainprops.set(addtype, add);
		
		player.sendResult(OperResultType.TRAIN);
		return 1;
		
	}
	/**
	 * 
	 */
	public int getRedProp(Training training){
		String []atkChange = training.getAtkChange().split("\\|");
		String []defChange = training.getDefChange().split("\\|");
		String []hpChange = training.getHpChange().split("\\|");
		String []dexChange = training.getDexChange().split("\\|");
		//判断增加属性
		List<Integer> qualityRates = Lists.newArrayList();
		qualityRates.add(Integer.parseInt(atkChange[0]));
		qualityRates.add(Integer.parseInt(defChange[0]));
		qualityRates.add(Integer.parseInt(hpChange[0]));
		qualityRates.add(Integer.parseInt(dexChange[0]));
		boolean sign = true;
		int index = 0;
		while(sign){
			index = Rnd.getIndex(qualityRates);
			switch (index) {
			case 0:
				int roleAtt = super.getStrength()+props.get(0);
				if(roleAtt>1)
				{
					sign = false;
				}
				break;
			case 1:
				int roleDef = super.getDefence()+props.get(1);
				if(roleDef>1)
				{
					sign = false;
				}
				break;
			case 2:
				int roleHp = super.getHpMax()+props.get(2);
				if(roleHp>1)
				{
					sign = false;
				}
				break;
			case 3:
				int roleDex = super.getAgility()+props.get(3);
				if(roleDex>1){
					sign = false;
				}
				break;
			default:
				break;
			}
		}
		
		return index;
		
	}
	/**
	 * 忍者训练增加属性
	 * @param training
	 * @param redtype  降低的属性
	 * @param qualityRates  属性增加概率
	 * @return
	 */
	public String getAddProp(Training training,int redtype){
		String []atkChange = training.getAtkChange().split("\\|");
		String []defChange = training.getDefChange().split("\\|");
		String []hpChange = training.getHpChange().split("\\|");
		String []dexChange = training.getDexChange().split("\\|");
		//判断增加属性
		List<Integer> qualityRates = Lists.newArrayList();
		qualityRates.add(Integer.parseInt(atkChange[1]));
		qualityRates.add(Integer.parseInt(defChange[1]));
		qualityRates.add(Integer.parseInt(hpChange[1]));
		qualityRates.add(Integer.parseInt(dexChange[1]));
		
		List<Integer> list = Lists.newArrayList();
		int num = this.level/training.getTrainLv()+1;
		list.add(num*training.getAtkLvUp());
		list.add(num*training.getDefLvUp());
		list.add(num*training.getHpLvUp());
		list.add(num*training.getDexLvUp());
		for(int i=0;i<props.size();i++){
			if(i==redtype){
				list.set(i, 0);
			}else{
				list.set(i, list.get(i)-props.get(i));//各属性可增加数值
			}
			if(list.get(i)==0){
				qualityRates.set(i, 0);//更新概率
			}
		}
		int index = Rnd.getIndex(qualityRates);
		
		return index+"_"+list.get(index);
	}
	
	/**
	 * 忍者训练结果确认
	 * @param player
	 * @param type  0放弃 1提交
	 */
	public void commitTrainProp(Player player,int type) {
		for (int i = 0; i < props.size(); ++i) {
			if (type == 1 && tmptrainprops != null && tmptrainprops.size() > 0) {
				props.set(i, props.get(i) + tmptrainprops.get(i));
			}
		}
		tmptrainprops.clear();
	}

	public List<ItemInstance> getEquip() {
		return equip;
	}

	public int getEquipNum() {
		int num = 0;
		for (ItemInstance item : equip) {
			if (item != null) {
				num++;
			}
		}
		return num;
	}

	public void setEquip(List<ItemInstance> equip) {
		this.equip = equip;
	}

	void removeSkill(Integer type, int idx) {
		List<Integer> skill = getSkillBytype(type);
		skill.set(idx, null);
	}
	
	
	/**
	 * 技能星级的key
	 * @param type
	 * @param idx
	 * @return
	 */
	public String getSkillStarKey(int type, int idx) {
		return type+"-"+ idx;
	}
	
	public Integer upSkillStar(Player player, int type,  int idx) {
		Integer skillId = getSkillId(type, idx);
		if(skillId == null) {
			logger.error("skillId is null. type={},idx={}",type,idx);
			return -1;
		}
		int upPoint = getSkillStarUpPoint(skillId, type,idx);
		logger.debug("up star points = {}",upPoint);
		String skey = getSkillStarKey(type, idx);
		VariousItemEntry spEntry = new VariousItemEntry("spoint", upPoint);
		int code = VariousItemUtil.doBonus(player, spEntry, LogConst.UP_STAR_SKILL, false);
		if(code == MessageCode.OK) {
			if(ninjitsu.contains(skillId)) {
				Integer star = skillStars.get(skey);
				if(star == null) {
					skillStars.put(skey, 1);
				}else {
					skillStars.put(skey, ++star);
				}
			}
		}
		return skillId;
		
	}
	
	/**
	 * 设置技能星数
	 * @param type
	 * @param idx
	 * @param count
	 */
	public void setSkillStar(int type, int idx, int count) {
		skillStars.put(getSkillStarKey(type, idx), count);
	}

	public void removeSkillStar(int type, int idx) {
		skillStars.remove(getSkillStarKey(type, idx));
	}
	

	public int getSkillStarCount(int type,int idx) {
		Integer count = skillStars.get(getSkillStarKey(type, idx));
		return count == null ? 0 : count;
	}

	public List<Integer> getSkillBytype(int type) {
		switch (type) {
		case SKILL_TYPE_NINJITSU:
			return ninjitsu;
		case SKILL_TYPE_PSYCHIC:
			return psychic;
//		case SKILL_TYPE_BLOOD:
//			return blood;
		case SKILL_TYPE_BEAST:
			return beast;

		default:
			return ninjitsu;
		}
	}

	public void setSkill(int type, int index, int skillId) {
		List<Integer> skillBytype = getSkillBytype(type);
		skillBytype.set(index, skillId);
	}

	private SkillConfigManager config() {
		return AllGameConfig.getInstance().skills;
	}

	public Integer getSkillId(int type, int index) {
		List<Integer> skills = getSkillBytype(type);
		Preconditions.checkElementIndex(index, skills.size());
		Integer id = skills.get(index);
		return id;
	}
	

	public Integer getSkillPlaceIdx(int type) {
		List<Integer> skills = getSkillBytype(type);
		if (skills.size() == 1) {
			return 0;
		}
		for (int i = 0; i < skills.size(); i++) {
			if (skills.get(i) == null) {
				return i;
			}
		}
		return -1;
	}

	public void addExp(int count, Player player) {
		if (count <= 0)
			return;
		int newXp = exp + count;
		
		if (level >= AllGameConfig.getInstance().levels.getLevels().size() - 1) {
			Level upLevels = AllGameConfig.getInstance().levels.getLevel(level + 1);
			Integer upexps = upLevels.getRxp();
			if(newXp>=upexps){
				newXp = upexps;  
			}
			this.setExp(newXp);
			return;
		}
		
		Level upLevel = AllGameConfig.getInstance().levels.getLevel(level + 1);
		
		int levelUpExp = upLevel.getRxp();
		if (newXp > levelUpExp) {
			int tarLv = level + 1;
			if(player.getProperty().getLevel() >= tarLv){
				this.setLevel(tarLv);
				if(player.getProperty().getLevel()>tarLv){
					this.setExp(0);
					this.addExp(newXp - levelUpExp, player);
				}else{
					Level nextlevel = AllGameConfig.getInstance().levels.getLevel(tarLv + 1);
					this.setExp(nextlevel.getRxp());
				}
				isLevelUp = true;
			}else{
				this.setExp(levelUpExp);
			}
		}else{
			this.setExp(newXp);
		}
		
//		if (newXp >= levelUpExp) {
//			if (level >= AllGameConfig.getInstance().levels.getLevels().size() - 1) {
//				if (exp < levelUpExp) {
//					this.setExp(levelUpExp);
//				}
//			} else {
//				// 升级了
//				int tarLv = level + 1;
//				// 根据玩家等级判断是否可以升级
//				if (player.getProperty().getLevel() >= tarLv) {
//					this.setLevel(tarLv);
//					this.setExp(0);
//					this.addExp(newXp - levelUpExp, player);
//					isLevelUp = true;
//				} else {
//					this.setExp(newXp);
//				}
//			}
//		} else {
//			this.setExp(newXp);
//		}
	}
	
	public int getWholeStarPoint() {
		int baseQuality = super.getQuality();
		QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(baseQuality);
		int basePoint = qualityRef.getPoints()[0];
		int qLvBasePoint = qlv * basePoint;
		int currQuality = getQuality();
		if(baseQuality != currQuality) {
			qualityRef = AllGameConfig.getInstance().qref.getQualityRef(currQuality);
		}
		return qualityRef.getPoints()[starLv % starLength] + qLvBasePoint;
	}
	
	public int getWholeSkillPoints(int skillId, int type, int idx) {
		Skill skill = AllGameConfig.getInstance().skills.getSkill(skillId);
		if(skill != null) {
			Integer baseQuality = skill.getQuality();
			int starCount = getSkillStarCount(type,idx);
			int currQuality = baseQuality + starCount/spointLength;
			QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(currQuality);
			int spointVal =  qualityRef.getSp()[starCount % spointLength];
			logger.debug("skill spoint value = {}",spointVal);
			return spointVal;
		}
		return 0;
	}
	
	public int getSkillQualityBySkillid(int skillid,int type ,int idx)
	{
		Skill skill = AllGameConfig.getInstance().skills.getSkill(skillid);
		if(skill != null) {
			Integer baseQuality = skill.getQuality();
			int starCount = getSkillStarCount(type,idx);
			int currQuality = baseQuality + starCount/spointLength;
			QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(currQuality);
			return qualityRef.getId().intValue();
		}
		return 1;
	}

	/**
	 * 品质可以升级到ss级别
	 * @return
	 */
	public void addStarPoint(int count) {
		int newPoint = starPoint + count;
		int quality = super.getQuality();
		int upLv = starLv +1;
		int delta = upLv / starLength;
		int idx = upLv % starLength;
		QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(quality+delta);
		if(qualityRef == null) {
			return;
		}
		int needPoint = qualityRef.getUpVals()[idx];
		if (newPoint >= needPoint) {
			setStarLv(upLv);
			setStarPoint(0);
			addStarPoint(newPoint - needPoint);
		} else {
			setStarPoint(newPoint);
		}
	}
	
	
	
	private int  getSkillStarUpPoint(int skillId, int type, int idx) {
		Skill skill = AllGameConfig.getInstance().skills.getSkill(skillId);
		if(skill != null) {
			int starCount = getSkillStarCount(type,idx);
			int upLv = starCount + 1;
			int delta = upLv / spointLength;
			int spVidx = upLv % spointLength;
			QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(skill.getQuality()+delta);
			if(qualityRef == null) {
				return 0;
			}
			int needPoint = qualityRef.getSpV()[spVidx];
			return needPoint;
		}
		return 0;
	}
	
	@Override
	public int getQuality() {
		return super.getQuality() + starLv/starLength;
	}

	public int getStarPoint() {
		return starPoint;
	}

	public void setStarPoint(int starPoint) {
		this.starPoint = starPoint;
	}
	
	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getExp() {
		return exp;
	}

	public int getWholeExp() {
		int exp = this.exp;
		for (int i = 1; i <= level; i++) {
			Integer hxp = AllGameConfig.getInstance().levels.getLevel(i).getRxp();
			exp += hxp;
		}
		return exp;
	}

	public ObjectNode toJson() {
		ObjectNode obj = toShortJson();
		ArrayNode arrayNode = DynamicJsonProperty.jackson.createArrayNode();
		arrayNode.add(DynamicJsonProperty.convertToJsonNode(ninjitsu));
		arrayNode.add(DynamicJsonProperty.convertToJsonNode(psychic));
		arrayNode.add(DynamicJsonProperty.convertToJsonNode(Collections.EMPTY_LIST));
		arrayNode.add(DynamicJsonProperty.convertToJsonNode(beast));
		obj.put("skill", arrayNode);
		obj.put("sstar", DynamicJsonProperty.convertToObjectNode(skillStars));
		ArrayNode array2 = DynamicJsonProperty.jackson.createArrayNode();
		int[] arrChakra = this.getChakra();
		for(int i=0; i<arrChakra.length; i++){
			array2.add(arrChakra[i]);	
		}
		obj.put("chakra", array2);
		ArrayNode earr = DynamicJsonProperty.jackson.createArrayNode();
		for (ItemInstance item : equip) {
			if (item != null) {
				earr.add(item.toJson());
			} else {
				earr.addNull();
			}
		}
		obj.put("equip", earr);
		
		// 状态列表
		ArrayNode stateArr = DynamicJsonProperty.jackson.createArrayNode();
		for(Integer id : this.hstates){
			stateArr.add(id);
		}
		obj.put("hstates", stateArr);
		return obj;
	}

	public int getAllPower() {
		int rolePower = getPower();
		// if (logger.isDebugEnabled()) {
		// logger.debug("hero name : {}",getName());
		// logger.debug("rolePower{}",rolePower);
		// }
		return rolePower;
	}

	private int getEquipAgi(ItemInstance e, int enhance, int elv) {
		Integer agi = e.getAgi();
		if (agi != 0) {
			return agi + enhance * elv;
		}
		return 0;
	}

	private int getEquipAtt(ItemInstance e, int enhance, int elv) {
		Integer att = e.getAtt();
		if (att != 0) {
			return att + enhance * elv;
		}
		return 0;
	}

	private int getEquipDef(ItemInstance e, int enhance, int elv) {
		Integer def = e.getDef();
		if (def != 0) {
			return def + enhance * elv;
		}
		return 0;
	}

	private int getEquipHp(ItemInstance e, int enhance, int elv) {
		Integer hp = e.getHp()*10;
		if (hp != 0) {
			return hp + enhance * elv * 10;
		}
		return 0;
	}

	public int getEquipAtts() {
		int value = 0;
		for (ItemInstance e : equip) {
			if (e == null) {
				continue;
			}
			Accessory acc = (Accessory) e.getBaseItem();
			QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(acc.getQuality());
			value += getEquipAtt(e, qualityRef.getEnhance(), e.getLv());
		}
		return value;
	}

	public int getEquipDefs() {
		int value = 0;
		for (ItemInstance e : equip) {
			if (e == null) {
				continue;
			}
			Accessory acc = (Accessory) e.getBaseItem();
			QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(acc.getQuality());
			value += getEquipDef(e, qualityRef.getEnhance(), e.getLv());
		}
		return value;
	}

	public int getEquipHps() {
		int value = 0;
		for (ItemInstance e : equip) {
			if (e == null) {
				continue;
			}
			Accessory acc = (Accessory) e.getBaseItem();
			QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(acc.getQuality());
			value += getEquipHp(e, qualityRef.getEnhance(), e.getLv());
		}
		return value;
	}

	public int getEquipAgis() {
		int value = 0;
		for (ItemInstance e : equip) {
			if (e == null) {
				continue;
			}
			Accessory acc = (Accessory) e.getBaseItem();
			QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(acc.getQuality());
			value += getEquipAgi(e, qualityRef.getEnhance(), e.getLv());
		}
		return value;
	}
	
	public Map<Integer, Integer> getEquipRefineProps() {		
		Map<Integer, Integer> rprops = Maps.newHashMap();
		for (ItemInstance acc : equip) {
			if (acc != null) {
				for (Map.Entry<Integer, Integer> prop : acc.getRefineProps().entrySet()) {
					rprops.put(prop.getKey(), rprops.get(prop.getKey()) != null 
							? rprops.get(prop.getKey()) + prop.getValue() : prop.getValue());
				}
			}
		}
		return rprops;
	}

	private int getEquipPower() {
		int power = 0;
		for (ItemInstance e : equip) {
			if (e == null) {
				continue;
			}
			Accessory acc = (Accessory) e.getBaseItem();
			QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(acc.getQuality());
			int equipAgi = getEquipAgi(e, qualityRef.getEnhance(), e.getLv());

			int equipAtt = getEquipAtt(e, qualityRef.getEnhance(), e.getLv());
			int equipDef = getEquipDef(e, qualityRef.getEnhance(), e.getLv());

			int equipHp = getEquipHp(e, qualityRef.getEnhance(), e.getLv()) / 10;
			power += (equipAgi + equipAtt + equipDef + equipHp);
			if (logger.isDebugEnabled()) {
				logger.debug("equipAgi:{}", equipAgi);
				logger.debug("equipAtt:{}", equipAtt);
				logger.debug("equipDef:{}", equipDef);
				logger.debug("equipHp:{}", equipHp);
				logger.debug("equip power:{}", power);
			}
		}

		return power;
	}

	public ObjectNode toHpJson(float hpRate) {
		ObjectNode obj = toShortJson();
		obj.put("hpRate", hpRate);
		return obj;

	}

	public ObjectNode toShortJson() {
		//兼容老玩家数据
		if(blood != null && ninjitsu.size() != NINJITSU_COUNT) {
			ninjitsu.addAll(blood);
			blood = null;
		}
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("id", getRoleId());
		obj.put("lv", level);
		if (exp > 0) {
			obj.put("rxp", exp);
		}
		if (qlv > 0) {
			obj.put("qlv", qlv);
		}
		if (starLv > 0) {
			obj.put("slv", starLv);
		}
		obj.put("pt", starPoint);
		// obj.put("power", getAllPower());
		//返回训练属性
		obj.put("props", DynamicJsonProperty.convertToArrayNode(props));
		obj.put("tmptrainprops", DynamicJsonProperty.convertToArrayNode(tmptrainprops));
		return obj;
	}

	@Override
	public List<BaseSkill> getSkills() {
		List<BaseSkill> skills = Lists.newArrayList();
		for (int i = 0; i < ninjitsu.size(); i++) {
			Integer sid = ninjitsu.get(i);
			if(sid != null) {
				Skill skill = AllGameConfig.getInstance().skills.getSkill(sid);
				skills.add(new BaseSkill(skill, getSkillStarCount(SKILL_TYPE_NINJITSU, i)));
			}
		}
		for (int i = 0; i < psychic.size(); i++) {
			Integer sid = psychic.get(i);
			if(sid != null) {
				Skill skill = AllGameConfig.getInstance().skills.getSkill(sid);
				skills.add(new BaseSkill(skill, 0));
			}
		}
		for (int i = 0; i < beast.size(); i++) {
			Integer sid = beast.get(i);
			if(sid != null) {
				Skill skill = AllGameConfig.getInstance().skills.getSkill(sid);
				skills.add(new BaseSkill(skill, 0));
			}
		}
		return skills;
	}

	public boolean isLevelUp() {
		return isLevelUp;
	}

	public void setLevelUp(boolean isLevelUp) {
		this.isLevelUp = isLevelUp;
	}

	@Override
	public int getStrength() {
		int roleAtt = super.getStrength();
		int equpAtt = getEquipAtts();
		int strength = roleAtt + equpAtt + (props.size() < 1 ? 0 : props.get(0));
//		 if (logger.isDebugEnabled()) {
//		 logger.debug("roleAtt {}",roleAtt);
//		 logger.debug("equpAtt {}",equpAtt);
//		 logger.debug("strength {}",strength);
//		 }
		return strength;
	}

	@Override
	public int getDefence() {
		int roleDef = super.getDefence();
		int equipDef = getEquipDefs();
		int defence = roleDef + equipDef + (props.size() < 2 ? 0 : props.get(1));
		// if (logger.isDebugEnabled()) {
		// logger.debug("roleDef {}",roleDef);
		// logger.debug("equipDef {}",equipDef);
		// logger.debug("defence {}",defence);
		// }
		return defence;
	}

	@Override
	public int getHpMax() {
		int roleHp = super.getHpMax();
		int equipHp = getEquipHps();
		int hp = roleHp + equipHp + (props.size() < 3 ? 0 : props.get(2));
		// if (logger.isDebugEnabled()) {
		// logger.debug("roleHp {}",roleHp);
		// logger.debug("equipHp {}",equipHp);
		// logger.debug("hp {}",hp);
		// }

		return hp;
	}

	@Override
	public int getAgility() {
		int roleAgi = super.getAgility();
		int equipAgi = getEquipAgis();
		int agility = roleAgi + equipAgi + (props.size() < 4 ? 0 : props.get(3));
		// if (logger.isDebugEnabled()) {
		// logger.debug("roleAgi {}",roleAgi);
		// logger.debug("equipAgi {}",equipAgi);
		// logger.debug("agility {}",agility);
		// }
		return agility;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/**
	 * 计算忍者资质
	 * @return
	 */
	public float getQualification ()
	{
		this.setRoleConfig();
		int _qlv = this.qlv;
		int _slv = this.getStarLv();
	    /**
	    资质=(0级攻+0级防+0级敏+(0级血/N) ) / 4
	    D级忍者N=5
	    C级忍者N=6
	    B级忍者N=7
	    A级忍者N=8
	    S级忍者N=9
	    SS级忍者N=10
	    资质保留小数点后1位，四舍五入取整
	     **/

		Float att = roleConfig.getAtt();
		Float  def = roleConfig.getDef();
		Float hp = roleConfig.getHp();
		Float agi = roleConfig.getAgi();
		int n = 4+roleConfig.getQuality();
	    float mun = ((att+def+agi+(hp*1.0f/n))/4f);
	    mun += mun* ((_qlv)+(_slv))/20f;
	    return ((int)(mun*10))/10f;
	}
	
	
	// 状态操作
	public void addHeroStates(Integer state)
	{
		if(this.hstates.indexOf(state) != -1){
			logger.info("the hero state is exit!"+state.intValue());
			return;
		}
		this.hstates.add(state);
	}
	
	public boolean isHeroHaveState(Integer state)
	{
		if(this.hstates.indexOf(state) != -1){
			return true;
		}
		return false;
	}
	
	public void removeHeroState(Integer state)
	{
		for(int i = 0;i<this.hstates.size();i++)
		{
			Integer s = this.hstates.get(i);
			if(s.intValue()== state.intValue()){
				this.hstates.remove(i);
				break;
			}
		}
	}
	
	public List<Integer> getHeroStates()
	{
		return this.hstates;
	}
	
}
