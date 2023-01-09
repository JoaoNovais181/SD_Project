package Servidor;

/**
 * Implementação de uma classe usada para representar os caminhos aos quais uma recompensa é
 * associada.
 *
 * @author João Carlos Fernandes Novais
 * @author Beatriz Ribeiro Monteiro
 * @author João Pedro Machado Ribeiro
 * @author Telmo José Pereira Maciel
 * */
public class Recompensa
{

	/**
	 * Coordenadas de origem e destino da recompensa
	 * */
	private Coord origem, destino;

	/**
	 * Valor da distância à qual uma coordenada deve estar no máximo para ser considerada
	 * parte da vizinhança de outra coordenada
	 * */
	private final float D;

	/**
	 * Constroi um objeto da classe {@code Recompensa}
	 *
	 * @param  origem  {@link Coord} de origem da recompensa
	 * @param  destino  {@link Coord} de destino da recompensa
	 * @param  D  valor para o parâmetro D da recompensa
	 * */
	public Recompensa (Coord origem, Coord destino, float D)
	{
		this.origem = origem;
		this.destino = destino;
		this.D = D;
	}

	/**
	 * Retorna a {@link Coord} de origem
	 *
	 * @return a {@link Coord} de origem
	 * */
	public Coord getOrigem() { return this.origem; }

	/**
	 * Retorna a {@link Coord} de destino
	 *
	 * @return a {@link Coord} de destino
	 * */
	public Coord getDestino() { return this.destino; }

	/**
	 * Verifica se a coordenada de inicio é igual à coordenada de origem da recompensa
	 * ou se a coordenada final é igual à coordenada do destino
	 *
	 * @param  inicio  {@link Coord} de inicio a verificar
	 * @param  fim  {@link Coord} de a verificar
	 *
	 * @return valor correspondente a se a coordenada de inicio é igual à coordenada
	 * de origem da recompensa ou se a coordenada final é igual à coordenada do destino
	 * */
	public boolean coordsIguais (Coord inicio, Coord fim)
	{
		return this.origem.equals(inicio) || this.destino.equals(fim);
	}

	/**
	 * Método utilizado para verificar se uma dada {@link Coord} tem o mesmo destino que a 
	 * instância de {@link Recompensa}
	 *
	 * @param destino {@link Coord} a verificar
	 *
	 * @return Se tem o mesmo destino ou não
	 **/ 
	public boolean mesmoDestino(Coord destino)
	{
		return this.destino.equals(destino);
	}

	/**
	 * Método utilizado para verificar se uma dada {@link Coord} é perto do destino da 
	 * instância de {@link Recompensa}
	 *
	 * @param destino {@link Coord} a verificar
	 *
	 * @return Se tem o destino perto ou não
	 **/ 
	public boolean destinoPerto(Coord destino)
	{
		return this.destino.DistanceTo(destino) <= this.D;
	}

	/**
	 * Método usado para verificar se um par de coordenadas é elegível para uma recompensa,
	 * isto é, se a coordenada de início está a menos que D de distância da coordenada
	 * de origem e se a coordenada final está a menos que D de distância da coordenada
	 * de destino
	 *
	 * @param  inicio  {@link Coord} de inicio a verificar
	 * @param  fim  {@link Coord} de a verificar
	 *
	 * @return {@code True} se o par {@code inicio-fim} for elegível
	 * */
	public boolean elegivel (Coord inicio, Coord fim)
	{
		return this.origem.DistanceTo(inicio) <= this.D && this.destino.DistanceTo(fim) <= this.D;
	}

	/**
	 * Método para verificar se algum outro objeto é igual à instância de {@code Recompensa}
	 *
	 * @param  obj  objeto a comparar
	 *
	 * @return {@code true} se os objetos forem iguals
	 * */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) return false;

		if (obj.getClass() != this.getClass()) return false;

		Recompensa r = (Recompensa)obj;
		return this.origem.equals(r.getOrigem()) && this.destino.equals(r.getDestino());
	}

	/**
	 * Representação textual de uma {@code Recompensa}
	 *
	 * @return  representação textual de uma {@code Recompensa}
	 * */
	@Override
	public String toString()
	{
		return "Recompensa de " + this.origem.toString() + " - para -> " + this.destino.toString();
	}
}
