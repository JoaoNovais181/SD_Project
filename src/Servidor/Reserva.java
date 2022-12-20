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
	 * Variavel estática que conta o número de reservas, usado para dar um código de reserva
	 * */
	private static int contagemReservas = 0;

	/**
	 * Código associado a uma reserva
	 * */
	private int codigoReserva;

	/**
	 * Código de retorno associado a uma reserva (0 se correr tudo bem, !=0 se occorrer algum erro)
	 * */
	private int codigoRetorno;

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
	public Reserva(Coord localReserva)
	{
		this.dataReserva = LocalDateTime.now();
		this.codigoReserva = contagemReservas++;
		this.codigoRetorno = 0; // codigoRetorno == 0 -> sucesso
		this.localReserva = localReserva;
	}

	/**
	 * Constroi um objeto da classe {@code Reserva} correspondente a uma reserva mal sucedida
	 *
	 * @param  codigoRetorno  codigo de erro da reserva
	 * */
	public Reserva(int codigoRetorno)
	{
		this.dataReserva = null;
		this.codigoReserva = -1;
		this.codigoRetorno = codigoRetorno; // erro na reserva
		this.localReserva = null;
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
	 * Retorna o código de retorno
	 *
	 * @return o código de retorno
	 * */
	public int getCodigoRetorno() { return this.codigoRetorno; }

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
		return this.dataReserva.format(format) + ";" + this.codigoReserva + ";" + this.localReserva.toString() + ";" + this.codigoRetorno;
	}
}
