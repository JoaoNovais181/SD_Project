package Servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ListaRecompensas
{
	private ReentrantReadWriteLock lock;
	private List<Recompensa> recompensas;

	public ListaRecompensas()
	{
		this.lock = new ReentrantReadWriteLock(true);
		this.recompensas = new ArrayList<>();
	}

	public boolean addRecompensa(Recompensa r)
	{
		boolean possivel = true;
		this.lock.writeLock().lock();
		Coord origem = r.getOrigem(), destino = r.getDestino();
		try
		{
			for (Recompensa recompensa : this.recompensas)
				if (recompensa.coordsIguais(origem, destino))
					possivel = false;

			if (possivel)
				this.recompensas.add(r);
			return possivel;
		}
		finally { this.lock.writeLock().unlock(); }
	}

	public boolean removeRecompensa(Recompensa r)
	{
		this.lock.writeLock().lock();
		try
		{
			return this.recompensas.remove(r);
		}
		finally { this.lock.writeLock().unlock(); }
	}

	@Override
	public String toString()
	{
		return this.recompensas.toString();
	}
}
