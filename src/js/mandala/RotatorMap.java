package js.mandala;

import java.awt.Color;
import java.util.ArrayList;

import csk.taprats.geometry.Ngon;
import csk.taprats.geometry.Point;
import csk.taprats.geometry.Primitive;


public class RotatorMap {
	int num, denom;
	double wobble;
	boolean minsky;
	double wrap_range;
	int extra_hskew;
	int colorid;
	int width, height, skew_maps_size, skew_maps_base, skew_maps_min, skew_maps_max;
	int show_base = -32;  //  Lowest value for both x and y on screen--upper left corner.
	double angle, c, s, epsilon;
	int hskew1[], vskew[], hskew2[];
	int step_counts [] [];
	int max_n_steps = 20000;
	Color colors [];
	int biggest_size;
	
	
	void setColorFor_n_steps( int n_steps ) {
		float z, h, s, b, r, gr;
		int which_colormap;
		Color color = null;  // Convince the compiler that it will be initialized.
		
		if( n_steps <= max_n_steps + 1  &&  colors[ n_steps ] != null ) {
//			g.setColor( colors[ n_steps ] );
		} else {
			if( n_steps == max_n_steps + 1) {
				color = Color.black;  
			} else {
				if( wrap_range == 0.0 )	which_colormap = 1;
				else					which_colormap = 1;
				if( which_colormap == 1 ) {
					z = (float) Math.log( n_steps );
					h = z - (float) Math.floor( z );
					b = (float) ( 1 - .5 * ( z / Math.log( max_n_steps ) ) );
					if( b > .5 )	s = (float) ( ( 1.0 - b ) * 2 /* + .5 */ );
					else			s = 1;
	//		        color = Color.getHSBColor( h, s, b );
					color = Color.getHSBColor( h, (float)0.85, 1 );
				} else if( which_colormap == 2 ) {  // wrapping--assume all paths cycle
					z = (float) ( n_steps / 6.333333 );
					h = z - (float) Math.floor( z );
					b = (float) ( .25 + .75 / ( 1 + (float) n_steps / max_n_steps ) );
					if( b > .5 )	s = (float) ( ( 1.0 - b ) + .5 );
					else			s = 1;
	//				color = Color.getHSBColor( h, s, b );
					color = Color.getHSBColor( h, (float) 0.85, 1 );
				} else {
					b  = (float) ( n_steps % 17 ) / 16;
					r  = (float) ( n_steps % 91 ) / 90;
					gr = (float) ( n_steps % 123 ) / 122;
					color = new Color( r, gr, b );
				}
			}
			if( n_steps <= max_n_steps + 1 )  colors[ n_steps ] = color;
//			g.setColor( color );
		}
	}
	   
	
	public RotatorMap( int width, int height , int colorid) {
		this.width = width;
		this.height = height;
		this.colorid=colorid;
		step_counts = new int [width] [height];
		show_base=-width/2;
		
		}
		
	
	// Here is the rotation transformation expressed as a procedure that changes
	// the x and y of a Point in-place.  It's not actually used because calling
	// this procedure inside the inner loop was a lot slower than calculating
	// in-line.
	// Two wrongs don't make a right but three skews make a rotate.
	void rotate( Point p ) {
		double x,y;
		x=p.getX();
		y=p.getY();
	    x += (int) Math.round( c * y );
	    y += (int) Math.round( s * x );
	    x += (int) Math.round( c * y);
        p.setX(x);
        p.setY(y);
	   }
	   
	   
	// This procedure sets up hskew[] and vskew[] to have the same effect as
	// either rotate(), or Minsky's eliptical variation of it.
	// Plus sets up for the wobble and wrap effects.
	void set_up_skews( boolean minsky, double wobble, double wrap_range, int extra_hskew ) {

		
		this.minsky = minsky;
		this.wobble = wobble;
		this.wrap_range = wrap_range;
		this.extra_hskew = extra_hskew;
		
		// The rotation, whether regular or Minsky, is done with skews,
		// adding an int function of (the int) y to x for horizontal skew, or
		// adding an int function of (the int) x to y for vertical skew.
		// Since the range of the functions is relatively small, and computing
		// them is a little complicated with "wobble," I make tables out of them.
		// There are two horizontal skew maps because in the Minsky variation the
		// second horizontal skew isn't done.
		
		// The range of the functions that is pre-calculated is generally larger than the range
		// of x and y values shown on the screen, so that the trajectories of the points can
		// take an off-screen trip around (0,0).  But this depends on the "wrap_range" parameter.  
		// If wrap_range zero, then the skew tables are expanded whenever a point goes out of range.
		// If wrap_range is nonzero, it's a multiplier for the largest of the screen width or
		//    height.  A fixed range is set, the same for both x and y, and x and y values are
		//    wrapped to stay within this range.  This whole operation is reversible because:
		//       o  the wrapping is done after each skew,
		//       o  horizontal wrap doesn't change the input to horizontal skew,
		//       o  vertical wrap doesn't change the input to vertical skew, &
		//       o  skew then wrap can be reversed by reverse skew then wrap.
						
		if( width > height )		biggest_size = width;
		else						biggest_size = height;
		
		if( wrap_range == 0.0 )		skew_maps_size =         biggest_size * 2;
		else						skew_maps_size = (int) ( biggest_size * wrap_range );
		
		// Make skew_maps_size odd so that it can have a center value:
		skew_maps_size |= 1;
		
		// Try to center the wrap range around zero, but if it's too tight for that,
		// adjust the center as little as possible to get the screen inside the range
		// --but the range is kept the same for x and y.
		skew_maps_base = skew_maps_size / 2;
		if( skew_maps_base   + ( show_base + biggest_size ) > skew_maps_size ) {
			skew_maps_base = - ( show_base + biggest_size ) + skew_maps_size;
			// System.out.println( "skew_maps_base = " + skew_maps_base );
		}
		
		hskew1 = null;  // Having this null triggers recreating them.
		int x_or_y = adjust_skew_maps( 0 );
	}
		
			
	int adjust_skew_maps( int x_or_y ) {
		if( wrap_range == 0.0 ) {
			// Can't count on skew_maps_min or skew_maps_max here.
			while(	skew_maps_base + x_or_y < 0  ||
					skew_maps_base + x_or_y >= skew_maps_size ) {
				skew_maps_size *= 2;
				skew_maps_base = skew_maps_size / 2;
				hskew1 = null;
			}
		}
		if( hskew1 == null )  {
			// System.out.println( "adjust_skew_maps( " + x_or_y + " ) skew_maps_size == " + skew_maps_size );
			skew_maps_min = -skew_maps_base;
			skew_maps_max = skew_maps_min + skew_maps_size - 1;
			// System.out.println( "skew_maps_min = " + skew_maps_min + ", _max = " + skew_maps_max );

			
			hskew1 = new int[ skew_maps_size ];
			vskew = new int[ skew_maps_size ];
			hskew2 = new int[ skew_maps_size ];

			double lowangle = num / ( ( 1 + wobble ) * denom );
			double hiangle = num / ( ( 1 - wobble ) * denom );
			double wobble_frequency = 1 / 512.0;
		
			for( int i = -skew_maps_base;  i + skew_maps_base < skew_maps_size;  i++ ) {
				double x = i * 2 * Math.PI * wobble_frequency;
				angle = Math.PI * 2 * ( lowangle - (Math.cos(x)-1)/2 * (hiangle - lowangle) );
				if( minsky ) { // eliptical  HAKMEM items 149 (Minsky), 151 (Gosper)
					epsilon = Math.sqrt( 2 * ( 1 - Math.cos( angle ) ) );  //
					// See below about the .00000001 
					hskew1[ i + skew_maps_base ] = (int) -Math.round( epsilon * i + .00000001 ) + extra_hskew;
					vskew[  i + skew_maps_base ] = (int)  Math.round( epsilon * i + .00000001 );
					hskew2[ i + skew_maps_base ] = 0;
				} else { // Normal, circular, "Givens":
					s = Math.sin( angle );
					c = ( Math.cos( angle ) - 1 ) / s;
					hskew1[ i + skew_maps_base ] = (int) Math.round( c * i );  // indexed by y
					// In Java the following things are true:
					// s = sin( Math.PI * 2 * ( 1 / 12 ) ) = .49999999999999994
					// Math.round(s)       = 1.0   (!?)
					// Math.round( s * 2 ) = 1.0
					// Math.round( s * 3 ) = 1.0
					// This causes a mess in the picture when denom == 12,
					// so this is the fix I came up with:
					vskew[  i + skew_maps_base ] = (int) Math.round( s * i + .00000001 );  // indexed by x
					hskew2[ i + skew_maps_base ] = hskew1[ i + skew_maps_base ] + extra_hskew;
				}
			}
		}
		return( x_or_y - skew_maps_size * 
			(int) Math.floor( (double) ( skew_maps_base + x_or_y ) / skew_maps_size )
		);
	}
	   
	   
	public void drawMap(  ArrayList< Primitive>  primitives, int num, int denom, boolean minsky, double wobble, double wrap_range,
					int extra_hskew ) {
//		Graphics g = canvas.image.getGraphics();
		int n_steps;
		int min_show_x, max_show_x, min_show_y, max_show_y;
		
		this.num = num;
		this.denom = denom;
		
		// angle = Math.PI * 2 * num / denom ... on average, but see:
		set_up_skews( minsky, wobble, wrap_range, extra_hskew );

		int offs_x = -show_base;     int offs_y = -show_base;
		min_show_x = show_base;  max_show_x = min_show_x + width - 1;
		min_show_y = show_base;  max_show_y = min_show_y + height - 1;
		colors = new Color[ max_n_steps + 2 ];

	    int paintDelay = height;
		for( int x = min_show_x;  x <= max_show_x;  x++ ) {
			for( int y = min_show_y;  y <= max_show_y;  y++ ) {
				if( step_counts[ x + offs_x ][ y + offs_y ] == 0 ) {
					int px = x;
					int py = y;
//					g.setColor( Color.black );  // show some kind of progress...
					for( n_steps = 1;  n_steps <= max_n_steps /* ||  wrap_range > 0.0 */;  n_steps++ ) {
						for( int i_denom = 0;  i_denom < denom;  i_denom++ ) {
							px += hskew1[ py + skew_maps_base ];
							if( px < skew_maps_min  || px > skew_maps_max )  px = adjust_skew_maps( px );
							py += vskew[ px + skew_maps_base ];
							if( py < skew_maps_min  || py > skew_maps_max )  py = adjust_skew_maps( py );
							px += hskew2[ py + skew_maps_base ];
							if( px < skew_maps_min  || px > skew_maps_max )  px = adjust_skew_maps( px );
						}
						if( px == x  &&  py == y ) {  // Found a cycle...
							break;
							}
						if( n_steps > max_n_steps ) {
//						 	g.fillRect( px + offs_x, py + offs_y, 1, 1 );
	//						Ngon poly=new Ngon(4,1.0/(float)biggest_size,0.0,new Point(px + offs_x, py + offs_y),colors[n_steps],0.0);
	//					    primitives.add(poly);

							if( --paintDelay <= 0 ) {
//								canvas.paint( canvas.getGraphics() );
								paintDelay = height;
								}

						 	}
						}
					// Now retrace that path, knowing the (non-)cycle length:
					px = x;
					py = y;
					setColorFor_n_steps(  n_steps );
					for( int n = 1;  n <= n_steps;  n++ ) {
						if( px >= min_show_x  &&  px <= max_show_x  &&  
							py >= min_show_y  &&  py <= max_show_y ) {
							if( step_counts[ px + offs_x ][ py + offs_y ] != 0 ) {
								break;  //  been there done...something...
							}
//							g.fillRect( px + offs_x, py + offs_y, 1, 1 );
							

							if(colorid==0)
					           primitives.add(new Ngon(4,1.0,0.0,new Point(px + offs_x, py + offs_y),colors[n_steps],0.0));
							else
							{
								float[] hsbValues = new float[3];
								hsbValues = Color.RGBtoHSB(colors[n_steps].getRed(),colors[n_steps].getGreen(),colors[n_steps].getBlue(),hsbValues);
								double col=(double)hsbValues[0];
						        primitives.add(new Ngon(4,1.0,0.0,new Point(px + offs_x, py + offs_y),col,0.0));
							}

							
							if( --paintDelay <= 0 ) {
//								canvas.paint( canvas.getGraphics() );
								paintDelay = height;
								}

							step_counts[ px + offs_x ][ py + offs_y ] = n_steps;
						}
						for( int i_denom = 0;  i_denom < denom;  i_denom++ ) {
							px += hskew1[ py + skew_maps_base ];
							if( px < skew_maps_min  || px > skew_maps_max )  px = adjust_skew_maps( px );
							py += vskew[ px + skew_maps_base ];
							if( py < skew_maps_min  || py > skew_maps_max )  py = adjust_skew_maps( py );
							px += hskew2[ py + skew_maps_base ];
							if( px < skew_maps_min  || px > skew_maps_max )  px = adjust_skew_maps( px );
						}
						if( px == x  &&  py == y ) {  // End of the cycle...
							break;
							}
						} // for n_steps retracing path
					} // if step_count was zero
				} // for y
			} // for x
//		canvas.paint( canvas.getGraphics() );
	    } // drawMap
	   
	}
