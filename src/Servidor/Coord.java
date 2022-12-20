package Servidor;

import java.util.Random;


/**
 * Implementação da classe Coord, usada para representar uma coordenada no
 * {@link Mapa}.
 *
 * @author João Carlos Fernandes Novais
 * */
public class Coord
{
	/**
	 * Coordenadas x e y
	 * */
	private int x, y;

	/**
	 * Constroi um objeto do tipo {@code Coord} com coordenadas x e y
	 *
	 * @param  x  Coordenada X do objeto
	 * @param  y  Coordenada Y do objeto
	 * */
	public Coord(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Método para obter o valor da coordenada x
	 *
	 * @return Valor da coordenada no eixo X
	 * */
	public int getX() { return this.x; }
	
	/**
	 * Método para obter o valor da coordenada y
	 *
	 * @return Valor da coordenada no eixo y
	 * */
	public int getY() { return this.y; }

	/**
	 * Retorna a distância de Manhattan até outro objeto da classe {@code Coord}
	 *
	 * @return a distância de Manhattan até outro objeto da classe {@code Coord}
	 * */
	public float DistanceTo(Coord coord)
	{
		return Math.abs(this.x - coord.getX()) + Math.abs(this.y - coord.getY());
	}

	/**
	 * Retorna uma instância de {@code Coord} com coordenadas aleatórias
	 *
	 * @return uma instância de {@code Coord} com coordenadas aleatórias 
	 * */
	public static Coord randomCoord(int N)
	{
		Random rnd = new Random(ProcessHandle.current().pid());
		
		return new Coord(rnd.nextInt(N), rnd.nextInt(N));
	}

	/**
	 * Definição do método equals
	 *
	 * @param  obj Objeto a testar a igualdade 
	 *
	 * @return {@code boolean} respetivo à igualdade entre a instância do objeto e 
	 * o objeto dado como parâmetro
	 * */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) return false;

		if (obj.getClass() != this.getClass()) return false;

		Coord coord = (Coord)obj;
		return this.x == coord.getX() && this.y == coord.getY();
	}

	/**
	 * Representação textual de uma coordenada
	 *
	 * @return representação textual de uma coordenada
	 * */
	@Override
	public String toString()
	{
		return String.format("(%d,%d)", this.x, this.y);
	}
}
