package Servidor;

import java.time.LocalDateTime;


public class Reserva
{
	private LocalDateTime dataReserva;
	private static int contagemReservas = 0;
	private int codigoReserva, codigoRetorno;
	private Coord localReserva;
	
	public Reserva(Coord localReserva)
	{
		this.dataReserva = LocalDateTime.now();
		this.codigoReserva = contagemReservas++;
		this.codigoRetorno = 0; // codigoRetorno == 0 -> sucesso
		this.localReserva = localReserva;
	}

	public Reserva(int codigoRetorno)
	{
		this.dataReserva = null;
		this.codigoReserva = -1;
		this.codigoRetorno = codigoRetorno; // erro na reserva
		this.localReserva = null;
	}

	public LocalDateTime getDataReserva() { return this.dataReserva; }

	public int getCodigoReserva() { return this.codigoReserva; }

	public int getCodigoRetorno() { return this.codigoRetorno; }

	public Coord getLocalReserva() { return this.localReserva; }

	@Override
	public String toString()
	{
		return this.dataReserva.toString() + ";" + this.codigoReserva + ";" + this.localReserva.toString() + ";" + this.codigoRetorno;
	}
}
