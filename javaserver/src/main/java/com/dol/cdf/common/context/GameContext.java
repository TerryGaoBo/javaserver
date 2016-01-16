package com.dol.cdf.common.context;

import io.nadron.app.Player;

import com.dol.cdf.common.ObjectWithDynamicAttributes;
import com.dol.cdf.common.gamefunction.parameter.IGameFunctionParameter;
import com.jelly.combat.context.CBContext;
import com.jelly.hero.BaseSkill;
import com.jelly.player.IFighter;

public class GameContext extends ObjectWithDynamicAttributes{

	private static final String CONTEXT_PLAYER = "player";
	
	private static final String CONTEXT_PARAM_X = "x";
	
	private static final String CONTEXT_PARAM_E = "e";
	
	private static final String CONTEXT_PARAM_I = "i";
	
	private static final String CONTEXT_PARAM_S = "s";
	
	private static final String CONTEXT_PARAM_R = "r";
	
	private static final String CONTEXT_ITEM_USE_PARAM = "itemUse";
	
	private static final String CONTEXT_GAME_FUNCTION_PARAM = "gameFunction";
	
	private static final String CONTEXT_CB_CONTEXT_PARAM = "cbcontext";
	
	private static final String CONTEXT_BUFF_OWNER = "buffOwner";

	public GameContext setR(int r){
		setAttribute(GameContext.CONTEXT_PARAM_R, r);
		return this;
	}
	
	public int getR(){
		return  (Integer)getAttribute(GameContext.CONTEXT_PARAM_R);
	}
	
	public GameContext setItemUseParam(String param){
		setAttribute(GameContext.CONTEXT_ITEM_USE_PARAM, param);
		return this;
	}
	
	public String getItemUseParam() {
		return  (String)getAttribute(GameContext.CONTEXT_ITEM_USE_PARAM);
	}
	
	

	public GameContext setS(BaseSkill s){
		setAttribute(GameContext.CONTEXT_PARAM_S, s);
		return this;
	}
	
	public Player getPlayer(){
		return  (Player)getAttribute(GameContext.CONTEXT_PLAYER);
	}
	
	public GameContext setPlayer(Player player){
		setAttribute(GameContext.CONTEXT_PLAYER, player);
		return this;
	}
	
	public BaseSkill getS(){
		return  (BaseSkill)getAttribute(GameContext.CONTEXT_PARAM_S);
	}
	public GameContext setI(int i){
		setAttribute(GameContext.CONTEXT_PARAM_I, i);
		return this;
	}
	
	public int getI(){
		return  (Integer)getAttribute(GameContext.CONTEXT_PARAM_I);
	}
	
	public GameContext setX(int x){
		setAttribute(GameContext.CONTEXT_PARAM_X, x);
		return this;
	}
	
	public int getX(){
		return  (Integer)getAttribute(GameContext.CONTEXT_PARAM_X);
	}
	
	public GameContext setE(int e){
		setAttribute(GameContext.CONTEXT_PARAM_E, e);
		return this;
	}
	
	public int getE(){
		return  (Integer)getAttribute(GameContext.CONTEXT_PARAM_E);
	}

	public GameContext setGameFunctionParam(IGameFunctionParameter param) {
		setAttribute(GameContext.CONTEXT_GAME_FUNCTION_PARAM, param);
		return this;
	}

	public IGameFunctionParameter getGameFunctionParam() {
		return (IGameFunctionParameter) getAttribute(GameContext.CONTEXT_GAME_FUNCTION_PARAM);
	}
	
	public GameContext setCBContextParam(CBContext param) {
		setAttribute(GameContext.CONTEXT_CB_CONTEXT_PARAM, param);
		return this;
	}
	
	public CBContext getCBContextParam() {
		return (CBContext) getAttribute(GameContext.CONTEXT_CB_CONTEXT_PARAM);
	}
	
	public GameContext setBuffOwner(IFighter param) {
		setAttribute(GameContext.CONTEXT_BUFF_OWNER, param);
		return this;
	}
	
	public IFighter getBuffOwner() {
		return (IFighter) getAttribute(GameContext.CONTEXT_BUFF_OWNER);
	}

}
