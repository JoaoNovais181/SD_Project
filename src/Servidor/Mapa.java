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

	// ┼ ─	

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
		Mapa m = new Mapa(30, 2);

		System.out.println(m.toString());
	}
}
