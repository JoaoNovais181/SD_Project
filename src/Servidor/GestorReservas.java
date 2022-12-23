package Servidor;

import Servidor.Reserva;
import Servidor.Mapa;
import Servidor.ListaRecompensas;
import Servidor.Coord;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class GestorReservas
{
	/**
	 * Variavel estática que conta o número de reservas, usado para dar um código de reserva
	 * */
	private static int contagemReservas = 0;

	private final static float percentagemRecompensa = 0.25f;
	private final static float IVA = 0.23f;
	private final static float precoPorUD = 0.5f;

	private Mapa mapa;	
	private Map<Integer, Reserva> reservas;
	private ListaRecompensas listaRecompensas;
	private ReentrantLock lock;

	public GestorReservas(Mapa mapa)
	{
		this.mapa = mapa;
		this.reservas = new HashMap<>();
		this.lock = new ReentrantLock();
	}

	public int reservar(Coord coord)
	{
		Coord localReserva = this.mapa.reservar(coord);
		if (localReserva == null)
			return 1;

		this.lock.lock();
		try
		{
			Reserva r = new Reserva(contagemReservas++, localReserva);
			this.reservas.put(r.getCodigoReserva(), r);
		}
		finally { this.lock.unlock(); }

		return 0;
	}

	public float estacionar(int codigoReserva, Coord coord)
	{
		this.lock.lock();
		try
		{
			Reserva r = this.reservas.get(codigoReserva);

			if (r == null)
				return -1;

			this.mapa.estacionar(coord);

			Coord inicio = r.getLocalReserva();

			boolean elegivel = this.listaRecompensas.elegivel(inicio, coord);

			float valorAPagar = inicio.DistanceTo(coord) * precoPorUD * IVA;

			return valorAPagar - ((elegivel) ?valorAPagar * percentagemRecompensa :0); 
		}
		finally { this.lock.unlock(); }
	}
}
