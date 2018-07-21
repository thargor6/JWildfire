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
 * RenderView.java
 *
 * A RenderView is a GeoView that delegates its drawing function to
 * a RenderStyle.  The RenderStyle is determined by the currently active
 * style in a RenderPanel.
 */

package csk.taprats.ui1;




import java.util.ArrayList;

import org.jwildfire.create.tina.variation.FlameTransformationContext;

import csk.taprats.app.DesignElement;
import csk.taprats.app.ExplicitFigure;
import csk.taprats.app.Figure;
import csk.taprats.app.Infer;
import csk.taprats.app.Prototype;
import csk.taprats.app.RadialFigure;
import csk.taprats.app.Rosette;
import csk.taprats.app.Star;
import csk.taprats.geometry.Map;

import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Primitive;
import csk.taprats.tile.Feature;
import csk.taprats.tile.PlacedFeature;
import csk.taprats.tile.Tiling;
import csk.taprats.toolkit.*;


public class RenderVecView
	extends GeoView
{

	private Map	map=null;;

    private Tiling tiling=null;
    
	public RenderStyle		current=null;

	
	
	public RenderVecView( String tilingName, int Style ,double width,double gap, boolean fill, double q,int s,int rosetteflag, double sd, int ss,int infer)
	{
	   super( -1.5, 1.5, 3.0 );  // left,top & width
	   tiling=Tiling.find( tilingName );

       setSink( true );

		
		if(Style==0)
		{
			RenderStyle plain = new RenderPlain();
			this.current = plain;	
		}
		else if(Style==1)
		{
			RenderStyle outline = new RenderOutline(width);
			this.current = outline;	

		}
		else if(Style==2)
		{
			RenderStyle interlace = new RenderInterlace(width,gap);
			this.current = interlace;	
		}
		else if(Style==3)
		{
			RenderStyle emboss = new RenderEmboss(width);
			this.current = emboss;	
		}
		else if(Style==4)
		{
			RenderStyle filled = new RenderFilled();
			this.current = filled;	
		}
		else if(Style==5)
		{
			RenderStyle sketch = new RenderSketch();
			this.current = sketch;	
		}

	       setSize( 200, 200 );
	       genera(fill,q,s,rosetteflag,sd,ss,infer);
	}

	
	public RenderStyle getRenderStyle()
	{
	   return current;	
	}
	
	public Map getMap()
	{
		return map;
	}
	
	
	public 	 ArrayList< Primitive > getPrimitives()
	{
		return current.primitives;
	}
	
	public Primitive getPrimitive(FlameTransformationContext pContext)
	{
		
		int idx=(int) (pContext.random()*(current.primitives.size()));
		
		Primitive primitive=current.primitives.get(idx);

		return primitive;
	}

	
// Working Infer
public void genera(boolean fill, double q, int s,int rosetteflag,double sd, int ss, int infer )
	{
		Map mapinfer=null;
	    Prototype proto=new Prototype(tiling);
        DesignElement de=null;
        Figure figure=null;
        
        // Crea un prototipo sin Dibujos sobre ExplicitFigure
    		for( int idx = 0; idx < tiling.numFeatures(); ++idx ) {
    				PlacedFeature pf = tiling.getFeature( idx );
    				Feature feature=pf.getFeature();
    				if( feature.isRegular() && (feature.numPoints() > 4) )
    				{
    					if(rosetteflag==1)
    					  figure=new Rosette(feature.numPoints(),q,s);	
    					else
    					  figure=new Star(feature.numPoints(),sd,ss);	
        				de=new DesignElement(feature,figure);		
    				}
    				else
        				de=new DesignElement(feature);		
    				proto.addElement( de );
    			}	
                if(infer==1)
                {
// Modifica el prototipo para dibujar Inferidos solamente en ExplicitFigures	
		          for( int idx = 0; idx < tiling.numFeatures(); ++idx ) {
			         PlacedFeature pf = tiling.getFeature( idx );
			 
			        if( proto.getFigure(pf.getFeature())  instanceof RadialFigure)
			        {
                       continue;
			        }
			        else  // infer solo a ExplicitFigure
				    {
				      mapinfer=new Infer(proto).infer(pf.getFeature());
		 		      figure=new ExplicitFigure(mapinfer);
				      de=new DesignElement(pf.getFeature(),figure);
					  proto.addElement( de );
				   }
		        }
}
		this.computeTransform();
		Polygon pgon = getBoundary();
		map = proto.construct( pgon );
		current.setMap( map );
		current.draw(fill);
	}
}
 

