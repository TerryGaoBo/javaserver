package com.jelly.node.cache;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dol.cdf.common.CaseInsensitiveConcurrentHashMap;
import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.SBUtil;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SortedSetMultimap;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.node.datastore.mapper.RoleMapper;
import com.jelly.player.PlayerProperty;


/**
 * 
 * 所有玩家基本信息“RoleEntity”的缓存，调用此类的方法比较慢，谨慎使用
 * 
 */
public class AllPlayersCache {

	private static final Logger logger = LoggerFactory.getLogger(AllPlayersCache.class);

	private ConcurrentHashMap<String, RoleEntity> playerInfos;

	private ConcurrentHashMap<String, String> name2ids;

	private CaseInsensitiveConcurrentHashMap allNames;

	private SortedSetMultimap<Integer, RoleEntity> levelSortedMap;

	@Autowired
	private RoleMapper mapper;
	//全服最高等级
	private int serverMaxLevel;

	public void init() {
		long l = System.currentTimeMillis();
		playerInfos = new ConcurrentHashMap<String, RoleEntity>();
		name2ids = new ConcurrentHashMap<String, String>();
		allNames = new CaseInsensitiveConcurrentHashMap();
		Map<Integer, Collection<RoleEntity>> map = Maps.newHashMap();
		CountingSupplier<TreeSet<RoleEntity>> factory = new SortedSetSupplier();
		SortedSetMultimap<Integer, RoleEntity> multimap = Multimaps.newSortedSetMultimap(map, factory);
		levelSortedMap = Multimaps.synchronizedSortedSetMultimap(multimap);
		final List<RoleEntity> loadAllRoleEntity = mapper.loadAll();
		logger.info(SBUtil.concat("all player couts ：", loadAllRoleEntity.size() + "", "cost：", (System.currentTimeMillis() - l) + "ms..."));
		for (final RoleEntity roleEntity : loadAllRoleEntity) {
			playerInfos.put(roleEntity.getGuid(), roleEntity);
			name2ids.put(roleEntity.getName(), roleEntity.getGuid());
			allNames.put(roleEntity.getName(), "");
			levelSortedMap.put(roleEntity.getLevel(), roleEntity);
		}
		if (levelSortedMap.keySet().size() > 0) {
			serverMaxLevel = Collections.max(levelSortedMap.keySet());	
		}
	}
	
	/**
	 * 合服时，计算重复的名字
	 */
	public Map<String, String> getExistedName() {
		Map<String, String> existedName = new HashMap<String, String>();
		HashSet<String> tmpNames = new HashSet<String>();
		for (RoleEntity roleEntity : playerInfos.values()) {
			if (tmpNames.contains(roleEntity.getName())) {
				logger.info("重复的名字={}，sid={}", roleEntity.getName(), roleEntity.getNet());
				existedName.put(roleEntity.getGuid(), roleEntity.getName());
				
			} else {
				tmpNames.add(roleEntity.getName());
			}
		}
		logger.info("账号总数={}，重复名字数量={}，所有名字={}", playerInfos.size(), existedName.size(), tmpNames.size());
		return existedName;
	}
	
	/**
	 * 玩家升级的时候，把等级区间的数据更改
	 * @param beforeLv
	 * @param afterLv
	 * @param roleEntity
	 */
	public void updateRoleLevel(int beforeLv, int afterLv,RoleEntity roleEntity,Player player) {
		levelSortedMap.remove(beforeLv, roleEntity);
		levelSortedMap.put(afterLv, roleEntity);
		
		setUpdateProperty(player, roleEntity);
		// TODO for test need to delete 
		updateRolePropAndSave(player, roleEntity);
	}
	
