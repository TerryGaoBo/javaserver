package com.jelly.buff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.IEffectGF;
import com.dol.cdf.common.gamefunction.gfi.effect.AlterEffectGFI;
import com.dol.cdf.common.gamefunction.gfi.effect.BaseBuffEffectGFI;
import com.google.common.base.Objects;
import com.jelly.combat.event.CombatEventHandlerPriority;
import com.jelly.combat.event.CombatEventType;
import com.jelly.hero.BaseSkill;
import com.jelly.player.BaseFighter;

public class IntermittentBuff extends BaseBuff {

	private static final Logger logger = LoggerFactory.getLogger(IntermittentBuff.class);
	
	/**
	 * 失效回合计数
	 */
	private int endNumber;

	private int startNumber;

	private final BaseBuffEffectGFI effectGFI;

	private final BaseSkill skill;
	
	private int act = -1;
	
	public IntermittentBuff(IEffectGF gf,BaseSkill skill) {
		this.effectGFI = (BaseBuffEffectGFI) gf.getGfi();
		Object actParam = effectGFI.getParameter().getParamter("act");
		this.act = actParam == null ? -1 : (Integer)actParam;
		this.skill = skill;
	}

	@Override
	public void onActive() {
		startNumber = getOwner().getCBContext().getCurrentIndex();
		if(effectGFI.getEvent() == CombatEventType.ON_ATTACKING_OTHER){
			endNumber = startNumber + 1;
		}else{
			endNumber = startNumber + effectGFI.getTurnNumber() * 2;
		}
		getOwner().registerEventHandler(this, effectGFI.getEvent(), getEventPriority());
		if(act != -1){
			((BaseFighter)getOwner()).addActHandlers(act, this);
		}

	}

	private int getEventPriority() {
		int eventPriority = effectGFI instanceof AlterEffectGFI ? ((AlterEffectGFI) effectGFI).getType()
				: CombatEventHandlerPriority.NORMAL;
		return eventPriority + effectGFI.getId();
	}

	@Override
	public void onCancel() {
		getOwner().unregigsterEventHandler(this, effectGFI.getEvent());
	}

	@Override
	public void onEvent(int eventType) {
//		if(logger.isDebugEnabled()){
//			logger.debug("onEvent skill id = {},effectId = {}",skillId,effectGFI.getId());
//		}
		if (checkEvent()) {
			int currentIndex = getOwner().getCBContext().getCurrentIndex();
			GameContext gameContext = new GameContext().setCBContextParam(getOwner().getCBContext());
			gameContext.setE(eventType);
			gameContext.setI((currentIndex - startNumber) / 2);
			gameContext.setBuffOwner(getOwner());
			gameContext.setS(skill);
//			try {
				effectGFI.execute(gameContext);
//			} catch (ArrayIndexOutOfBoundsException e) {
//				logger.error("{},skillid : {},startNumber:{},endNumber:{},currentIndx:{}",e,skillId,startNumber,endNumber,currentIndex);
//			}
			
		}
		
		
	}

	@Override
	public boolean checkEvent() {
//		if(logger.isDebugEnabled()){
//			logger.debug("check Eventskill id = {},effectId = {}",skillId,effectGFI.getId());
//		}
		int currentIndex = getOwner().getCBContext().getCurrentIndex();
		if (currentIndex >= endNumber) {
			getOwner().unregigsterEventHandler(this, effectGFI.getEvent());
			if(act != -1){
				((BaseFighter)getOwner()).removeActHandlers(act, this);
			}
			//累计取消BUFF的消息
			getOwner().getCBContext().addBeforeAttackRemovedBuff(skill.getSkill().getId());
//			if (logger.isDebugEnabled()) {
//				logger.debug("remove buff." + this.toString());
//			}
			
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return Objects.toStringHelper(this)
	       .add("skillId", skill.getSkill().getId())
	       .add("effectGFI", effectGFI.getId())
	       .add("startNumber", startNumber)
	       .add("endNumber", endNumber)
	       .add("idx", ((BaseFighter)getOwner()).getIdx())
	       .toString();
	}

	public int getSkillId() {
		return skill.getSkill().getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + act;
		result = prime * result + ((effectGFI == null) ? 0 : effectGFI.hashCode());
		result = prime * result + skill.getSkill().getId();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntermittentBuff other = (IntermittentBuff) obj;
		if (act != other.act)
			return false;
		if (effectGFI == null) {
			if (other.effectGFI != null)
				return false;
		} else if (!effectGFI.equals(other.effectGFI))
			return false;
		if (skill.getSkill().getId() != other.skill.getSkill().getId())
			return false;
		return true;
	}

	
	

}
