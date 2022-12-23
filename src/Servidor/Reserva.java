package Servidor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementação da classe reserva, que representa a reserva de uma trotinete
 *
 * @author João Carlos Fernandes Novais
 * */
public class Reserva
{
	/**
	 * Data da reserva
	 * */
	private LocalDateTime dataReserva;
	

	/**
	 * Código associado a uma reserva
	 * */
	private int codigoReserva;

	/**
	 * {@link Coord} onde foi reservada uma trotinete
	 * */
	private Coord localReserva;
	
	/**
	 * Constroi um objeto da classe {@code Reserva}, com data igual à data atual e codigo
	 * de retorno igual a 0
	 *
	 * @param  localReserva  {@link Coord} onde foi reservada a trotinete
	 * */
	public Reserva(int codigoReserva, Coord localReserva)
	{
		this.dataReserva = LocalDateTime.now();
		this.codigoReserva = codigoReserva;
		this.localReserva = localReserva;
	}

	/**
	 * Retorna a data da reserva
	 *
	 * @return a data da resera
	 * */
	public LocalDateTime getDataReserva() { return this.dataReserva; }

	/**
	 * Retorna o código da reserva
	 *
	 * @return o código da reserva
	 * */
	public int getCodigoReserva() { return this.codigoReserva; }

	/**
	 * Retorna a {@link Coord} do local onde foi reservada a trotinete
	 *
	 * @return a {@link Coord} do local onde foi reservada a trotinete
	 * */
	public Coord getLocalReserva() { return this.localReserva; }

	/**
	 * Representação textual da reserva
	 *
	 * @return representação textual da reserva
	 * */
	@Override
	public String toString()
	{
		DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
		return this.dataReserva.format(format) + ";" + this.codigoReserva + ";" + this.localReserva.toString();
	}
}
