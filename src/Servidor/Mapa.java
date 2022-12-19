package Servidor;

import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Mapa
{
	private ReentrantReadWriteLock lock;
	private int N, D;
	private int[][] mapa;
	private final float ratioTrotinete = 1f;

	public Mapa(int N, int D)
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

	
	
	@Override
	public String toString()
	{
		String r = "";
		this.lock.readLock().lock();
		try
		{
			int biggestLen = 0;

			for (int[] line : this.mapa)
				for (int val : line)
					if (String.valueOf(val).length() > biggestLen)
						biggestLen = String.valueOf(val).length();

			biggestLen++;

			for (int i=0 ; i<this.N ; i++)
			{
				for (int j=0 ; j<this.N ; j++)
				{
					String texto = String.valueOf(this.mapa[i][j]);
					int n = biggestLen - texto.length();
					String padding = String.format("%0" + n + "d", 0).replace("0"," ");
					r += padding + texto + " ";
				}
					// r += String.format("%*d ",biggestLen, this.mapa[i][j]);
				r += "\n";
			}

		}
		finally { this.lock.readLock().unlock(); }
		return r;
	}

	public static void main(String[] args)
	{
		Mapa m = new Mapa(20, 2);

		System.out.println(m.toString());
	}
}
