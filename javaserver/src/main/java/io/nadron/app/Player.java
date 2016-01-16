package io.nadron.app;

import io.nadron.app.impl.OperResultType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jelly.activity.PlayerActivity;
import com.jelly.hero.PlayerHeros;
import com.jelly.hero.PlayerPractise;
import com.jelly.hero.PlayerRecruit;
import com.jelly.hero.PlayerWars;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.ObjectCacheService;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.player.PlayerAdventure;
import com.jelly.player.PlayerArena;
import com.jelly.player.PlayerBuilding;
import com.jelly.player.PlayerExam;
import com.jelly.player.PlayerFriends;
import com.jelly.player.PlayerInventory;
import com.jelly.player.PlayerMail;
import com.jelly.player.PlayerProperty;
import com.jelly.player.PlayerShop;
import com.jelly.player.PlayerTeam;
import com.jelly.player.shop.NewPlayerShop;
import com.jelly.quest.PlayerTask;

/**
 * A Player is a human or machine that is playing single or multiple games. This
 * interface aims to abstract the basic properties of such a domain object.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface Player {

	/**
	 * A unique key representing a gamer. This could be an email id or something
	 * unique.
	 * 
	 * @return Returns the unique key for the gamer.
	 */
	public String getId();

	/**
	 * A unique key representing a gamer. This could be an email id or something
	 * unique.
	 * 
	 */
	public void setId(Object uniqueKey);

	/**
	 * Add a session to a player. This session signifies the players session to
	 * a game.
	 * 
	 * @param session
	 *            The session to add.
	 * @return true if add was successful, false if not.
	 */
	public boolean addSession(PlayerSession session);

	/**
	 * Remove the players session to a game.
	 * 
	 * @param session
	 *            The session to remove.
	 * @return true if remove is successful, false other wise.
	 */
	public boolean removeSession(PlayerSession session);

	/**
	 * When a player logs out, this method can be called. It can also be called
	 * by the remove session method when it finds that there are no more
	 * sessions for this player.
	 * 
	 * @param playerSession
	 *            The session which is to be logged out.
	 */
	public void logout(PlayerSession playerSession);
	
	public PlayerSession getPlayerSession();

	public String getName();
	
	public PlayerProperty getProperty();

	public void setProperty(PlayerProperty property);

	public PlayerHeros getHeros();

	public void setHeros(PlayerHeros heros);

	public PlayerAdventure getAdventure();

	public void setAdventure(PlayerAdventure adventure);

	public void sendChangedMessage();

	public void sendMessage(ObjectNode object);
	
	public void sendMessage(String key, JsonNode node);

	public PlayerMail getMail();

	public void setMail(PlayerMail mail);

	public PlayerInventory getInvenotry();

	public void setInvenotry(PlayerInventory invenotry);

	public PlayerFriends getFriends();

	public void setFriends(PlayerFriends friends);

	public PlayerTask getTask();

	public void setTask(PlayerTask task);
	
	public void setShop(PlayerShop shop);
	
	public void setTeam(PlayerTeam team);

	public RoleEntity getRole();

	public void setRole(RoleEntity role);

	public void sendMiddleMessage(int id, String... strs);

	public void broadcastChatMessage(int id, String... strs);
	
	public PlayerBuilding getBuilding();

	public void setBuilding(PlayerBuilding building);

	public PlayerArena getArena();

	public void setArena(PlayerArena arena);

	public PlayerExam getExam();

	public PlayerPractise getPractise();
	
	public PlayerRecruit getRecruit();
	
	public PlayerActivity getActivity();
	
	public PlayerShop getShop();
	
	public NewPlayerShop getNewShop();
	
	public PlayerWars getWars();
	
	public PlayerTeam getTeam();
	
	public void sendResult(OperResultType type);
	
	public void sendResult(OperResultType type, int res);

	public void sendResult(OperResultType type, int res, Object param);
	public void sendResult(OperResultType type,Object param);
	
	public AllPlayersCache getAllPlayersCache() ;
	
	public ObjectCacheService getObjectCache() ;
	
	public void sendPlayerWholeJSON();
	
	public void logoff();

	public void setClientInfo(String clientInfo);
	
	public String getClientInfo();

}