package org.jwildfire.create.tina.variation;


import java.util.Random;
import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutTriScaleTruchetFunc  extends VariationFunc {

	/*
	 * Variation :cut_tstruchet
	 * Date: october 16, 2019
	 * Reference:  https://www.shadertoy.com/view/XttBW7
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_SHAPE = "shape";
	private static final String PARAM_3LEVEL = "levels";
	private static final String PARAM_ARCS = "arcs";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
	int seed=1000;
	int mode=1;

	// Various curve shapes, for anyone curious.
	// Circle: 0, Octagon: 1, Dodecahedron: 2, Hexadecagon: 3
	int shape=0;
	// Three levels or two. Comment it out for two.
	int levels=0;
	// Just the arcs.
	int arcs=0;
	
    double zoom=5.0;
    int invert=0;

	Random randomize=new Random(seed);
 	
    double x0=0.,y0=0.;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_SHAPE,PARAM_3LEVEL,PARAM_ARCS,PARAM_ZOOM,PARAM_INVERT};




	// Standard 2D rotation formula.
	public mat2 r2(double a)
	{ double c = Math.cos(a), s = Math.sin(a);
	  return new mat2(c, -s, s, c); 
	}


	// IQ's standard vec2 to double hash formula.
	public vec2 hash22(vec2 p){
	 
	    double n = G.dot(p, new vec2(57., 27.));
	    return G.fract(new vec2(262144., 32768.).multiply(Math.sin(n)));
	}


	// Cheap and nasty 2D smooth noise function with inbuilt hash function -- based on IQ's 
	// original. Very trimmed down. In fact, I probably went a little overboard. I think it 
	// might also degrade with large time values.
	public  double n2D(vec2 p) {

		vec2 i = G.floor(p);
		p = p.minus(i);
		p = (new vec2(3.).minus( p.multiply(2.))).multiply(p).multiply(p);  
	    
		return G.dot(new vec2(1. - p.y, p.y).times(new mat2(G.fract(G.sin(new vec4(0, 1, 113, 114).plus(G.dot(i, new vec2(1, 113))).multiply(43758.5453)))))
	                , new vec2(1. - p.x, p.x) );

	}

	// FBM -- 4 accumulated noise layers of modulated amplitudes and frequencies.
	public double fbm(vec2 p)
	{ 
		return n2D(p)*.533 + n2D(p.multiply(2.))*.267 + n2D(p.multiply(4.))*.133 + n2D(p.multiply(8.))*.067;
    }

	// Distance formula with various shape metrics.
	// See the "SHAPE" define above.
	public double dist(vec2 p){
	    
	    if (shape == 0)
	    // Standard circular shaped curves.
	      return G.length(p);
	    else
	        p = G.abs(p);
	   if (shape == 1)
	        	// Octagon.
	        	return Math.max(Math.max(p.x, p.y), (p.x + p.y)*.7071);
	   else if (shape == 2)
	        	// Dodecahedron.
	   {  vec2 p2 = p.multiply(.8660254).plus( new vec2(p.y,p.x).multiply(.5));
	      return Math.max(Math.max(p2.x, p2.y), Math.max(p.x, p.y));
	   }
	   else
	   {     	// Hexadecagon (regular, 16 sideds) -- There'd be a better formula for this.
	        	vec2 p2 = p.times(r2(3.14159/8.));
	        	double c = Math.max(Math.max(p2.x, p2.y), (p2.x + p2.y)*.7071);
	        	return Math.max(c, Math.max(Math.max(p.x, p.y), (p.x + p.y)*.7071));
	  }
	} 
	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y,px_center,py_center;
		    
		    if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		      px_center=0.0;
		      py_center=0.0;
		    }else
		    {
		     x=pContext.random();
		     y=pContext.random();
		      px_center=0.5;
		      py_center=0.5;		     
		    }
		    
		    
		    vec2 uv =new vec2(x,y);
		    
		    // Scaling and translation.
            vec2 oP=new vec2(x*zoom,y*zoom);
		    oP=oP.plus(new vec2(x0,y0));
	
		    // Distance file values.
		    vec4 d = new vec4(1e5);
		    
		    // Initial cell dimension.
		    double dim =  2.;
		    
		    // Random entries -- One for each layer. The X values represent the chance that
		    // a tile for that particular layer will be rendered. For instance, the large
		    // tile will have a 35% chance, the middle tiles, 70%, and the remaining smaller
		    // tiles will have a 100% chance. I.e., they'll fill in the rest of the squares.
		    
		    vec2[] rndTh =  { new vec2(.35, .5), new vec2(.7, .5), new vec2(1, .5)};
		    
		    // Set the second level random X value to "1." to ensure that the loop breaks on 
		    // the second iteration... which is a long way to say, "Two levels only." :)
		    if (levels==1)
		        rndTh[0].x = .5; rndTh[1].x = 1.;
		    
		    
		    
		    // Grid line width, and a global diagonal-side variable.
		    double lwg = .015;
		    double side = 1e5;
		    
		    // Random variable.
		    vec2 rnd = new vec2(0);
		    
		    
		    for(int k=0; k<3; k++){
		    	
		        // Base cell ID.
				vec2 ip = G.floor(oP.multiply(dim));
		        
		        // Unique random ID for the cell.
		        rnd = hash22(ip);

		        
				// If the random cell ID at this particular scale is below a certain threshold, 
		        // render the tile.         
		        if(rnd.x<rndTh[k].x){
		            
		            // Tile construction: By the way, the tile designs you use are limited by your imagination. 
		        	// I chose the ones that seemed most logical at the time -- Arcs and grid vertice circles.
		      
		            // Local cell coordinate.
		            vec2 p = oP.minus((ip.plus( .5)).division(dim)); // Equivalent to: mod(oP, 1./dim) - .5/dim;
		            
		            // Reusing "rnd" to calculate a new random number. Not absolutely necessary,
		            // but I wanted to mix things up a bit more.
		            rnd = G.fract(rnd.multiply(27.63).plus( (double)(k*57 + 1)));
		           
		            // Grid lines.
		 	        d.y = Math.abs(Math.max(Math.abs(p.x), Math.abs(p.y)) - .5/dim) - lwg/2.;

		            
		            // Use the unique random cell number to flip half the tiles vertically, which,
		            // in this case, has the same effect as rotating by 90 degrees.
		            p.y *= rnd.y<.5? 1. : -1.;
		           
		            
		            // Arc width: Arranged to be one third of the cell side length. This is half that
		            // length, but it gets doubled below.
		            double aw = .5/3./dim;

		            // Tile rendering: The arcs, circles, etc. I made the tiles up as I went along,
		            // but it's just a positioning of arcs and circles, so I'm hoping it's pretty 
		            // straight forward. 
		            double c1 = Math.abs(dist(p.minus( new vec2(.5).division(dim))) - .5/dim) - aw;
		            
		            // Arcs only, or a mixture of arcs and circles.
		            double c2;
		            if (arcs==1)
		               c2 = G.abs(dist(p.minus(new vec2(-.5).division(dim))) - .5/dim) - aw;
		            else
		            { 
		              if(G.fract(rnd.y*57.53 +.47)<.35) {
		                c2 = dist(p.minus(new vec2(-.5, 0).division(dim)))- aw;
		                c2 = G.min(c2, dist(p.minus(new vec2(0, -.5).division(dim)))-aw);
		               }
		               else
		            	c2 = G.abs(dist(p.minus( new vec2(-.5).division(dim))) - .5/dim) - aw;
		            }
		            // Combining the arc and\or circle elements.
		            d.x = Math.min(c1, c2);
		            
		            // Determining which side of the diagonal the blue neon tri-level lines are on.
		            // That way, you can blink them individually.
		            side = c1>c2? 0. : 1.57*(rnd.y*.5 + 1.);
		            
		            
		            // Negate the arc distance field values on the second tile.
		            d.x *= k==1? -1. : 1.;
		            
		             
		            // Four mid border circles. There's some 90 degree rotation and repeat
		            // trickery hidden in amongst this. If you're not familiar with it, it's
		            // not that hard, and gets easier with practice.
		            vec2 p2 = G.abs(new vec2(p.y - p.x, p.x + p.y).multiply(.7071)).minus(.5*.7071/dim);
		            p2 = new vec2(p2.y - p2.x, p2.x + p2.y).multiply(.7071);
		            double c3 = dist(p2) - aw/2.; 
		             
		            
		            
		            // Placing circles at the four corner grid vertices. If you're only rendering
		            // one level (rndTh[0].x=1.), you won't need them... unless you like them, I guess. :)
		            p = G.abs(p).minus(.5/dim);
		            if(k<2 && rndTh[0].x<.99) d.x = Math.min(d.x, (dist(p) - aw));
		            
		            // Depending upon which tile scale we're rendering at, draw some circles,
		            // or cut some holes. If you look at the individual tiles in the example,
		            // you can see why.
		            if(rndTh[1].x<.99){
		                
		                // Cut out some mid border holes on the first iteration. 
		                if(k==0) d.x = Math.max(d.x, -c3); 
		                
		                // On the middle iteration, cut out vertice corner holes.
		                // On the other iterations, add smaller vertice holes.
		                // I made this up as I went along, so there's probably a
		                // more elegant way to go about it.
		                if(k==1) 
		                	d.x = Math.max(d.x, -(dist(p) - aw));
		            	else 
		            		d.x = Math.max(d.x, -(dist(p) - aw/2.));
		            }
		            
		            
		            // Increasing the overall width of the pattern slightly.
		            d.x -= .01;

		            // Since we don't need to worry about neighbors
		            break;
		        }
		        
		        // Subdividing. I.e., decrease the cell size by doubling the frequency.
		        dim *= 2.;
		        
		    }
		    
		    // RENDERING
		    
		    // Background.
		    vec3 bg = new vec3(0.);//*vec3(1, .9, .95);
		    //float pat =  clamp(sin((oP.x - oP.y)*6.283*iResolution.y/22.5) + .75, 0., 1.);
		    //bg *= (pat*.35 + .65);
		    double ns = fbm(oP.multiply(32.)); // Noise.
		   // bg *= ns*.5 + .5; // Apply some noise to the background.
		    
		    // Scene color. Initiated to the background.
		    vec3 col = bg;

		    // Falloff variable.
		    double fo;
		  
		    // Render the grid lines.
		    fo = 4./1e3;
		    
		 //   #ifdef SHOW_GRID
		 //   col = mix(col, vec3(0), (1. - smoothstep(0., fo*5., d.y - .01))*.5); // Shadow.
		 //   col = mix(col, vec3(1), (1. - smoothstep(0., fo, d.y))*.15); // Overlay.
		 //   #endif


		    // Pattern falloff, overlay color, shade and electronic looking overlay.
		    fo = 10./1e3/Math.sqrt(dim);
		    // Distance field color: I couldn't seem to make vibrant color work, so fell
		    // back go greyscale with a dash of color. It's a cliche, but it often works. :)
		    //vec3 pCol = vec3(.3, .25, .275);
		    vec3 pCol = new vec3(1., 1., 1.);
		    double sh = Math.max(.75 - d.x*10., 0.); // Distance field-based shading.
		    sh *= G.clamp(-Math.sin(d.x*6.283*18.) + .75, -.25, 1.) + .25; // Overlay pattern.


		    // Drop shadow, edges and overlay.
		    col = G.mix(col, new vec3(0), (1. - G.smoothstep(0., fo*5., d.x))*.75);
		    col = G.mix(col, new vec3(0), 1. - G.smoothstep(0., fo, d.x));
		    col = G.mix(col, pCol.multiply(sh), 1. - G.smoothstep(0., fo, d.x + .015));

		    // Darkening the rounded quads around the lit centers.
		    col = G.mix(col, bg.multiply(sh), 1. - G.smoothstep(0., fo, Math.max(d.x + .1, -(d.x + .14))));
		   
//		    if (TRI_LEVEL==1)
//		    {
//		        // Apply some blue blinking neon to the tri level pattern.
//		    	vec3 neon = mix(col, col*vec3(1.5, .1, .3).yxz*2., 1. - smoothstep(.7, .9, sin(rnd.y*6.283 + iTime*4. + side)));
//		    	col = G.mix(col, col.multiply(neon), 1. - G.smoothstep(0., fo, d.x + .16));
//		    }
//		    else
//		    {
//		        // Apply some animated noisy reddish neon to the dual level pattern. 
//		    	vec3 neon = mix(bg, col*vec3(1.5, .1, .3)*2., smoothstep(-.5, .5, n2D(oP*3. + vec2(iTime*2.))*2. - 1.));
//		    	col = G.mix(col, neon, 1. - G.smoothstep(0., fo, d.x + .16));// + .125
//		    }
		            
		   
		      
		    // Add some subtle noise.        
		    // col  =col.multiply(ns*.25 + .75);
		    
		    
		    // A bit of gradential color mixing.
		   // col = G.mix(new vec3(col.x,col.z,col.y), col, G.sign(uv.y)*uv.y*uv.y*2. + .5);
		   // col = G.mix(new vec3(col.x,col.z,col.y), col, (-uv.y*.66 - uv.x*.33) + .5);
		    
		    // Mild spotlight.
		  //  col =col.multiply( Math.max(1.25 - G.length(uv)*.25, 0.));
		    
            double color=col.x;
            
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color>0.30)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (color<=0.30)
			      { x=0;
			        y=0;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * (x-px_center);
		    pVarTP.y = pAmount * (y-py_center);
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
		  x0=seed*randomize.nextDouble();
		  y0=seed*randomize.nextDouble();
	   }

	  
	public String getName() {
		return "cut_tstruchet";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, mode,shape,levels,arcs,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		 if(pName.equalsIgnoreCase(PARAM_SEED))
			{
				   seed =   (int)pValue;
			       randomize=new Random(seed);
			}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_SHAPE)) {
			shape =(int)Tools.limitValue(pValue, 0 , 3);
		}
		else if (pName.equalsIgnoreCase(PARAM_3LEVEL)) {
			levels =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ARCS)) {
			arcs =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			invert =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else
		      throw new IllegalArgumentException(pName);
	}
	
	@Override
	public boolean dynamicParameterExpansion() {
		return true;
	}

	@Override
	public boolean dynamicParameterExpansion(String pName) {
		// preset_id doesn't really expand parameters, but it changes them; this will make them refresh
		return true;
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION};
	}

}

