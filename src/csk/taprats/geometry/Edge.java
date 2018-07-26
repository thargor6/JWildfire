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
 * Edge.java
 *
 * The edge component of the planar map abstraction.
 */

package csk.taprats.geometry;

public class Edge
{
	Map 		map;

	Vertex 		v1;
	Vertex 		v2;

	Object 		data;

	Edge( Map map, Vertex v1, Vertex v2 )
	{
		this.map = map;

		this.v1 = v1;
		this.v2 = v2;
	}

	public final Vertex getV1()
	{
		return v1;
	}

	public final Vertex getV2()
	{
		return v2;
	}

	public final Object getData()
	{
		return data;
	}

	public final void setData( Object data )
	{
		this.data = data;
	}

	public final Map getMap()
	{
		return map;
	}

	public final Vertex getOther( Vertex v )
	{
		if( v.equals( v1 ) ) {
			return v2;
		} else {
			return v1;
		}
	}

	/*
	 * Used to sort the edges in the map.
	 */
	public final double getMinX()
	{
		return Math.min( v1.getPosition().getX(), v2.getPosition().getX() );
	}
}
