package Servidor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe que funciona como gestor para as reservas, todas as reservas são feitas
 * numa instância desta classe e nela armazenadas
 *
 * @author João Carlos Fernandes Novais
 * @author Beatriz Ribeiro Monteiro
 * @author João Pedro Machado Ribeiro
 * @author Telmo José Pereira Maciel
 * */
public class GestorReservas
{
	/**
	 * Percentagem do valor a pagar correspondente à recompensa
	 * */
	private final static float percentagemRecompensa = 0.25f;
	/**
	 * IVA aplicado ao valor a pagar
	 * */
	private final static float IVA = 0.23f;
	/**
	 * Preco a pagar por cada unidade de medida andada
	 * */
	private final static float precoPorUD = 0.5f;
	/**
	 * Preco por minuto de viagem
	 * */
	private final static float precoPorMin = 0.4f;

	/**
	 * {@link Mapa} onde estão as trotinetes
	 * */
	private Mapa mapa;
	/**
	 * {@link Map} que pareia uma reserva com o seu codigo
	 * */
	private Map<Integer, Reserva> reservas;
	/**
	 * {@link ListaRecompensas} lista das recompensas do sistema
	 * */
	private ListaRecompensas listaRecompensas;
	/**
	 * {@link LockReservas} utilizado para garantir exclusão mútua no que toca a reservas
	 * */
	private LockReservas lock;
	/**
	 * Contador do número de reservas feitas
	 * */
	private Contador contador;

	/**
	 * Construtor da class
	 *  @param mapa Mapa
	 *  @param listaRecompensas lista de recompensas
	 * */
	public GestorReservas(Mapa mapa, ListaRecompensas listaRecompensas)
	{
		this.mapa = mapa;
		this.listaRecompensas = listaRecompensas;
		this.reservas = new HashMap<>();
		this.lock = LockReservas.getInstance();
		this.contador = Contador.getInstance();
	}

	/**
	 * Método utilizador para reservar uma trotinete na vizinhança de uma dada coordenada
	 *
	 * @param coord Coordenada perto da qual se quer reservar uma trotinete
	 * @return {@link Reserva} correspondente à reserva
	 * */
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

	/**
	 * Método utilizador para estacionar uma dada trotinete numa dada coordenada
	 *
	 * @param codigoReserva codigo da reserva a estacionar
	 * @param coord Coordenada onde se pretende estacionar
	 *
	 * @return {@link float[]} em que a primeira posição é o valor a pagar e a segunda é o valor da recompensa
	 * */
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
