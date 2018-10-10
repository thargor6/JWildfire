
package csk.taprats.geometry;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import java.awt.Color;

public class Ngon extends Primitive
{

	private int sides;
	private double scale;
	private Point pos;
	private double angle;
	private double rcosa;
	private double rsina;
	private double fill;
	
	private double color=0;
	private Color rgbColor=new Color(0,0,0,100);

	public Ngon( int sides, double scale,double angle, Point point, double color, double fill )
	{
		super(4);
		this.pos = point;
		this.color=color;
		this.scale=scale;
		this.sides=sides;
		this.angle=angle;
		this.fill=fill;
		rcosa=cos(angle*M_PI/180.0);
		rsina=sin(angle*M_PI/180.0); 
	}

	
	public Ngon( int sides, double scale,double angle, Point p1, Color rgbColor, double fill )
	{
		super(4);
		this.pos = p1;
		this.rgbColor=rgbColor;
		this.scale=scale;
		this.sides=sides;
		this.angle=angle;
		this.fill=fill;
		rcosa=cos(angle*M_PI/180.0);
		rsina=sin(angle*M_PI/180.0); 
	}
	
	public Ngon( int sides,Point p1, double scale)
	{
		super(3);
		this.pos = p1;
        this.scale=scale;
        this.sides=sides;
	}
	
	public final Point getPos()
	{
		return pos;
	}

	public final double getScale()
	{
		return scale;
	}
	
	public final int getSides()
	{
		return sides;
	}
	
	public final double getColor()
	{
		return this.color;
	}
	
	public final Color getRGBColor()
	{
		return this.rgbColor;
	}
	
	public final double getCosa()
	{
		return this.rcosa;
	}
	
	public final double getSina()
	{
		return this.rsina;
	}
	
	public double getFill()
	{
		return fill;
	}
	
	public final String toString()
	{
		return "[ type: " + gettype() + " (" + pos.toString() +  ") color: " + color +"]" ;
	}
}
