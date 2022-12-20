package Servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Mapa
{
	private ReentrantReadWriteLock lock;
	private final int N;
	private int[][] mapa;
	private final float ratioTrotinete = 1f, D;

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

	public int getN() { return this.N; }

	public float getD() { return this.D; }

	public void estacionar(Coord coord)
	{
		this.lock.writeLock().lock();
		try
		{
			this.mapa[coord.getY()][coord.getX()]++;
		}
		finally { this.lock.writeLock().unlock(); }
	}

	public Reserva reservar(Coord coord)
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

			if (localReserva == null)
				return new Reserva(1);
			this.mapa[localReserva.getY()][localReserva.getX()]--;
			return new Reserva(localReserva);
		}
		finally { this.lock.writeLock().unlock(); }
	}

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

	private String pad(int width, String padStr, String str)
	{
		int n = width - str.length();
		if (n==0) return str;
		return String.format("%0" + n + "d", 0).replace("0", padStr) + str;
	}

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

	public static void main(String[] args)
	{
		Mapa m = new Mapa(20, 2.0f);

		System.out.println(m.toString());
		System.out.println("Zonas com pouca troti madje:" + m.zonasPoucoPopuladas().toString());
		System.out.println("Zonas com buesda troti madje:" + m.zonasMuitoPopuladas().toString());
		// Reserva r = m.reservar(new Coord(0, 0));
		// System.out.println(r.toString());
		// System.out.println(m.toString());
		// System.out.println("Zonas com pouca troti madje:" + m.zonasPoucoPopuladas().toString());
	}
}
