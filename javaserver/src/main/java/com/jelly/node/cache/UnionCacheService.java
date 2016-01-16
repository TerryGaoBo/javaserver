package com.jelly.node.cache;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.union.model.UnionEntity;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.node.datastore.mapper.Union;
import com.jelly.node.datastore.mapper.UnionMapper;

public class UnionCacheService {
	private static final Logger logger = LoggerFactory.getLogger(UnionCacheService.class);

	private List<UnionEntity> allunion = Lists.newCopyOnWriteArrayList();
	/** key:帮会id,value:帮会 */
	private Map<String, UnionEntity> unions = Maps.newConcurrentMap();
	/** key:用户id，value:帮会id */
	private Map<String, String> player2union = Maps.newConcurrentMap();
	/** key:帮会名字，value:帮会id */
	private Map<String, UnionEntity> name2union = Maps.newConcurrentMap();
	private static UnionCacheService instance;
	UnionMapper mapper;
	ObjectMapper jackson;

	public static UnionCacheService getInstance() {
		if (instance == null) {
			synchronized (UnionCacheService.class) {
				if (instance == null) {
					instance = new UnionCacheService();

				}
			}

		}
		return instance;
	}

	public void init() {
		long l = System.currentTimeMillis();
		final List<Union> loadAllRoleEntity = mapper.loadAll();
		//logger.info(SBUtil.concat("帮会总数 ：", loadAllRoleEntity.size() + "", "耗时：", (System.currentTimeMillis() - l) + "ms..."));
		for (Union naru : loadAllRoleEntity) {
			byte[] val = naru.getVal();
			try {
				UnionEntity unionEntity = jackson.readValue(val, UnionEntity.class);
				if (unionEntity.isDeleted()) {
					continue;
				}
				allunion.add(unionEntity);
				unions.put(unionEntity.guid, unionEntity);
				name2union.put(unionEntity.getName(), unionEntity);
				// 更改pet
				Set<String> members = unionEntity.getMembers();
				for (String member : members) {
					player2union.put(member, unionEntity.guid);
				}
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addUnionEntity(UnionEntity unionEntity) {
		allunion.add(unionEntity);
		unions.put(unionEntity.guid, unionEntity);
		name2union.put(unionEntity.getName(), unionEntity);
	}

	public synchronized void removeUnionEntity(UnionEntity unionEntity) {
		unions.remove(unionEntity.guid);
		name2union.remove(unionEntity.getName());
	}

	/**
	 * 是否帮会成员
	 * 
	 * @param guid
	 * @return
	 */
	public boolean isUnionMember(String guid) {
		return player2union.containsKey(guid);
	}

	public Map<String, UnionEntity> getUnions() {
		return unions;
	}

	public Map<String, String> getPlayer2union() {
		return player2union;
	}

	public UnionEntity getUnionEntityByName(String name) {
		return this.name2union.get(name);
	}

	public boolean containEntityByName(String name) {
		return this.name2union.containsKey(name);
	}

	public void removeName(String name) {
		this.name2union.remove(name);
	}

	public List<UnionEntity> getAllunion() {
		return allunion;
	}

	public UnionMapper getMapper() {
		return mapper;
	}

	public void setMapper(UnionMapper mapper) {
		this.mapper = mapper;
	}

	public ObjectMapper getJackson() {
		return jackson;
	}

	public void setJackson(ObjectMapper jackson) {
		this.jackson = jackson;
	}
}
