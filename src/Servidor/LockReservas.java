package Servidor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * Classe singleton que implementa a interface {@link Lock} no contexto
 * de reservas
 *
 * @author João Carlos Fernandes Novais a96626
 * @author Beatriz Ribeiro Monteiro
 * @author João Pedro Machado Ribeiro
 * @author Telmo José Pereira Maciel
 * */
public class LockReservas implements Lock
{
	/**
	 * {@link Lock} utilizado para manter a exclusão mútua
	 * */
	private ReentrantLock lock;
	/**
	 * {@link Condition} utilizada para notificar/esperar uma/por uma ação
	 * */
	private Condition acaoFeita;
	/**
	 * Instância singleton da classe
	 **/
	private static LockReservas singleton = null;

	/**
	 * Construtor da classe
	 * */
	private LockReservas()
	{
		this.lock = new ReentrantLock();
		this.acaoFeita = this.newCondition();
	}

	/**
	 * Método utilizado para obter a instância singleton
	 *
	 * @return instância singleton da classe {@link LockReservas}
	 * */
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

	/**
	 * Método utilizado para esperar por uma ação
	 * */
	public void esperaAcao() throws InterruptedException
	{
		this.acaoFeita.await();
	}

	/**
	 * Método utilizado para sinalizar que ocorreu uma ação
	 * */
	public void sinalizaAcao()
	{
		this.acaoFeita.signalAll();
	}
}
