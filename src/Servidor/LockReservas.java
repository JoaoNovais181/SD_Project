package Servidor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;


public class LockReservas implements Lock
{
	private ReentrantLock lock;
	private Condition acaoFeita;
	private static LockReservas singleton = null;

	private LockReservas()
	{
		this.lock = new ReentrantLock();
		this.acaoFeita = this.newCondition();
	}

	public static LockReservas getInstance()
	{
		if (LockReservas.singleton == null)
			LockReservas.singleton = new LockReservas();

		return LockReservas.singleton;
	}

	public boolean tryLock()
	{
		return this.lock.tryLock();
	}

	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
	{
		return this.lock.tryLock(time, unit);
	}

	public void lock()
	{
		this.lock.lock();
	}

	public void unlock()
	{
		this.lock.unlock();
	}

	public void lockInterruptibly() throws InterruptedException
	{
		this.lock.lockInterruptibly();
	}

	public Condition newCondition()
	{
		return this.lock.newCondition();
	}

	public void esperaAcao() throws InterruptedException
	{
		this.acaoFeita.await();
	}

	public void sinalizaAcao()
	{
		this.acaoFeita.signalAll();
	}
}
