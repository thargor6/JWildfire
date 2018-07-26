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

public class Line extends Primitive
{
	private double x;
	private double y;

	private double x2;
	private double y2;
	
	private double color=0.0;

	public Line( double x, double y, double x2, double y2, double color )
	{
		super(2);
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
		this.color=color;
	}

	public Line( Point pt, double dx, double dy , double color)
	{
		super(2);
		this.x = pt.getX();
		this.y = pt.getY();
		this.x2 = this.x+dx;
		this.y2 = this.y+dy;
		this.color=color;
	}

	public Line( Point tl, Point t2, double color )
	{
		super(2);
		this.x = tl.getX();
		this.y = tl.getY();

		this.x2 = t2.getX();
		this.y2 = t2.getY();
		this.color=color;
	}

	public Line( double x, double y, double x2, double y2)
	{
		super(2);
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;

	}

	public Line( Point pt, double dx, double dy)
	{
		super(2);
		this.x = pt.getX();
		this.y = pt.getY();
		this.x2 = this.x+dx;
		this.y2 = this.y+dy;
	}

	public Line( Point tl, Point t2 )
	{
		super(2);
		this.x = tl.getX();
		this.y = tl.getY();

		this.x2 = t2.getX();
		this.y2 = t2.getY();
	}
	
	public final double getX1()
	{
		return x;
	}

	public final double getX2()
	{
		return x2;
	}

	public final double getY2()
	{
		return y2;
	}

	public final double getY1()
	{
		return y;
	}

	public final double getColor()
	{
		return this.color;
	}
	
	public final String toString()
	{
		return "[ type: " + gettype() + " (" + x + ", " + y + "); (" + x2 + ", " + y2 + ") color: " + color +"]" ;
	}
}
