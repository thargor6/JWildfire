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
 * Transform.java
 *
 * A two-dimensional affine transform.  I store the top two rows of 
 * the homogeneous 3x3 matrix.  Or, looked at another way, it's the
 * 2D linear transform ((a b) (d e)) together with the translation (c f).
 */

public class Transform
	implements Cloneable
{
	public static final Transform ZERO = scale( 0.0 );
	public static final Transform IDENTITY = scale( 1.0 );

	private double a, b, c; 
	private double d, e, f; 

	public Transform( 
		double a, double b, double c, 
		double d, double e, double f )
	{
		this.a = a; this.b = b; this.c = c; 
		this.d = d; this.e = e; this.f = f; 
	}

	public Transform( double[] ds )
	{
		this.a = ds[0]; this.b = ds[1]; this.c = ds[2];  
		this.d = ds[2]; this.e = ds[4]; this.f = ds[5];
	}

	public void get( double[] ds )
	{
		ds[0] = a;
		ds[1] = b;
		ds[2] = c;
		ds[3] = d;
		ds[4] = e;
		ds[5] = f;
	}

	public final Object clone()
	{
		return new Transform( a, b, c, d, e, f );
	}

	/*
	 * Methods to build some common transforms.
	 */

	static public final Transform scale( double r )
	{
		return new Transform(
			r, 0, 0,
			0, r, 0 );
	}

	static public final Transform scale( double xs, double ys )
	{
		return new Transform(
			xs, 0,  0, 
			0,  ys, 0 );
	}

	static public final Transform translate( double x, double y )
	{
		return new Transform(
			1.0, 0, x,
			0, 1.0, y );
	}

	static public final Transform translate( Point pt )
	{
		return new Transform(
			1.0, 0, pt.getX(),
			0, 1.0, pt.getY() );
	}

	static public final Transform rotate( double t )
	{
		return new Transform(
			Math.cos( t ), -Math.sin( t ), 0,
			Math.sin( t ),  Math.cos( t ), 0 );
	}

	static public final Transform rotateAroundPoint( Point pt, double t )
	{
		return Transform.translate( pt ).compose(
			Transform.rotate( t ).compose(
			Transform.translate( -pt.getX(), -pt.getY() ) ) );
	}

	static public final Transform reflectThroughLine( Point p, Point q )
	{
		Transform T = matchLineSegment( p, q );
		return T.compose( 
			Transform.scale( 1.0, -1.0 ).compose( T.invert() ) );
	}

	/*
	 * Matrix multiplication.
	 */

	public final Transform compose( Transform other )
	{
		return new Transform(
			a * other.a + b * other.d,
			a * other.b + b * other.e,
			a * other.c + b * other.f + c,

			d * other.a + e * other.d,
			d * other.b + e * other.e,
			d * other.c + e * other.f + f );
	}

	public final void composeD( Transform other )
	{
		double na = a * other.a + b * other.d;
		double nb = a * other.b + b * other.e;
		double nc = a * other.c + b * other.f + c;
		double nd = d * other.a + e * other.d;
		double ne = d * other.b + e * other.e;
		double nf = d * other.c + e * other.f + f;

		a = na; b = nb; c = nc;
		d = nd; e = ne; f = nf;
	}

	/*
	 * Transforming points.
	 */

	public final Point apply( Point v )
	{
		double x = v.getX();
		double y = v.getY();

		return new Point( 
			a * x + b * y + c,
			d * x + e * y + f );
	}

	public final Point apply( double x, double y )
	{
		return new Point( 
			a * x + b * y + c,
			d * x + e * y + f );
	}

	public final void applyD( Point v )
	{
		double x = v.getX();
		double y = v.getY();
		
		v.setX( a * x + b * y + c );
		v.setY( d * x + e * y + f );
	}

	/* 
	 * These two routines don't create any new objects since 
	 * doubles can be moved around on the Java stack.  Therefore
	 * they can be a lot more efficient for mathematical computation
	 * than the versions above, even though two calls are involved
	 * instead of one.  These were added late in the game, though,
	 * so it's not clear they'll get used.
	 */

	public final double applyX( double x, double y )
	{
		return a * x + b * y + c;
	}

	public final double applyY( double x, double y )
	{
		return d * x + e * y + f;
	}

	/*
	 * Transforming a vector of points.
	 */

	public final Point[] apply( Point[] vs )
	{
		int len = vs.length;

		Point[] ret = new Point[ len ];
		for( int idx = 0; idx < len; idx++ ) {
			ret[ idx ] = apply( vs[ idx ] );
		}

		return ret;
	}

	public final void applyD( Point[] vs )
	{
		int len = vs.length;
		for( int idx = 0; idx < len; idx++ ) {
			applyD( vs[ idx ] );
		}
	}

	public final Transform invert()
	{
		double det = a * e - b * d; 

		if( det == 0.0 ) {
			throw new IllegalArgumentException( "Non invertible matrix." );
		}

		return new Transform(
			e / det, -b / det, ( b * f - c * e ) / det,
			-d / det, a / det, ( c * d - a * f ) / det );
	}

	public final void invertD()
	{
		double det = a * e - b * d; 

		if( det == 0.0 ) {
			throw new IllegalArgumentException( "Non invertible matrix." );
		}

		double na = e / det;
		double nb = -b / det;
		double nc = ( b * f - c * e ) / det;
		double nd = -d / det;
		double ne = a / det;
		double nf = ( c * d - a * f ) / det;

		a = na; b = nb; c = nc;
		d = nd; e = ne; f = nf;
	}

	/*
	 * Provide the transform matrix to carry the unit interval
	 * on the positive X axis to the line segment from p to q.
	 */
	public static final Transform matchLineSegment( Point p, Point q )
	{
		double px = p.getX();
		double py = p.getY();
		double qx = q.getX();
		double qy = q.getY();

		return new Transform( 
			qx - px, py - qy, px,
			qy - py, qx - px, py );
	}

    /* 
	 * get the transform that carries p1->q1 to p2->q2.
	 */
	public static final Transform matchTwoSegments( 
		Point p1, Point q1, Point p2, Point q2 )
	{
		Transform top1q1 = matchLineSegment( p1, q1 );
		Transform top2q2 = matchLineSegment( p2, q2 );
		return top2q2.compose( top1q1.invert() );
	}

	public final String toString()
	{
		return
			"[ " + a + " " + b + " " + c + " ]\n" +
			"[ " + d + " " + e + " " + f + " ]\n" +
			"[ 0 0 1 ]";
	}

	public final boolean equals( Object other )
	{
		if( !( other instanceof Transform ) ) {
			return false;
		}

		Transform t = (Transform)other;

		return (a == t.a) && (b == t.b) && (c == t.c) &&
			   (d == t.d) && (e == t.e) && (f == t.f);
	}

	public final int hashCode()
	{
		return (new Double( a )).hashCode() ^
			(new Double( b )).hashCode() ^
			(new Double( c )).hashCode() ^
			(new Double( d )).hashCode() ^
			(new Double( e )).hashCode() ^
			(new Double( f )).hashCode();
	}

	/*
	 * Returns true if this transform includes a reflection.
	 */
	public final boolean flips()
	{
		return (a*e - b*d) < 0.0;
	}
}
