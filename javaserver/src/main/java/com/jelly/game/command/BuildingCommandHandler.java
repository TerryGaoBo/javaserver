package com.jelly.game.command;

import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.app.impl.DefaultPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.config.BuildingType;
import com.fasterxml.jackson.databind.JsonNode;
import com.jelly.player.PlayerBuilding;

public class BuildingCommandHandler extends SubJsonCommandHandler {

	private final PlayerSession playerSession;
	private final DefaultPlayer player;
	private static final Logger LOG = LoggerFactory.getLogger(BuildingCommandHandler.class);

	private final PlayerBuilding building;

	public BuildingCommandHandler(Session session) {
		this.playerSession = (PlayerSession) session;
		this.player = (DefaultPlayer) this.playerSession.getPlayer();
		this.building = player.getBuilding();
		addHandler(closeBuild);
//		addHandler(clearCd);
		addHandler(upBuild);
		addHandler(produceBuild);
//		addHandler(buyValue);
	}


	/**
	 * 关闭建筑
	 */
	JsonCommandHandler closeBuild = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "closeBuild";
		}

		@Override
		public void run(JsonNode object) {
			int id = object.get("id").asInt(-1);
			BuildingType buildType = BuildingType.getBuildType(id);
			buildType.close(player);
		}
	};

//	/**
//	 * 清理CD
//	 */
//	JsonCommandHandler clearCd = new JsonCommandHandler() {
//		@Override
//		public String getCommand() {
//			return "clearCd";
//		}
//
//		@Override
//		public void run(JsonNode object) {
//			int id = object.get("id").asInt(-1);
//			BuildingType buildType = BuildingType.getBuildType(id);
//			buildType.clearCd(player);
//		}
//	};
//	/**
//	 * 购买次数
//	 */
//	JsonCommandHandler buyValue = new JsonCommandHandler() {
//		@Override
//		public String getCommand() {
//			return "buyValue";
//		}
//		
//		@Override
//		public void run(JsonNode object) {
//			int id = object.get("id").asInt(-1);
//			BuildingType buildType = BuildingType.getBuildType(id);
//			buildType.buyValue(player);
//		}
//	};

	/**
	 * 建筑产出
	 */
	JsonCommandHandler produceBuild = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "produceBuild";
		}

		@Override
		public void run(JsonNode object) {
			int id = object.get("id").asInt(-1);
			JsonNode param = object.get("param");
			building.produce(player, id, param);
		}
	};

	/**
	 * 打开建筑
	 */
	JsonCommandHandler upBuild = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "upBuild";
		}

		@Override
		public void run(JsonNode object) {
			int id = object.get("id").asInt(-1);
			building.upLevel(id, player);
		}
	};
}
