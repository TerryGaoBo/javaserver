package com.jelly.team;

import io.nadron.context.AppContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.Util.ValueComparator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.datastore.mapper.TeamEntity;
import com.jelly.node.datastore.mapper.TeamMapper;

public class TeamMapperWrapper {

	private TeamMapper mapper;
	
	private Map<String, Team> name2TeamMap;
	
	private Map<String,Long> teamCounter;
	
	private static Logger logger = LoggerFactory.getLogger(AllPlayersCache.class);
	
	public TeamMapperWrapper() {
		mapper = AppContext.getBean(TeamMapper.class);
		name2TeamMap = new ConcurrentHashMap<String, Team>();
		teamCounter = Maps.newConcurrentMap();
	}
	
	public List<Team> loadAllTeams() throws JsonParseException, JsonMappingException, IOException {
		List<Team> teams = Lists.newArrayList();
		for (TeamEntity ent : mapper.loadAll()) {
			Team t = DynamicJsonProperty.jackson.readValue(ent.getVal(), Team.class);
			if(ent.getName().indexOf("'")>-1){
				System.out.println("dbname="+ent.getName()+" teamname="+t.getName());
				logger.info("dbname="+ent.getName()+" teamname="+t.getName());
			}
			boolean k = false;
			if(!ent.getName().equals(t.getName())){
				logger.info("team name is not sanme dbname={},-team name={}",ent.getName(),t.getName());
			}
			
			for (Team team : teams) {
				if(team.getName().equals(t.getName())){
					logger.info("the team name is already exists ! dbname={},--team name={}",ent.getName(),t.getName());
					k = true;
				}
			}
			if(!k){
				teams.add(t);
			}
		}
		return teams;
	}
	
	public void deleteTeams(String ...names) {
		long l = System.currentTimeMillis();
		for (String name : names) {
			if (name2TeamMap.get(name) != null) {
				name2TeamMap.remove(name);
			}
			mapper.delete(name);
		}
		logger.info("deleteTeams elapse " + (System.currentTimeMillis() - l) + "(ms)");
	}
	
	public void insertTeams(Team ...teams) throws JsonProcessingException {
		long l = System.currentTimeMillis();
		for (Team team : teams) {
			mapper.insert(team.getName(), DynamicJsonProperty.jackson.writeValueAsBytes(team));;
		}
		logger.info("insertTeams elapse " + (System.currentTimeMillis() - l) + "(ms)");
	}
	
	public void markUpdate(Team ...teams) {
		for (Team team : teams) {
			if (!name2TeamMap.containsKey(team.getName())) {
				name2TeamMap.put(team.getName(), team);
				teamCounter.put(team.getName(), 1L);
			}else {
				Long counter = teamCounter.get(team.getName());
				Long newCounter = counter == null ? 1L: (counter +1L);
				teamCounter.put(team.getName(), newCounter);
			}
		}
	}
	
	public void update(boolean isShutdown) throws JsonProcessingException {
		int max = isShutdown ? teamCounter.size() : 50; // 每次更新最多处理的数量
		logger.info((isShutdown ? "Shutdown server save all teams " : "Timer save teams ") + max);
		ValueComparator bvc = new ValueComparator(teamCounter);
		TreeMap<String, Long> sortedMap = new TreeMap<String, Long>(bvc);
		sortedMap.putAll(teamCounter);
		List<String> removedTeams = Lists.newArrayList();
		for (String name : sortedMap.keySet()) {
			try {
				Team team = name2TeamMap.get(name);
				if(team != null){
					mapper.update(name, DynamicJsonProperty.jackson.writeValueAsBytes(team));
					removedTeams.add(name);
				}
				// 此处的优化处理是为了避免每次更新操作占用太多时间
				if (--max <= 0) {
					break;
				}
			} catch (Exception e) {
				logger.error("update team error: {}",e);
			}
		}
		for (String name : removedTeams) {
			name2TeamMap.remove(name);
			teamCounter.remove(name);
		}
	}
	
	public void updateAllTeams() throws JsonProcessingException{
		Map<String, Team> teams = TeamManager.getSingleton().getAllTeams();
		logger.info("==save all team !! --count={}",teams.size());
		for(Team t : teams.values()){
			try{
				if(t !=null){
					String tName=t.getName();
					logger.info("teamname={}",t.getName());
					mapper.update(tName, DynamicJsonProperty.jackson.writeValueAsBytes(t));
				}
			}catch(Exception e){
				logger.error("shut down update team error: {}",e);
			}
		}
		
		Map<String,Team> deleteTeams = TeamManager.getSingleton().getDeleteAllTeams();
		for(Team t : deleteTeams.values()){
			try{
				if(t !=null){
					mapper.update(t.getName(), DynamicJsonProperty.jackson.writeValueAsBytes(t));
				}
			}catch(Exception e){
				logger.error("shut down update delete team error: {}",e);
			}
		}
	}
}
