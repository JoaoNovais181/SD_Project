package Servidor;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementação da classe contador, usado para ajudar 
 * na sincronização do {@link TrabalhadorRecompensas}
 * 
 * <p> É de notar que esta classe também necessita 
 * de implementar métodos para garantir a exclusão
 * mútua, uma vez que será partilhado entre várias
 * threads, e ainda que é singleton </p>
 *
 * @author João Carlos Fernandes Novais
 * */
public class Contador
{
	/**
	 * {@code long} usado como contador 
	 * */
	private long contador;

	/**
	 * {@code Lock} usado para garantir a exclusão mútua
	 * dentro da classe {@code Contador}, uma vez que 
	 * esta é partilhada entre vários processos
	 * */
	private ReentrantLock lock;

	/**
	 * {@code Contador} usado como instância que será
	 * acedida
	 * */
	private static Contador singleton = null;	

	/**
	 * Constroi um contador, começando-o a 0
	 * */
	private Contador()
	{
		this.contador = 0;
		this.lock = new ReentrantLock();
	}

	/**
	 * Método usado para obter a instância
	 * singleton de  {@code Contador}
	 *
	 * @return singleton do Contador
	 * */
	public static Contador getInstance()
	{
		if (Contador.singleton == null)
			Contador.singleton = new Contador();

		return Contador.singleton;
	}

	/**
	 * Incrementa o valor do contador
	 * */
	public void incrementar()
	{
		this.lock.lock();
		try
		{
			this.contador++;
		}
		finally { this.lock.unlock(); }
	}

	/**
	 * Retorna o valor do contador num dado momento
	 *
	 * @return o valor do contador num dado momento
	 * */
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