	public List<RoleEntity> getHighestRoleEntities(int count) {
		List<RoleEntity> entities = Lists.newArrayList();
		for (int i = serverMaxLevel; i >= 1; i--) {
			SortedSet<RoleEntity> reList = levelSortedMap.get(i);
			if (reList.size() < count) {
				entities.addAll(reList);
				count -= reList.size();
			}else {
				for (RoleEntity roleEntity : reList) {
					entities.add(roleEntity);
					count--;
					if (count <= 0) {
						break;
					}
				}
			}
			if (count <= 0) {
				break;
			}
		}
		return entities;
		
	}
	/**
	 * 
	 * @param role 玩家角色
	 * @param level 玩家等级
	 * @param delta 等级修正默认为0
	 * @return
	 */
	public RoleEntity getRndRoundPlayer(List<RoleEntity> exsitRoles,int level, int delta) {
		//如果尝试10次后直接返回null
		if (delta > 10) {
			return null;
		}
		//最小可随机的组队玩家为15级
		int defaultMinLv = 15;
		level = level - delta;
		int minLv = level-5 < 1 ? 1 :level-5;
		int maxLv = level+5 > serverMaxLevel ? serverMaxLevel :level+5;
		int tarLv = Rnd.get(minLv, maxLv);
		tarLv = tarLv > (defaultMinLv - delta) ? tarLv: defaultMinLv - delta;
		SortedSet<RoleEntity> roles = levelSortedMap.get(tarLv);
		if (roles.size() == 0) {
			for (int i= tarLv; i > 0; i--) {
				roles = levelSortedMap.get(i);
				if(roles.size() > 0) {
					if(roles.size() == 1 && exsitRoles.contains(roles.first())){
						continue;
					}else{
						break;
					}
				}
			}
		}
		if (roles.size() == 0) {
			for (int i= tarLv; i < 100; i++) {
				roles = levelSortedMap.get(i);
				if(roles.size() > 0) {
					if(roles.size() == 1 && exsitRoles.contains(roles.first())){
						continue;
					}else{
						break;
					}
				}
			}
		}
		if (roles.isEmpty()) {
			return getRndRoundPlayer(exsitRoles, level,++delta);
		}
		ArrayList<RoleEntity> newArrayList = Lists.newArrayList(roles);
		int i = Rnd.get(roles.size());
		//是否是同一个人
		RoleEntity tarRole = newArrayList.get(i);
		if (exsitRoles.contains(tarRole)) {
			return getRndRoundPlayer(exsitRoles, level,++delta);
		}
		
		return tarRole;
		
	}
	
	public List<RoleEntity> getRndFriends(List<String> exist, int level, int count, int diffLevel) {
		List<RoleEntity> result = new ArrayList<RoleEntity>();
		int tryTimes = 0;
		int minLv = level-diffLevel < 1 ? 1 :level-diffLevel;
		int maxLv = level+diffLevel > serverMaxLevel ? serverMaxLevel :level+diffLevel;
		while (tryTimes < 10 && result.size() < count) {
			int tarLv = Rnd.get(minLv, maxLv);
			SortedSet<RoleEntity> roles = levelSortedMap.get(tarLv);
			if (roles.size() > 0) {
				ArrayList<RoleEntity> newArrayList = Lists.newArrayList(roles);
				for (int i = 0; i< newArrayList.size(); i++) {
					RoleEntity role = newArrayList.get(Rnd.get(newArrayList.size()));
					if (!exist.contains(role.getGuid()) && !result.contains(role)) {
						result.add(role);
						if (result.size() >= count)
							break;
					}
				}
			} 
			tryTimes++;
		}
		return result;
	}
	
	public List<RoleEntity> getRndFriends(int minLv, int maxLv, int count) {
		List<RoleEntity> result = new ArrayList<RoleEntity>();
		int tryTimes = 0;
		while (tryTimes < 10 && result.size() < count) {
			int tarLv = Rnd.get(minLv, maxLv);
			SortedSet<RoleEntity> roles = levelSortedMap.get(tarLv);
			if (roles.size() > 0) {
				ArrayList<RoleEntity> newArrayList = Lists.newArrayList(roles);
				for (int i = 0; i< newArrayList.size(); i++) {
					RoleEntity role = newArrayList.get(Rnd.get(newArrayList.size()));
					result.add(role);
					if (result.size() >= count)
						break;
				}
			} 
			tryTimes++;
		}
		return result;
	}
	
	

	

	public void putPlayerInfo(final DefaultPlayer player, String name, String channel) {
		name2ids.put(name, player.guid);
		allNames.put(name, "");
		final RoleEntity role = createRoleEntity(player, name, channel);
		player.setRole(role);
		playerInfos.put(player.guid, role);
		try {
			mapper.insert(role);
		} catch (Exception e) {
			logger.error("创建帐号错误" + role.toString() + e);
		}

	}
	
	public void setPlayerTeamName(String playerID, String teamName) {
		RoleEntity role = getPlayerInfo(playerID);
		role.setTeamName(teamName);
		mapper.update(role);
	}

	public void removeName2Id(final String name) {
		if (name == null) {
			throw new NullPointerException("name is null");
		}
		name2ids.remove(name);
		allNames.remove(name);
	}

	public void updatePlayerInfo(final String guid, final RoleEntity player) {
		playerInfos.put(guid, player);
	}

