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
 * Tiling.java
 *
 * The representation of a tiling, which will serve as the skeleton for
 * Islamic designs.  A Tiling has two translation vectors and a set of
 * PlacedFeatures that make up a translational unit.  The idea is that
 * the whole tiling can be replicated across the plane by placing
 * a copy of the translational unit at every integer linear combination
 * of the translation vectors.  In practice, we only draw at those
 * linear combinations within some viewport.
 *
 * This class also has a whole pile of static helper methods for building
 * Tiling objects, and possibly placing them in a static dictionary of
 * built-in tilings.  The reason the helper functions may seem a bit
 * more complicated than necessary is that the code that creates the 
 * built-in tilings (in the static {} block at the bottom of the file) is 
 * generated automatically from the DesignerPanel tool.  The helper
 * functions help simplify the automatic code generation.
 *
 * The helper functions are also used by a system for reading tilings from
 * a file.  This could be useful if I decide to maintain a library of
 * tilings on the server side and serve them up dynamically.
 */

package csk.taprats.tile;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.*;

import csk.taprats.geometry.*;

public class Tiling
{
	private Point	t1;
	private Point	t2;

	private Vector/*of PlacedFeature*/	features;

	private String	name;
	private String 	desc;

	public Tiling( String name, Point t1, Point t2 )
	{
		this.t1 = t1;
		this.t2 = t2;
		this.features = new Vector();
		this.name = name;
		this.desc = null;
	}

	public void setDescription( String desc )
	{
		this.desc = desc;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return desc;
	}

	public void add( PlacedFeature pf ) 
	{
		features.addElement( pf );
	}

	public void add( Feature f, Transform T )
	{
		add( new PlacedFeature( f, T ) );
	}

	public final int numFeatures()
	{
		return features.size();
	}

	public final PlacedFeature getFeature( int n )
	{
		return (PlacedFeature)( features.elementAt( n ) );
	}

	public final Point getTrans1()
	{
		return t1;
	}

	public final Point getTrans2()
	{
		return t2;
	}

	/*
	 * The built-in library of tiling types.
	 */
	public static Hashtable 	builtins;

	public static Tiling find( String name )
	{
		if( builtins.containsKey( name ) ) {
			return (Tiling)( builtins.get( name ) );
		} else {
			return null;
		}
	}

	public static final int numTilings()
	{
		return builtins.size();
	}

	public static final Enumeration getTilingNames()
	{
		return builtins.keys();
	}

	public static final Enumeration getTilings()
	{
		return builtins.elements();
	}

	//
	// The helper functions for constructing tilings.
	//

	private static String 		b_name;
	private static String		b_desc;
	private static Point 		b_t1;
	private static Point 		b_t2;
	private static Vector 		b_pfs;
	private static Feature 		b_f;
	private static Point[] 		b_pts;
	private static int 			b_ct;

	private static void beginTiling( String name )
	{
		b_t1 = null;
		b_t2 = null;
		b_pts = null;
		b_f = null;
		b_pfs = new Vector();
		b_desc = null;

		b_name = name;
	}

	private static void setTranslations( Point t1, Point t2 )
	{
		b_t1 = t1;
		b_t2 = t2;
	}

	private static void beginPolygonFeature( int n )
	{
		b_pts = new Point[ n ];
		b_ct = 0;
	}

	private static void addPoint( Point pt ) 
	{
		b_pts[ b_ct ] = pt;
		++b_ct;
	}

	private static void commitPolygonFeature()
	{
		b_f = new Feature( b_pts );
	}

	private static void addPlacement( Transform T )
	{
		b_pfs.addElement( new PlacedFeature( b_f, T ) );
	}

    private static void endFeature()
	{
		// Don't really need to do anything.
		b_f = null;
		b_pts = null;
		b_ct = 0;
	}

	private static void beginRegularFeature( int n )
	{
		b_f = new Feature( n );
	}

	private static void endTiling()
	{
		Tiling tiling = new Tiling( b_name, b_t1, b_t2 );
		for( int idx = 0; idx < b_pfs.size(); ++idx ) {
			tiling.add( (PlacedFeature)( b_pfs.elementAt( idx ) ) ); 
		}

		if( b_desc != null ) {
			tiling.setDescription( b_desc );
		}

		builtins.put( b_name, tiling );
	}

	//
	// Functions for reading tilings in from files.  No real error-checking
	// is done, because I don't expect you to write these files by hand --
	// they should be auto-generated from DesignerPanel.
	//

	private static final double nextDouble( StreamTokenizer st )
		throws IOException
	{
		st.nextToken();
		return (new Double( st.sval )).doubleValue();
	}

	public static Tiling readTiling( Reader r )
		throws IOException
	{
		StreamTokenizer st = new StreamTokenizer( r );

		// reset the tokenizer to not recognize numbers, since its
		// handling of numbers is impovrished.  We can't just call
		// wordChars(): in StreamTokenizer.java, you can see that
		// 'being in a word' and 'being in a number' are _independent_ (!)
		// So first turn off everything using ordinaryChars()
		st.ordinaryChars( '0', '9' );
		st.wordChars( '0', '9' );
		st.ordinaryChar( '.' );
		st.wordChars( '.', '.' );
		st.ordinaryChar( '-' );
		st.wordChars( '-', '-' );

		// Turn on all possible commenting styles.  The actual syntax
		// of the file is trivial, so there's no reason not to give
		// everyone their favorite commenting style.
		st.commentChar( '#' );
		st.commentChar( '%' );
		st.slashSlashComments( true );
		st.slashStarComments( true );

		st.nextToken();
		if( !st.sval.equals( "tiling" ) ) {
			System.err.println( "This isn't a tiling file." );
			return null;
		}
		st.nextToken();
		beginTiling( st.sval );

		st.nextToken();
		int nf = Integer.parseInt( st.sval );

		double x1 = nextDouble( st );
		double y1 = nextDouble( st );
		double x2 = nextDouble( st );
		double y2 = nextDouble( st );

		setTranslations( new Point( x1, y1 ), new Point( x2, y2 ) );

		for( int idx = 0; idx < nf; ++idx ) {
			st.nextToken();
			boolean reg = false;
			if( st.sval.equals( "regular" ) ) {
				reg = true;
			}

			st.nextToken();
			int num_sides = Integer.parseInt( st.sval );
			st.nextToken();
			int num_xforms = Integer.parseInt( st.sval );

			if( reg ) {
				beginRegularFeature( num_sides );
			} else {
				beginPolygonFeature( num_sides );
				for( int v = 0; v < num_sides; ++v ) {
					x1 = nextDouble( st );
					y1 = nextDouble( st );
					addPoint( new Point( x1, y1 ) );
				}
				commitPolygonFeature();
			}

			for( int jdx = 0; jdx < num_xforms; ++jdx ) {
				double a = nextDouble( st );
				double b = nextDouble( st );
				double c = nextDouble( st );
				double d = nextDouble( st );
				double e = nextDouble( st );
				double f = nextDouble( st );

				addPlacement( new Transform( a, b, c, d, e, f ) );
			}
			endFeature();
		}

		Tiling tiling = new Tiling( b_name, b_t1, b_t2 );
		for( int idx = 0; idx < b_pfs.size(); ++idx ) {
			tiling.add( (PlacedFeature)( b_pfs.elementAt( idx ) ) ); 
		}

		return tiling;
	}

	// The code to create the built-in tiling types.

