package Servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implementação da classe Mapa, usada para armazenar a quantidade de trotinetes
 * em cada zona, no contexto do Trabalho Prático de Sistemas Distribuídos,
 * garantindo a exclusão mútua no acesso às suas variáveis.
 *
 * @author João Carlos Fernandes Novais
 * */
public class Mapa
{
	/**
	 * {@code Lock} de escrita e leitura usado para garantir a exclusão mútua 
	 * */
	private ReentrantReadWriteLock lock;

	/**
	 * Valor constante referente à largura e comprimento do mapa
	 * */
	private final int N;

	/**
	 * Matriz de inteiros com o número de trotinetes em cada zona (sendo
	 * cada zona denominada como um valor na tabela) 
	 * */
	private int[][] mapa;

	/**
	 * {@code float} que representa uma média de trotinetes por posição,
	 * usado para o povoamento do mapa
	 * */
	private final float ratioTrotinete = 1f; 
	
	/**
	 * {@code float} usado para representar a distância fixa usada para uma vizinhança
	 * */
	private final float D;

	/**
	 * Constroi um mapa NxN, que considera uma vizinhança como todos os pontos
	 * com distância &lt;= D de um ponto central
	 *
	 * @param  N  Valor constante referente à largura e comprimento do mapa
     *
	 * @param  D {@code float} usado para representar a distância fixa usada para uma vizinhança 
	 * */
	public Mapa(int N, float D)
	{
		this.lock = new ReentrantReadWriteLock();
		this.N = N;
		this.D = D;
		this.mapa = new int[N][N];
	
		for (int i = 0 ; i<this.N ; i++)
			for (int j = 0 ; j<this.N ; j++)
				this.mapa[i][j] = 0;

		int TotalTroti = (int) (this.N*this.N * this.ratioTrotinete);

		Random rnd = new Random(ProcessHandle.current().pid());

		while (TotalTroti > 0)
		{
			int x = rnd.nextInt(this.N);			
			int y = rnd.nextInt(this.N);
			
			this.mapa[y][x] += 1;
			TotalTroti--;
		}
	}

	/**
	 * Retorna o valor de N
	 *
	 * @return valor de N
	 * */
	public int getN() { return this.N; }

	/**
	 * Retorna o valor de D
	 *
	 * @return valor de D
	 * */
	public float getD() { return this.D; }

	/**
	 * Método usado quando um cliente estaciona uma trotinete numa coordenada.
	 *
	 * @param  coord  {@link Coord} onde se irá estacionar a trotinete
	 * */
	public void estacionar(Coord coord)
	{
		this.lock.writeLock().lock();
		try
		{
			this.mapa[coord.getY()][coord.getX()]++;
		}
		finally { this.lock.writeLock().unlock(); }
	}

	/**
	 * Método usado para reservar uma trotinete numa determinada vizinhança
	 *
	 * <p> O método começa por obter o {@code Lock} e por verificar se é
	 * possível reservar uma trotinete na posição dada como argumento,
	 * caso não seja verifica se é possivel na vizinhança. No final é 
	 * retornado uma {@link Reserva} referente à reserva pedida, que 
	 * se for bem sucedida terá uma data, coordenada de origem e código de
	 * reserva, e caso contrário terá apenas um código de erro</p>
	 *
	 * @param  coord  {@link Coord} referente ao ponto central da vizinhança
	 * onde se pretende reservar uma trotinete
	 *
	 * @return Objeto do tipo {@link Reserva} referente à reserva pedida
	 * */
	public Coord reservar(Coord coord)
	{
		Coord localReserva = null;
		this.lock.writeLock().lock();
		try
		{
			if (this.mapa[coord.getY()][coord.getX()] > 0)
				localReserva = coord;
		
			int xCentral = coord.getX(), yCentral = coord.getY();
			int xInicio = xCentral - (int)this.D - 1;
			while (xInicio < 0)
				xInicio++;
			int xFinal = xCentral + (int)this.D + 1;
			while (xFinal >= this.N)
				xFinal--;
			
			int yInicio = yCentral - (int)this.D - 1;
			while (yInicio < 0)
				yInicio++;
			int yFinal = yCentral + (int)this.D + 1;
			while (yFinal >= this.N)
				yFinal--;
			
			for (int y = yInicio ; y<=yFinal && localReserva==null ; y++)
			{
				for (int x = xInicio ; x<=xFinal && localReserva==null ; x++)
				{
					if (this.mapa[y][x] > 0)
					{
						localReserva = new Coord(x, y);
					}
				}
			}

			if (localReserva != null)
				this.mapa[localReserva.getY()][localReserva.getX()]--;
		}
		finally { this.lock.writeLock().unlock(); }
		return localReserva;
	}

