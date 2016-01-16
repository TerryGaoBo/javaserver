package com.jelly.hero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.bean.Skill;


public class BaseSkill {
	private static final Logger logger = LoggerFactory.getLogger(BaseSkill.class);
	private float hitRate = 0;
	private float effectRate = 0;

	public BaseSkill(Skill skill) {
		this.skill = skill;
	}
	
	public BaseSkill(Skill skill, int starCount) {
		this.skill = skill;
		this.starCount = starCount;
	}

	private Skill skill;
	
	private int starCount;

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public int getStarCount() {
		return starCount;
	}

	public void setStarCount(int starCount) {
		this.starCount = starCount;
	}
	
	public float getHitRate() {
		return this.hitRate;
	}

	public void setHitRate(float hitRate) {
		this.hitRate = hitRate;
	}

	public float getEffectRate() {
		return this.effectRate;
	}

	public void setEffectRate(float effectRate) {
		this.effectRate = effectRate;
	}
	
	public float calcChakraEffectRate(IHero attaker, IHero defencer){
		//忍者的查克拉肯定是两个值，技能不确定
		float rate = 0;
		int[] aCharkra = attaker.getChakra();
		int[] sCharkra = this.skill.getChakra();
		if(sCharkra == null){
			this.effectRate = 0;
			this.hitRate = 0;
			return 0;
		}
		int[] bCharkra = defencer.getChakra();
		//计算忍者与技能
		if(aCharkra[1]==0 && sCharkra.length==1){
			if(aCharkra[0] == sCharkra[0]){
				rate += 0.1f;
			}
			if(CharkraType.isRestrain( sCharkra[0],aCharkra[0])){
				rate += -0.1f;
			}
		}else if(aCharkra[1]!=0 && sCharkra.length==1){
			if(sCharkra[0]==aCharkra[0] || sCharkra[0]==aCharkra[1]){
				rate += 0.1f;
			}
			if(CharkraType.isRestrain( sCharkra[0], aCharkra[0]) && sCharkra[0]!=aCharkra[1]){
				rate += -0.1f;
			}
			if(CharkraType.isRestrain( sCharkra[0], aCharkra[1]) && sCharkra[0]!=aCharkra[0]){
				rate += -0.1f;
			}
		}else if(sCharkra.length==2 && aCharkra[1]==0){
			if(aCharkra[0]==sCharkra[0] && !CharkraType.isBeRestrain(aCharkra[0], sCharkra[1])){
				rate += 0.1f;
			}
			if(aCharkra[0]==sCharkra[1] && !CharkraType.isBeRestrain(aCharkra[0], sCharkra[0])){
				rate += 0.1f;
			}
			if(aCharkra[0]!=sCharkra[1] && CharkraType.isBeRestrain(aCharkra[0], sCharkra[0])){
				rate += -0.1f;
			}
			if(aCharkra[0]!=sCharkra[0] && CharkraType.isBeRestrain(aCharkra[0], sCharkra[1])){
				rate += -0.1f;
			}
		}else if(sCharkra.length==2 && aCharkra[1]!=0){
			if(aCharkra[0]==sCharkra[0] && aCharkra[1]==sCharkra[1]){
				rate += 0.2f;
			}
			if(aCharkra[0]==sCharkra[1] && aCharkra[1]==sCharkra[0]){
				rate += 0.2f;
			}
			if(CharkraType.isRestrain( sCharkra[0], aCharkra[0]) && CharkraType.isRestrain( sCharkra[1], aCharkra[1])){
				rate += -0.2f;
			}
			if(CharkraType.isRestrain( sCharkra[1], aCharkra[0]) && CharkraType.isRestrain( sCharkra[0], aCharkra[1])){
				rate += -0.2f;
			}
			
			//技能一个属性克制忍者一个属性，另一属性与忍者一个属性相同
			if(CharkraType.isRestrain( sCharkra[0], aCharkra[0]) && (sCharkra[1]==aCharkra[0] || sCharkra[1]==aCharkra[1])){
				
			}
			if(CharkraType.isRestrain( sCharkra[0], aCharkra[1]) && (sCharkra[1]==aCharkra[0] || sCharkra[1]==aCharkra[1])){
				
			}
			if(CharkraType.isRestrain( sCharkra[1], aCharkra[0]) && (sCharkra[0]==aCharkra[0] || sCharkra[0]==aCharkra[1])){
				
			}
			if(CharkraType.isRestrain( sCharkra[1], aCharkra[1]) && (sCharkra[0]==aCharkra[0] || sCharkra[0]==aCharkra[1])){
				
			}
			//技能一个属性与忍者一个属性相同，另一属性不克制忍者任何属性，也与忍者任何属性不同
			if(sCharkra[0]== aCharkra[0] 
					&&( !CharkraType.isRestrain( sCharkra[1], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[1], aCharkra[1]))
					&&(sCharkra[1]!=aCharkra[0] && sCharkra[1]!=aCharkra[1])
					){
				rate += 0.1f;
			}
			if(sCharkra[0]== aCharkra[1] 
					&&( !CharkraType.isRestrain( sCharkra[1], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[1], aCharkra[1]))
					&&(sCharkra[1]!=aCharkra[0] && sCharkra[1]!=aCharkra[1])
					){
				rate += 0.1f;
			}
			if(sCharkra[1]== aCharkra[0] 
					&&( !CharkraType.isRestrain( sCharkra[0], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[0], aCharkra[1]))
					&&(sCharkra[0]!=aCharkra[0] && sCharkra[0]!=aCharkra[1])
					){
				rate += 0.1f;
			}
			if(sCharkra[1]== aCharkra[1] 
					&&( !CharkraType.isRestrain( sCharkra[0], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[0], aCharkra[1]))
					&&(sCharkra[0]!=aCharkra[0] && sCharkra[0]!=aCharkra[1])
					){
				rate += 0.1f;
			}
			//技能一个属性克制忍者一个属性，另一属性不克制忍者任何属性，也与忍者任何属性不同
			if(CharkraType.isRestrain(sCharkra[0], aCharkra[0]) 
					&&( !CharkraType.isRestrain( sCharkra[1], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[1], aCharkra[1]))
					&&(sCharkra[1]!=aCharkra[0] && sCharkra[1]!=aCharkra[1])
					){
				rate += -0.1f;
			}
			if(CharkraType.isRestrain(sCharkra[0], aCharkra[1]) 
					&&( !CharkraType.isRestrain( sCharkra[1], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[1], aCharkra[1]))
					&&(sCharkra[1]!=aCharkra[0] && sCharkra[1]!=aCharkra[1])
					){
				rate += -0.1f;
			}
			if(CharkraType.isRestrain(sCharkra[1], aCharkra[0]) 
					&&( !CharkraType.isRestrain( sCharkra[0], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[0], aCharkra[1]))
					&&(sCharkra[0]!=aCharkra[0] && sCharkra[0]!=aCharkra[1])
					){
				rate += -0.1f;
			}
			if(CharkraType.isRestrain(sCharkra[1], aCharkra[1]) 
					&&( !CharkraType.isRestrain( sCharkra[0], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[0], aCharkra[1]))
					&&(sCharkra[0]!=aCharkra[0] && sCharkra[0]!=aCharkra[1])
					){
				rate += -0.1f;
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("calc effect value 1 :{}", rate);
		}
		//计算忍者与技能
		if(bCharkra[1]==0 && sCharkra.length==1){
			if(CharkraType.isRestrain( sCharkra[0],bCharkra[0])){
				rate += 0.1f;
			}
			if(CharkraType.isBeRestrain( sCharkra[0],bCharkra[0])){
				rate += -0.1f;
			}
		}else if(bCharkra[1]!=0 && sCharkra.length==1){
			if(CharkraType.isRestrain( sCharkra[0], bCharkra[0]) && sCharkra[0]!=bCharkra[1]){
				rate += 0.1f;
			}
			if(CharkraType.isRestrain( sCharkra[0], bCharkra[1]) && sCharkra[0]!=bCharkra[0]){
				rate += 0.1f;
			}
			if(CharkraType.isRestrain( sCharkra[0], bCharkra[0]) && sCharkra[0]==bCharkra[1]){
				rate += 0.05f;
			}
			if(CharkraType.isRestrain( sCharkra[0], bCharkra[1]) && sCharkra[0]==bCharkra[0]){
				rate += 0.05f;
			}
			if(CharkraType.isBeRestrain( sCharkra[0], bCharkra[0]) && sCharkra[0]!=bCharkra[1]){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain( sCharkra[0], bCharkra[1]) && sCharkra[0]!=bCharkra[0]){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain( sCharkra[0], bCharkra[0]) && sCharkra[0]==bCharkra[1]){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain( sCharkra[0], bCharkra[1]) && sCharkra[0]==bCharkra[0]){
				rate += -0.1f;
			}
		}else if(sCharkra.length==2 && bCharkra[1]==0){
			if(CharkraType.isRestrain(sCharkra[0], bCharkra[0])
					&& !CharkraType.isBeRestrain(sCharkra[1], bCharkra[0])
					&& sCharkra[1]!= bCharkra[0]){
				rate += 0.1f;
			}
			if(CharkraType.isRestrain(sCharkra[1], bCharkra[0])
					&& !CharkraType.isBeRestrain(sCharkra[0], bCharkra[0])
					&& sCharkra[0]!= bCharkra[0]){
				rate += 0.1f;
			}
			if(CharkraType.isRestrain(sCharkra[0], bCharkra[0])
					&& !CharkraType.isBeRestrain(sCharkra[1], bCharkra[0])
					&& sCharkra[1]== bCharkra[0]){
				rate += 0.05f;
			}
			if(CharkraType.isRestrain(sCharkra[1], bCharkra[0])
					&& !CharkraType.isBeRestrain(sCharkra[0], bCharkra[0])
					&& sCharkra[0]== bCharkra[0]){
				rate += 0.05f;
			}
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[0])
					&& !CharkraType.isRestrain(sCharkra[1], bCharkra[0])
					&& sCharkra[1]!= bCharkra[0]){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain(sCharkra[1], bCharkra[0])
					&& !CharkraType.isRestrain(sCharkra[0], bCharkra[0])
					&& sCharkra[0]!= bCharkra[0]){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[0])
					&& !CharkraType.isRestrain(sCharkra[1], bCharkra[0])
					&& sCharkra[1]== bCharkra[0]){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain(sCharkra[1], bCharkra[0])
					&& !CharkraType.isRestrain(sCharkra[0], bCharkra[0])
					&& sCharkra[0]== bCharkra[0]){
				rate += -0.1f;
			}
			//技能一个属性克制敌人属性，另一个属性被敌人属性克制(不变)
			
			
		}else if(sCharkra.length==2 && bCharkra[1]!=0){	//2-----------2
			if(CharkraType.isRestrain(sCharkra[0], bCharkra[0]) && CharkraType.isRestrain(sCharkra[1], bCharkra[1])){
				rate += 0.2f;
			}
			if(CharkraType.isRestrain(sCharkra[0], bCharkra[1]) && CharkraType.isRestrain(sCharkra[1], bCharkra[0])){
				rate += 0.2f;
			}
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) && CharkraType.isBeRestrain(sCharkra[1], bCharkra[1])){
				rate += -0.2f;
			}
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]) && CharkraType.isBeRestrain(sCharkra[1], bCharkra[0])){
				rate += -0.2f;
			}
			//技能一个属性克制敌人一个属性，另一个属性被敌人其中一个属性克制(不变)
			
			//技能一个属性克制敌人一个属性，另一个属性不被敌人属性所克制也与其不同
			if(CharkraType.isRestrain(sCharkra[0], bCharkra[0])
					&& (!CharkraType.isBeRestrain(sCharkra[1], bCharkra[0]) &&  sCharkra[1]!=bCharkra[0])
					&& ( !CharkraType.isBeRestrain(sCharkra[1], bCharkra[1]) && sCharkra[1]!=bCharkra[1])
					){
				rate += 0.1f;
			}
			if(CharkraType.isRestrain(sCharkra[0], bCharkra[1])
					&& (!CharkraType.isBeRestrain(sCharkra[1], bCharkra[0]) &&  sCharkra[1]!=bCharkra[0])
					&& ( !CharkraType.isBeRestrain(sCharkra[1], bCharkra[1]) && sCharkra[1]!=bCharkra[1])
					){
				rate += 0.1f;
			}
			if(CharkraType.isRestrain(sCharkra[1], bCharkra[0])
					&& (!CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) &&  sCharkra[0]!=bCharkra[0])
					&& ( !CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]) && sCharkra[0]!=bCharkra[1])
					){
				rate += 0.1f;
			}
			if(CharkraType.isRestrain(sCharkra[1], bCharkra[1])
					&& (!CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) &&  sCharkra[0]!=bCharkra[0])
					&& ( !CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]) && sCharkra[0]!=bCharkra[1])
					){
				rate += 0.1f;
			}
			//技能一个属性克制敌人一个属性，另一个属性不被敌人属性所克制但与敌人一个属性相同
			if(CharkraType.isRestrain(sCharkra[0], bCharkra[0])
					&& (!CharkraType.isBeRestrain(sCharkra[1], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[1], bCharkra[1]))
					&& ( sCharkra[1]==bCharkra[0] ||  sCharkra[1]== bCharkra[1])
					){
				rate += 0.05f;
			}
			if(CharkraType.isRestrain(sCharkra[0], bCharkra[1])
					&& (!CharkraType.isBeRestrain(sCharkra[1], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[1], bCharkra[1]))
					&& ( sCharkra[1]==bCharkra[0] ||  sCharkra[1]== bCharkra[1])
					){
				rate += 0.05f;
			}
			if(CharkraType.isRestrain(sCharkra[1], bCharkra[0])
					&& (!CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]))
					&& ( sCharkra[0]==bCharkra[0] ||  sCharkra[0]== bCharkra[1])
					){
				rate += 0.05f;
			}
			if(CharkraType.isRestrain(sCharkra[1], bCharkra[1])
					&& (!CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]))
					&& ( sCharkra[0]==bCharkra[0] ||  sCharkra[0]== bCharkra[1])
					){
				rate += 0.05f;
			}
			//技能一个属性被敌人一个属性克制，另一个属性不被敌人属性所克制也与其不同
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[0])
					&& (!CharkraType.isBeRestrain(sCharkra[1], bCharkra[0]) &&  sCharkra[1]!=bCharkra[0])
					&& ( !CharkraType.isBeRestrain(sCharkra[1], bCharkra[1]) && sCharkra[1]!=bCharkra[1])
					){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[1])
					&& (!CharkraType.isBeRestrain(sCharkra[1], bCharkra[0]) &&  sCharkra[1]!=bCharkra[0])
					&& ( !CharkraType.isBeRestrain(sCharkra[1], bCharkra[1]) && sCharkra[1]!=bCharkra[1])
					){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain(sCharkra[1], bCharkra[0])
					&& (!CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) &&  sCharkra[0]!=bCharkra[0])
					&& ( !CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]) && sCharkra[0]!=bCharkra[1])
					){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain(sCharkra[1], bCharkra[1])
					&& (!CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) &&  sCharkra[0]!=bCharkra[0])
					&& ( !CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]) && sCharkra[0]!=bCharkra[1])
					){
				rate += -0.1f;
			}
			//技能一个属性被敌人一个属性克制，另一个属性不被敌人属性所克制但与敌人一个属性相同
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[0])
					&& (!CharkraType.isBeRestrain(sCharkra[1], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[1], bCharkra[1]))
					&& ( sCharkra[1]==bCharkra[0] ||  sCharkra[1]== bCharkra[1])
					){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[1])
					&& (!CharkraType.isBeRestrain(sCharkra[1], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[1], bCharkra[1]))
					&& ( sCharkra[1]==bCharkra[0] ||  sCharkra[1]== bCharkra[1])
					){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain(sCharkra[1], bCharkra[0])
					&& (!CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]))
					&& ( sCharkra[0]==bCharkra[0] ||  sCharkra[0]== bCharkra[1])
					){
				rate += -0.1f;
			}
			if(CharkraType.isBeRestrain(sCharkra[1], bCharkra[1])
					&& (!CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]))
					&& ( sCharkra[0]==bCharkra[0] ||  sCharkra[0]== bCharkra[1])
					){
				rate += -0.1f;
			}
		}
		
		
		effectRate = rate;
		return rate;
	}
	

	public float calcChakraHitRate(IHero attaker, IHero defencer){
		//忍者的查克拉肯定是两个值，技能不确定
		float rate = 0;
		int[] aCharkra = attaker.getChakra();
		int[] sCharkra = this.skill.getChakra();
		int[] bCharkra = defencer.getChakra();
		//计算忍者与技能
		if(aCharkra[1]==0 && sCharkra.length==1){
			if(CharkraType.isRestrain( sCharkra[0],aCharkra[0])){
				rate += -0.25f;
			}
		}else if(aCharkra[1]!=0 && sCharkra.length==1){
			if(CharkraType.isRestrain( sCharkra[0], aCharkra[0]) && sCharkra[0]!=aCharkra[1]){
				rate += -0.25f;
			}
			if(CharkraType.isRestrain( sCharkra[0], aCharkra[1]) && sCharkra[0]!=aCharkra[0]){
				rate += -0.25f;
			}
		}else if(sCharkra.length==2 && aCharkra[1]==0){
			if(aCharkra[0]!=sCharkra[1] && CharkraType.isBeRestrain(aCharkra[0], sCharkra[0])){
				rate += -0.25f;
			}
			if(aCharkra[0]!=sCharkra[0] && CharkraType.isBeRestrain(aCharkra[0], sCharkra[1])){
				rate += -0.25f;
			}
		}else if(sCharkra.length==2 && aCharkra[1]!=0){
			if(aCharkra[0]==sCharkra[0] && aCharkra[1]==sCharkra[1]){
				rate += 0.2f;
			}
			if(aCharkra[0]==sCharkra[1] && aCharkra[1]==sCharkra[0]){
				rate += 0.2f;
			}
			if(CharkraType.isRestrain( sCharkra[0], aCharkra[0]) && CharkraType.isRestrain( sCharkra[1], aCharkra[1])){
				rate += -0.5f;
			}
			if(CharkraType.isRestrain( sCharkra[1], aCharkra[0]) && CharkraType.isRestrain( sCharkra[0], aCharkra[1])){
				rate += -0.5f;
			}
			
			//技能一个属性克制忍者一个属性，另一属性不克制忍者任何属性，也与忍者任何属性不同
			if(CharkraType.isRestrain(sCharkra[0], aCharkra[0]) 
					&&( !CharkraType.isRestrain( sCharkra[1], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[1], aCharkra[1]))
					&&(sCharkra[1]!=aCharkra[0] && sCharkra[1]!=aCharkra[1])
					){
				rate += -0.25f;
			}
			if(CharkraType.isRestrain(sCharkra[0], aCharkra[1]) 
					&&( !CharkraType.isRestrain( sCharkra[1], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[1], aCharkra[1]))
					&&(sCharkra[1]!=aCharkra[0] && sCharkra[1]!=aCharkra[1])
					){
				rate += -0.25f;
			}
			if(CharkraType.isRestrain(sCharkra[1], aCharkra[0]) 
					&&( !CharkraType.isRestrain( sCharkra[0], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[0], aCharkra[1]))
					&&(sCharkra[0]!=aCharkra[0] && sCharkra[0]!=aCharkra[1])
					){
				rate += -0.25f;
			}
			if(CharkraType.isRestrain(sCharkra[1], aCharkra[1]) 
					&&( !CharkraType.isRestrain( sCharkra[0], aCharkra[0]) && !CharkraType.isRestrain( sCharkra[0], aCharkra[1]))
					&&(sCharkra[0]!=aCharkra[0] && sCharkra[0]!=aCharkra[1])
					){
				rate += -0.25f;
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("calc hit value 1 :{}", rate);
		}
		//计算忍者与技能
		if(bCharkra[1]==0 && sCharkra.length==1){

		}else if(bCharkra[1]!=0 && sCharkra.length==1){
			if(CharkraType.isBeRestrain( sCharkra[0], bCharkra[0]) && sCharkra[0]==bCharkra[1]){
				rate += -0.25f;
			}
			if(CharkraType.isBeRestrain( sCharkra[0], bCharkra[1]) && sCharkra[0]==bCharkra[0]){
				rate += -0.25f;
			}
		}else if(sCharkra.length==2 && bCharkra[1]==0){
			
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[0])
					&& !CharkraType.isRestrain(sCharkra[1], bCharkra[0])
					&& sCharkra[1]== bCharkra[0]){
				rate += -0.25f;
			}
			if(CharkraType.isBeRestrain(sCharkra[1], bCharkra[0])
					&& !CharkraType.isRestrain(sCharkra[0], bCharkra[0])
					&& sCharkra[0]== bCharkra[0]){
				rate += -0.25f;
			}
			//技能一个属性克制敌人属性，另一个属性被敌人属性克制(不变)
			
			
		}else if(sCharkra.length==2 && bCharkra[1]!=0){	//2-----------2
			
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) && CharkraType.isBeRestrain(sCharkra[1], bCharkra[1])){
				rate += -0.5f;
			}
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]) && CharkraType.isBeRestrain(sCharkra[1], bCharkra[0])){
				rate += -0.5f;
			}
			//技能一个属性克制敌人一个属性，另一个属性被敌人其中一个属性克制(不变)
			
			
			//技能一个属性被敌人一个属性克制，另一个属性不被敌人属性所克制但与敌人一个属性相同
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[0])
					&& (!CharkraType.isBeRestrain(sCharkra[1], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[1], bCharkra[1]))
					&& ( sCharkra[1]==bCharkra[0] ||  sCharkra[1]== bCharkra[1])
					){
				rate += -0.25f;
			}
			if(CharkraType.isBeRestrain(sCharkra[0], bCharkra[1])
					&& (!CharkraType.isBeRestrain(sCharkra[1], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[1], bCharkra[1]))
					&& ( sCharkra[1]==bCharkra[0] ||  sCharkra[1]== bCharkra[1])
					){
				rate += -0.25f;
			}
			if(CharkraType.isBeRestrain(sCharkra[1], bCharkra[0])
					&& (!CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]))
					&& ( sCharkra[0]==bCharkra[0] ||  sCharkra[0]== bCharkra[1])
					){
				rate += -0.25f;
			}
			if(CharkraType.isBeRestrain(sCharkra[1], bCharkra[1])
					&& (!CharkraType.isBeRestrain(sCharkra[0], bCharkra[0]) )
					&& ( !CharkraType.isBeRestrain(sCharkra[0], bCharkra[1]))
					&& ( sCharkra[0]==bCharkra[0] ||  sCharkra[0]== bCharkra[1])
					){
				rate += -0.25f;
			}
		}
		
		
		hitRate = rate;
		return rate;
	}
}
