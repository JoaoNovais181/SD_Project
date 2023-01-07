package Servidor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ListaUtilizadores implements Map<String,Utilizador>
{
	private Map<String, Utilizador> utilizadores;
	private ReentrantReadWriteLock lock;

	public ListaUtilizadores()
	{
		this.utilizadores = new HashMap<>();
		this.lock = new ReentrantReadWriteLock();
	}

	public int size()
	{
		this.lock.readLock().lock();
		try
		{
			return this.utilizadores.size();
		}
		finally { this.lock.readLock().unlock(); }
	}

	public Utilizador remove(Object key)
	{
		this.lock.writeLock().lock();
		try
		{
			return this.utilizadores.remove(key);
		}
		finally { this.lock.writeLock().unlock(); }
	}

	public Utilizador put(String key, Utilizador value)
	{
		this.lock.writeLock().lock();
		try
		{
			return this.utilizadores.put(key, value);
		}
		finally { this.lock.writeLock().unlock(); }
	}

	@Override
	public void putAll(Map<? extends String, ? extends Utilizador> u)
	{
		this.lock.writeLock().lock();
		try
		{
			this.utilizadores.putAll(u);
	
		}
		finally { this.lock.writeLock().unlock(); }
	}
}
