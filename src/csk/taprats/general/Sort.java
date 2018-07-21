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
 * Sort.java
 *
 * An algorithm for sorting vectors of objects.  Taken directly from the 
 * quicksort pseudocode on page 154 of CLR (The White Book).  Comes complete
 * with a little test program that sorts vectors of integers.
 *
 */

package csk.taprats.general;

import java.util.Vector;
import java.io.*;

public class Sort
{
	public static void quickSort( Vector v, int i1, int i2, Comparison comp )
	{
		if( i1 < i2 ) {
			int q = partition( v, i1, i2, comp );
			quickSort( v, i1, q, comp );
			quickSort( v, q + 1, i2, comp );
		}
	}

	private static int partition( Vector v, int i1, int i2, Comparison comp )
	{
		Object x = v.elementAt( i1 );
		int i = i1 - 1;
		int j = i2 + 1;

		while( true ) {
			do --j; while( comp.compare( v.elementAt( j ), x ) > 0 );
			do ++i; while( comp.compare( v.elementAt( i ), x ) < 0 );

			if( i < j ) {
				Object t1 = v.elementAt( i );
				Object t2 = v.elementAt( j );
				v.setElementAt( t1, j );
				v.setElementAt( t2, i );
			} else {
				return j;
			}
		}
	}

	public static final void main( String[] args )
		throws java.io.IOException
	{
		Vector v = new Vector();
		Comparison c = new Comparison() {
			public int compare( Object a, Object b )
			{
				return ((Integer)a).intValue() - ((Integer)b).intValue();
			}
		};

		StreamTokenizer st = new StreamTokenizer( 
			new InputStreamReader( System.in ) );
		while( true ) {
			int tt = st.nextToken();
			if( tt == st.TT_EOF ) {
				break;
			}
			v.addElement( new Integer( (int)( st.nval ) ) );
		}

		quickSort( v, 0, v.size() - 1, c );

		for( int idx = 0; idx < v.size(); ++idx ) {
			System.out.println( v.elementAt( idx ) );
		}
	}
}
