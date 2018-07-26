
package csk.taprats.geometry;

public class Triangle extends Primitive
{
	private Point p1;
	private Point p2;
	private Point p3;
	
	private double color=0;

	public Triangle( Point p1,Point p2, Point p3, double color )
	{
		super(3);
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.color=color;
	}

	public Triangle( Point p1,Point p2, Point p3 )
	{
		super(3);
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
	
	public final Point getP1()
	{
		return p1;
	}

	public final Point getP2()
	{
		return p2;
	}

	public final Point getP3()
	{
		return p3;
	}

	public final double getColor()
	{
		return this.color;
	}
	
	public final String toString()
	{
		return "[ type: " + gettype() + " (" + p1.toString() + " , " + p2.toString() + " , " + p2.toString() + " , " + p3.toString() + ") color: " + color +"]" ;
	}
}
