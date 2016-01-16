package io.nadron.service;

import io.nadron.app.Session;

public interface SessionRegistryService<T>
{
	public Session getSession(T key);
	
	public boolean putSession(T key, Session session);
	
	public T getUserKey(T id);
	public boolean putUser(T id, T key);
	
	public T removeUser(T id);
	
	public Session removeSession(T key);
	// Add a session type object also to get udp/tcp/any
}
