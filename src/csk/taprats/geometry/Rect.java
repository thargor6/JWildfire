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

/*
 * Rect.java
 *
 * A Rectangle based on Point.
 */

package csk.taprats.geometry;

public class Rect
{
	private double x;
	private double y;

	private double width;
	private double height;

	public Rect( double x, double y, double width, double height )
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rect( Point pt, double width, double height )
	{
		this.x = pt.getX();
		this.y = pt.getY();
		this.width = width;
		this.height = height;
	}

	public Rect( Point tl, Point br )
	{
		this.x = tl.getX();
		this.y = tl.getY();

		this.width = (br.getX() - this.x);
		this.height = (this.y - br.getY());
	}

	public final double getX()
	{
		return x;
	}

	public final double getLeft()
	{
		return x;
	}

	public final double getTop()
	{
		return y;
	}

	public final double getY()
	{
		return y;
	}

	public final double getWidth()
	{
		return width;
	}

	public final double getHeight()
	{
		return height;
	}

	public final double getRight()
	{
		return x + width;
	}

	public final double getBottom()
	{
		return y - height;
	}

	public final Rect union( Rect other )
	{
		double nx = Math.min( x, other.x );
		double ny = Math.max( y, other.y );
		double nrx = Math.max( x + width, other.x + other.width );
		double nry = Math.min( y - height, other.y - other.height );

		return new Rect( nx, ny, nrx - nx, ny - nry );
	}

	public final Rect intersection( Rect other )
	{
		double nx = Math.max( x, other.x );
		double ny = Math.min( y, other.y );
		double nrx = Math.min( x + width, other.x + other.width );
		double nry = Math.max( y - height, other.y - other.height );

		if( nrx < nx || nry > ny ) {
			return null;
		} else {
			return new Rect( nx, ny, nrx - nx, ny - nry );
		}
	}

	public final Rect centralScale( double s )
	{
		double cx = x + width * 0.5;
		double cy = y - height * 0.5;

		double nw = width * s;
		double nh = height * s;

		return new Rect( cx - nw * 0.5, cy + nh * 0.5, nw, nh );
	}

	/*
	 * A useful routine when doing things like printing to
	 * postscript.  Given two rectangles, find a good transform
	 * for centering the first one inside the second one.
	 */
	public final Transform centerInside( Rect other )
	{
		// System.out.println( "Make " + this + " fit inside " + other );
		double xscale = other.width / width;
		double yscale = other.height / height;

		double scale = Math.min( xscale, yscale );

		Transform Ts = Transform.scale( scale, scale );

		Point my_center = new Point( x + (width / 2.0), y - (height / 2.0) );
		Point your_center = new Point( 
			other.x + (other.width / 2.0), other.y - (other.height / 2.0) );

		return Transform.translate( your_center ).compose( Ts ).compose(
			Transform.translate( my_center.scale( -1.0 ) ) );
	}

	public final String toString()
	{
		return "[ (" + x + ", " + y + "); (" + width + ", " + height + ") ]";
	}
}
