package Servidor;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
	private ReentrantLock lockReservas;

	/**
	 * {@code Condition} usada para colocar a {@code Thread} em espera enquanto não ocorrer nenhuma reserva 
	 * ou estacionamento de uma trotinete
	 * */
	private Condition esperaAcao;

	/**
	 * Variável usada para ditar se a thread deve continuar o seu trabalho ou não
	 * */
	private boolean running;

	/**
	 * Constrói um objeto deste tipo, recebendo uma {@link ListaRecompensas}, um {@link Mapa}, um {@link Contador},
	 * {@link ReentrantLock} e {@link Condition}, e inicializando a sua variável {@code running} como true
	 * */
	public TrabalhadorRecompensas (ListaRecompensas recompensas, Mapa mapa, Contador contador, ReentrantLock lockReservas, Condition esperaAcao)
	{
		this.recompensas = recompensas;
		this.mapa = mapa;
		this.contador = contador;
		this.lockReservas = lockReservas;
		this.esperaAcao = esperaAcao;
		this.running = true;
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
		this.lockReservas.lock();
		try
		{
			while (this.running)
			{
				long c = this.contador.getContador();

				List<Coord> poucoPopuladas = mapa.zonasPoucoPopuladas();
				if (poucoPopuladas.size() != 0)
				{
					List<Coord> muitoPopuladas = mapa.zonasMuitoPopuladas();
					
					for (Coord origem : poucoPopuladas)
					{
						Coord destino = null;
						if (muitoPopuladas.size() > 0)
						{
							destino = muitoPopuladas.get(0);
							muitoPopuladas.remove(0);
						}
						else
							do
							{
								destino = Coord.randomCoord(this.mapa.getN());
							} while(!poucoPopuladas.contains(destino));
						Recompensa recompensa = new Recompensa(origem,destino,this.mapa.getD());

						this.recompensas.addRecompensa(recompensa);
					}
				}

				while (this.running && c == contador.getContador())
				{
					this.esperaAcao.await();;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally { this.lockReservas.unlock(); }
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
