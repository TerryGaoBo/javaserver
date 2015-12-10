package game.inter;

import java.lang.reflect.Array;

public interface IManager {
	public void registerFn(String key,Object o);
	public void applyFn(String key,Array s);
	public void unRegisterFn(String key);
	public void clearAll();
}