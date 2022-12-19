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
	public String toString()
	{
		return String.format("(%d,%d)", this.x, this.y);
	}
}
