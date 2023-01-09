package Servidor;

import java.util.List;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Classe utilizada para notificar um utilizador de recompensas
 *
 * @author João Carlos Fernande Novais
 * @author Beatriz Ribeiro Monteiro
 * @author João Pedro Machado Ribeiro
 * @author Telmo José Pereira Maciel
 * */
public class TrabalhadorNotificacoes implements Runnable
{
	/**
	 * {@link DataOutputStream} utilizada para enviar as notificações
	 * */
	private DataOutputStream out;
	/**
	 * {@link ListaRecompensas} com as recompensas do sistema
	 * */
	private ListaRecompensas recompensas;
	/**
	 * {@link LockReservas} utilizado para a exclusão mútua nas reservas
	 * */
	private LockReservas lock;
	/**
	 * {@link Utilizador} a notificar  
	 * */
	private Utilizador u;
	/**
	 * boolean que indica se o trabalhador está a funcionar ou não
	 * */
	private boolean running;

	/**
	 * Construtor da classe
	 *
	 * @param out out
	 * @param recompensas listaRecompensas
	 * @param u u
	 * */
	public TrabalhadorNotificacoes(DataOutputStream out, ListaRecompensas recompensas, Utilizador u)
	{
		this.out = out;
		this.recompensas = recompensas;
		this.lock = LockReservas.getInstance();
		this.u = u;
		this.running = true;
	}

	public void run()
	{
		this.lock.lock();
		try
		{
			while(this.running)
			{
				List<Coord> notificar = u.getNotificar();
				for(Coord c : notificar)
					if (this.recompensas.destinoPerto(c))
					{
						out.writeUTF("NOTIFICACAO;Recompensa perto de " + c.toString());
						out.flush();
						u.removeNotificar(c);
					}

				this.lock.esperaAcao();
			}
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		finally { this.lock.unlock(); }
	}

	/**
	 * Método utilizado para desligar o trabalhador
	 * */
	public void shutdown()
	{
		this.running = false;
		this.lock.lock();
		this.lock.sinalizaAcao();
		this.lock.unlock();
	}
}

