package io.nadron.service.impl;

import io.nadron.app.Session;
import io.nadron.service.SessionRegistryService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class SessionRegistry<T> implements SessionRegistryService<T>
{
	protected final Map<T, Session> sessions;
	
	protected final Map<T, T> users;
	
	public SessionRegistry()
	{
		sessions = new ConcurrentHashMap<T, Session>(1000);
		users = new ConcurrentHashMap<T, T>(1000);
	}
	
	@Override
	public Session getSession(T key)
	{
		return sessions.get(key);
	}

	@Override
	public boolean putSession(T key, Session session)
	{
		if(null == key ||  null == session)
		{
			return false;
		}
		
		if(null == sessions.put(key, session))
		{
			return true;
		}
		return false;
	}
	
	
	@Override
	public boolean putUser(T id, T key) {
		users.put(id, key);
		return false;
	}
	
	@Override
	public T getUserKey(T id) {
		return users.get(id);
	}
	
	@Override
	public T removeUser(T id) {
		return users.remove(id);
	}
	
	@Override
	public Session removeSession(Object key)
	{
		return sessions.remove(key);
	}

}
