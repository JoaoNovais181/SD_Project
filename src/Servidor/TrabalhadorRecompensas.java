package Servidor;

import java.util.List;
import java.util.concurrent.locks.Condition;

/**
 * Implementação de uma classe a ser usada numa thread, que será gerida por um {@link Servidor},
 * utilizada no contexto deste projeto para gerir a lista de recompensas, ou seja, verificar as zonas 
 * do {@link Mapa} que estão com pouca abundância de trotinetes, e gerar recompensas para quem
 * levar trotinetes de alguma outra zona que tenha trotinetes suficientes (no mínimo, de preferência
 * que tenha em abundância)
 *
 * <p>Nesta classe são usados princípios de exclusão mútua para garantir que enquanto esta thread
 * estiver a realizar operações sobre a {@link ListaRecompensas} nenhuma outra estará a fazer o mesmo</p>
 *
 * @author João Carlos Fernandes Novais
 * @author Beatriz Ribeiro Monteiro
 * @author João Pedro Machado Ribeiro
 * @author Telmo José Pereira Maciel
 * */
public class TrabalhadorRecompensas implements Runnable
{	
	/**
	 * Lista de recompensas onde serão armazenados todos os objetos do tipo {@link Recompensa}
	 * */
	private ListaRecompensas recompensas;

	/**
	 * Mapa onde estão as informações relativas à quantidade de trotinetes em cada zona
	 * */
	private Mapa mapa;

	/**
	 * Objeto do tipo {@link Contador} para garantir que esta {@code Thread} não está ativamente a realizar as 
	 * operações de gestão da lista de recompensas
	 * */
	private Contador contador;

	/**
	 * {@code Lock} usado para garantir a exclusão mútua no que toca às reservas
	 * */
	private LockReservas lock;

	/**
	 * Variável usada para ditar se a thread deve continuar o seu trabalho ou não
	 * */
	private boolean running;

	/**
	 * Constrói um objeto deste tipo, recebendo uma {@link ListaRecompensas}, um {@link Mapa}, um {@link Contador},
	 * {@link ReentrantLock} e {@link Condition}, e inicializando a sua variável {@code running} como true
	 * */
	public TrabalhadorRecompensas (ListaRecompensas recompensas, Mapa mapa)
	{
		this.recompensas = recompensas;
		this.mapa = mapa;
		this.contador = Contador.getInstance();
		this.lock = LockReservas.getInstance();
		this.running = true;
	}

	/**
	 * Método utilziado para adicionar recompensas ao sistema
	 *
	 * @param poucoPopuladas lista com as coordenadas de vizinhanças com poucas trotinetes
	 * @param muitoPopuladas lista com as coordenadas de vizinhanças com muitas trotinetes
	 * */
	public void adicionarRecompensas(List<Coord> poucoPopuladas, List<Coord> muitoPopuladas)
	{
		if (poucoPopuladas.size() != 0)
		{
			
			for (Coord destino : poucoPopuladas)
			{
				Coord origem = null;
				if (muitoPopuladas.size() > 0)
				{
					origem = muitoPopuladas.get(0);
					muitoPopuladas.remove(0);
				}
				else
					do
					{
						origem = Coord.randomCoord(this.mapa.getN());
					} while(poucoPopuladas.contains(origem));
				Recompensa recompensa = new Recompensa(origem,destino,this.mapa.getD());

				this.recompensas.addRecompensa(recompensa);
			}
		}
	}

	/**
	 * Método utilizado para remover recompensas do sistema
	 *
	 * @param poucoPopuladas lista com as coordenadas das vizinhanças com poucas trotinetes
	 **/
	public void removerRecompensas(List<Coord> poucoPopuladas)
	{
		List<Recompensa> lr = this.recompensas.getListaRecompensas();
	
		for (Recompensa r : lr)
			if (!poucoPopuladas.contains(r.getDestino()) || poucoPopuladas.contains(r.getOrigem()))
				this.recompensas.removeRecompensa(r);
	}

	/**
	 * Implementação do método obrigatório da interface {@link Runnable}.
	 *
	 * <p> Neste método começa-se por obter o {@code Lock}, de forma a que nenhuma reserva seja feita enquanto 
	 * se altera a lista de recompensas.</p>
	 *
	 * <p> De seguida entra-se num loop que é executado enquanto a thread deva estar ativa (ou seja, enquanto
	 * a variável {@code running == true}). Dentro do ciclo obtemos o valor do contador, de seguida obtemos
	 * a lista de zonas que tenham pouca abundância de trotinetes e, caso exista alguma, associamos uma
	 * recompensa respetiva a levar trotinetes de outra zona até à mesma</p>
	 * */
	public void run()
	{
		this.lock.lock();
		try
		{
			while (this.running)
			{
				long c = this.contador.getContador();

				List<Coord> poucoPopuladas = mapa.zonasPoucoPopuladas();
				List<Coord> muitoPopuladas = mapa.zonasMuitoPopuladas();

				this.adicionarRecompensas(poucoPopuladas, muitoPopuladas);
				this.removerRecompensas(poucoPopuladas);
				
				while (this.running && c == contador.getContador())
				{
					this.lock.esperaAcao();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally { this.lock.unlock(); }
	}

	/**
	 * Método usado para colocar a variável {@code running} como false, fazendo com que a 
	 * {@code Thread} acabe a sua execução.
	 * */
	public void shutdown()
	{
		this.running = false;
	}
}