	/**
	 * Função que retorna o número de trotinetes numa vizinhança
	 *
	 * @param  coord  {@link Coord} referente à coordenada central da
	 * vizinhança
	 *
	 * @return  número total de trotinetes na vizinhança de centro em {@code coord}
	 * */
	public int trotinetesNaVizinhanca (Coord coord)
	{
		int xCentral = coord.getX(), yCentral = coord.getY();
		
		int xInicio = xCentral - (int)this.D - 1;
		while (xInicio < 0)
			xInicio++;
		int xFinal = xCentral + (int)this.D + 1;
		while (xFinal >= this.N)
			xFinal--;
		
		int yInicio = yCentral - (int)this.D - 1;
		while (yInicio < 0)
			yInicio++;
		int yFinal = yCentral + (int)this.D + 1;
		while (yFinal >= this.N)
			yFinal--;
		
		this.lock.readLock().lock();
		try
		{
			int r = this.mapa[yCentral][xCentral];
			for (int y = yInicio ; y<=yFinal ; y++)
			{
				for (int x = xInicio ; x<=xFinal ; x++)
				{
					if (coord.DistanceTo(new Coord(x, y)) <= this.D)
					{
						r += this.mapa[y][x];
					}
				}
			}

			return r;
		}
		finally { this.lock.readLock().unlock(); }	
	}

	public List<Coord> coordTrotinetesVizinhanca(Coord coord){

		int xCentral = coord.getX(), yCentral = coord.getY();
		List<Coord> coordTr = new ArrayList<>();

		int xInicio = xCentral - (int)this.D - 1;
		while (xInicio < 0)
			xInicio++;
		int xFinal = xCentral + (int)this.D + 1;
		while (xFinal >= this.N)
			xFinal--;

		int yInicio = yCentral - (int)this.D - 1;
		while (yInicio < 0)
			yInicio++;
		int yFinal = yCentral + (int)this.D + 1;
		while (yFinal >= this.N)
			yFinal--;

		this.lock.readLock().lock();
		try
		{
			if(this.mapa[yCentral][xCentral] > 0)
				coordTr.add(new Coord(xCentral,yCentral));
			for (int y = yInicio ; y<=yFinal ; y++)
			{
				for (int x = xInicio ; x<=xFinal ; x++)
				{
					if (coord.DistanceTo(new Coord(x, y)) <= this.D)
					{
						for(int i = 0; i < this.mapa[y][x]; i++)
							coordTr.add(new Coord(x, y));

					}
				}
			}

			return coordTr;
		}
		finally { this.lock.readLock().unlock(); }
	}
	
	/**
	 * Retorna uma lista com todas as zonas pouco populadas, isto é,
	 * que tenham em média numeroTrotinetes &lt; 2^D
	 *
	 * @return {@link List}&lt;{@link Coord}&gt; com as coordenadas centrais
	 * das vizinhanças com pouca abundância de trotinetes
	 * */
	public List<Coord> zonasPoucoPopuladas()
	{
		List<Coord> r = new ArrayList<>();

		for (int y = 0 ; y<this.N ; y++)
			for (int x = 0 ; x<this.N ; x++)
			{
				Coord coord = new Coord(x, y);
				int numTroti = this.trotinetesNaVizinhanca(coord);
				if (numTroti < (int)(Math.pow(2,this.D)))
					r.add(coord);
			}

		return r;
	}

	/**
	 * Retorna uma lista com todas as zonas pouco populadas, isto é,
	 * que tenham em média numeroTrotinetes &gt; 3*(2^(D+1))
	 *
	 * @return {@link List}&lt;{@link Coord}&gt; com as coordenadas centrais
	 * das vizinhanças com muita abundância de trotinetes
	 * */
	public List<Coord> zonasMuitoPopuladas()
	{
		List<Coord> r = new ArrayList<>();
		
		for (int y = 0 ; y<this.N ; y++)
			for (int x = 0 ; x<this.N ; x++)
			{
				Coord coord = new Coord(x, y);
				int numTroti = this.trotinetesNaVizinhanca(coord);
				if (numTroti > (int)(3*Math.pow(2,this.D+1)))
					r.add(coord);
			}

		return r;
	}

	/**
	 * Função usada para pretty print do mapa
	 *
	 * @param  witdh  largura total onde se quer colocar {@code str}
	 * @param  padStr  {@link String} a colocar à esquerda de {@code str}
	 * @param  str  {@link String} a colocar a direita
	 *
	 * @return texto formatado
	 * */
	private String pad(int width, String padStr, String str)
	{
		int n = width - str.length();
		if (n==0) return str;
		return String.format("%0" + n + "d", 0).replace("0", padStr) + str;
	}

	/**
	 * Representação textual do mapa
	 *
	 * @return representação textual do mapa
	 * */
	@Override
	public String toString()
	{
		String r = "";
		this.lock.readLock().lock();
		try
		{
			int Nlen = String.valueOf(this.N).length(), biggestLen = Nlen + 1;

			for (int[] line : this.mapa)
				for (int val : line)
					if (String.valueOf(val).length() > biggestLen)
						biggestLen = String.valueOf(val).length();

			r += pad(Nlen+1, " ", "│");
			for (int i=0 ; i<this.N ; i++)
			{
				String texto = String.valueOf(i);
				r += this.pad(biggestLen, " ", texto);
			}

			r += "\n" + pad(Nlen+1, "─", "┼") + pad(biggestLen*this.N + 1, "─","\n");

			for (int i=0 ; i<this.N ; i++)
			{
				r += pad(Nlen, " ",""+i) + "│";
				for (int j=0 ; j<this.N ; j++)
				{
					String texto = String.valueOf(this.mapa[i][j]);
					r += pad(biggestLen, " ", texto);
				}
				r += "\n";
			}

		}
		finally { this.lock.readLock().unlock(); }
		return r;
	}
}
