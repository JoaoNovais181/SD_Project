package Servidor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Classe singleton utilizada para armazenar os dados de utilziadores e manter
 * a exclusão mútua no acesso aos mesmo
 *
 * @author João Carlos Fernandes Novais a96626
 * @author Beatriz Ribeiro Monteiro
 * @author João Pedro Machado Ribeiro
 * @author Telmo José Pereira Maciel
 * */
public class ListaUtilizadores implements Map<String,Utilizador>
{
	/**
	 * {@link Map} que liga um {@link Utilziador} ao seu login
	 * */
	private Map<String, Utilizador> utilizadores;
	/**
	 * {@link ReentrantReadWriteLock} utilizado para manter a exclusão mútua
	 * */
	private ReentrantReadWriteLock lock;
	/**
	 * Instância singleton de {@code ListaUtilizadores}
	 * */
	private static ListaUtilizadores singleton = null;

	/**
	 * Construtor da classe
	 * */
	private ListaUtilizadores()
	{
		this.utilizadores = new HashMap<>();
		this.lock = new ReentrantReadWriteLock();
	}

	/**
	 * Método estático utilizado para obter a instância ativa da classe
	 *
	 * @return instância ativa da classe
	 * */
	public static ListaUtilizadores getInstance()
	{
		if (ListaUtilizadores.singleton == null)
			ListaUtilizadores.singleton = new ListaUtilizadores();

		return ListaUtilizadores.singleton;
	}

	@Override
	public void clear()
	{		
		this.lock.writeLock().lock();
		try
		{
			this.utilizadores.clear();
		}
		finally { this.lock.writeLock().unlock(); }
	}

	@Override
	public boolean containsKey(Object key)
	{
		this.lock.readLock().lock();
		try
		{
			return this.utilizadores.containsKey(key);
		}
		finally { this.lock.readLock().unlock(); }
	}

	@Override
	public boolean containsValue(Object value)
	{
		this.lock.readLock().lock();
		try
		{
			return this.utilizadores.containsValue(value);
		}
		finally { this.lock.readLock().unlock(); }
	}

	@Override
	public Set<Map.Entry<String,Utilizador>> entrySet()
	{
		this.lock.readLock().lock();
		try
		{
			return this.utilizadores.entrySet();
		}
		finally { this.lock.readLock().unlock(); }
	}

	@Override
	public Utilizador get(Object key)
	{
		this.lock.readLock().lock();
		try
		{
			return this.utilizadores.get(key);
		}
		finally { this.lock.readLock().unlock(); }
	}

	@Override
	public boolean isEmpty()
	{
		this.lock.readLock().lock();
		try
		{
			return this.utilizadores.isEmpty();
		}
		finally { this.lock.readLock().unlock(); }
	}

	@Override
	public Set<String> keySet()
	{
		this.lock.readLock().lock();
		try
		{
			return this.utilizadores.keySet();
		}
		finally { this.lock.readLock().unlock(); }
	}

	@Override
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

	@Override
	public Utilizador remove(Object key)
	{
		this.lock.writeLock().lock();
		try
		{
			return this.utilizadores.remove(key);
		}
		finally { this.lock.writeLock().unlock(); }
	}

	@Override
	public int size()
	{
		this.lock.readLock().lock();
		try
		{
			return this.utilizadores.size();
		}
		finally { this.lock.readLock().unlock(); }
	}


	@Override
	public Collection<Utilizador> values()
	{
		this.lock.readLock().lock();
		try
		{
			return this.utilizadores.values();
		}
		finally { this.lock.readLock().unlock(); }
	}

}
