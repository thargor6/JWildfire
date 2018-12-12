////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
//
// taprats -- an interactive design tool for computer-generated 
//            Islamic patterns.
//
// Copyright 2000 Craig S. Kaplan.
// email: csk at cs.washington.edu
//
// Copyright 2010 Pierre Baillargeon
// email: pierrebai at hotmail.com
//
// This file is part of Taprats.
//
// Taprats is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Taprats is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Taprats.  If not, see <http://www.gnu.org/licenses/>.

package csk.taprats.geometry;

/*
 * csk.taprats.geometry.Point
 * 
 * A simple two-dimensional cartesian point class.  Since Point
 * instances will always be heap-allocated, I include some destructive
 * methods (with names ending in "D") that modify the receiver
 * rather than return a fresh instance.  This helps keep down
 * the overhead of allocating Points, particularly as temporaries
 * in long calculations.
 */

public class Point extends Primitive
	implements Cloneable
{
	public static final double TOLERANCE = 1.0E-7;
	public static final double TOLERANCE2 = TOLERANCE * TOLERANCE;
	private static final double TRUNC = 1.0E7;

	/* 
	 * Three common Points so we can save even more on object creation.
	 */
	public static final Point ORIGIN = new Point( 0.0, 0.0 );
	public static final Point UNIT_X = new Point( 1.0, 0.0 );
	public static final Point UNIT_Y = new Point( 0.0, 1.0 );

	private double x;
	private double y;
	private double color;

	public Point( double x, double y )
	{
		super(1);
		this.x = x;
		this.y = y;
	}

	public Point()
	{ 
		this( 0.0, 0.0 );
	}
	
	public Point(double x,double y,double color)
	{
		super(1);
		this.x=x;
		this.y=y;
		this.color=color;
	}

	public final Object clone()
	{
		return new Point( x, y );
	}

	public final double getX()
	{
		return x;
	}

	public final double getY()
	{
		return y;
	}

	public final void setX( double x )
	{
		this.x = x;
	}

	public final void setY( double y )
	{
		this.y = y;
	}

	public final void setColor( double color )
	{
		this.color = color;
	}

	public final double getColor( )
	{
		return this.color;
	}
	
	public final double[] get()
	{
		double[] res = new double[ 2 ];
		res[ 0 ] = x;
		res[ 1 ] = y;

		return res;
	}

	public final double mag2()
	{
		return x * x + y * y;
	}

	public final double mag()
	{
		return Math.sqrt( mag2() );
	}

	public final double dist2( Point other )
	{
		double dx = x - other.x;
		double dy = y - other.y;

		return dx * dx + dy * dy;
	}

	public final double dist( Point other )
	{
		return Math.sqrt( dist2( other ) );
	}

	public final Point normalize()
	{
		double m = mag();
		if( m != 0.0 ) {
			return scale( 1.0 / m );
		} else {
			return this;
		}
	}

	public final void normalizeD()
	{
		double m = mag();
		if( m != 0.0 ) {
			scaleD( 1.0 / m );
		}
	}

	/* 
	 * Sigh -- my kingdom for operator overloading.  Oh well.
	 */
	public final Point add( Point other )
	{
		return new Point( x + other.x, y + other.y );
	}

	public final void addD( Point other )
	{
		x += other.x;
		y += other.y;
	}

	public final Point subtract( Point other )
	{
		return new Point( x - other.x, y - other.y );
	}

	public final void subtractD( Point other )
	{
		x -= other.x;
		y -= other.y;
	}

	public final double dot( Point other )
	{
		return x * other.x + y * other.y;
	}

	public final Point scale( double r )
	{
		return new Point( x * r, y * r );
	}

	public final Point scale( double xr, double yr )
	{
		return new Point( x * xr, y * yr );
	}

	public final void scaleD( double r )
	{
		x *= r;
		y *= r;
	}

	public final Point perp()
	{
		// Return a vector ccw-perpendicular to this.
		return new Point( -y, x );
	}

	public final void perpD()
	{
		double tmp = x;
		x = -y;
		y = tmp;
	}

	public final Point convexSum( Point other, double t )
	{
		double mt = 1.0 - t;
		return new Point( mt * x + t * other.x, mt * y + t * other.y );
	}

	public final void convexSumD( Point other, double t )
	{
		double mt = 1.0 - t;
		x = mt * x + t * other.x;
		y = mt * y + t * other.y;
	}

	// Return the absolute angle of the edge from this to other, in the
	// range -PI to PI.
	public final double getAngle( Point other )
	{
		return Math.atan2( other.getY() - y, other.getX() - x );
	}

	// Angle wrt the origin.
	public final double getAngle()
	{
		return Math.atan2( y, x );
	}

	public final double cross( Point other )
	{
		return (x * other.y) - (y * other.x);
	}

	/*
	 * Get the section of arc swept out between the edges this ==> from
	 * and this ==> to.
	 */
	public final double sweep( Point from, Point to )
	{
		double afrom = getAngle( from );
		double ato = getAngle( to );

		double res = ato - afrom;

		while( res < 0.0 ) {
			res += 2.0 * Math.PI;
		}

		return res;
	}

	public final double distToLine( Point p, Point q )
	{
		Point qmp = q.subtract( p );
		double t = subtract( p ).dot( qmp ) / qmp.dot( qmp );
		if( t >= 0.0 && t <= 1.0 ) {
			double ox = p.x + t * ( q.x - p.x );
			double oy = p.y + t * ( q.y - p.y );
			return Math.sqrt( (x-ox)*(x-ox) + (y-oy)*(y-oy) );
		} else if( t < 0.0 ) {
			return p.dist( this );
		} else {
			return q.dist( this );
		}
	}

	public final double parameterizationOnLine( Point p, Point q )
	{
		Point qmp = q.subtract( p );
		return subtract( p ).dot( qmp ) / qmp.dot( qmp );
	}

	public final String toString()
	{
		return "[ " + x + " " + y + " ]";
	}

	public final boolean equals( Object other )
	{
		if( !(other instanceof Point) ) {
			return false;
		}

		Point v2 = (Point)other;

		double dx = x - v2.x;
		double dy = y - v2.y;
		double d = dx * dx + dy * dy;
		return d < TOLERANCE2;

		// return (x == v2.x) && (y == v2.y);
	}

	public final int hashCode()
	{
		// return (new Double( x )).hashCode() ^ (new Double( y )).hashCode();
		long xx = Double.doubleToLongBits( Math.floor( x * TRUNC ) );
		long yy = Double.doubleToLongBits( Math.floor( y * TRUNC ) );
		return (int)( (xx ^ (xx >> 32)) ^ (yy ^ (yy >> 32)));
	}

	public static final String toString( Point[] pts )
	{
		int len = pts.length;

		if( len == 0 ) {
			return "";
		} else if( len == 1 ) {
			return pts[ 0 ].toString();
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append( pts[ 0 ].toString() );
			for( int idx = 1; idx < len; idx++ ) {
				sb.append( " " );
				sb.append( pts[ idx ] );
			}

			return sb.toString();
		}
	}

	public static final void main( String[] args ) {
		Point p = new Point( 
			(new Double(args[0])).doubleValue(),
			(new Double(args[1])).doubleValue() );
		Point a = new Point( 
			(new Double(args[2])).doubleValue(),
			(new Double(args[3])).doubleValue() );
		Point b = new Point( 
			(new Double(args[4])).doubleValue(),
			(new Double(args[5])).doubleValue() );

		System.out.println( p.distToLine( a, b ) );
	}
}
