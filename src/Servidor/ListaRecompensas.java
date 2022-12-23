package Servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implementação de uma classe que guarda uma lista de objetos 
 * do tipo <code>Recompensa</code>, no contexto de uma aplicação de
 * Sistemas Distribuídos.
 *
 * <p> Nesta classe é garantido o princípio da exclusão mútua,
 * atingindo isso através do uso de um <em>ReentrantReadWriteLock</em>
 * em todos os métodos que necessitem de ler/escrever no parâmetro
 * <code>recompensas</code>
 * 
 * @author João Carlos Fernandes Novais a96626
 * */
public class ListaRecompensas
{
	/**
	 * Variável de instância do tipo <code>Lock</code> usada para aplicar
	 * os mecanismos de sincronização necessários para garantir a 
	 * exclusão mútua
	 * */
	private ReentrantReadWriteLock lock;
	
	/**
	 * Varíavel de instância do tipo <code>List&lt;Recompensa&gt;</code> usada
	 * para armazenar todas as recompensas que estejam ativas na aplicação
	 * */
	private List<Recompensa> recompensas;

	/**
	 * Constrói uma lista de recompensas vazia e inicializa o seu
	 * <code>Lock</code>.
	 * */
	public ListaRecompensas()
	{
		this.lock = new ReentrantReadWriteLock();
		this.recompensas = new ArrayList<>();
	}

	/**
	 * Adiciona uma recompensa à lista de recompensas, caso não exista
	 * nenhuma recompensa já armazenada que contenha:
	 * 
	 * <ul>
	 * <li> A mesma coordenada de origem </li>
	 * <li> A mesma coordenada de destino </li>
	 * </ul>
	 *
	 * <p> Esta estratégia é usada para impedir que hajam várias recompensas
	 * com a mesma coordenada de origem ou destino </p>
	 *
	 * @param  r  <code>Recompensa</code> a adicionar à lista de recompensas
	 *
	 * @return <code>True</code> caso seja possivel adicionar a recompensa à lista
	 * */
	public boolean addRecompensa(Recompensa r)
	{
		boolean possivel = true;
		Coord origem = r.getOrigem(), destino = r.getDestino();
		this.lock.writeLock().lock();
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

	/**
	 * Remove uma recompensa da lista de recompensas, tirando partido do
	 * método presente na interface <code>List</code> para remover a primeira 
	 * ocorrência de um objeto em alguma instância de uma classe que a instancie,
	 * e, como neste caso só podemos ter este objeto uma única vez na lista (devido
	 * à maneira que os mesmos são adicionados), irá remover a única ocorrência
	 * do objeto
	 *
	 * @param  r  Objeto do tipo <code>Recompensa</code> a remover da lista de recompensas
	 *
	 * @return resultado do método <code>remove</code> da classe {@link ArrayList} instanciada
	 * na variável <code>recompensas</code> tomando <code>r</code> como argumento
	 * */
	public boolean removeRecompensa(Recompensa r)
	{
		this.lock.writeLock().lock();
		try
		{
			return this.recompensas.remove(r);
		}
		finally { this.lock.writeLock().unlock(); }
	}

	public boolean elegivel (Coord inicio, Coord fim)
	{
		this.lock.readLock().lock();
		try
		{
			for (Recompensa r : this.recompensas)
				if (r.elegivel(inicio, fim))
					return true;
			return false;
		}
		finally { this.lock.readLock().unlock(); }
	}


	public List<Recompensa> getListaRecompensas()
	{
		return new ArrayList<>(this.recompensas);
	}

	/**
	 * Retorna uma representação textual da classe, na qual se vê, equivalente
	 * à representação textual do objeto onde guarda as recompensas
	 *
	 * @return Representação textual da classe
	 * */
	@Override
	public String toString()
	{
		this.lock.readLock().lock();
		try
		{
			return this.recompensas.toString();
		}
		finally { this.lock.readLock().unlock(); }
	}
}
