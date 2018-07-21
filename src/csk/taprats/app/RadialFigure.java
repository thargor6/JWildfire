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
 * RadialFigure.java
 *
 * A RadialFigure is a special kind of Figure that has d_n symmetry.  That
 * means that it can be rotated by 360/n degrees and flipped across certain
 * lines through the origin and it looks the same.
 *
 * We take advantage of this by only making subclasses produce a basic
 * unit, i.e. a smaller map that generates the complete figure through the
 * action of c_n (just the rotations; the reflections are factored in
 * by subclasses).
 */

package csk.taprats.app;

import java.util.Vector;

import csk.taprats.geometry.*;

public abstract class RadialFigure
	extends Figure
	implements Cloneable
{
	protected int 			n;
	protected double		dn;
	protected double 		don;
	protected Transform 	Tr;

	protected double 		time;

	protected RadialFigure( int n )
	{
		this.n = n;
		this.dn = (double)n;
		this.don = 1.0 / this.dn;
		this.Tr = Transform.rotate( 2.0 * Math.PI * don );

		this.time = 0.0;
	}

	abstract public Object clone();

	// Get the point frac of the way around the unit circle.
	protected static Point getArc( double frac )
	{
		double ang = frac * 2.0 * Math.PI;
		return new Point( Math.cos( ang ), Math.sin( ang ) );
	}

	// Subclasses provide a method for getting the basic unit.
	abstract public Map buildUnit();

	// Apply c_n to get a complete map from unit.
	protected Map getMap( Map unit )
	{
		Map ret = new Map();

		Vector transforms = new Vector( n );
		Transform base = (Transform)Tr.clone();
		for( int idx = 0; idx < n; ++idx ) {
			transforms.addElement( base );
			base = base.compose( Tr );
		}

		ret.mergeSimpleMany( unit, transforms );

		return ret;
	}

	public Map getMap()
	{
		Map unit = buildUnit();
		return getMap( unit );
	}

	public void setN(int n)
	{
		this.n=n;
	}
	
	public int getN()
	{
		return n;
	}
}
