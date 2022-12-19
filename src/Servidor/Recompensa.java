package Servidor;

public class Recompensa
{
	private Coord origem, destino;
	private final float D;

	public Recompensa (Coord origem, Coord destino, float D)
	{
		this.origem = origem;
		this.destino = destino;
		this.D = D;
	}

	public Coord getOrigem() { return this.origem; }

	public Coord getDestino() { return this.destino; }

	public boolean coordsIguais (Coord inicio, Coord fim)
	{
		return this.origem.equals(inicio) || this.destino.equals(fim);
	}

	public boolean elegivel (Coord inicio, Coord fim)
	{
		return this.origem.DistanceTo(inicio) <= this.D && this.destino.DistanceTo(fim) <= this.D;
	}

	@Override
	public String toString()
	{
		return this.origem.toString() + "->" + this.destino.toString();
	}
}
