package Servidor;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TrabalhadorRecompensas implements Runnable
{
	private ListaRecompensas recompensas;
	private Mapa mapa;
	private Contador contador;
	private ReentrantLock lock;
	private Condition esperaAcao;
	private boolean running;

	public TrabalhadorRecompensas (ListaRecompensas recompensas, Mapa mapa, Contador contador, Condition esperaAcao)
	{
		this.recompensas = recompensas;
		this.mapa = mapa;
		this.contador = contador;
		this.lock = new ReentrantLock();
		this.esperaAcao = esperaAcao;
		this.running = true;
	}

	public void run()
	{
		try
		{
			while (this.running)
			{
				long c = this.contador.getContador();

				List<Coord> poucoPopuladas = mapa.zonasPoucoPopuladas();
				if (poucoPopuladas.size() != 0)
				{
					List<Coord> muitoPopuladas = mapa.zonasMuitoPopuladas();
					
					int lastIndx = 0;

					for (Coord origem : poucoPopuladas)
					{
						Coord destino = null;
						if (muitoPopuladas.size() == 0)
							destino = Coord.randomCoord(this.mapa.getN());
						else
							destino = muitoPopuladas.get((lastIndx++)%muitoPopuladas.size());
						Recompensa recompensa = new Recompensa(origem,destino,this.mapa.getD());

						this.recompensas.addRecompensa(recompensa);
					}
				}

				while (c == contador.getContador())
				{
					this.lock.lock();
					this.esperaAcao.await();;
					this.lock.unlock();
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	public void shutdown()
	{
		this.lock.lock();
		try
		{
			this.running = false;
		}
		finally { this.lock.unlock(); }
	}
}
