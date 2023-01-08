package Servidor;

import java.util.List;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrabalhadorNotificacoes implements Runnable
{
	private DataOutputStream out;
	private ListaRecompensas recompensas;
	private LockReservas lock;
	private Utilizador u;
	private boolean running;

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
				System.out.println("OLAAAAA");
				List<Coord> notificar = u.getNotificar();
				System.out.println(notificar);
				for(Coord c : notificar)
					if (this.recompensas.destinoPerto(c))
					{
						System.out.println("OIEEE" + c);
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

	public void shutdown()
	{
		this.running = false;
		this.lock.lock();
		this.lock.sinalizaAcao();
		this.lock.unlock();
	}
}