	// 异步更新到数据库
	public void updatePlayerInfoToDB(final Player player) {
		// ThreadPoolManager.getInstance().excute(new Runnable() {
		// @Override
		// public void run() {
		// try{
		// final RoleEntity convert = convert(player);
		// service.updateRoleEntity(convert);
		// }
		// catch(final Exception e){
		// logger.error("更新玩家数据到mysql出错", e);
		// // 系统错误
		// }
		// }
		// });

	}

	private RoleEntity createRoleEntity(final DefaultPlayer player, String name,String channel) {
		PlayerProperty pp = player.getProperty();
		final RoleEntity roleEntity = new RoleEntity();
		roleEntity.setChannel(channel);
		roleEntity.setGuid(player.guid);
		roleEntity.setNet(Integer.parseInt(player.getProperty().getNet()));
		roleEntity.setUserId(player.getProperty().getUserId());
		roleEntity.setFirstLogin(new Timestamp(pp.getFirstOnlineTime() * 1000L));
		roleEntity.setName(name);
		roleEntity.setTeamName("");
		setUpdateProperty(player, roleEntity);
		return roleEntity;
	}

	private void setUpdateProperty(Player player, RoleEntity roleEntity) {
		int firstHeroRoleId = player.getHeros().getFirstHeroRoleId();
		roleEntity.setCharId(firstHeroRoleId);
		roleEntity.setLevel(player.getProperty().getLevel());
		roleEntity.setLastLogin(new Timestamp(player.getProperty().getLastOnlineTime() * 1000L));
		roleEntity.setVipScore(player.getProperty().getVipScore());
		//只要充值过，就不再重新赋值。防止首充重置后，变为未充值状态
		if (roleEntity.getPaid() != 1) { 
			roleEntity.setPaid(player.getProperty().getPaid());
		}
		roleEntity.setPower(player.getHeros().getPlayerPower());
		roleEntity.setHeroPow(player.getHeros().getFirstHeroPower());
		roleEntity.setGold(player.getProperty().getGold());
		roleEntity.setSilver(player.getProperty().getSilver());
		roleEntity.setCoin(player.getProperty().getCoin());
		roleEntity.setExp(player.getProperty().getExp());
		roleEntity.setExamLv(player.getExam().getMaxPass());
		roleEntity.setChapter(player.getAdventure().getMaxMainChapter());
		roleEntity.setStage(player.getAdventure().getMaxMainStage());
	}
	
	public void updatePlayerPower(Player player) {
		RoleEntity role = this.getPlayerInfo(player.getId());
		if (role != null) {
			role.setPower(player.getHeros().getPlayerPower());
		}
	}
	
	public void updateRolePropAndSave(Player player, RoleEntity roleEntity) {
		setUpdateProperty(player, roleEntity);
		mapper.update(roleEntity);
	}

	public RoleEntity getPlayerInfo(String guid) {
		if (guid == null) {
			throw new NullPointerException("guid is null");
		}
		
		return playerInfos.get(guid);
	}

	/**
	 * 通过名称查询玩家Guid
	 * 
	 * @param name
	 * @return
	 */
	public String getPlayerIdByName(final String name) {
		if (name == null) {
			throw new NullPointerException("name is null");
		}
		return name2ids.get(name);
	}

	public boolean hasThisName(String name) {
		return allNames.get(name) != null;
	}

	/**
	 * 
	 * 通过玩家Guid查询名称
	 * 
	 * @param name
	 * @return
	 */
	public String getNameByPlayerId(String guid) {
		if (guid == null) {
			throw new NullPointerException("guid is null");
		}
		RoleEntity roleEntity = playerInfos.get(guid);
		return roleEntity.getName();
	}


	/**
	 * 获得所有玩家
	 * 
	 * @return
	 */
	public Collection<RoleEntity> getAllPlayerInfo() {
		return playerInfos.values();
	}

	public RoleMapper getMapper() {
		return mapper;
	}

	public void setMapper(RoleMapper mapper) {
		this.mapper = mapper;
	}
	
	private abstract static class CountingSupplier<E> implements Supplier<E>, Serializable {
		private static final long serialVersionUID = 1L;

		abstract E getImpl();

		@Override
		public E get() {
			return getImpl();
		}
	}

	private static class SortedSetSupplier extends CountingSupplier<TreeSet<RoleEntity>> {
		@Override
		public TreeSet<RoleEntity> getImpl() {
			return new TreeSet<RoleEntity>(){
				private static final long serialVersionUID = 1L;

				@Override
				public boolean add(RoleEntity arg0) {
					boolean isAdd = super.add(arg0);
					if (isAdd && size() > 10) {
						remove(first());
					}
					return isAdd;
				}
			};
		}
		

		private static final long serialVersionUID = 0;
	}
}
