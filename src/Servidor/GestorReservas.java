package Servidor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class GestorReservas
{
	private final static float percentagemRecompensa = 0.25f;
	private final static float IVA = 0.23f;
	private final static float precoPorUD = 0.5f;
	private final static float precoPorMin = 0.4f;

	private Mapa mapa;	
	private Map<Integer, Reserva> reservas;
	private ListaRecompensas listaRecompensas;
	private LockReservas lock;
	private Contador contador;

	public GestorReservas(Mapa mapa, ListaRecompensas listaRecompensas)
	{
		this.mapa = mapa;
		this.listaRecompensas = listaRecompensas;
		this.reservas = new HashMap<>();
		this.lock = LockReservas.getInstance();
		this.contador = Contador.getInstance();
	}

	public Reserva reservar(Coord coord)
	{
		Coord localReserva = this.mapa.reservar(coord);
		if (localReserva == null)
			return null;

		Reserva r; 
		try
		{
			this.lock.lock();
			r = new Reserva((int)this.contador.getContador(), localReserva);
			this.contador.incrementar();
			this.reservas.put(r.getCodigoReserva(), r);
			this.lock.sinalizaAcao();
		}
		finally { this.lock.unlock(); }

		return r;
	}

	public float[] estacionar(int codigoReserva, Coord coord)
	{
		this.lock.lock();
		try
		{
			Reserva r = this.reservas.get(codigoReserva);

			if (r == null)
				return null;

			this.reservas.remove(codigoReserva);
			this.mapa.estacionar(coord);
			this.contador.incrementar();
			this.lock.sinalizaAcao();;

			Coord inicio = r.getLocalReserva();

			boolean elegivel = this.listaRecompensas.elegivel(inicio, coord);

			float valorAPagar = inicio.DistanceTo(coord) * precoPorUD * IVA;

			long difMin = ChronoUnit.MINUTES.between(r.getDataReserva(), LocalDateTime.now());
		
			float[] ans = new float[2];
			ans[0] = difMin*precoPorMin +  valorAPagar;
			ans[1] = ((elegivel) ?valorAPagar * percentagemRecompensa :0); 
			return ans; 
		}
		finally { this.lock.unlock(); }
	}
}
