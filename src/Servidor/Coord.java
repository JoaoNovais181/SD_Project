package Servidor;


public class Coord
{
	private int x, y;

	public Coord(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX() { return this.x; }
	
	public int getY() { return this.y; }

	public float DistanceTo(Coord coord)
	{
		return Math.abs(this.x - coord.getX()) + Math.abs(this.y - coord.getY());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) return false;

		if (obj.getClass() != this.getClass()) return false;

		Coord coord = (Coord)obj;
		return this.x == coord.getX() && this.y == coord.getY();
	}

	@Override
	public String toString()
	{
		return String.format("(%d,%d)", this.x, this.y);
	}
}
