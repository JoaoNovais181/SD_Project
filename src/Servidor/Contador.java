package Servidor;

import java.util.concurrent.locks.ReentrantLock;

public class Contador
{
	private long contador;
	private ReentrantLock lock;

	public Contador()
	{
		this.contador = 0;
		this.lock = new ReentrantLock();
	}

	public void incrementar()
	{
		this.lock.lock();
		try
		{
			this.contador++;
		}
		finally { this.lock.unlock(); }
	}

	public long getContador()
	{
		this.lock.lock();
		try
		{
			return this.contador;
		}
		finally { this.lock.unlock(); }
	}

}
