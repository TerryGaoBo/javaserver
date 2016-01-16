package com.jelly.game.command;

import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.app.impl.DefaultPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.bean.Item;
import com.dol.cdf.common.bean.Skill;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.log.LogConst;
import com.fasterxml.jackson.databind.JsonNode;
import com.jelly.hero.Hero;
import com.jelly.hero.PlayerHeros;
import com.jelly.player.GradeType;

public class SkillCommandHandler extends SubJsonCommandHandler {

	private final PlayerSession playerSession;
	private final DefaultPlayer player;
	private final PlayerHeros heros;
	private static final Logger LOG = LoggerFactory.getLogger(SkillCommandHandler.class);

	public SkillCommandHandler(Session session) {
		this.playerSession = (PlayerSession) session;
		this.player = (DefaultPlayer) this.playerSession.getPlayer();
		this.heros = player.getHeros();
		addHandler(forgetSkill);
		addHandler(studySkill);
		addHandler(replaceSkill);
		addHandler(upSkill);
		addHandler(giveupSkill);
		
	}

	/**
	 * 遗忘技能
	 */
	JsonCommandHandler giveupSkill = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "giveupSkill";
		}

		@Override
		public void run(JsonNode object) {
			player.getHeros().giveUpSkill(player);
		}
	};
	/**
	 * 遗忘技能
	 */
	JsonCommandHandler upSkill = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "upSkill";
		}
		
		@Override
		public void run(JsonNode object) {
			int heroId = object.get("hid").asInt(-1);
			int type = object.get("type").asInt(-1);
			int idx = object.get("idx").asInt(-1);
			heros.upHeroSkill(player,heroId, type, idx);
		}
	};
	
	/**
	 * 遗忘技能
	 */
	JsonCommandHandler forgetSkill = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "forgetSkill";
		}

		@Override
		public void run(JsonNode object) {
			int heroId = object.get("hid").asInt(-1);
			int type = object.get("type").asInt(-1);
			int idx = object.get("idx").asInt(-1);
			heros.removeHeroSkill(heroId, type, idx);
		}
	};

	/**
	 * 学习技能
	 * 
	 */
	 public static final int SKILL_C = 5065;
	 public static final int SKILL_B = 5061;
	 public static 	final int SKILL_A = 5062;
	 public static 	final int SKILL_S = 5063;
	 public static 	final int SKILL_SS = 5064;
	JsonCommandHandler studySkill = new JsonCommandHandler() {
	
		@Override
		public String getCommand() {
			return "studySkill";
		}

		@Override
		public void run(JsonNode object) {
			int heroId = object.get("hid").asInt(-1);
			int type = object.get("type").asInt(-1);
			int studyType = object.get("studyType").asInt(1);
			int idx = object.get("idx").asInt(0);
			int itemSkillId = -1;
			int itemSkillGrade = -1;
			if(object.has("itemId")) {
				int itemId = object.get("itemId").asInt(-1);
				Item skillItem = AllGameConfig.getInstance().items.getItemById(itemId);
				if(skillItem == null) return;
				Integer needCount =  skillItem.getSnum();
				if(needCount == null) {
					LOG.error("没有配置技能道具的数量 itemId= {}",itemId);
					return;
				}
				VariousItemEntry skillItemEntry = new VariousItemEntry(itemId, needCount);
				int code = VariousItemUtil.doBonus(player, skillItemEntry, LogConst.STUDY_SKILL, false);
				if(code != MessageCode.OK) {
					player.sendMiddleMessage(code);
					return;
				}
				//固定技能道具
				if(skillItem.getCategory() == 13) {
					itemSkillId = skillItem.getSid();
					itemSkillGrade = skillItem.getQuality();
				}else if(skillItem.getCategory() == 12) {
					int grade = -1;
					Hero hero = heros.getHero(heroId);
					switch (itemId) {
					case SKILL_C:
						grade = GradeType.C.getId();
						break;
					case SKILL_B:
						grade = GradeType.B.getId();
						break;
					case SKILL_A:
						grade = GradeType.A.getId();
						break;
					case SKILL_S:
						grade = GradeType.S.getId();
						break;
					case SKILL_SS:
						grade = GradeType.SS.getId();
						break;
					default:
						LOG.error("没有改技能道具{}",itemId);
						return;
					}
					Skill rndSkill = AllGameConfig.getInstance().skills.getRndSkill(grade, Hero.SKILL_TYPE_NINJITSU, hero.getSkillBytype(Hero.SKILL_TYPE_NINJITSU));
					itemSkillId = rndSkill.getId();
					itemSkillGrade = rndSkill.getQuality();
				}
			}
			heros.studyHeroSkill(player, heroId, type, studyType,idx,itemSkillId,itemSkillGrade);
		}
	};
	
	/**
	 * 替换技能
	 */
	JsonCommandHandler replaceSkill = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "replaceSkill";
		}

		@Override
		public void run(JsonNode object) {
			heros.replaceSkill(player,object.get("idx").asInt());
		}
	};

}