	static {
		try {
		builtins = new Hashtable();

        beginTiling( "csk_7" );

        setTranslations(
            new Point( -3.3375233734285267E-16, 3.899711648727295 ),
            new Point( 4.89008373582526, -1.8488927466117464E-32 ) );

        beginPolygonFeature( 8 );
        addPoint( new Point( 1.0, 0.48157461880752866 ) );
        addPoint( new Point( 1.7530203962825335, 1.082088346128531 ) );
        addPoint( new Point( 1.3351256037378867, 1.949855824363648 ) );
        addPoint( new Point( 1.753020396282533, 2.817623302598764 ) );
        addPoint( new Point( 0.9999999999999999, 3.418137029919767 ) );
        addPoint( new Point( 0.2469796037174669, 2.817623302598764 ) );
        addPoint( new Point( 0.6648743962621135, 1.9498558243636477 ) );
        addPoint( new Point( 0.24697960371746702, 1.082088346128531 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
            1.0000000000000002, 1.6653345369377348E-16, 2.4450418679126296,
            -1.6653345369377348E-16, 1.0000000000000002, -1.949855824363648 ) );      
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 7 );
        addPlacement( new Transform(
            0.9009688679024195, 0.43388373911755823, 2.0000000000000004,
            -0.43388373911755823, 0.9009688679024195, -2.220446049250313E-16 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.9009688679024193, 0.43388373911755795, -0.44504186791262884,
            -0.43388373911755795, 0.9009688679024193, 1.9498558243636475 ) );
        addPlacement( new Transform(
            0.623489801858734, 0.7818314824680299, 2.4450418679126296,
            -0.7818314824680299, 0.623489801858734, 1.949855824363648 ) );
        endFeature();

		b_desc = 
			"A novel tiling based on an arrangement of heptagons with " +
			"octagonal regions filling in the gaps.  This tiling would " +
			"probably not have been used by Islamic artisans.";

        endTiling();


        beginTiling( "4.8^2" );

        setTranslations(
            new Point( 1.6653345369377358E-16, 1.9999999999999987 ),
            new Point( 2.000000000000001, -4.996003610813204E-16 ) );

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            0.29289321881345176, -0.29289321881345187, 0.9999999999999989,
            0.29289321881345187, 0.29289321881345176, 0.9999999999999999 ) );
        endFeature();

        beginRegularFeature( 8 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = 
			"The classic Archimedean tiling by octagons and squares. " +
			"Extremely important in Islamic design as the source of the " +
			"Seal of Solomon, the [8/3]2 star.";

        endTiling();


        beginTiling( "10" );

        setTranslations(
            new Point( 2.0, -5.551115123125783E-17 ),
            new Point( 0.6180339887498949, 1.9021130325903073 ) );

        beginPolygonFeature( 6 );
        addPoint( new Point( 0.9999999999999996, 0.32491969623290634 ) );
        addPoint( new Point( 1.381966011250105, 0.8506508083520401 ) );
        addPoint( new Point( 2.0, 1.0514622242382679 ) );
        addPoint( new Point( 1.6180339887498947, 1.5771933363574013 ) );
        addPoint( new Point( 1.2360679774997896, 1.0514622242382676 ) );
        addPoint( new Point( 0.6180339887498947, 0.8506508083520403 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 10 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = 
			"A tiling by regular decagons and hexagonal fillers that " +
			"gives rise to one of the most famous of all Islamic patterns.";

        endTiling();


        beginTiling( "6" );

        setTranslations(
            new Point( 2.0, -5.551115123125783E-16 ),
            new Point( 1.0, 1.7320508075688767 ) );

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = "The regular tiling by hexagons.";

        endTiling();


        beginTiling( "4.6.12" );

        setTranslations(
            new Point( 5.265320091679377E-16, 2.5358983848622456 ),
            new Point( 2.1961524227066316, 1.2679491924311224 ) );

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            0.46410161513775433, -1.1102230246251565E-16, 0.7320508075688772,
            1.1102230246251565E-16, 0.46410161513775433, 1.2679491924311224 ) );        addPlacement( new Transform(
            0.2320508075688775, 0.4019237886466846, 1.4641016151377548,
            -0.4019237886466846, 0.2320508075688775, -6.661338147750939E-16 ) );        endFeature();

        beginRegularFeature( 12 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            0.13397459621556135, 0.23205080756887725, 1.098076211353316,
            -0.23205080756887725, 0.13397459621556135, 0.6339745962155612 ) );
        addPlacement( new Transform(
            0.2679491924311227, -3.985521342092001E-18, 2.220446049250313E-16,
            3.985521342092001E-18, 0.2679491924311227, 1.2679491924311226 ) );
        addPlacement( new Transform(
            -0.13397459621556124, 0.23205080756887697, 1.0980762113533153,
            -0.23205080756887697, -0.13397459621556124, -0.6339745962155618 ) );        endFeature();

		b_desc = 
			"Another of the semiregular, or Archimedean tilings, " + 
			"4.6.12 is sometimes encountered as an Islamic design " +
			"as is, without any further embellishment.";

        endTiling();

        beginTiling( "3.12^2" );

        setTranslations(
            new Point( 0, 2 ),
            new Point( 1.7320508075688772, 1 ) );

        beginRegularFeature( 3 );
        addPlacement( new Transform(
            0.07735026918962594, -0.13397459621556157, 1.1547005383792515,
            0.13397459621556157, 0.07735026918962594, 0 ) );
        addPlacement( new Transform(
            0.15470053837925152, 0, 0.5773502691896257,
            0, 0.15470053837925152, 1 ) );
        endFeature();

        beginRegularFeature( 12 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = 
			"The Archimedean tiling by dodecagons and triangles. " +
			"Very common in the construction of Islamic designs.";

        endTiling();
		
        beginTiling( "4^4" );
        
        setTranslations(
        new Point( 0.0, 2.0 ),
        new Point( 2.0, 0.0 ) );
 
        beginRegularFeature( 4 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = "The plain old tiling by squares.";
 
        endTiling();

        beginTiling( "csk_5" );
        setTranslations(
            new Point( 2.2204460492503126E-16, 2.3511410091698925 ),
            new Point( 3.618033988749896, 1.1755705045849458 ) );
 
        beginRegularFeature( 5 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.8090169943749477, -0.5877852522924732, 2.0,
            0.5877852522924732, 0.8090169943749477, -3.3306690738754696E-16 ) );
        endFeature();
 
        beginPolygonFeature( 4 );
        addPoint( new Point( -0.38196601125010476, 1.1755705045849458 ) );
        addPoint( new Point( 0.9999999999999998, 0.7265425280053609 ) );
        addPoint( new Point( 2.3819660112501055, 1.1755705045849463 ) );
        addPoint( new Point( 0.9999999999999991, 1.624598481164532 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = 
			"A tiling by pentagons and rhombs derived from the example " +
			"in figure 1.5.11 of Grunbaum and Shephard.";
 
        endTiling();


        beginTiling( "csk_9" );
 
        setTranslations(
            new Point( -8.870997979654522E-16, 3.464101615137757 ),
            new Point( 3.0000000000000027, 1.7320508075688774 ) );
 
        beginRegularFeature( 6 );
        addPlacement( new Transform(
            0.701866670643066, -0.4052229112310017, 1.0000000000000018,
            0.4052229112310017, 0.701866670643066, 1.732050807568877 ) );
        endFeature();
 
        beginPolygonFeature( 3 );
        addPoint( new Point( 0.5320888862379561, 0.9216049851068762 ) );
        addPoint( new Point( 1.0000000000000002, 0.3639702342662023 ) );
        addPoint( new Point( 1.4679111137620449, 0.9216049851068764 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            0.5000000000000002, 0.8660254037844377, -0.9999999999999998,
            -0.8660254037844377, 0.5000000000000002, 1.7320508075688763 ) );
        addPlacement( new Transform(
            -0.5000000000000007, -0.8660254037844385, 3.0000000000000027,
            0.8660254037844385, -0.5000000000000007, 1.732050807568878 ) );
        addPlacement( new Transform(
            -1.000000000000001, 4.440892098500626E-16, 2.000000000000002,
            -4.440892098500626E-16, -1.000000000000001, 3.464101615137757 ) );
        addPlacement( new Transform(
            0.4999999999999999, -0.8660254037844388, 2.0000000000000018,
            0.8660254037844388, 0.4999999999999999, -2.220446049250313E-16 ) );
        addPlacement( new Transform(
            -0.5000000000000013, 0.8660254037844404, -8.881784197001252E-16,
            -0.8660254037844404, -0.5000000000000013, 3.4641016151377575 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();
 
        beginRegularFeature( 9 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.9396926207859095, -0.3420201433256686, 2.000000000000001,
            0.3420201433256686, 0.9396926207859095, -1.0547118733938987E-15 ) );
        endFeature();
 
		b_desc = 
			"A really unusual base for an Islamic design, derived from " +
			"the star tiling in figure 2.5.4(m) of Grunbaum and Shephard. " +
			"Probably wouldn't appear in historial work because the regular " +
			"polygons don't line up edge-to-edge.";

        endTiling();

        beginTiling( "18" );
 
        setTranslations(
            new Point( 0.3472963553338614, 1.969615506024418 ),
            new Point( 1.8793852415718204, 0.6840402866513363 ) );
 
        beginPolygonFeature( 6 );
        addPoint( new Point( -0.6527036446661408, 0.7778619134302066 ) );
        addPoint( new Point( -0.3472963553338607, 0.9541888941386714 ) );
        addPoint( new Point( 5.551115123125783E-17, 1.0154266118857453 ) );
        addPoint( new Point( -0.30540728933227923, 1.1917535925942109 ) );
        addPoint( new Point( -0.5320888862379569, 1.461902200081545 ) );
        addPoint( new Point( -0.5320888862379574, 1.1092482386646145 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.49999999999999767, -0.8660254037844398, 1.8793852415718184,
            0.8660254037844398, 0.49999999999999767, 0.6840402866513395 ) );
        endFeature();
 
        beginRegularFeature( 18 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = 
			"A simple modification of the 3.12^2 tiling yields " +
			"this variant with 18-gons and 3-star fillers.  Also found " +
			"in figure 2.5.4(m) of Grunbaum and Shephard. ";

		endTiling();

        beginTiling( "8.12" );
 
        setTranslations(
            new Point( 6.049433830906403E-16, 3.2937731487882695 ),
            new Point( 1.6468865743941357, 1.6468865743941352 ) );
 
        beginRegularFeature( 12 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();
 
        beginPolygonFeature( 6 );
        addPoint( new Point( 0.26794919243112286, 1.0 ) );
        addPoint( new Point( 0.7320508075688773, 0.7320508075688772 ) );
        addPoint( new Point( 0.9999999999999999, 0.26794919243112264 ) );
        addPoint( new Point( 1.378937381963013, 0.6468865743941334 ) );
        addPoint( new Point( 0.9148357668252576, 0.9148357668252568 ) );
        addPoint( new Point( 0.6468865743941348, 1.3789373819630117 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            -1.6653345369377348E-16, 0.9999999999999989, 7.771561172376096E-16,
            -0.9999999999999989, -1.6653345369377348E-16, -7.216449660063518E-16 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();
 
        beginRegularFeature( 8 );
        addPlacement( new Transform(
            -3.937023596388081E-17, -0.6468865743941347, 1.6653345369377348E-16,
            0.6468865743941347, -3.937023596388081E-17, 1.6468865743941348 ) );
        endFeature();
 
		b_desc = 
			"A tiling by octagons, dodecagons and hexagonal fillers " +
            "derived by examination of an Islamic pattern from Bougoin.";

        endTiling();


        beginTiling( "9.12" );
 
        setTranslations(
            new Point( 1.5035797577461385, 2.6042765336484153 ),
            new Point( 3.007159515492277, -5.551115123125783E-16 ) );
 
        beginRegularFeature( 12 );
        addPlacement( new Transform(
            0.9999999999999999, -5.551115123125783E-17, 5.551115123125783E-17,
            5.551115123125783E-17, 0.9999999999999999, 0.0 ) );
        endFeature();
 
        beginPolygonFeature( 6 );
        addPoint( new Point( 0.9999999999999999, 0.26794919243112214 ) );
        addPoint( new Point( 0.9999999999999996, -0.26794919243112403 ) );
        addPoint( new Point( 1.5035797577461378, -0.08466115003254471 ) );
        addPoint( new Point( 2.007159515492278, -0.26794919243112264 ) );
        addPoint( new Point( 2.007159515492278, 0.26794919243112225 ) );
        addPoint( new Point( 1.5035797577461387, 0.0846611500325426 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            -0.4999999999999996, -0.866025403784437, -1.2767564783189298E-15,
            0.866025403784437, -0.4999999999999996, 1.221245327087672E-15 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.4999999999999984, -0.8660254037844374, 9.992007221626405E-16,
            0.8660254037844374, 0.4999999999999984, 1.9984014443252814E-15 ) );
        endFeature();
 
        beginRegularFeature( 9 );
        addPlacement( new Transform(
            0.7250000612042714, -0.12783707180560153, 1.5035797577461387,
            0.12783707180560153, 0.7250000612042714, 0.8680921778828049 ) );
        addPlacement( new Transform(
            0.7250000612042713, 0.12783707180560155, 2.220446049250313E-16,
            -0.12783707180560155, 0.7250000612042713, 1.7361843557656105 ) );
        endFeature();
 
		b_desc = 
			"A tiling by nonagons, dodecagons and hexagonal fillers " +
            "derived by examination of an Islamic pattern from " +
			"Abas and Salman.";

        endTiling();

        beginTiling( "square_12" );
 
        setTranslations(
            new Point( 4.440892098500627E-16, 1.9999999999999991 ),
            new Point( 2.000000000000001, -3.9968028886505635E-15 ) );
 
        beginRegularFeature( 12 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();
 
        beginPolygonFeature( 8 );
        addPoint( new Point( 0.2679491924311229, 0.9999999999999997 ) );
        addPoint( new Point( 0.7320508075688773, 0.7320508075688772 ) );
        addPoint( new Point( 0.9999999999999991, 0.26794919243111953 ) );
        addPoint( new Point( 1.2679491924311224, 0.7320508075688748 ) );
        addPoint( new Point( 1.7320508075688779, 0.9999999999999978 ) );
        addPoint( new Point( 1.2679491924311228, 1.2679491924311208 ) );
        addPoint( new Point( 1.0, 1.732050807568876 ) );
        addPoint( new Point( 0.7320508075688775, 1.2679491924311224 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();
 
		b_desc = 
			"A dodecagonal alternative to 3.12^2, 12-gons placed in a " +
			"square arrangement with 4-star fillers.";

        endTiling();
		
        beginTiling( "16.8" );

        setTranslations(
            new Point( -8.625193531108086E-17, 2.9604338701034165 ),
            new Point( 1.4802169350517105, 1.4802169350517103 ) );

        beginRegularFeature( 8 );
        addPlacement( new Transform(
            0.4802169350517094, -6.435664431831275E-17, 1.3877787807814457E-16,
            6.435664431831275E-17, 0.4802169350517094, 1.4802169350517094 ) );
        endFeature();

        beginRegularFeature( 16 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginPolygonFeature( 8 );
        addPoint( new Point( 0.1989123673796581, 1.0 ) );
        addPoint( new Point( 0.5664544973505217, 0.8477590650225735 ) );
        addPoint( new Point( 0.8477590650225735, 0.5664544973505214 ) );
        addPoint( new Point( 1.0, 0.19891236737965795 ) );
        addPoint( new Point( 1.2813045676720523, 0.48021693505171037 ) );
        addPoint( new Point( 0.9137624377011893, 0.6324578700291367 ) );
        addPoint( new Point( 0.6324578700291368, 0.9137624377011888 ) );
        addPoint( new Point( 0.4802169350517105, 1.2813045676720518 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            -3.3306690738754696E-16, -1.0000000000000013, 1.2212453270876722E-15,
            1.0000000000000013, -3.3306690738754696E-16, -6.661338147750939E-16 ) );
        endFeature();

		b_desc = 
			"A tiling constructing by random exploration with the tiling " +
			"design tool that complements Taprats.  I'm not sure if this " +
			"tiling occurs in Islamic art, but I wouldn't be surprised.";

        endTiling();

        //new
        
        beginTiling("12.18");

        setTranslations(new Point(0.9822292928345829D, 2.698652802789897D), new Point(2.828216529627419D, 0.4986908814589764D));

        beginRegularFeature(12);
        addPlacement(new Transform(0.4229936183964185D, 0.5041041643683555D, 1.270148607487334D, -0.5041041643683555D, 0.4229936183964185D, 1.065781228082957D));

        addPlacement(new Transform(0.4229936183964193D, 0.5041041643683561D, 1.558067922140086D, -0.5041041643683561D, 0.4229936183964193D, -0.5670903466239823D));

        endFeature();

        beginRegularFeature(18);
        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D));

        endFeature();

        beginPolygonFeature(10);
        addPoint(new Point(0.6527036446661394D, 0.7778619134302061D));
        addPoint(new Point(0.5914659269190655D, 1.125158268764067D));
        addPoint(new Point(0.7120806853472488D, 1.456544593998474D));
        addPoint(new Point(0.9822292928345831D, 1.683226190904152D));
        addPoint(new Point(0.6349329375007224D, 1.744463908651225D));
        addPoint(new Point(0.329525648168443D, 1.920790889359691D));
        addPoint(new Point(0.3907633659155169D, 1.57349453402583D));
        addPoint(new Point(0.2701486074873334D, 1.242108208791422D));
        addPoint(new Point(0.0D, 1.015426611885745D));
        addPoint(new Point(0.3472963553338608D, 0.9541888941386711D));
        commitPolygonFeature();

        addPlacement(new Transform(0.5D, 0.8660254037844395D, 0.0D, -0.8660254037844395D, 0.5D, 0.0D));

        addPlacement(new Transform(0.5D, -0.8660254037844382D, 0.0D, 0.8660254037844382D, 0.5D, 0.0D));

        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D));

        endFeature();

        //setAuthor( "Craig S. Kaplan" );

        endTiling();

        beginTiling("alhambra16");

        setTranslations(new Point(0.0D, 2.735084259941728D), new Point(2.735084259941727D, 0.0D));

        beginRegularFeature(8);
        addPlacement(new Transform(0.1837710649854322D, 0.4436625974595775D, 1.367542129970865D, -0.4436625974595775D, 0.1837710649854322D, 2.168629762591206D));

        addPlacement(new Transform(0.4436625974595772D, 0.183771064985432D, 1.367542129970864D, -0.183771064985432D, 0.4436625974595772D, 0.5664544973505217D));

        addPlacement(new Transform(0.4436625974595779D, 0.1837710649854321D, 2.168629762591206D, -0.1837710649854321D, 0.4436625974595779D, 1.367542129970865D));

        addPlacement(new Transform(0.183771064985432D, 0.4436625974595772D, 0.5664544973505228D, -0.4436625974595772D, 0.183771064985432D, 1.367542129970864D));

        endFeature();

        beginPolygonFeature(20);
        addPoint(new Point(0.5664544973505226D, 0.8477590650225736D));
        addPoint(new Point(0.8477590650225735D, 0.5664544973505217D));
        addPoint(new Point(1.0D, 0.9339966273213854D));
        addPoint(new Point(1.367542129970864D, 1.086237562298812D));
        addPoint(new Point(1.735084259941728D, 0.9339966273213856D));
        addPoint(new Point(1.887325194919154D, 0.5664544973505218D));
        addPoint(new Point(2.168629762591206D, 0.8477590650225736D));
        addPoint(new Point(1.801087632620342D, 1.0D));
        addPoint(new Point(1.648846697642915D, 1.367542129970865D));
        addPoint(new Point(1.801087632620342D, 1.73508425994173D));
        addPoint(new Point(2.168629762591207D, 1.887325194919156D));
        addPoint(new Point(1.887325194919156D, 2.168629762591206D));
        addPoint(new Point(1.735084259941729D, 1.801087632620342D));
        addPoint(new Point(1.367542129970865D, 1.648846697642915D));
        addPoint(new Point(1.0D, 1.801087632620342D));
        addPoint(new Point(0.847759065022574D, 2.168629762591206D));
        addPoint(new Point(0.5664544973505228D, 1.887325194919154D));
        addPoint(new Point(0.9339966273213867D, 1.735084259941728D));
        addPoint(new Point(1.086237562298813D, 1.367542129970864D));
        addPoint(new Point(0.9339966273213867D, 1.D));
        commitPolygonFeature();

        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D));

        endFeature();

        beginRegularFeature(16);
        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D));

        endFeature();

        beginPolygonFeature(6);
        addPoint(new Point(1.0D, -0.1989123673796584D));
        addPoint(new Point(1.367542129970863D, -0.0466714324022323D));
        addPoint(new Point(1.735084259941728D, -0.1989123673796581D));
        addPoint(new Point(1.735084259941728D, 0.198912367379658D));
        addPoint(new Point(1.367542129970864D, 0.04667143240223126D));
        addPoint(new Point(1.0D, 0.198912367379658D));
        commitPolygonFeature();

        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 2.735084259941728D));

        addPlacement(new Transform(0.0D, -1.0D, 2.73508425994173D, 1.0D, 0.0D, 0.0D));

        endFeature();

        b_desc="This tiling leads to one of the most well-known designs in the Alhambra, " +
                 "featuring large 16-pointed stars.  Unfortunately, the Taprats algorithm " +
                 "isn't smart enough to fill in all of the details of the original design. " +
                 "They must be added in by hand.";
        //setAuthor( "Craig S. Kaplan" );

        endTiling();

        beginTiling("8_var");

        setTranslations(new Point(0.8284271247461903D, 1.999999999999999D), new Point(1.999999999999997D, -0.8284271247461841D));

        beginPolygonFeature(8);
        addPoint(new Point(-2.215096782454491D, 0.8491379310344823D));
        addPoint(new Point(-1.629310344827586D, 0.2633514934075772D));
        addPoint(new Point(-1.629310344827585D, -0.5650756313386074D));
        addPoint(new Point(-1.043523907200681D, 0.02071080628829514D));
        addPoint(new Point(-0.215096782454494D, 0.02071080628829469D));
        addPoint(new Point(-0.800883220081397D, 0.606497243915198D));
        addPoint(new Point(-0.800883220081397D, 1.434924368661386D));
        addPoint(new Point(-1.386669657708301D, 0.8491379310344823D));
        commitPolygonFeature();

        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D));

        endFeature();

        beginRegularFeature(8);
        addPlacement(new Transform(1.0D, 0.0D, -2.629310344827586D, 0.0D, 1.0D, -0.1508620689655178D));

        endFeature();

        b_desc="An alternative octagonal tiling from the world of star polygons.";
        //setAuthor( "Craig S. Kaplan" );

        endTiling();

        beginTiling("18.9");

        setTranslations(new Point(2.226681596905676D, 1.285575219373078D), new Point(2.226681596905678D, -1.285575219373081D));

        beginRegularFeature(9);
        addPlacement(new Transform(0.4552382228487901D, -0.1656931626172038D, 1.484454397937118D, 0.1656931626172038D, 0.4552382228487901D, 0.0D));

        addPlacement(new Transform(0.3711135994842802D, -0.311401284452132D, 0.7422271989685589D, 0.311401284452132D, 0.3711135994842802D, -1.285575219373079D));

        endFeature();

        beginPolygonFeature(8);
        addPoint(new Point(0.6527036446661393D, 0.7778619134302061D));
        addPoint(new Point(0.8793852415718167D, 0.5077133059428725D));
        addPoint(new Point(1.0D, 0.176326980708465D));
        addPoint(new Point(1.226681596905678D, 0.4464755881957985D));
        addPoint(new Point(1.573977952239538D, 0.507713305942873D));
        addPoint(new Point(1.34729635533386D, 0.7778619134302068D));
        addPoint(new Point(1.226681596905678D, 1.109248238664613D));
        addPoint(new Point(1.0D, 0.8390996311772799D));
        commitPolygonFeature();

        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D));

        addPlacement(new Transform(-0.5D, 0.8660254037844383D, 2.226681596905677D, -0.8660254037844383D, -0.5D, 1.285575219373079D));

        addPlacement(new Transform(0.5D, 0.8660254037844402D, 0.0D, -0.8660254037844402D, 0.5D, 0.0D));

        endFeature();

        beginRegularFeature(18);
        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D));

        endFeature();

        b_desc="18- and 9-gons make up this tiling derived from plate 136 of Bourgoin.";
        //setAuthor( "Craig S. Kaplan" );

        endTiling();

        beginTiling("24.12");

        setTranslations(new Point(2.237000714909252D, 1.291532964930242D), new Point(2.237000714909248D, -1.291532964930243D));

        beginRegularFeature(12);
        addPlacement(new Transform(0.4913338099395024D, 0.0D, 1.491333809939502D, 0.0D, 0.4913338099395024D, 0.0D));

        addPlacement(new Transform(0.4913338099395D, 0.0D, 0.7456669049697486D, 0.0D, 0.4913338099395D, -1.291532964930241D));

        endFeature();

        beginPolygonFeature(12);
        addPoint(new Point(0.6140144073823544D, 0.8001991549907406D));
        addPoint(new Point(0.8001991549907407D, 0.6140144073823541D));
        addPoint(new Point(0.9318516525781365D, 0.3859855926176456D));
        addPoint(new Point(1.0D, 0.1316524975873958D));
        addPoint(new Point(1.131652497587396D, 0.3596813123521053D));
        addPoint(new Point(1.359681312352106D, 0.4913338099395017D));
        addPoint(new Point(1.622986307526897D, 0.4913338099394999D));
        addPoint(new Point(1.43680155991851D, 0.6775185575478864D));
        addPoint(new Point(1.305149062331114D, 0.9055473723125951D));
        addPoint(new Point(1.237000714909251D, 1.159880467342845D));
        addPoint(new Point(1.105348217321855D, 0.9318516525781366D));
        addPoint(new Point(0.8773194025571464D, 0.8001991549907406D));
        commitPolygonFeature();

        addPlacement(new Transform(-0.5D, 0.866025403784439D, 2.237000714909252D, -0.866025403784439D, -0.5D, 1.291532964930241D));

        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D));

        addPlacement(new Transform(0.5D, 0.8660254037844379D, 0.0D, -0.8660254037844379D, 0.5D, 0.0D));

        endFeature();

        beginRegularFeature(24);
        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D));

        endFeature();

        b_desc="Still under development, from Bourgoin #140.  Note the octagons that aren't represented -- they're not regular!";
        //setAuthor( "Craig S. Kaplan" );

        endTiling();

        beginTiling("14");

        setTranslations(new Point(1.246979603717467D, 1.563662964936059D), new Point(2.493959207434934D, 0.0D));

        beginRegularFeature(14);
        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D));

        endFeature();

        beginPolygonFeature(10);
        addPoint(new Point(0.8019377358048383D, 0.6395240038449662D));
        addPoint(new Point(1.0D, 0.2282434743901499D));
        addPoint(new Point(1.0D, -0.2282434743901502D));
        addPoint(new Point(0.8019377358048383D, -0.6395240038449666D));
        addPoint(new Point(1.246979603717467D, -0.5379461016635061D));
        addPoint(new Point(1.692021471630096D, -0.6395240038449663D));
        addPoint(new Point(1.493959207434934D, -0.22824347439015D));
        addPoint(new Point(1.493959207434934D, 0.22824347439015D));
        addPoint(new Point(1.692021471630096D, 0.639524003844966D));
        addPoint(new Point(1.246979603717467D, 0.5379461016635057D));
        commitPolygonFeature();

        addPlacement(new Transform(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D));

        endFeature();

        b_desc="A lot like the famous 10 tiling, this is a less well known variation with 14-gons.";
        //setAuthor( "Craig S. Kaplan" );

        endTiling();

        beginTiling( "6.5x6" );

        setTranslations(
                new Point( 0.0, 4.809734344744129 ),
                new Point( 4.165352128002921, 2.404867172372068 ) );

        beginRegularFeature( 6 );
        addPlacement( new Transform(
                    1.0, 0.0, 0.0,
                    0.0, 1.0, 0.0 ) );
        endFeature();

        beginPolygonFeature( 8 );
        addPoint( new Point( -1.3884507093343057, 2.4048671723720654 ) );
        addPoint( new Point( -0.24007574132260734, 2.28416809956095 ) );
        addPoint( new Point( 0.0, 1.1547005383792512 ) );
        addPoint( new Point( 0.24007574132260745, 2.28416809956095 ) );
        addPoint( new Point( 1.3884507093343061, 2.404867172372065 ) );
        addPoint( new Point( 0.24007574132260756, 2.525566245183181 ) );
        addPoint( new Point( 0.0, 3.6550338063648793 ) );
        addPoint( new Point( -0.24007574132260723, 2.525566245183181 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
                    1.0, 0.0, 0.0,
                    0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
                    0.5, -0.8660254037844382, 0.0,
                    0.8660254037844382, 0.5, 0.0 ) );
        addPlacement( new Transform(
                    0.5, 0.8660254037844385, 0.0,
                    -0.8660254037844385, 0.5, 0.0 ) );
        endFeature();

        beginRegularFeature( 5 );
        addPlacement( new Transform(
                    0.7259529835575207, 0.3232150924651246, 0.8973272361458824,
                    -0.3232150924651246, 0.7259529835575207, -1.5542163640200255 ) );
        addPlacement( new Transform(
                    0.7772893654845796, 0.16521795495007546, -0.8973272361458834,
                    -0.16521795495007546, 0.7772893654845796, -1.5542163640200253 ) );
        addPlacement( new Transform(
                    0.7946544722917659, 0.0, -1.7946544722917657,
                    0.0, 0.7946544722917659, 0.0 ) );
        addPlacement( new Transform(
                    0.7772893654845793, -0.16521795495007544, -0.8973272361458828,
                    0.16521795495007544, 0.7772893654845793, 1.5542163640200255 ) );
        addPlacement( new Transform(
                    0.6428889727400949, -0.467086179481358, 1.7946544722917663,
                    0.467086179481358, 0.6428889727400949, 0.0 ) );
        addPlacement( new Transform(
                    0.725952983557521, -0.3232150924651245, 0.8973272361458832,
                    0.3232150924651245, 0.725952983557521, 1.554216364020025 ) );
        endFeature();

        b_desc="As seen in the hidden ruins of old Kadath.";
        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "3.4.6" );

        setTranslations(
                new Point( 1.5773502691896257, 2.7320508075688763 ),
                new Point( 3.1547005383792515, 0.0 ) );

        beginRegularFeature( 4 );
        addPlacement( new Transform(
                    0.5, 0.288675134594813, 0.7886751345948131,
                    -0.288675134594813, 0.5, 1.3660254037844384 ) );
        addPlacement( new Transform(
                    0.5, -0.2886751345948128, -0.7886751345948125,
                    0.2886751345948128, 0.5, 1.3660254037844388 ) );
        addPlacement( new Transform(
                    0.0, 0.5773502691896263, 1.577350269189626,
                    -0.5773502691896263, 0.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
                    1.0, 0.0, 0.0,
                    0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 3 );
        addPlacement( new Transform(
                    -0.2886751345948132, 0.1666666666666669, 1.5773502691896257,
                    -0.1666666666666669, -0.2886751345948132, -0.9106836025229609 ) );
        addPlacement( new Transform(
                    0.0, 0.33333333333333354, 1.5773502691896262,
                    -0.33333333333333354, 0.0, 0.9106836025229585 ) );
        endFeature();

        b_desc="Inferred from the study of old manuscript of Al-Alzhared.";
        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "8 Ring" );

        setTranslations(
                new Point( 0.0, 4.828427124746193 ),
                new Point( 4.828427124746193, 0.0 ) );

        beginRegularFeature( 4 );
        addPlacement( new Transform(
                    0.4142135623730951, 0.0, 0.0,
                    0.0, 0.4142135623730951, 1.4142135623730951 ) );
        endFeature();

        beginRegularFeature( 8 );
        addPlacement( new Transform(
                    1.0, 0.0, 0.0,
                    0.0, 1.0, -2.0 ) );
        addPlacement( new Transform(
                    1.0, 0.0, 1.4142135623730954,
                    0.0, 1.0, 1.4142135623730954 ) );
        endFeature();

        beginRegularFeature( 8 );
        addPlacement( new Transform(
                    1.0, 0.0, 0.0,
                    0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
                    1.0, 0.0, 3.4142135623730967,
                    0.0, 1.0, 1.4142135623730954 ) );
        endFeature();

        beginPolygonFeature( 16 );
        addPoint( new Point( 1.0, 0.41421356237309515 ) );
        addPoint( new Point( 1.0, -0.41421356237309576 ) );
        addPoint( new Point( 0.41421356237309714, -1.0 ) );
        addPoint( new Point( 1.0, -1.5857864376269066 ) );
        addPoint( new Point( 1.0, -2.4142135623730976 ) );
        addPoint( new Point( 1.8284271247461918, -2.414213562373097 ) );
        addPoint( new Point( 2.4142135623730963, -3.0 ) );
        addPoint( new Point( 3.0, -2.414213562373097 ) );
        addPoint( new Point( 3.828427124746192, -2.414213562373097 ) );
        addPoint( new Point( 3.8284271247461925, -1.5857864376269064 ) );
        addPoint( new Point( 4.414213562373098, -1.0 ) );
        addPoint( new Point( 3.8284271247461934, -0.41421356237309587 ) );
        addPoint( new Point( 3.8284271247461934, 0.4142135623730943 ) );
        addPoint( new Point( 3.0, 0.41421356237309426 ) );
        addPoint( new Point( 2.414213562373096, 1.0 ) );
        addPoint( new Point( 1.8284271247461912, 0.4142135623730955 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
                    1.0, 0.0, 0.0,
                    0.0, 1.0, 0.0 ) );
        endFeature();

        b_desc="Eight friends around a table.";
        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "8 and 8" );

        setTranslations(
                new Point( 0.0, 4.828427124746193 ),
                new Point( 4.828427124746193, 0.0 ) );

        beginRegularFeature( 4 );
        addPlacement( new Transform(
                    0.4142135623730951, 0.0, 0.0,
                    0.0, 0.4142135623730951, 1.4142135623730951 ) );
        endFeature();

        beginRegularFeature( 8 );
        addPlacement( new Transform(
                    1.0, 0.0, 0.0,
                    0.0, 1.0, -2.0 ) );
        addPlacement( new Transform(
                    1.414213562373094, 0.0, 2.4142135623730985,
                    0.0, 1.414213562373094, -1.0 ) );
        addPlacement( new Transform(
                    1.0, 0.0, 3.4142135623730967,
                    0.0, 1.0, 1.4142135623730954 ) );
        addPlacement( new Transform(
                    1.0, 0.0, 1.4142135623730954,
                    0.0, 1.0, 1.4142135623730954 ) );
        addPlacement( new Transform(
                    1.0, 0.0, 0.0,
                    0.0, 1.0, 0.0 ) );
        endFeature();

        beginPolygonFeature( 3 );
        addPoint( new Point( 1.8284271247461912, 0.4142135623730955 ) );
        addPoint( new Point( 3.0, 0.41421356237309426 ) );
        addPoint( new Point( 2.414213562373096, 1.0 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
                    -0.7071067811865474, 0.7071067811865476, 4.8284271247461925,
                    -0.7071067811865476, -0.7071067811865474, 0.0 ) );
        addPlacement( new Transform(
                    0.7071067811865476, -0.7071067811865482, 0.0,
                    0.7071067811865482, 0.7071067811865476, -2.0 ) );
        addPlacement( new Transform(
                    -0.7071067811865479, -0.7071067811865481, 3.4142135623730985,
                    0.7071067811865481, -0.7071067811865479, -3.4142135623730994 ) );
        addPlacement( new Transform(
                    1.0, 0.0, 0.0,
                    0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
                    0.0, -1.0, 1.4142135623730987,
                    1.0, 0.0, -3.4142135623731 ) );
        addPlacement( new Transform(
                    0.7071067811865472, 0.707106781186547, 1.414213562373099,
                    -0.707106781186547, 0.7071067811865472, 1.4142135623730938 ) );
        addPlacement( new Transform(
                    0.0, 1.0, 3.4142135623730967,
                    -1.0, 0.0, 1.414213562373096 ) );
        addPlacement( new Transform(
                    -1.0, 0.0, 4.828427124746195,
                    0.0, -1.0, -2.0 ) );
        endFeature();

        b_desc="Ninth in the middle.";
        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Seville Alcazar 12.8.6" );

        setTranslations(
                new Point( 0.0, -8.29252873988396 ),
                new Point( 7.181540550352065, 4.146264369941974 ) );

        beginPolygonFeature( 4 );
        addPoint( new Point( 1.816496580927724, -1.3938468501173504 ) );
        addPoint( new Point( 1.8164965809277234, -1.9915638315627247 ) );
        addPoint( new Point( 2.971197119306975, -1.9915638315627247 ) );
        addPoint( new Point( 2.9711971193069755, -1.3938468501173507 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
                    -0.5, 0.8660254037844374, 4.787693700234702,
                    -0.8660254037844374, -0.5, 0.0 ) );
        addPlacement( new Transform(
                    -0.5, -0.8660254037844386, -2.3938468501173586,
                    0.8660254037844386, -0.5, -4.14626436994197 ) );
        addPlacement( new Transform(
                    0.5, 0.8660254037844385, 2.393846850117357,
                    -0.8660254037844385, 0.5, 4.146264369941972 ) );
        addPlacement( new Transform(
                    0.5, -0.8660254037844382, -4.787693700234704,
                    0.8660254037844382, 0.5, 0.0 ) );
        addPlacement( new Transform(
                    1.0, 0.0, -2.393846850117349,
                    0.0, 1.0, 4.146264369941976 ) );
        addPlacement( new Transform(
                    -1.0, 0.0, 2.3938468501173475,
                    0.0, -1.0, -4.146264369941979 ) );
        endFeature();

        beginRegularFeature( 12 );
        addPlacement( new Transform(
                    2.154700538379252, 0.0, 0.0,
                    0.0, 2.154700538379252, 0.0 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 4.5485473884966, -3.56891410075235 ) );
        addPoint( new Point( 4.5485473884965995, -4.723614639131604 ) );
        addPoint( new Point( 5.0661854787016445, -5.0224731298542915 ) );
        addPoint( new Point( 6.181540550352052, -4.723614639131609 ) );
        addPoint( new Point( 6.181540550352054, -3.5689141007523597 ) );
        addPoint( new Point( 5.066185478701646, -3.2700556100296634 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
                    -0.5, -0.8660254037844366, -2.393846850117349,
                    0.8660254037844366, -0.5, -4.146264369941962 ) );
        addPlacement( new Transform(
                    0.5, -0.8660254037844369, -4.787693700234691,
                    0.8660254037844369, 0.5, 0.0 ) );
        addPlacement( new Transform(
                    1.0, 0.0, -2.393846850117349,
                    0.0, 1.0, 4.146264369941976 ) );
        addPlacement( new Transform(
                    -1.0, 0.0, 2.393846850117338,
                    0.0, -1.0, -4.1462643699419655 ) );
        addPlacement( new Transform(
                    0.5, 0.866025403784437, 2.3938468501173524,
                    -0.866025403784437, 0.5, 4.146264369941976 ) );
        addPlacement( new Transform(
                    -0.5, 0.8660254037844364, 4.787693700234689,
                    -0.8660254037844364, -0.5, 0.0 ) );
        endFeature();

        beginRegularFeature( 8 );
        addPlacement( new Transform(
                    -0.36075411076652936, 1.3463526704200182, -3.590770275176035,
                    -1.3463526704200182, -0.36075411076652936, -2.0731321849709863 ) );
        addPlacement( new Transform(
                    0.0, 1.3938468501173515, 0.0,
                    -1.3938468501173515, 0.0, -4.1462643699419806 ) );
        addPlacement( new Transform(
                    1.2071067811865457, 0.6969234250586751, -3.5907702751760286,
                    -0.6969234250586751, 1.2071067811865457, 2.073132184970989 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
                    1.0, 0.0, -4.787693700234713,
                    0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
                    1.0, 0.0, -2.3938468501173666,
                    0.0, 1.0, -4.146264369941946 ) );
        endFeature();

        b_desc="Trying to capture a complex design that can be seen in the Real Alcazar in Seville (Spain).";
        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "7.6" );

        setTranslations(
                new Point( 4.440892098500626E-16, 8.574998680526786 ),
                new Point( 6.7527082408906125, 0.0 ) );

        beginPolygonFeature( 16 );
        addPoint( new Point( -2.413901319993915, 2.2183182498892533 ) );
        addPoint( new Point( -3.3166825537215976, 1.4983742400090012 ) );
        addPoint( new Point( -2.9763285951392566, 0.3949738067071721 ) );
        addPoint( new Point( -3.761724421634227, -0.45148158435464647 ) );
        addPoint( new Point( -3.2607186344811803, -1.4918308211845148 ) );
        addPoint( new Point( -3.7617244216342276, -2.532180058014383 ) );
        addPoint( new Point( -2.976328595139257, -3.3786354490762016 ) );
        addPoint( new Point( -3.316682553721599, -4.482035882378031 ) );
        addPoint( new Point( -2.413901319993916, -5.201979892258283 ) );
        addPoint( new Point( -1.5111200862662335, -4.482035882378031 ) );
        addPoint( new Point( -1.851474044848575, -3.378635449076202 ) );
        addPoint( new Point( -1.0660782183536048, -2.532180058014383 ) );
        addPoint( new Point( -1.5670840055066524, -1.4918308211845144 ) );
        addPoint( new Point( -1.0660782183536046, -0.4514815843546458 ) );
        addPoint( new Point( -1.851474044848575, 0.3949738067071725 ) );
        addPoint( new Point( -1.5111200862662337, 1.4983742400090014 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
                    1.0, 0.0, 0.0,
                    0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
                    1.0000000000000004, -7.771561172376096E-16, 3.376354120445307,
                    7.771561172376096E-16, 1.0000000000000004, -4.287499340263392 ) );
        endFeature();

        beginRegularFeature( 7 );
        addPlacement( new Transform(
                    -1.198880187289056, -1.1102230246251565E-16, -1.21502113270486,
                    1.1102230246251565E-16, -1.198880187289056, -5.779330161447909 ) );
        addPlacement( new Transform(
                    1.1988801872890569, -3.885780586188048E-16, -0.23642738683766673,
                    3.885780586188048E-16, 1.1988801872890569, -1.491830821184514 ) );
        addPlacement( new Transform(
                    1.198880187289056, -1.1102230246251565E-16, -3.6127815072829716,
                    1.1102230246251565E-16, 1.198880187289056, -5.779330161447907 ) );
        addPlacement( new Transform(
                    -1.1988801872890558, 4.440892098500626E-16, -4.591375253150164,
                    -4.440892098500626E-16, -1.1988801872890558, -1.4918308211845142 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
                    -0.955572805786141, -0.29475517441090404, -4.102078380216568,
                    0.29475517441090404, -0.955572805786141, -3.635580491316211 ) );
        addPlacement( new Transform(
                    -0.9555728057861412, 0.2947551744109051, -0.7257242597712629,
                    -0.2947551744109051, -0.9555728057861412, -3.635580491316212 ) );
        addPlacement( new Transform(
                    -0.9555728057861412, 0.2947551744109044, -4.102078380216568,
                    -0.2947551744109044, -0.9555728057861412, 0.6519188489471828 ) );
        addPlacement( new Transform(
                    -0.9555728057861407, -0.29475517441090376, -0.7257242597712639,
                    0.29475517441090376, -0.9555728057861407, 0.651918848947183 ) );
        endFeature();

        b_desc="Seven is the new six.";
        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Al-Mustansiriyya" );

        setTranslations(
            new Point( 2.2360679774997854, -0.7265425280053588 ),
            new Point( -1.381966011250105, -1.9021130325903068 ) );

        beginRegularFeature( 10 );
        addPlacement( new Transform(
            0.9999999999999998, -5.551115123125783E-16, 4.529615509856923E-16,
            5.551115123125783E-16, 0.9999999999999998, 2.3511410091698925 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 0.381966011250106, 1.5771933363574004 ) );
        addPoint( new Point( 2.220446049250313E-16, 1.051462224238267 ) );
        addPoint( new Point( 0.6180339887498948, 0.85065080835204 ) );
        addPoint( new Point( 0.9999999999999998, 0.32491969623290634 ) );
        addPoint( new Point( 1.3819660112501062, 0.8506508083520397 ) );
        addPoint( new Point( 0.7639320225002111, 1.051462224238267 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.3090169943749481, -0.9510565162951531, 7.86028458373239E-16,
            0.9510565162951531, -0.3090169943749481, 2.351141009169893 ) );
        addPlacement( new Transform(
            -0.9999999999999991, 4.9960036108132E-16, 1.3819660112501047,
            -4.9960036108132E-16, -0.9999999999999991, 4.253254041760199 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 2.000000000000001, 1.0514622242382674 ) );
        addPoint( new Point( 1.3819660112501062, 0.8506508083520397 ) );
        addPoint( new Point( 0.9999999999999998, 0.32491969623290634 ) );
        addPoint( new Point( 1.0, -0.3249196962329064 ) );
        addPoint( new Point( 1.6180339887498947, -0.12410828034667876 ) );
        addPoint( new Point( 2.000000000000001, 0.4016228317724545 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.809016994374948, 0.587785252292471, -1.8541019662496856,
            -0.587785252292471, 0.809016994374948, 4.253254041760195 ) );
        endFeature();

        b_desc="Seen at the Abbasid Al-Mustansiriyya Madrasa in Baghdad.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "6.5 flower" );

        setTranslations(
            new Point( 2.883725453719955, -1.6649196669741957 ),
            new Point( 3.552713678800501E-15, 3.329839333948383 ) );

        beginPolygonFeature( 3 );
        addPoint( new Point( -2.376205458551365, -1.7600437436895509 ) );
        addPoint( new Point( -1.9483327866011453, -2.3489599536255272 ) );
        addPoint( new Point( -1.6522527240712483, -1.8361342422155817 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.5000000000000004, -0.8660254037844388, -0.8342634397287776,
            0.8660254037844388, 0.5000000000000004, -2.5922820361786547 ) );
        addPlacement( new Transform(
            0.5000000000000001, 0.8660254037844388, 3.234254544895275,
            -0.8660254037844388, 0.5000000000000001, -4.941241989804197 ) );
        addPlacement( new Transform(
            -0.5000000000000004, 0.8660254037844392, 1.8780818833539181,
            -0.8660254037844392, -0.5000000000000004, -7.290201943429729 ) );
        addPlacement( new Transform(
            1.0, 5.551115123125783E-17, 1.8780818833539268,
            -5.551115123125783E-17, 1.0, -2.592282036178661 ) );
        addPlacement( new Transform(
            -0.5000000000000002, -0.866025403784439, -2.190436101270136,
            0.866025403784439, -0.5000000000000002, -4.941241989804186 ) );
        addPlacement( new Transform(
            -0.9999999999999999, -2.7755575615628914E-16, -0.8342634397287871,
            2.7755575615628914E-16, -0.9999999999999999, -7.290201943429721 ) );
        endFeature();

        beginRegularFeature( 5 );
        addPlacement( new Transform(
            0.3352090403018005, -0.37228735524306006, -0.08378241471194614,
            0.37228735524306006, 0.3352090403018005, -3.892153301624177 ) );
        addPlacement( new Transform(
            0.49001482729911283, -0.104155866858027, 1.1276008583370862,
            0.104155866858027, 0.49001482729911283, -5.990330677984206 ) );
        addPlacement( new Transform(
            0.5009620500336578, 2.5006720306521233E-16, 1.73329249486161,
            -2.5006720306521233E-16, 0.5009620500336578, -4.941241989804196 ) );
        addPlacement( new Transform(
            0.45765160525957554, -0.20375962253905983, -0.08378241471195613,
            0.20375962253905983, 0.45765160525957554, -5.9903306779842005 ) );
        addPlacement( new Transform(
            0.4052868120141443, -0.2944581049679874, -0.6894740512364725,
            0.2944581049679874, 0.4052868120141443, -4.941241989804186 ) );
        addPlacement( new Transform(
            0.4900148272991114, 0.10415586685802894, 1.1276008583370933,
            -0.10415586685802894, 0.4900148272991114, -3.892153301624183 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            0.4441200937948428, -0.2564128557049713, 0.52190922181257,
            0.2564128557049713, 0.4441200937948428, -4.941241989804191 ) );
        endFeature();

        beginPolygonFeature( 4 );
        addPoint( new Point( 1.693289352449827, -3.6402925570962053 ) );
        addPoint( new Point( 1.5419420188225788, -4.35232577986822 ) );
        addPoint( new Point( 2.234254544895268, -4.577271755537994 ) );
        addPoint( new Point( 2.385601878522517, -3.8652385327659737 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.5000000000000017, 0.8660254037844329, 4.540195700323059,
            -0.8660254037844329, 0.5000000000000017, -2.018634350343046 ) );
        addPlacement( new Transform(
            0.49999999999999856, -0.8660254037844385, -4.018286478510506,
            0.8660254037844385, 0.49999999999999856, -2.9226076394611504 ) );
        endFeature();

        beginRegularFeature( 3 );
        addPlacement( new Transform(
            0.10506915636530237, -0.18198511713310112, -0.4393325960940751,
            0.18198511713310112, 0.10506915636530237, -3.2763223228299942 ) );
        addPlacement( new Transform(
            -0.10506915636530144, 0.18198511713310103, 1.483151039719224,
            -0.18198511713310103, -0.10506915636530144, -3.276322322830003 ) );
        endFeature();

        b_desc="David stars of pentagons.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "9.6" );

        setTranslations(
            new Point( -8.881784197001252E-16, -4.479528227023502 ),
            new Point( 3.8793852415718195, -2.239764113511752 ) );

        beginRegularFeature( 9 );
        addPlacement( new Transform(
            1.4905938356746224, 0.5425317875662494, -5.172513655429092,
            -0.5425317875662494, 1.4905938356746224, 4.479528227023504 ) );
        endFeature();

        beginPolygonFeature( 5 );
        addPoint( new Point( -3.8793852415718186, 5.564591802156002 ) );
        addPoint( new Point( -3.4844543979371183, 4.479528227023504 ) );
        addPoint( new Point( -3.03535561282583, 5.257390140453712 ) );
        addPoint( new Point( -2.137158042603261, 5.257390140453709 ) );
        addPoint( new Point( -2.8793852415718186, 6.141942071345627 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            -0.5000000000000009, 0.8660254037844398, -7.758770483143649,
            -0.8660254037844398, -0.5000000000000009, 4.479528227023504 ) );
        addPlacement( new Transform(
            -0.5000000000000008, -0.866025403784439, 1.3322676295501878E-15,
            0.866025403784439, -0.5000000000000008, 8.95905645404701 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            0.6736481776669262, 0.3889309567151031, -2.5862568277145477,
            -0.3889309567151031, 0.6736481776669262, 4.479528227023508 ) );
        addPlacement( new Transform(
            1.0000000000000007, -1.1102230246251565E-16, -3.8793852415718186,
            1.1102230246251565E-16, 1.0000000000000007, 2.2397641135117525 ) );
        endFeature();

        b_desc="A shiny new tiling!";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "9.5.3.6" );

        setTranslations(
            new Point( 4.068517984624055, 2.348959953625527 ),
            new Point( 1.5543122344752192E-15, 4.697919907251058 ) );

        beginRegularFeature( 9 );
        addPlacement( new Transform(
            0.9396926207859071, 0.34202014332566866, 1.3561726615413492,
            -0.34202014332566866, 0.9396926207859071, -2.3489599536255303 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginPolygonFeature( 3 );
        addPoint( new Point( -2.376205458551365, -1.7600437436895509 ) );
        addPoint( new Point( -1.9483327866011453, -2.3489599536255272 ) );
        addPoint( new Point( -1.6522527240712483, -1.8361342422155817 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.5000000000000012, -0.8660254037844385, -2.7123453230826975,
            0.8660254037844385, 0.5000000000000012, 5.329070518200751E-15 ) );
        addPlacement( new Transform(
            0.5000000000000011, 0.8660254037844414, 1.3561726615413563,
            -0.8660254037844414, 0.5000000000000011, -2.3489599536255383 ) );
        addPlacement( new Transform(
            -0.5000000000000007, -0.8660254037844388, -4.068517984624053,
            0.8660254037844388, -0.5000000000000007, -2.348959953625526 ) );
        addPlacement( new Transform(
            -0.5000000000000006, 0.8660254037844403, -1.6653345369377348E-15,
            -0.8660254037844403, -0.5000000000000006, -4.697919907251068 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            -1.0, 0.0, -2.7123453230827055,
            0.0, -1.0, -4.697919907251057 ) );
        endFeature();

        beginRegularFeature( 5 );
        addPlacement( new Transform(
            -0.2504810250168296, 0.4338458616610785, -1.9618642980658758,
            -0.4338458616610785, -0.2504810250168296, -3.3980486418055413 ) );
        addPlacement( new Transform(
            0.4576516052595745, 0.20375962253906185, -1.9618642980658745,
            -0.20375962253906185, 0.4576516052595745, -1.2998712654455173 ) );
        addPlacement( new Transform(
            0.40528681201414196, -0.2944581049679879, -2.5675559345903967,
            0.2944581049679879, 0.40528681201414196, -2.348959953625527 ) );
        addPlacement( new Transform(
            0.4900148272991113, -0.10415586685802888, -0.7504810250168301,
            0.10415586685802888, 0.4900148272991113, -3.3980486418055413 ) );
        addPlacement( new Transform(
            -0.40528681201414185, -0.2944581049679883, -0.1447893884923075,
            0.2944581049679883, -0.40528681201414185, -2.3489599536255295 ) );
        addPlacement( new Transform(
            0.49001482729911194, 0.1041558668580288, -0.7504810250168292,
            -0.1041558668580288, 0.49001482729911194, -1.299871265445518 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            0.44412009379484285, -0.25641285570497135, -1.3561726615413567,
            0.25641285570497135, 0.44412009379484285, -2.3489599536255303 ) );
        endFeature();

        beginPolygonFeature( 5 );
        addPoint( new Point( -0.33613986453133937, 1.7600437436895535 ) );
        addPoint( new Point( -0.1847925309040953, 1.0480105209175394 ) );
        addPoint( new Point( 0.5320888862379561, 0.9216049851068762 ) );
        addPoint( new Point( 0.8240837753033958, 1.4273549685186544 ) );
        addPoint( new Point( 0.3561726615413521, 1.9849897193593282 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.4999999999999998, 0.8660254037844379, -2.7123453230827024,
            -0.8660254037844379, 0.4999999999999998, -4.697919907251057 ) );
        addPlacement( new Transform(
            -0.9999999999999988, -4.996003610813204E-16, -2.712345323082701,
            4.996003610813204E-16, -0.9999999999999988, 9.2036370562228E-16 ) );
        addPlacement( new Transform(
            -0.500000000000001, 0.8660254037844373, -4.0685179846240525,
            -0.8660254037844373, -0.500000000000001, -2.348959953625525 ) );
        addPlacement( new Transform(
            -0.49999999999999956, -0.8660254037844389, -1.1102230246251565E-16,
            0.8660254037844389, -0.49999999999999956, -6.661338147750939E-16 ) );
        addPlacement( new Transform(
            0.9999999999999991, 1.6653345369377348E-16, -1.9984014443252818E-15,
            -1.6653345369377348E-16, 0.9999999999999991, -4.697919907251058 ) );
        addPlacement( new Transform(
            0.4999999999999999, -0.8660254037844374, 1.3561726615413492,
            0.8660254037844374, 0.4999999999999999, -2.3489599536255303 ) );
        endFeature();

        b_desc="Complex amalgam of nonagons, hexagons, pentagons...";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "8 6 ~5" );

        setTranslations(
            new Point( -2.428825434727682, 2.428825434727669 ),
            new Point( 2.4288254347276723, 2.4288254347276714 ) );

        beginPolygonFeature( 5 );
        addPoint( new Point( 1.0, 0.4142135623730951 ) );
        addPoint( new Point( 0.9999999999999998, -0.41421356237309576 ) );
        addPoint( new Point( 1.80019915499074, -0.6286262797369315 ) );
        addPoint( new Point( 2.4288254347276714, 4.440892098500626E-16 ) );
        addPoint( new Point( 1.8001991549907408, 0.6286262797369311 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -3.3306690738754696E-16, 0.9999999999999981, 2.428825434727672,
            -0.9999999999999981, -3.3306690738754696E-16, 2.4288254347276674 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            -3.3306690738754696E-16, -1.0000000000000018, 2.4288254347276723,
            1.0000000000000018, -3.3306690738754696E-16, -2.4288254347276754 ) );
        addPlacement( new Transform(
            -1.0000000000000036, 6.661338147750951E-16, 4.857650869455352,
            -6.661338147750951E-16, -1.0000000000000036, 2.220446049250313E-15 ) );
        endFeature();

        beginRegularFeature( 8 );
        addPlacement( new Transform(
            0.9999999999999999, -4.930380657631324E-32, 0.0,
            4.930380657631324E-32, 0.9999999999999999, 5.551115123125783E-17 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            0.5073059361772879, -0.5073059361772884, 1.2144127173638357,
            0.5073059361772884, 0.5073059361772879, 1.2144127173638357 ) );
        addPlacement( new Transform(
            0.18568686013153446, -0.692992796308822, 1.2144127173638353,
            0.692992796308822, 0.18568686013153446, -1.2144127173638355 ) );
        endFeature();

        b_desc="An octogon surronded by hexagons and quasi-pentagons.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "pbn_7" );

        setTranslations(
            new Point( 2.445041867912629, -1.9498558243636483 ),
            new Point( 0.0, 3.899711648727295 ) );

        beginRegularFeature( 7 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.9009688679024193, 0.43388373911755795, -0.44504186791262884,
            -0.43388373911755795, 0.9009688679024193, 1.9498558243636475 ) );
        endFeature();

        beginPolygonFeature( 5 );
        addPoint( new Point( 0.24697960371746683, 2.817623302598764 ) );
        addPoint( new Point( 0.6648743962621135, 1.9498558243636477 ) );
        addPoint( new Point( 1.3351256037378867, 1.949855824363648 ) );
        addPoint( new Point( 1.753020396282533, 2.817623302598764 ) );
        addPoint( new Point( 0.9999999999999998, 3.418137029919767 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            -1.0, 0.0, 2.0,
            0.0, -1.0, 3.8997116487272954 ) );
        endFeature();

        b_desc="A new tiling based on an arrangement of heptagons with quasi-pentagonal regions. A variation of csk_7.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Alcazar De Seville 12 8 - Shorter" );

        setTranslations(
            new Point( 8.881784197001252E-16, 7.097094776993207 ),
            new Point( 6.146264369941973, -3.5485473884966234 ) );

        beginPolygonFeature( 4 );
        addPoint( new Point( -3.270055610029663, 0.27849177846694523 ) );
        addPoint( new Point( -3.270055610029662, -0.27849177846693934 ) );
        addPoint( new Point( -2.154700538379252, -0.5773502691896248 ) );
        addPoint( new Point( -2.1547005383792515, 0.5773502691896266 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.5000000000000002, 0.8660254037844386, -4.784396297761732E-16,
            -0.8660254037844386, 0.5000000000000002, -2.392198148880866E-16 ) );
        addPlacement( new Transform(
            -0.5000000000000002, -0.8660254037844392, 0.0,
            0.8660254037844392, -0.5000000000000002, 7.176594446642598E-16 ) );
        addPlacement( new Transform(
            -1.0000000000000016, -2.974833628667433E-17, -3.109857593545126E-15,
            2.974833628667433E-17, -1.0000000000000016, 0.0 ) );
        addPlacement( new Transform(
            0.4999999999999999, -0.8660254037844382, -8.372693521083032E-16,
            0.8660254037844382, 0.4999999999999999, -1.1960990744404332E-15 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            -0.49999999999999944, 0.8660254037844388, 5.980495372202166E-16,
            -0.8660254037844388, -0.49999999999999944, 4.784396297761732E-16 ) );
        endFeature();

        beginRegularFeature( 8 );
        addPlacement( new Transform(
            0.6969234250586736, 1.2071067811865492, 3.073132184970987,
            -1.2071067811865492, 0.6969234250586736, 1.774273694248299 ) );
        addPlacement( new Transform(
            0.9855985596534893, -0.9855985596534893, 5.980495372202166E-16,
            0.9855985596534893, 0.9855985596534893, 3.548547388496604 ) );
        addPlacement( new Transform(
            1.2071067811865477, 0.6969234250586804, 3.0731321849709845,
            -0.6969234250586804, 1.2071067811865477, -1.774273694248308 ) );
        endFeature();

        beginRegularFeature( 12 );
        addPlacement( new Transform(
            2.154700538379252, 0.0, 0.0,
            0.0, 2.154700538379252, 0.0 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 3.2700556100296594, 0.2784917784669412 ) );
        addPoint( new Point( 3.2700556100296603, -0.27849177846694384 ) );
        addPoint( new Point( 4.27005561002966, -0.8558420476565691 ) );
        addPoint( new Point( 4.752417519824616, -0.5773502691896267 ) );
        addPoint( new Point( 4.752417519824616, 0.5773502691896244 ) );
        addPoint( new Point( 4.27005561002966, 0.8558420476565666 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.49999999999999944, 0.866025403784437, 4.440892098500626E-15,
            -0.866025403784437, 0.49999999999999944, 7.0970947769932025 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        b_desc="Trying to capture a complex design that can be seen in the Real Alcazar in Seville (Spain).";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Green Mosque" );

        setTranslations(
            new Point( 3.236067977499776, -1.4210854715202004E-14 ),
            new Point( -1.9095836023552692E-14, -2.3511410091698695 ) );

        beginPolygonFeature( 6 );
        addPoint( new Point( 3.9639320225002086, -0.6803212150683722 ) );
        addPoint( new Point( 4.345898033750315, -1.2060523271875043 ) );
        addPoint( new Point( 4.3458980337503155, -1.8558917196533167 ) );
        addPoint( new Point( 4.963932022500211, -1.6550803037670831 ) );
        addPoint( new Point( 4.581966011250107, -1.1293491916479488 ) );
        addPoint( new Point( 4.581966011250106, -0.47950979918213665 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.8090169943749335, 0.587785252292482, -1.1652811641984284,
            -0.587785252292482, 0.8090169943749335, 5.0447393571128725 ) );
        addPlacement( new Transform(
            0.9999999999999933, 1.4210854715202004E-14, -3.704173604237923,
            -1.4210854715202004E-14, 0.9999999999999933, 2.6439169477554283 ) );
        addPlacement( new Transform(
            0.3090169943749455, -0.9510565162951539, -2.8482558435017444,
            0.9510565162951539, 0.3090169943749455, -1.5960968303729244 ) );
        endFeature();

        beginRegularFeature( 10 );
        addPlacement( new Transform(
            0.9999999999999927, 1.28440886650093E-14, -0.35827557048765124,
            -1.28440886650093E-14, 0.9999999999999927, 1.1129449243349792 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 2.000000000000001, 1.0514622242382674 ) );
        addPoint( new Point( 1.3819660112501062, 0.8506508083520397 ) );
        addPoint( new Point( 0.9999999999999998, 0.32491969623290634 ) );
        addPoint( new Point( 1.0, -0.3249196962329064 ) );
        addPoint( new Point( 1.6180339887498947, -0.12410828034667876 ) );
        addPoint( new Point( 2.000000000000001, 0.4016228317724545 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.9999999999999912, -8.715250743307479E-15, 2.259758418262247,
            8.715250743307479E-15, -0.9999999999999912, 3.01505795692525 ) );
        addPlacement( new Transform(
            -0.3090169943749537, 0.9510565162951427, 1.8777924070121568,
            -0.9510565162951427, -0.3090169943749537, 4.1906284615101885 ) );
        addPlacement( new Transform(
            -0.8090169943749476, 0.5877852522924594, 2.259758418262249,
            -0.5877852522924594, -0.8090169943749476, 3.0150579569252476 ) );
        endFeature();

        b_desc="Half plane of the Ottoman Green Mosque in Bursa, Turkey (1424 C.E.)";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Ilkhanid Uljaytu" );

        setTranslations(
            new Point( 0.0, -2.7527638409423467 ),
            new Point( -2.763932022500211, -1.199040866595169E-14 ) );

        beginRegularFeature( 10 );
        addPlacement( new Transform(
            1.0, -3.351421106883819E-16, 4.581966011250107,
            3.351421106883819E-16, 1.0, 1.221791817521943 ) );
        endFeature();

        beginPolygonFeature( 4 );
        addPoint( new Point( 2.818033988749896, -0.15459010294924225 ) );
        addPoint( new Point( 2.436067977499791, -0.6803212150683756 ) );
        addPoint( new Point( 2.818033988749896, -1.206052327187509 ) );
        addPoint( new Point( 3.1999999999999993, -0.6803212150683748 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0000000000000029, -1.6674097402385707E-15, 3.5278640450004137,
            1.6674097402385707E-15, 1.0000000000000029, 1.051462224238287 ) );
        addPlacement( new Transform(
            1.0000000000000027, -1.3778064550522368E-15, 3.5278640450004146,
            1.3778064550522368E-15, 1.0000000000000027, 2.0872192862952943E-14 ) );
        addPlacement( new Transform(
            1.0000000000000004, -1.1309750576335073E-16, 2.7639320225002098,
            1.1309750576335073E-16, 1.0000000000000004, 1.2434497875801753E-14 ) );
        addPlacement( new Transform(
            0.9999999999999999, -2.997602166487924E-15, 3.1458980337503144,
            2.997602166487924E-15, 0.9999999999999999, 0.5257311121191379 ) );
        addPlacement( new Transform(
            1.0000000000000004, -1.1102230246251565E-16, 2.76393202250021,
            1.1102230246251565E-16, 1.0000000000000004, 1.051462224238279 ) );
        endFeature();

        beginRegularFeature( 5 );
        addPlacement( new Transform(
            0.36180339887498936, -0.26286555605956685, 5.0291796067500645,
            0.26286555605956685, 0.36180339887498936, -0.15459010294923026 ) );
        addPlacement( new Transform(
            0.44721359549995787, -8.326672684688674E-17, 4.13475241575015,
            8.326672684688674E-17, 0.44721359549995787, -0.15459010294923026 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 2.000000000000001, 1.0514622242382674 ) );
        addPoint( new Point( 1.3819660112501062, 0.8506508083520397 ) );
        addPoint( new Point( 0.9999999999999998, 0.32491969623290634 ) );
        addPoint( new Point( 1.0, -0.3249196962329064 ) );
        addPoint( new Point( 1.6180339887498947, -0.12410828034667876 ) );
        addPoint( new Point( 2.000000000000001, 0.4016228317724545 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.809016994374946, 0.5877852522924745, 6.9639320225002095,
            -0.5877852522924745, -0.809016994374946, 2.3973623221068925 ) );
        endFeature();

        b_desc="Ilkhanid Uljaytu Mausoleum in Sultaniya, Iran. (1304 C.E)";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Ilkhanid Uljaytu Vault" );

        setTranslations(
            new Point( -0.9999999999999911, 3.0776835371752567 ),
            new Point( 1.9999999999999973, 8.881784197001252E-16 ) );

        beginRegularFeature( 10 );
        addPlacement( new Transform(
            1.0000000000000038, 3.497202527569243E-15, 4.8019660112501015,
            -3.497202527569243E-15, 1.0000000000000038, 0.8241082803466866 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 5.183932022500213, 3.051141009169904 ) );
        addPoint( new Point( 5.183932022500212, 2.4013016167040897 ) );
        addPoint( new Point( 5.801966011250107, 2.6021130325903172 ) );
        addPoint( new Point( 6.420000000000002, 2.40130161670409 ) );
        addPoint( new Point( 6.420000000000003, 3.051141009169904 ) );
        addPoint( new Point( 5.801966011250108, 2.8503295932836767 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.3090169943749497, -0.9510565162951539, 6.101850457519444,
            0.9510565162951539, 0.3090169943749497, -3.9974962495128215 ) );
        addPlacement( new Transform(
            -0.30901699437494534, -0.9510565162951552, 8.687662654043669,
            0.9510565162951552, -0.30901699437494534, -2.312598817263398 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 2.000000000000001, 1.0514622242382674 ) );
        addPoint( new Point( 1.3819660112501062, 0.8506508083520397 ) );
        addPoint( new Point( 0.9999999999999998, 0.32491969623290634 ) );
        addPoint( new Point( 1.0, -0.3249196962329064 ) );
        addPoint( new Point( 1.6180339887498947, -0.12410828034667876 ) );
        addPoint( new Point( 2.000000000000001, 0.4016228317724545 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.8090169943749479, 0.5877852522924729, 5.801966011250109,
            -0.5877852522924729, -0.8090169943749479, 3.9017918175219433 ) );
        addPlacement( new Transform(
            -0.8090169943749499, 0.5877852522924715, 6.801966011250106,
            -0.5877852522924715, -0.8090169943749499, 3.1752492895165827 ) );
        endFeature();

        b_desc="Interior ceiling on a vault in the gallery of the Ilkhanid Uljaytu Mausoleum in Sultaniya, Iran. (1304 C.E)";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Mamluk Quran" );

        setTranslations(
            new Point( 2.6180339887498967, 1.9021130325902935 ),
            new Point( -2.6180339887499033, 1.9021130325903077 ) );

        beginPolygonFeature( 6 );
        addPoint( new Point( 3.9639320225002086, -0.6803212150683722 ) );
        addPoint( new Point( 4.345898033750315, -1.2060523271875043 ) );
        addPoint( new Point( 4.3458980337503155, -1.8558917196533167 ) );
        addPoint( new Point( 4.963932022500211, -1.6550803037670831 ) );
        addPoint( new Point( 4.581966011250107, -1.1293491916479488 ) );
        addPoint( new Point( 4.581966011250106, -0.47950979918213665 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.30901699437495345, -0.9510565162951466, 1.9736463179788755,
            0.9510565162951466, 0.30901699437495345, -5.157201828399527 ) );
        addPlacement( new Transform(
            -0.30901699437493946, -0.9510565162951505, 5.041525025728002,
            0.9510565162951505, -0.30901699437493946, -6.829937102703914 ) );
        addPlacement( new Transform(
            0.309016994374948, -0.9510565162951576, -0.026353682021123648,
            0.9510565162951576, 0.309016994374948, -5.157201828399577 ) );
        addPlacement( new Transform(
            0.8090169943749491, -0.5877852522924684, -2.1431445567955927,
            0.5877852522924684, 0.8090169943749491, -3.9027897367501376 ) );
        endFeature();

        beginRegularFeature( 10 );
        addPlacement( new Transform(
            0.8090169943749522, 0.5877852522924716, 3.463626590993021,
            -0.5877852522924716, 0.8090169943749522, -3.1747026016970517 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 2.000000000000001, 1.0514622242382674 ) );
        addPoint( new Point( 1.3819660112501062, 0.8506508083520397 ) );
        addPoint( new Point( 0.9999999999999998, 0.32491969623290634 ) );
        addPoint( new Point( 1.0, -0.3249196962329064 ) );
        addPoint( new Point( 1.6180339887498947, -0.12410828034667876 ) );
        addPoint( new Point( 2.000000000000001, 0.4016228317724545 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.3090169943749409, 0.9510565162951494, 3.46362659099303,
            -0.9510565162951494, 0.3090169943749409, 0.6295234634835598 ) );
        addPlacement( new Transform(
            0.8090169943749399, 0.5877852522924722, 2.845592602243136,
            -0.5877852522924722, 0.8090169943749399, -1.2725895691067421 ) );
        addPlacement( new Transform(
            0.8090169943749508, 0.5877852522924749, 1.2275586134932095,
            -0.5877852522924749, 0.8090169943749508, -0.09701906452178743 ) );
        addPlacement( new Transform(
            -0.8090169943749448, 0.587785252292466, 4.463626590993027,
            -0.587785252292466, -0.8090169943749448, -0.09701906452180897 ) );
        addPlacement( new Transform(
            0.30901699437495206, 0.951056516295155, 1.8455926022431108,
            -0.951056516295155, 0.30901699437495206, -0.5460470411013816 ) );
        endFeature();

        b_desc="The Mamluk Quran of Aydughdi ibn Abdallah al-Badri. (~1313 C.E.)";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Mughal I'timad" );

        setTranslations(
            new Point( 6.472135954999587, -4.440892098500626E-15 ),
            new Point( 1.2434497875801753E-14, 6.1553670743505045 ) );

        beginPolygonFeature( 6 );
        addPoint( new Point( 3.9639320225002086, -0.6803212150683722 ) );
        addPoint( new Point( 4.345898033750315, -1.2060523271875043 ) );
        addPoint( new Point( 4.3458980337503155, -1.8558917196533167 ) );
        addPoint( new Point( 4.963932022500211, -1.6550803037670831 ) );
        addPoint( new Point( 4.581966011250107, -1.1293491916479488 ) );
        addPoint( new Point( 4.581966011250106, -0.47950979918213665 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.9999999999999956, -8.602153237544129E-15, 12.745592602243075,
            8.602153237544129E-15, -0.9999999999999956, -7.807956643457295 ) );
        addPlacement( new Transform(
            -0.809016994374949, 0.5877852522924637, 13.3884317275315,
            -0.5877852522924637, -0.809016994374949, -4.373326980398846 ) );
        addPlacement( new Transform(
            -0.8090169943749388, -0.5877852522924776, 10.206700162203592,
            0.5877852522924776, -0.8090169943749388, -10.208779052814752 ) );
        addPlacement( new Transform(
            0.809016994374949, -0.5877852522924637, 2.1748894319542993,
            0.5877852522924637, 0.809016994374949, -5.829501250504974 ) );
        addPlacement( new Transform(
            0.309016994374938, 0.9510565162951522, 8.821796133757822,
            -0.9510565162951522, 0.309016994374938, 0.45593341814526145 ) );
        addPlacement( new Transform(
            -0.30901699437495694, 0.9510565162951358, 8.035572875257266,
            -0.9510565162951358, -0.30901699437495694, -3.1189148887494667 ) );
        addPlacement( new Transform(
            0.30901699437495384, -0.9510565162951473, 7.909714295478657,
            0.9510565162951473, 0.30901699437495384, -5.908342837569446 ) );
        addPlacement( new Transform(
            0.9999999999999956, 8.435619783850356E-15, 2.8177285572427238,
            -8.435619783850356E-15, 0.9999999999999956, -2.394871587446524 ) );
        addPlacement( new Transform(
            0.3090169943749582, -0.9510565162951359, 7.527748284228535,
            0.9510565162951359, 0.3090169943749582, -7.083913342154351 ) );
        addPlacement( new Transform(
            0.30901699437495267, -0.951056516295147, 7.527748284228526,
            0.951056516295147, 0.30901699437495267, -10.888139407335002 ) );
        addPlacement( new Transform(
            0.309016994374953, -0.9510565162951506, 2.6736463179788634,
            0.9510565162951506, 0.309016994374953, -5.908342837569455 ) );
        addPlacement( new Transform(
            -0.30901699437493807, -0.951056516295152, 6.741525025727979,
            0.951056516295152, -0.30901699437493807, -10.65876164904908 ) );
        addPlacement( new Transform(
            -0.3090169943749541, 0.9510565162951471, 11.889674841506938,
            -0.9510565162951471, -0.3090169943749541, -1.2168018561591305 ) );
        addPlacement( new Transform(
            0.3090169943749559, -0.9510565162951398, 3.0556123292289836,
            0.9510565162951398, 0.3090169943749559, -7.083913342154344 ) );
        addPlacement( new Transform(
            0.30901699437495417, -0.9510565162951468, 3.6736463179788617,
            0.9510565162951468, 0.30901699437495417, -8.986026374744686 ) );
        addPlacement( new Transform(
            0.8090169943749389, 0.5877852522924772, 5.356620997282207,
            -0.5877852522924772, 0.8090169943749389, 0.00595082191093077 ) );
        endFeature();

        beginRegularFeature( 10 );
        addPlacement( new Transform(
            0.8090169943749517, 0.587785252292473, 6.781660579742907,
            -0.587785252292473, 0.8090169943749517, -2.023730578276656 ) );
        addPlacement( new Transform(
            1.0000000000000038, -2.7200464103316335E-15, 8.781660579742907,
            2.7200464103316335E-15, 1.0000000000000038, -2.02373057827666 ) );
        addPlacement( new Transform(
            1.0, -3.351421106883819E-16, 7.7816605797429,
            3.351421106883819E-16, 1.0, -5.1014141154519095 ) );
        addPlacement( new Transform(
            1.000000000000004, -2.55351295663786E-15, 11.017728557242688,
            2.55351295663786E-15, 1.000000000000004, -5.101414115451916 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 2.000000000000001, 1.0514622242382674 ) );
        addPoint( new Point( 1.3819660112501062, 0.8506508083520397 ) );
        addPoint( new Point( 0.9999999999999998, 0.32491969623290634 ) );
        addPoint( new Point( 1.0, -0.3249196962329064 ) );
        addPoint( new Point( 1.6180339887498947, -0.12410828034667876 ) );
        addPoint( new Point( 2.000000000000001, 0.4016228317724545 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.8090169943749406, 0.5877852522924729, 8.781660579742919,
            -0.5877852522924729, 0.8090169943749406, -2.023730578276657 ) );
        addPlacement( new Transform(
            -0.3090169943749495, 0.9510565162951464, 6.781660579742914,
            -0.9510565162951464, -0.3090169943749495, -2.023730578276666 ) );
        addPlacement( new Transform(
            0.30901699437494146, 0.9510565162951491, 5.163626590993022,
            -0.9510565162951491, 0.30901699437494146, -3.199301082861603 ) );
        addPlacement( new Transform(
            -0.8090169943749408, -0.5877852522924712, 11.635762545992561,
            0.5877852522924712, -0.8090169943749408, -7.003527148042219 ) );
        addPlacement( new Transform(
            -0.3090169943749435, -0.9510565162951476, 6.163626590993003,
            0.9510565162951476, -0.3090169943749435, -7.730069676047556 ) );
        addPlacement( new Transform(
            -0.809016994374948, 0.587785252292464, 8.781660579742912,
            -0.587785252292464, -0.809016994374948, -2.0237305782766737 ) );
        addPlacement( new Transform(
            -0.8090169943749402, -0.5877852522924729, 11.017728557242675,
            0.5877852522924729, -0.8090169943749402, -5.101414115451918 ) );
        addPlacement( new Transform(
            0.8090169943749399, 0.5877852522924729, 4.545592602243125,
            -0.5877852522924729, 0.8090169943749399, -5.101414115451899 ) );
        addPlacement( new Transform(
            0.809016994374951, 0.5877852522924732, 3.9275586134931975,
            -0.5877852522924732, 0.809016994374951, -7.003527148042215 ) );
        addPlacement( new Transform(
            0.809016994374945, -0.5877852522924659, 6.781660579742894,
            0.5877852522924659, 0.809016994374945, -8.179097652627146 ) );
        addPlacement( new Transform(
            -0.9999999999999943, -5.329070518200751E-15, 10.399694568492794,
            5.329070518200751E-15, -0.9999999999999943, -3.199301082861618 ) );
        addPlacement( new Transform(
            0.3090169943749499, 0.9510565162951561, 4.545592602243109,
            -0.9510565162951561, 0.3090169943749499, -1.297188050271296 ) );
        addPlacement( new Transform(
            -0.30901699437494146, -0.9510565162951491, 10.399694568492777,
            0.9510565162951491, -0.30901699437494146, -7.003527148042217 ) );
        addPlacement( new Transform(
            -0.8090169943749429, 0.587785252292463, 12.017728557242672,
            -0.587785252292463, -0.8090169943749429, -2.0237305782767043 ) );
        addPlacement( new Transform(
            0.8090169943749418, -0.5877852522924636, 3.545592602243138,
            0.5877852522924636, 0.8090169943749418, -8.179097652627116 ) );
        addPlacement( new Transform(
            0.30901699437494373, 0.9510565162951479, 9.399694568492796,
            -0.9510565162951479, 0.30901699437494373, -2.472758554856261 ) );
        addPlacement( new Transform(
            0.8090169943749513, 0.5877852522924732, 3.9275586134932157,
            -0.5877852522924732, 0.8090169943749513, -3.1993010828616013 ) );
        addPlacement( new Transform(
            0.9999999999999938, 4.163336342344337E-15, 5.163626590993012,
            -4.163336342344337E-15, 0.9999999999999938, -7.003527148042201 ) );
        addPlacement( new Transform(
            0.3090169943749496, -0.9510565162951463, 8.781660579742885,
            0.9510565162951463, 0.3090169943749496, -8.179097652627153 ) );
        addPlacement( new Transform(
            -0.309016994374941, -0.9510565162951492, 11.017728557242654,
            0.9510565162951492, -0.309016994374941, -8.905640180632522 ) );
        endFeature();

        b_desc="The Mughal I'timad al-Daula Mausoleum in Agra, India. (~1622 C.E.)";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Sultan Lodge" );

        setTranslations(
            new Point( -2.6180339887498945, 0.8506508083520417 ),
            new Point( 0.6180339887498949, 1.9021130325903073 ) );

        beginRegularFeature( 10 );
        addPlacement( new Transform(
            1.0, 0.0, 2.096661237558754,
            0.0, 1.0, -5.16303361797123 ) );
        endFeature();

        beginRegularFeature( 5 );
        addPlacement( new Transform(
            0.4472135954999582, 2.498001805406602E-16, 3.8855156195585865,
            -2.498001805406602E-16, 0.4472135954999582, -4.111571393732964 ) );
        addPlacement( new Transform(
            0.3618033988749897, 0.26286555605956685, 3.543874833058712,
            -0.26286555605956685, 0.3618033988749897, -5.16303361797123 ) );
        endFeature();

        beginPolygonFeature( 4 );
        addPoint( new Point( -0.3819660112501051, -3.2784949530614806 ) );
        addPoint( new Point( 0.2360679774997898, -3.4793063689477077 ) );
        addPoint( new Point( 0.6180339887498951, -2.9535752568285742 ) );
        addPoint( new Point( 1.1102230246251565E-16, -2.7527638409423467 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.8090169943749472, 0.5877852522924736, 4.95076320380844,
            -0.5877852522924736, 0.8090169943749472, -1.8845386649097504 ) );
        addPlacement( new Transform(
            0.8090169943749469, 0.587785252292474, 5.9507632038084415,
            -0.587785252292474, 0.8090169943749469, -2.2094583611426595 ) );
        endFeature();

        b_desc="An archway in the Sultan's Lodge in the Green Mosque in Bursa, Turkey from 1424.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Timurid Tumaq Aqa" );

        setTranslations(
            new Point( 2.0, -5.551115123125783E-17 ),
            new Point( -4.440892098500626E-16, 2.35114100916989 ) );

        beginRegularFeature( 10 );
        addPlacement( new Transform(
            1.0, 0.0, -2.02,
            0.0, 1.0, 0.3799999999999999 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( -0.638033988749895, 1.880490200817852 ) );
        addPoint( new Point( -0.6380339887498953, 1.2306508083520398 ) );
        addPoint( new Point( -0.020000000000000132, 1.4314622242382675 ) );
        addPoint( new Point( 0.5980339887498947, 1.2306508083520404 ) );
        addPoint( new Point( 0.5980339887498933, 1.8804902008178521 ) );
        addPoint( new Point( -0.02000000000000073, 1.6796787849316248 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.9999999999999998, 2.220446049250313E-16, -2.0,
            -2.220446049250313E-16, 0.9999999999999998, -4.440892098500626E-16 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( -1.020000000000001, 3.457683537175253 ) );
        addPoint( new Point( -1.4019660112501064, 2.93195242505612 ) );
        addPoint( new Point( -1.401966011250106, 2.282113032590307 ) );
        addPoint( new Point( -1.0200000000000005, 1.7563819204711735 ) );
        addPoint( new Point( -0.6380339887498954, 2.282113032590307 ) );
        addPoint( new Point( -0.6380339887498955, 2.93195242505612 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.9999999999999996, 4.996003610813204E-16, -1.3322676295501878E-15,
            -4.996003610813204E-16, 0.9999999999999996, -1.051462224238267 ) );
        endFeature();

        b_desc="The Timurid Tumad Aqa Mausoleum in the Shah-i-Zinda complex in Samarkand, Uzbekistan.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "7.6.5" );

        setTranslations(
            new Point( 3.3763541204453076, -4.287499340263395 ),
            new Point( 4.440892098500626E-16, 8.574998680526786 ) );

        beginPolygonFeature( 5 );
        addPoint( new Point( -3.316682553721598, 1.498374240009001 ) );
        addPoint( new Point( -2.976328595139256, 0.39497380670717214 ) );
        addPoint( new Point( -1.8514740448485751, 0.39497380670717275 ) );
        addPoint( new Point( -1.5111200862662337, 1.4983742400090017 ) );
        addPoint( new Point( -2.413901319993915, 2.2183182498892533 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.9999999999999994, 1.735998752714194E-16, -4.827802639987829,
            -1.735998752714194E-16, -0.9999999999999994, -2.9836616423690314 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 7 );
        addPlacement( new Transform(
            1.198880187289056, -1.1102230246251565E-16, -3.6127815072829716,
            1.1102230246251565E-16, 1.198880187289056, -5.779330161447907 ) );
        addPlacement( new Transform(
            -1.1988801872890558, 4.440892098500626E-16, -4.591375253150164,
            -4.440892098500626E-16, -1.1988801872890558, -1.4918308211845142 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            -0.9555728057861412, 0.2947551744109044, -4.102078380216568,
            -0.2947551744109044, -0.9555728057861412, 0.6519188489471828 ) );
        addPlacement( new Transform(
            -0.955572805786141, -0.29475517441090404, -4.102078380216568,
            0.29475517441090404, -0.955572805786141, -3.635580491316211 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( -3.7617244216342263, -2.532180058014382 ) );
        addPoint( new Point( -2.976328595139255, -3.378635449076204 ) );
        addPoint( new Point( -1.8514740448485747, -3.378635449076203 ) );
        addPoint( new Point( -1.066078218353605, -2.532180058014383 ) );
        addPoint( new Point( -1.5670840055066522, -1.4918308211845144 ) );
        addPoint( new Point( -3.26071863448118, -1.491830821184514 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            -1.0, 0.0, -4.827802639987832,
            0.0, -1.0, -2.9836616423690283 ) );
        endFeature();

        b_desc="Heptagons and hexagons completed with irregular pentagons and hexagons. A good candidate to use the rosette inferring technique.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Great Mosque of Malatya" );

        setTranslations(
            new Point( -3.2360679774997676, 2.1760371282653068E-14 ),
            new Point( -1.6180339887498851, 1.1755705045849671 ) );

        beginPolygonFeature( 6 );
        addPoint( new Point( 3.9639320225002086, -0.6803212150683722 ) );
        addPoint( new Point( 4.345898033750315, -1.2060523271875043 ) );
        addPoint( new Point( 4.3458980337503155, -1.8558917196533167 ) );
        addPoint( new Point( 4.963932022500211, -1.6550803037670831 ) );
        addPoint( new Point( 4.581966011250107, -1.1293491916479488 ) );
        addPoint( new Point( 4.581966011250106, -0.47950979918213665 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.30901699437494634, -0.9510565162951499, -3.4321878660019562,
            0.9510565162951499, 0.30901699437494634, -3.7960968303729032 ) );
        addPlacement( new Transform(
            0.309016994374955, -0.9510565162951429, -3.0502218547518787,
            0.9510565162951429, 0.309016994374955, -4.971667334957817 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 2.000000000000001, 1.0514622242382674 ) );
        addPoint( new Point( 1.3819660112501062, 0.8506508083520397 ) );
        addPoint( new Point( 0.9999999999999998, 0.32491969623290634 ) );
        addPoint( new Point( 1.0, -0.3249196962329064 ) );
        addPoint( new Point( 1.6180339887498947, -0.12410828034667876 ) );
        addPoint( new Point( 2.000000000000001, 0.4016228317724545 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.8090169943749457, 0.5877852522924747, -0.5602415817377304,
            -0.5877852522924747, -0.8090169943749457, 0.08851542891990061 ) );
        addPlacement( new Transform(
            0.3090169943749477, 0.9510565162951481, -1.560241581737732,
            -0.9510565162951481, 0.3090169943749477, 0.815057956925255 ) );
        addPlacement( new Transform(
            -0.809016994374936, -0.5877852522924729, 0.675826395762039,
            0.5877852522924729, -0.809016994374936, -2.2626255802499995 ) );
        endFeature();

        b_desc="Column in the courtyard of the Great Mosque in Malatya, Turkey (c. 1200 AD).";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "3-8-4-6" );

        setTranslations(
            new Point( -23.74077407376281, -13.706742302256998 ),
            new Point( 23.740774073762804, 41.12022690677104 ) );

        beginPolygonFeature( 14 );
        addPoint( new Point( -1.4494897427831759, 11.974691494688145 ) );
        addPoint( new Point( -3.181540550352011, 8.974691494688157 ) );
        addPoint( new Point( 0.16452466459917625, 8.078116022520113 ) );
        addPoint( new Point( 1.8965754721680543, 5.0781160225201125 ) );
        addPoint( new Point( 1.0, 1.7320508075688767 ) );
        addPoint( new Point( 3.4494897427831783, 4.181540550352053 ) );
        addPoint( new Point( 6.913591357920932, 4.181540550352053 ) );
        addPoint( new Point( 9.363081100704111, 1.732050807568875 ) );
        addPoint( new Point( 11.09513190827299, 4.732050807568875 ) );
        addPoint( new Point( 7.749066693321759, 5.628626279736925 ) );
        addPoint( new Point( 6.017015885752889, 8.628626279736919 ) );
        addPoint( new Point( 6.913591357920945, 11.974691494688145 ) );
        addPoint( new Point( 4.464101615137764, 9.525201751904966 ) );
        addPoint( new Point( 1.0000000000000022, 9.525201751904966 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.4999999999999989, -0.8660254037844376, -3.9968028886505635E-15,
            0.8660254037844376, -0.4999999999999989, -8.881784197001252E-16 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            -0.4999999999999995, 0.8660254037844367, 2.6645352591003757E-15,
            -0.8660254037844367, -0.4999999999999995, -3.403681997562399E-15 ) );
        endFeature();

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            -0.8660254037844373, 1.4999999999999956, 13.46115731205743,
            -1.4999999999999956, -0.8660254037844373, 23.31540839316071 ) );
        addPlacement( new Transform(
            0.8660254037844375, 1.4999999999999978, -5.547565954136446,
            -1.4999999999999978, 0.8660254037844375, 9.608666090903718 ) );
        addPlacement( new Transform(
            0.8660254037844389, -1.499999999999999, -5.547565954136498,
            1.499999999999999, 0.8660254037844389, -9.608666090903718 ) );
        addPlacement( new Transform(
            1.732050807568879, 5.436954040298205E-16, -3.1815405503520537,
            -5.436954040298205E-16, 1.732050807568879, 13.706742302257025 ) );
        addPlacement( new Transform(
            -0.866025403784435, -1.4999999999999962, 13.461157312057418,
            1.4999999999999962, -0.866025403784435, 4.098076211353307 ) );
        addPlacement( new Transform(
            -1.7320508075688785, -4.88711714280075E-16, 11.095131908272988,
            4.88711714280075E-16, -1.7320508075688785, -4.479637181131185E-15 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            2.9999999999999982, 1.7763568394002505E-15, -7.9135913579209385,
            -1.7763568394002505E-15, 2.9999999999999982, -13.70674230225703 ) );
        endFeature();

        beginRegularFeature( 8 );
        addPlacement( new Transform(
            4.039058011260054, -1.0822623322995863, -2.5907702751760295,
            1.0822623322995863, 4.039058011260054, -4.487345747344079 ) );
        addPlacement( new Transform(
            4.181540550352055, 7.554360501969756E-16, 5.181540550352055,
            -7.554360501969756E-16, 4.181540550352055, -1.5543122344752192E-15 ) );
        addPlacement( new Transform(
            4.039058011260046, 1.082262332299589, 10.504361633096963,
            -1.082262332299589, 4.039058011260046, 9.219396554912942 ) );
        addPlacement( new Transform(
            4.039058011260057, 1.0822623322995886, -2.5907702751760295,
            -1.0822623322995886, 4.039058011260057, 4.487345747344084 ) );
        addPlacement( new Transform(
            3.621320343559631, 2.0907702751760247, 10.504361633096968,
            -2.0907702751760247, 3.621320343559631, 18.194088049601085 ) );
        addPlacement( new Transform(
            4.181540550352059, 8.881784197001252E-16, 2.7320508075688847,
            -8.881784197001252E-16, 4.181540550352059, 13.706742302257023 ) );
        endFeature();

        beginRegularFeature( 3 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.4999999999999996, -0.866025403784437, 7.913591357920944,
            0.866025403784437, 0.4999999999999996, 13.706742302257018 ) );
        addPlacement( new Transform(
            0.8660254037844337, 0.49999999999998723, 11.095131908272933,
            -0.49999999999998723, 0.8660254037844337, -2.732050807568893 ) );
        addPlacement( new Transform(
            -0.8660254037844255, 0.5000000000000024, -7.913591357920922,
            -0.5000000000000024, -0.8660254037844255, -8.242640687119234 ) );
        addPlacement( new Transform(
            -5.551115123125768E-17, 1.0000000000000009, 11.09513190827299,
            -1.0000000000000009, -5.551115123125768E-17, 2.732050807568874 ) );
        addPlacement( new Transform(
            -0.8660254037844373, -0.4999999999999998, -3.1815405503520564,
            0.4999999999999998, -0.8660254037844373, -10.974691494688143 ) );
        addPlacement( new Transform(
            -9.103828801926284E-15, -0.9999999999999912, -3.181540550352029,
            0.9999999999999912, -9.103828801926284E-15, 10.97469149468814 ) );
        addPlacement( new Transform(
            0.8660254037844382, -0.4999999999999993, -7.91359135792092,
            0.4999999999999993, 0.8660254037844382, 8.24264068711928 ) );
        endFeature();

        b_desc="A third variation of the Real Alcazar of Seville pattern.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "8-6-5" );

        setTranslations(
            new Point( 8.881784197001252E-16, 4.857650869455345 ),
            new Point( 2.4288254347276714, 2.4288254347276714 ) );

        beginRegularFeature( 5 );
        addPlacement( new Transform(
            -0.5422125924469683, 0.1761755508315255, 2.42882543472767,
            -0.1761755508315255, -0.5422125924469683, -0.8587093762633851 ) );
        addPlacement( new Transform(
            0.17617555083152564, -0.5422125924469687, 3.2875348109910565,
            0.5422125924469687, 0.17617555083152564, 6.583493685247924E-16 ) );
        addPlacement( new Transform(
            -1.5704170551495319E-16, -0.5701160584642875, 2.428825434727672,
            0.5701160584642875, -1.5704170551495319E-16, 0.858709376263384 ) );
        addPlacement( new Transform(
            -0.1761755508315258, -0.5422125924469692, 1.5701160584642873,
            0.5422125924469692, -0.1761755508315258, -3.885780586188048E-16 ) );
        endFeature();

        beginRegularFeature( 8 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            0.5073059361772879, -0.5073059361772884, 1.2144127173638357,
            0.5073059361772884, 0.5073059361772879, 1.2144127173638357 ) );
        addPlacement( new Transform(
            0.18568686013153446, -0.692992796308822, 1.2144127173638353,
            0.692992796308822, 0.18568686013153446, -1.2144127173638355 ) );
        endFeature();

        beginPolygonFeature( 8 );
        addPoint( new Point( -0.15400717293728017, 2.4288254347276723 ) );
        addPoint( new Point( -0.628626279736931, 1.8001991549907412 ) );
        addPoint( new Point( 1.6653345369377348E-16, 2.27481826179039 ) );
        addPoint( new Point( 0.6286262797369306, 1.8001991549907403 ) );
        addPoint( new Point( 0.1540071729372806, 2.4288254347276705 ) );
        addPoint( new Point( 0.6286262797369295, 3.057451714464602 ) );
        addPoint( new Point( 7.54927596028038E-16, 2.5828326076649537 ) );
        addPoint( new Point( -0.6286262797369311, 3.057451714464604 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.051557536390142, -0.01933112333475473, 2.4837175891880374,
            0.01933112333475473, 1.051557536390142, -2.55107255880946 ) );
        endFeature();

        b_desc="An octogon surrounded by hexagons and pentagons. The pentagons slightly overlap the hexagons. The additional star shape fills the voids, but is not necessary and can be left blank.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "9.6 ~5" );

        setTranslations(
            new Point( 3.8793852415718124, 2.239764113511743 ),
            new Point( 3.879385241571825, -2.239764113511752 ) );

        beginRegularFeature( 9 );
        addPlacement( new Transform(
            1.4905938356746224, 0.5425317875662494, -5.172513655429092,
            -0.5425317875662494, 1.4905938356746224, 4.479528227023504 ) );
        endFeature();

        beginPolygonFeature( 5 );
        addPoint( new Point( -3.8793852415718186, 5.564591802156002 ) );
        addPoint( new Point( -3.484454397937118, 4.4795282270235015 ) );
        addPoint( new Point( -2.5862568277145472, 4.4795282270235 ) );
        addPoint( new Point( -2.137158042603261, 5.257390140453709 ) );
        addPoint( new Point( -2.8793852415718186, 6.141942071345627 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.999999999999998, 1.1102230246251565E-15, -1.509903313490213E-14,
            -1.1102230246251565E-15, 0.999999999999998, 7.993605777301127E-15 ) );
        addPlacement( new Transform(
            -0.5000000000000008, -0.866025403784439, 1.3322676295501878E-15,
            0.866025403784439, -0.5000000000000008, 8.95905645404701 ) );
        addPlacement( new Transform(
            -0.5000000000000004, 0.8660254037844318, -7.7587704831436035,
            -0.8660254037844318, -0.5000000000000004, 4.47952822702352 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            1.0000000000000007, -1.1102230246251565E-16, -3.8793852415718186,
            1.1102230246251565E-16, 1.0000000000000007, 2.2397641135117525 ) );
        endFeature();

        b_desc="Nonagons and hexagons accompanied with almost pentagons.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Square 12.4.3" );

        setTranslations(
            new Point( 1.7320508075688759, -1.0000000000000007 ),
            new Point( 1.000000000000003, 1.7320508075688712 ) );

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            0.23205080756887697, 0.1339745962155613, 1.3660254037844386,
            -0.1339745962155613, 0.23205080756887697, 0.3660254037844366 ) );
        endFeature();

        beginRegularFeature( 12 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 3 );
        addPlacement( new Transform(
            1.3877787807814457E-17, -0.15470053837925152, 0.9999999999999999,
            0.15470053837925152, 1.3877787807814457E-17, 0.5773502691896256 ) );
        addPlacement( new Transform(
            -2.2749828308178106E-17, 0.1547005383792514, 1.7320508075688767,
            -0.1547005383792514, -2.2749828308178106E-17, 0.15470053837924946 ) );
        addPlacement( new Transform(
            0.15470053837925124, -2.7755575615628914E-17, 1.5773502691896262,
            2.7755575615628914E-17, 0.15470053837925124, 0.7320508075688738 ) );
        addPlacement( new Transform(
            -0.15470053837925143, -8.786881048632237E-17, 1.1547005383792515,
            8.786881048632237E-17, -0.15470053837925143, -1.4432899320127035E-15 ) );
        endFeature();

        b_desc="Simplified version of the square 12 tiling, with a subdivided inter-dodecagon.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "wonka" );

        setTranslations(
            new Point( 5.8541019662496865, 4.253254041760194 ),
            new Point( -4.472135954999581, 6.155367074350513 ) );

        beginPolygonFeature( 4 );
        addPoint( new Point( -0.6108513047783548, -2.1270858174611336 ) );
        addPoint( new Point( -0.6108513047783557, -3.580170873471857 ) );
        addPoint( new Point( 0.7711147064717497, -3.1311428968922725 ) );
        addPoint( new Point( 0.77111470647175, -1.6780578408815492 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.30901699437494673, -0.9510565162951522, 3.5099659498334153,
            0.9510565162951522, 0.30901699437494673, 5.588691901081472 ) );
        endFeature();

        beginRegularFeature( 10 );
        addPlacement( new Transform(
            2.2360679774997902, 1.3322676295501878E-15, 0.8720453880366232,
            -1.3322676295501878E-15, 2.2360679774997902, 5.803517177105929 ) );
        endFeature();

        beginPolygonFeature( 3 );
        addPoint( new Point( 1.7261473542863088, 2.2768056633510874 ) );
        addPoint( new Point( 0.8720453880366157, 1.101235158766137 ) );
        addPoint( new Point( 2.254011399286725, 1.550263135345725 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            -0.9999999999999976, 3.1086244689504383E-15, 6.216226731072813,
            -3.1086244689504383E-15, -0.9999999999999976, 5.451667279861347 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( -0.6108513047783553, -2.127085817461134 ) );
        addPoint( new Point( -0.6108513047783557, -3.580170873471857 ) );
        addPoint( new Point( 0.7711147064717497, -3.1311428968922725 ) );
        addPoint( new Point( 2.1530807177218545, -3.5801708734718587 ) );
        addPoint( new Point( 2.1530807177218536, -2.1270858174611345 ) );
        addPoint( new Point( 0.7711147064717493, -2.5761137940407193 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.8090169943749478, 0.5877852522924734, 4.718295919048433,
            -0.5877852522924734, -0.8090169943749478, 1.8215061814033011 ) );
        addPlacement( new Transform(
            -0.809016994374948, 0.5877852522924728, 1.6281259752989579,
            -0.5877852522924728, -0.809016994374948, 6.074760223163504 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 1.7261473542863088, 2.2768056633510874 ) );
        addPoint( new Point( 2.254011399286725, 1.550263135345725 ) );
        addPoint( new Point( 3.6359774105368308, 1.999291111925312 ) );
        addPoint( new Point( 4.4900793767865155, 3.17486161651026 ) );
        addPoint( new Point( 3.9622153317860986, 3.901404144515619 ) );
        addPoint( new Point( 2.5802493205359935, 3.452376167936033 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( -11.896929262333513, -4.341139920693237 ) );
        addPoint( new Point( -11.042827296083829, -5.516710425278183 ) );
        addPoint( new Point( -9.660861284833725, -5.067682448698599 ) );
        addPoint( new Point( -8.80675931858404, -3.892111944113654 ) );
        addPoint( new Point( -9.660861284833725, -2.7165414395287084 ) );
        addPoint( new Point( -11.042827296083829, -3.1655694161082923 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.30901699437494734, 0.9510565162951543, 7.2951021094485125,
            -0.9510565162951543, 0.30901699437494734, -6.071761943875446 ) );
        addPlacement( new Transform(
            0.30901699437494884, 0.9510565162951566, 8.677068120698642,
            -0.9510565162951566, 0.30901699437494884, 0.532633107054622 ) );
        addPlacement( new Transform(
            1.0000000000000007, 1.0923150274288004E-15, 8.296838695370564,
            -1.0923150274288004E-15, 1.0000000000000007, 7.793516088629268 ) );
        addPlacement( new Transform(
            0.3090169943749484, -0.9510565162951549, -3.1983046874999355,
            0.9510565162951549, 0.3090169943749484, 17.28408478181951 ) );
        addPlacement( new Transform(
            -0.3090169943749471, 0.9510565162951532, 4.942395463573192,
            -0.9510565162951532, -0.3090169943749471, -5.677050427607628 ) );
        endFeature();

        b_desc="Something or other...";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "var_5" );

        setTranslations(
            new Point( 5.0, -0.726542528005361 ),
            new Point( 1.3819660112501055, 1.9021130325903064 ) );

        beginPolygonFeature( 4 );
        addPoint( new Point( -0.6108513047783548, -2.1270858174611336 ) );
        addPoint( new Point( -0.6108513047783557, -3.580170873471857 ) );
        addPoint( new Point( 0.7711147064717497, -3.1311428968922725 ) );
        addPoint( new Point( 0.77111470647175, -1.6780578408815492 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0000000000000013, -7.216449660063518E-16, 1.3819660112501062,
            7.216449660063518E-16, 1.0000000000000013, 1.9021130325903082 ) );
        endFeature();

        beginRegularFeature( 5 );
        addPlacement( new Transform(
            0.8090169943749475, -0.5877852522924729, -1.8469192822781433,
            0.5877852522924729, 0.8090169943749475, 0.22405519170875854 ) );
        addPlacement( new Transform(
            0.3090169943749477, -0.9510565162951533, -0.22888529352824927,
            0.9510565162951533, 0.3090169943749477, -0.951515312876188 ) );
        endFeature();

        beginPolygonFeature( 4 );
        addPoint( new Point( -1.464953271028038, 1.3996256962937044 ) );
        addPoint( new Point( -0.6108513047783539, 0.2240551917087581 ) );
        addPoint( new Point( 0.771114706471751, -0.22497278487082784 ) );
        addPoint( new Point( -0.08298725977793286, 0.9505977197141181 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, -3.885780586188048E-16, 6.661338147750939E-16,
            3.885780586188048E-16, 1.0, 4.440892098500626E-16 ) );
        endFeature();

        b_desc="Another variation of csk 5.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "9.6 Losange" );

        setTranslations(
            new Point( 8.881784197001252E-16, -9.974483065932741 ),
            new Point( 8.63815572471545, 4.987241532966373 ) );

        beginRegularFeature( 9 );
        addPlacement( new Transform(
            0.7931284138572717, 1.3737387097273117, 2.586256827714544,
            -1.3737387097273117, 0.7931284138572717, -9.992007221626409E-16 ) );
        addPlacement( new Transform(
            1.5862568277145435, 1.1102230246251565E-16, 8.931284138572721,
            -1.1102230246251565E-16, 1.5862568277145435, 2.6645352591003757E-15 ) );
        addPlacement( new Transform(
            0.7931284138572725, 1.3737387097273117, 7.3450273108581765,
            -1.3737387097273117, 0.7931284138572725, 2.747477419454623 ) );
        addPlacement( new Transform(
            1.5862568277145437, 2.450119647326695E-16, 4.1725136554290865,
            -2.450119647326695E-16, 1.5862568277145437, -2.747477419454622 ) );
        addPlacement( new Transform(
            1.5862568277145446, -3.3306690738754696E-16, 4.1725136554290865,
            3.3306690738754696E-16, 1.5862568277145446, 2.747477419454622 ) );
        addPlacement( new Transform(
            1.4905938356746211, -0.5425317875662488, 7.345027310858178,
            0.5425317875662488, 1.4905938356746211, -2.7474774194546185 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( -7.25899134657012E-16, 3.3248276886442483 ) );
        addPoint( new Point( 0.3949308436346982, 2.2397641135117494 ) );
        addPoint( new Point( 1.7422271989685585, 1.461902200081543 ) );
        addPoint( new Point( 2.879385241571814, 1.6624138443221237 ) );
        addPoint( new Point( 2.484454397937116, 2.7474774194546216 ) );
        addPoint( new Point( 1.1371580426032561, 3.525339332884828 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0000000000000018, 1.2212453270876722E-15, 8.638155724715446,
            -1.2212453270876722E-15, 1.0000000000000018, -4.987241532966368 ) );
        addPlacement( new Transform(
            -0.5000000000000002, 0.866025403784441, 8.63815572471544,
            -0.866025403784441, -0.5000000000000002, 4.987241532966371 ) );
        addPlacement( new Transform(
            -0.4999999999999998, -0.8660254037844402, 8.638155724715451,
            0.8660254037844402, -0.4999999999999998, -4.987241532966371 ) );
        endFeature();

        beginPolygonFeature( 4 );
        addPoint( new Point( -1.000000000000002, -3.9021779578338753 ) );
        addPoint( new Point( -2.137158042603259, -3.701666313593295 ) );
        addPoint( new Point( -2.879385241571817, -4.9872415329663715 ) );
        addPoint( new Point( -1.3949308436347019, -4.987241532966375 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.5000000000000018, 0.8660254037844366, 8.638155724715435,
            -0.8660254037844366, -0.5000000000000018, -4.987241532966372 ) );
        addPlacement( new Transform(
            1.0000000000000007, -2.4980018054066022E-15, 8.63815572471544,
            2.4980018054066022E-15, 1.0000000000000007, 4.987241532966384 ) );
        addPlacement( new Transform(
            -1.0000000000000016, 2.3314683517128287E-15, 2.8793852415718226,
            -2.3314683517128287E-15, -1.0000000000000016, -4.987241532966387 ) );
        addPlacement( new Transform(
            0.4999999999999998, 0.8660254037844384, 11.517540966287266,
            -0.8660254037844384, 0.4999999999999998, 1.7763568394002505E-15 ) );
        addPlacement( new Transform(
            0.5000000000000022, -0.8660254037844373, 2.8793852415718306,
            0.8660254037844373, 0.5000000000000022, 4.987241532966378 ) );
        addPlacement( new Transform(
            -0.5000000000000001, -0.8660254037844393, -3.9968028886505635E-15,
            0.8660254037844393, -0.5000000000000001, -3.3306690738754696E-16 ) );
        endFeature();

        beginPolygonFeature( 4 );
        addPoint( new Point( -2.1371580426032573, 0.7778619134302067 ) );
        addPoint( new Point( -2.137158042603259, -0.7778619134302074 ) );
        addPoint( new Point( -1.0000000000000004, -0.5773502691896257 ) );
        addPoint( new Point( -1.0, 0.5773502691896262 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.49999999999999956, 0.8660254037844385, 8.63815572471545,
            -0.8660254037844385, -0.49999999999999956, -4.987241532966368 ) );
        addPlacement( new Transform(
            1.0, 0.0, 8.63815572471545,
            0.0, 1.0, -4.987241532966368 ) );
        addPlacement( new Transform(
            -0.49999999999999967, -0.866025403784438, 8.63815572471545,
            0.866025403784438, -0.49999999999999967, 4.987241532966372 ) );
        addPlacement( new Transform(
            0.4999999999999996, -0.8660254037844384, 11.517540966287264,
            0.8660254037844384, 0.4999999999999996, 3.552713678800501E-15 ) );
        addPlacement( new Transform(
            0.4999999999999998, 0.8660254037844378, 11.517540966287264,
            -0.8660254037844378, 0.4999999999999998, 3.552713678800501E-15 ) );
        addPlacement( new Transform(
            -0.9999999999999998, 1.5165928555043597E-16, 2.8793852415718146,
            -1.5165928555043597E-16, -0.9999999999999998, -4.98724153296637 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            1.0, 0.0, 8.63815572471545,
            0.0, 1.0, -4.987241532966368 ) );
        addPlacement( new Transform(
            0.9999999999999998, -5.551115123125783E-17, 11.517540966287264,
            5.551115123125783E-17, 0.9999999999999998, 3.552713678800501E-15 ) );
        endFeature();

        b_desc="Interesting design involving hexagons, nonagons, hats and kites.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Curvy Hex" );

        setTranslations(
            new Point( -6.661338147750939E-15, -6.646889387578672 ),
            new Point( -5.756375065788287, 3.323444693789365 ) );

        beginPolygonFeature( 4 );
        addPoint( new Point( -3.2331814768873626, 2.445013417881989 ) );
        addPoint( new Point( -3.535294847784564, 1.0236817566829526 ) );
        addPoint( new Point( -2.153328836534466, 0.5746537801033692 ) );
        addPoint( new Point( -1.8512154656372575, 1.9959854413023976 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.4999999999999952, -0.8660254037844403, -2.732328482851403,
            0.8660254037844403, -0.4999999999999952, 7.920622476863444 ) );
        addPlacement( new Transform(
            -0.5000000000000141, 0.8660254037844275, -2.469249454387229,
            -0.8660254037844275, -0.5000000000000141, -1.7293993329906803 ) );
        addPlacement( new Transform(
            0.9999999999999996, 4.6074255521944E-15, -1.1546319456101628E-14,
            -4.6074255521944E-15, 0.9999999999999996, -1.3988810110276972E-14 ) );
        endFeature();

        beginRegularFeature( 5 );
        addPlacement( new Transform(
            0.9781476007338067, 0.20791169081776562, 1.3939893027185661,
            -0.20791169081776562, 0.9781476007338067, 5.265703971889555 ) );
        addPlacement( new Transform(
            0.8090169943749469, -0.5877852522924724, -2.2331814768873626,
            0.5877852522924724, 0.8090169943749469, 3.1715559458873486 ) );
        addPlacement( new Transform(
            0.9135454576426004, -0.40673664307580054, -1.0241245503520569,
            0.40673664307580054, 0.9135454576426004, 1.0774079198851276 ) );
        addPlacement( new Transform(
            0.9781476007338058, -0.2079116908177459, 1.393989302718516,
            0.2079116908177459, 0.9781476007338058, 1.0774079198851139 ) );
        addPlacement( new Transform(
            1.0000000000000016, 9.270362255620078E-15, 2.603046229253853,
            -9.270362255620078E-15, 1.0000000000000016, 3.171555945887318 ) );
        addPlacement( new Transform(
            0.9135454576426023, 0.4067366430758037, -1.0241245503520509,
            -0.4067366430758037, 0.9135454576426023, 5.265703971889571 ) );
        endFeature();

        beginPolygonFeature( 4 );
        addPoint( new Point( -0.9971134993875739, 3.1715559458873477 ) );
        addPoint( new Point( -1.8512154656372575, 1.9959854413023976 ) );
        addPoint( new Point( -0.4060905616021637, 2.147874189204397 ) );
        addPoint( new Point( 0.18493237618324354, 3.171555945887346 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.5000000000000149, -0.8660254037844286, 2.839114206753615,
            0.8660254037844286, 0.5000000000000149, 1.4256218371867178 ) );
        addPlacement( new Transform(
            0.9999999999999982, 2.1149748619109232E-14, -6.439293542825908E-14,
            -2.1149748619109232E-14, 0.9999999999999982, 8.659739592076221E-15 ) );
        addPlacement( new Transform(
            -0.499999999999987, -0.8660254037844454, 3.0240465829369114,
            0.8660254037844454, -0.499999999999987, 4.597177783074066 ) );
        addPlacement( new Transform(
            0.4999999999999962, 0.8660254037844419, -2.654181830570412,
            -0.8660254037844419, 0.4999999999999962, 1.745934108700593 ) );
        addPlacement( new Transform(
            -0.9999999999999999, -1.1435297153639112E-14, 0.3698647523665244,
            1.1435297153639112E-14, -0.9999999999999999, 6.343111891774688 ) );
        addPlacement( new Transform(
            -0.5000000000000073, 0.8660254037844349, -2.469249454387146,
            -0.8660254037844349, -0.5000000000000073, 4.917490054587949 ) );
        endFeature();

        beginRegularFeature( 3 );
        addPlacement( new Transform(
            -0.41946952412160593, -1.3877787807814457E-17, -1.7338593124128607,
            1.3877787807814457E-17, -0.41946952412160593, -0.15188874790199114 ) );
        addPlacement( new Transform(
            -0.209734762060803, -0.3632712640026802, -3.652651001008968,
            0.3632712640026802, -0.209734762060803, 3.171555945887349 ) );
        endFeature();

        b_desc="Pentagons arranged to create a curvy hexagon.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "4.5" );

        setTranslations(
            new Point( 4.5201470213401995, 4.520147021340199 ),
            new Point( -1.1102230246251565E-14, 9.040294042680392 ) );

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginPolygonFeature( 5 );
        addPoint( new Point( 0.9999999999999994, 1.0 ) );
        addPoint( new Point( 0.9999999999999991, -0.9999999999999999 ) );
        addPoint( new Point( 2.9021130325903055, -1.6180339887498945 ) );
        addPoint( new Point( 4.5201470213402, 1.7208456881689926E-15 ) );
        addPoint( new Point( 2.9021130325903046, 1.6180339887498938 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            -5.55111512312578E-17, -0.9999999999999999, 1.1102230246251565E-16,
            0.9999999999999999, -5.55111512312578E-17, 6.661338147750939E-16 ) );
        addPlacement( new Transform(
            -0.9999999999999999, -5.551115123125785E-17, -7.771561172376096E-16,
            5.551115123125785E-17, -0.9999999999999999, 0.0 ) );
        addPlacement( new Transform(
            -5.551115123125783E-17, 1.0, -3.3306690738754696E-16,
            -1.0, -5.551115123125783E-17, -6.661338147750939E-16 ) );
        endFeature();

        beginPolygonFeature( 4 );
        addPoint( new Point( 1.6180339887498933, 2.902113032590306 ) );
        addPoint( new Point( 0.9999999999999998, 0.9999999999999998 ) );
        addPoint( new Point( 2.902113032590306, 1.618033988749895 ) );
        addPoint( new Point( 3.5201470213402, 3.5201470213402013 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            6.106226635438361E-16, -1.0000000000000002, -4.440892098500626E-16,
            1.0000000000000002, 6.106226635438361E-16, -8.881784197001252E-16 ) );
        addPlacement( new Transform(
            0.9999999999999999, 6.07651844567604E-16, -7.770749474514578E-16,
            -6.07651844567604E-16, 0.9999999999999999, 8.881784197001252E-16 ) );
        endFeature();

        b_desc="Squares within squares.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "6.5" );

        setTranslations(
            new Point( 4.0000000000000036, 2.3094010767585016 ),
            new Point( 2.4424906541753444E-15, 4.618802153517007 ) );

        beginPolygonFeature( 5 );
        addPoint( new Point( 4.440892098500626E-16, 2.309401076758502 ) );
        addPoint( new Point( 2.0749013332209233E-16, 1.1547005383792512 ) );
        addPoint( new Point( 1.0, 0.5773502691896255 ) );
        addPoint( new Point( 2.0000000000000013, 1.154700538379251 ) );
        addPoint( new Point( 1.3333333333333341, 2.309401076758502 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.5000000000000002, 0.8660254037844384, 3.3306690738754696E-16,
            -0.8660254037844384, -0.5000000000000002, -1.1102230246251565E-16 ) );
        addPlacement( new Transform(
            -1.0000000000000004, -1.6653345369377348E-16, 2.874853133363391E-16,
            1.6653345369377348E-16, -1.0000000000000004, 2.220446049250313E-16 ) );
        addPlacement( new Transform(
            1.0, 5.551115123125783E-17, -2.623618914725532E-16,
            -5.551115123125783E-17, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.5000000000000001, -0.8660254037844386, -2.220446049250313E-16,
            0.8660254037844386, 0.5000000000000001, -1.1102230246251565E-16 ) );
        addPlacement( new Transform(
            0.5000000000000001, 0.8660254037844392, -6.661338147750939E-16,
            -0.8660254037844392, 0.5000000000000001, 2.220446049250313E-16 ) );
        addPlacement( new Transform(
            -0.4999999999999998, -0.8660254037844389, 2.220446049250313E-16,
            0.8660254037844389, -0.4999999999999998, -2.220446049250313E-16 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        b_desc="Hexagon made up of a smaller hexagon surrounded by pentagons.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "6.4 Boats" );

        setTranslations(
            new Point( 5.464101615137755, -4.440892098500626E-16 ),
            new Point( 4.440892098500626E-16, 5.464101615137753 ) );

        beginPolygonFeature( 3 );
        addPoint( new Point( 0.9999999999999994, -1.0 ) );
        addPoint( new Point( 1.9999999999999991, -2.732050807568877 ) );
        addPoint( new Point( 2.732050807568876, -2.0 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.999999999999999, -1.1102230246251573E-16, 2.2204460492503147E-15,
            1.1102230246251573E-16, 0.999999999999999, 5.464101615137749 ) );
        addPlacement( new Transform(
            -0.999999999999999, 1.1102230246251573E-16, 5.464101615137748,
            -1.1102230246251573E-16, -0.999999999999999, 0.0 ) );
        endFeature();

        beginPolygonFeature( 6 );
        addPoint( new Point( 1.9999999999999996, -2.7320508075688776 ) );
        addPoint( new Point( 0.9999999999999982, -4.464101615137756 ) );
        addPoint( new Point( 2.732050807568875, -3.464101615137757 ) );
        addPoint( new Point( 3.464101615137752, -2.7320508075688794 ) );
        addPoint( new Point( 4.4641016151377535, -1.0000000000000009 ) );
        addPoint( new Point( 2.7320508075688767, -2.0 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.9999999999999983, -1.1102230246251565E-16, 3.1086244689504383E-15,
            1.1102230246251565E-16, 0.9999999999999983, 5.464101615137748 ) );
        endFeature();

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            1.7320508075688772, -3.7768941181755933E-16, 2.7320508075688767,
            3.7768941181755933E-16, 1.7320508075688772, -2.220446049250313E-16 ) );
        addPlacement( new Transform(
            1.4999999999999993, -0.8660254037844386, 4.440892098500626E-16,
            0.8660254037844386, 1.4999999999999993, 2.7320508075688763 ) );
        endFeature();

        b_desc="Square lattice of hexagons.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "6.4 Circles" );

        setTranslations(
            new Point( 5.464101615137754, 0.0 ),
            new Point( 0.0, 5.464101615137753 ) );

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            1.0, 0.0, -6.003141617071581,
            0.0, 1.0, -8.261204060190243 ) );
        addPlacement( new Transform(
            0.36602540378443826, -0.3660254037844387, -3.271090809502703,
            0.3660254037844387, 0.36602540378443826, -5.5291532526213665 ) );
        endFeature();

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            1.4999999999999993, -0.8660254037844386, -6.003141617071581,
            0.8660254037844386, 1.4999999999999993, -5.5291532526213665 ) );
        addPlacement( new Transform(
            1.7320508075688772, -3.7768941181755933E-16, -3.271090809502704,
            3.7768941181755933E-16, 1.7320508075688772, -8.261204060190243 ) );
        endFeature();

        beginPolygonFeature( 3 );
        addPoint( new Point( -4.003141617071582, -5.529153252621367 ) );
        addPoint( new Point( -5.003141617071581, -7.261204060190243 ) );
        addPoint( new Point( -3.2710908095027045, -6.261204060190243 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -0.9999999999999994, -8.200941421452468E-16, -6.542181619005413,
            8.200941421452468E-16, -0.9999999999999994, -11.058306505242728 ) );
        addPlacement( new Transform(
            -1.2191094926376126E-16, -1.0000000000000007, -8.800244062124076,
            1.0000000000000007, -1.2191094926376126E-16, -2.25806244311866 ) );
        addPlacement( new Transform(
            -4.996003610813204E-16, 1.0000000000000004, 2.258062443118664,
            -1.0000000000000004, -4.996003610813204E-16, -8.800244062124076 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        b_desc="A square lattice of hexagons.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "PentaFlower" );

        setTranslations(
            new Point( 4.449489742783179, -4.4494897427831805 ),
            new Point( 4.449489742783179, 4.449489742783179 ) );

        beginPolygonFeature( 4 );
        addPoint( new Point( 0.9999999999999997, -1.0000000000000004 ) );
        addPoint( new Point( 1.517638090205041, -2.9318516525781373 ) );
        addPoint( new Point( 3.4494897427831788, -3.44948974278318 ) );
        addPoint( new Point( 2.9318516525781373, -1.5176380902050421 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            0.9999999999999998, -1.6098233857064774E-15, -1.3322676295501867E-15,
            1.6098233857064774E-15, 0.9999999999999998, -1.7763568394002516E-15 ) );
        addPlacement( new Transform(
            -1.6653345369377352E-15, -0.9999999999999998, 1.5543122344752203E-15,
            0.9999999999999998, -1.6653345369377352E-15, -1.221245327087671E-15 ) );
        endFeature();

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginPolygonFeature( 5 );
        addPoint( new Point( -0.9999999999999996, 1.0000000000000009 ) );
        addPoint( new Point( 0.9999999999999998, 0.9999999999999991 ) );
        addPoint( new Point( 1.517638090205041, 2.931851652578139 ) );
        addPoint( new Point( -1.7763568394002505E-15, 4.44948974278318 ) );
        addPoint( new Point( -1.5176380902050424, 2.931851652578136 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            7.216449660063522E-16, 1.0000000000000004, -4.440892098500626E-16,
            -1.0000000000000004, 7.216449660063522E-16, -8.881784197001252E-16 ) );
        addPlacement( new Transform(
            -1.0000000000000004, 6.661338147750944E-16, -9.992007221626409E-16,
            -6.661338147750944E-16, -1.0000000000000004, 2.220446049250313E-16 ) );
        addPlacement( new Transform(
            -6.661338147750942E-16, -1.0000000000000002, 0.0,
            1.0000000000000002, -6.661338147750942E-16, 6.661338147750939E-16 ) );
        endFeature();

        b_desc="Pentagoned square flowers.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "4.7 Stars" );

        setTranslations(
            new Point( 6.315456525104546, 6.315456525104546 ),
            new Point( 6.315456525104544, -6.315456525104543 ) );

        beginPolygonFeature( 8 );
        addPoint( new Point( 0.9999999999999997, -1.0 ) );
        addPoint( new Point( 2.246979603717466, -2.5636629649360594 ) );
        addPoint( new Point( 1.8019377358048372, -4.513518789299706 ) );
        addPoint( new Point( 3.7517935601684846, -4.068476921387079 ) );
        addPoint( new Point( 5.315456525104544, -5.315456525104546 ) );
        addPoint( new Point( 4.068476921387077, -3.751793560168487 ) );
        addPoint( new Point( 4.513518789299707, -1.8019377358048407 ) );
        addPoint( new Point( 2.563662964936059, -2.2469796037174676 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, -6.661338147750939E-16, -8.881784197001252E-16,
            6.661338147750939E-16, 1.0, -1.7763568394002505E-15 ) );
        addPlacement( new Transform(
            -8.881784197001252E-16, -0.9999999999999999, 2.4424906541753444E-15,
            0.9999999999999999, -8.881784197001252E-16, -4.440892098500626E-16 ) );
        endFeature();

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 7 );
        addPlacement( new Transform(
            2.0765213965723355, 2.2171955803932498E-16, -3.0765213965723346,
            -2.2171955803932498E-16, 2.0765213965723355, 6.661338147750939E-16 ) );
        addPlacement( new Transform(
            0.900968867902419, -1.8708811318449283, 4.440892098500626E-16,
            1.8708811318449283, 0.900968867902419, 3.0765213965723364 ) );
        addPlacement( new Transform(
            1.8708811318449285, -0.9009688679024189, 3.076521396572337,
            0.9009688679024189, 1.8708811318449285, -8.881784197001252E-16 ) );
        addPlacement( new Transform(
            2.2171955803932494E-16, -2.076521396572336, -7.771561172376096E-16,
            2.076521396572336, 2.2171955803932494E-16, -3.0765213965723355 ) );
        endFeature();

        beginPolygonFeature( 8 );
        addPoint( new Point( 4.513518789299707, 1.8019377358048407 ) );
        addPoint( new Point( 5.381286267534824, -1.7763568394002505E-15 ) );
        addPoint( new Point( 4.513518789299707, -1.8019377358048396 ) );
        addPoint( new Point( 6.315456525104544, -0.9341702575697233 ) );
        addPoint( new Point( 8.11739426090938, -1.8019377358048392 ) );
        addPoint( new Point( 7.249626782674265, 1.5379008124111138E-16 ) );
        addPoint( new Point( 8.117394260909382, 1.8019377358048387 ) );
        addPoint( new Point( 6.315456525104544, 0.9341702575697242 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        b_desc="Square lattice of heptagons.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "6 Losange" );

        setTranslations(
            new Point( 4.449489742783179, -4.4494897427831805 ),
            new Point( 4.449489742783179, 4.449489742783179 ) );

        beginPolygonFeature( 6 );
        addPoint( new Point( 0.9999999999999996, 1.0000000000000004 ) );
        addPoint( new Point( 4.440892098500626E-16, 0.0 ) );
        addPoint( new Point( 1.0000000000000004, -1.0 ) );
        addPoint( new Point( 2.931851652578139, -1.5176380902050428 ) );
        addPoint( new Point( 4.449489742783177, -8.881784197001252E-16 ) );
        addPoint( new Point( 2.9318516525781346, 1.5176380902050415 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.998401444325281E-15, 0.9999999999999993, 4.4408920985006173E-16,
            -0.9999999999999993, 1.998401444325281E-15, 4.440892098500623E-16 ) );
        addPlacement( new Transform(
            -0.9999999999999996, 1.3322676295501875E-15, 8.88178419700125E-16,
            -1.3322676295501875E-15, -0.9999999999999996, 5.9164567891575885E-31 ) );
        addPlacement( new Transform(
            -6.661338147750939E-16, -0.9999999999999998, 4.440892098500629E-16,
            0.9999999999999998, -6.661338147750939E-16, -4.440892098500625E-16 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginPolygonFeature( 4 );
        addPoint( new Point( -2.931851652578139, 1.5176380902050381 ) );
        addPoint( new Point( -1.0000000000000004, 0.9999999999999982 ) );
        addPoint( new Point( -1.5176380902050426, 2.931851652578133 ) );
        addPoint( new Point( -3.4494897427831797, 3.4494897427831797 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -7.216449660063511E-16, 1.0000000000000002, 2.2204460492503106E-16,
            -1.0000000000000002, -7.216449660063511E-16, 4.440892098500623E-16 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        b_desc="Flower of weird hexagons.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();

        beginTiling( "Square Wave" );

        setTranslations(
            new Point( 4.449489742783179, -4.4494897427831805 ),
            new Point( 4.449489742783179, 4.449489742783179 ) );

        beginPolygonFeature( 4 );
        addPoint( new Point( -3.4494897427831797, 3.4494897427831797 ) );
        addPoint( new Point( -2.9318516525781373, 1.5176380902050415 ) );
        addPoint( new Point( -0.9999999999999996, 1.0000000000000009 ) );
        addPoint( new Point( -1.5176380902050424, 2.931851652578136 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            1.7763568394002513E-15, -1.0, -4.449489742783176,
            1.0, 1.7763568394002513E-15, 4.449489742783179 ) );
        endFeature();

        beginPolygonFeature( 4 );
        addPoint( new Point( 4.50911648813859, 1.3423744710142615 ) );
        addPoint( new Point( 3.991478397933546, -0.5894771815638746 ) );
        addPoint( new Point( 7.026754578343629, -0.589477181563876 ) );
        addPoint( new Point( 6.509116488138589, 1.3423744710142613 ) );
        commitPolygonFeature();
        addPlacement( new Transform(
            -1.0000000000000002, 2.465190328815662E-32, 1.0596267453554127,
            -2.465190328815662E-32, -1.0000000000000002, -2.107115271768917 ) );
        addPlacement( new Transform(
            5.55111512312578E-17, -1.0000000000000004, -6.556605014552096,
            1.0000000000000004, 5.55111512312578E-17, -5.509116488138591 ) );
        addPlacement( new Transform(
            1.0000000000000004, 1.1102230246251565E-16, -9.95860623092177,
            -1.1102230246251565E-16, 1.0000000000000004, 2.1071152717689197 ) );
        addPlacement( new Transform(
            -1.1102230246251568E-16, 1.0000000000000002, -2.342374471014261,
            -1.0000000000000002, -1.1102230246251568E-16, 5.5091164881385914 ) );
        endFeature();

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            1.5176380902050428, 4.1339630215602603E-16, -4.449489742783177,
            -4.1339630215602603E-16, 1.5176380902050428, -8.881784197001252E-16 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        b_desc="A wave of squares.";

        //setAuthor( "Pierre Baillargeon" );

        endTiling();
        
        
        
		} catch( Exception e ) {
			System.err.println( e );
			e.printStackTrace();
		}
	}
}
