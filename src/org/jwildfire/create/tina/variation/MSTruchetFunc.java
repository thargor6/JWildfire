/*
JWildfire - an image and animation processor written in Java 
Copyright (C) 1995-2012 Andreas Maschke

This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
General Public License as published by the Free Software Foundation; either version 2.1 of the 
License, or (at your option) any later version.

This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this software; 
if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jwildfire.create.tina.variation;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.MSTruchetFunc.Point;
import org.jwildfire.create.tina.variation.MSTruchetFunc.QuadTree;
import org.jwildfire.create.tina.variation.MSTruchetFunc.Rectangle;
import org.jwildfire.image.SimpleImage;

public class MSTruchetFunc extends AbstractBufferedImageWFFunc {
	private static final long serialVersionUID = 1L;


    public static final String PARAM_SEED = "seed";
    public static final String PARAM_COMPLEXITY = "complexity";

	
    private  final String[] additionalParamNames = {PARAM_SEED, PARAM_COMPLEXITY};


    private int complexity = 25;


    private int seed = 0;
	private Random rnd;
	
	protected Graphics2D g2d;
	protected BufferedImage bimage;
	protected SimpleImage image;
	

	static public class Point
	{
		double x;
		double y;
		
		public Point(double x,double y)
		{
			this.x=x;
			this.y=y;
		}
	}
	
	static public class Rectangle
	{
		double x;
		double y;
		double w;
		double h;
		public Rectangle(double x,double y,double w,double h)
		{
			this.x=x;
			this.y=y;
			this.w=w;
			this.h=h;
		}

		public boolean contains (Point point)
		{
			return (point.x >= (this.x - this.w) &&
				    point.x  < (this.x + this.w) &&
				    point.y >= (this.y - this.h) &&
				    point.y  < (this.y + this.h) );
		}
		
		public boolean intersect(Rectangle range)
		{
		    return !(range.x - range.w > this.x + this.w ||
		    	      range.x + range.w < this.x - this.w ||
		    	      range.y - range.h > this.y + this.h ||
		    	      range.y + range.h < this.y - this.h);
		}
	}
	
	static public class wingtile
	{
	  	String motif;
	  	Rectangle boundary;
        int phase;
        Color[] color;
	  	
	  	public wingtile(String motif,int phase, Rectangle boundary, Color[] color)
	  	{
	  		this.motif=new String(motif);
	  		this.boundary=boundary;
	  		this.phase=phase;
	  		this.color=color;
	  	    if (this.phase==1) 
	  	    {
	  	      Color tempcol;
	  	      tempcol = this.color[1];
	  	      this.color[1] = this.color[0];
	  	      this.color[0] = tempcol;
	  	    }
	  	}
	  	
	  	public void rectangle(Graphics2D g2d,double x,double y, double w, double h)
	  	{
			  g2d.fill( new Rectangle2D.Double(x-w, y-h,2* w, 2*h));
	  	}
	  	
	  	public void circle(Graphics2D g2d,double x,double y, double r)
	  	{
			  g2d.fill( new Ellipse2D.Double(x-r, y-r, 2*r, 2*r));
	  	}
	  	
	  	
	  	public void arc(Graphics2D g2d,double x,double y,double r, double a1,double a2)
	  	{
	  		
	  	    Arc2D.Float arc = new Arc2D.Float(Arc2D.PIE);
	  	    arc.setFrame(x-r/2, y-r/2, r, r);
	  	    arc.setAngleStart(a1);
	  	    arc.setAngleExtent(a2);
			g2d.fill( arc);
	  	}
	  	
	  	public void drawtile(Graphics2D g2d)
	  	{
	  		double x=this.boundary.x;
	  		double y=this.boundary.y;
	  		double w=this.boundary.w;
	  		double h=this.boundary.h;
	  		double smallr=2.*w/6.;
	  		double bigr=2.*w/3.;
	  		double arcd=2*2*2.*w/3.;

			  g2d.setColor( color[1]);  // NOTE -- so we can *see* something.
			  rectangle(g2d,x,y,w,h);
	  	    double px;
	  	    double py;    
		    g2d.setColor( color[0]);

	  	    switch (this.motif) {

	        case "\\":
	        	px=x+w;
	        	py=y-h;
	        	arc(g2d,px,py,arcd,180.,90.);
	        	px=x-w;
	        	py=y+h;
	        	arc(g2d,px,py,arcd,0.,90.);
	          break;
	        case "/":
	        	px=x-w;
	        	py=y-h;
	        	arc(g2d,px,py,arcd,270.,90.);
	          px=x+w;
	          py=y+h;
	        	arc(g2d,px,py,arcd,90.,90.);
	          break;
	        case "-":
		  	    rectangle(g2d,x,y,w,smallr);
	          break;
	        case "|":		
			  	    rectangle(g2d,x,y,smallr,h);
	          break;
	        case "+.":
	          break;
	        case "x.":
				g2d.setColor( color[0]);
		  	    rectangle(g2d,x,y,w,h);
	          break;
	        case "+":
		  	    rectangle(g2d,x,y,w,smallr);
		  	    rectangle(g2d,x,y,smallr,h);
	          break;
	        case "fne":
	        	px=x+w;
	        	py=y-h;          	
	        	arc(g2d,px,py,arcd,180.,90.);
	          break;
	        case "fsw":
	        	px=x-w;
	        	py=y+h;
	        	arc(g2d,px,py,arcd,0.,90.);
	          break;
	        case "fnw":
	        	px=x-w;
	        	py=y-h;
	        	arc(g2d,px,py,arcd,270.,90.);
	          break;
	        case "fse":
	        	px=x+w;
	        	py=y+h;
	        	arc(g2d,px,py,arcd,90.,90.);
	          break;
	        case "tn":
	        	px=x;
	        	py=y-smallr;
				  g2d.setColor( color[0]);
		  	    rectangle(g2d,px,py,w,bigr);
	          break;
	        case "ts":
	        	px=x;
	        	py=y+smallr;
				  g2d.setColor( color[0]);
		  	    rectangle(g2d,px,py,w,bigr);
	          break;
	        case "te":
	        	px=x+smallr;
	        	py=y;
			 g2d.setColor( color[0]);
			   rectangle(g2d,px,py,bigr,h);
	          break;
	        case "tw":
              px=x-smallr;
              py=y;
			  g2d.setColor( color[0]);
		  	    rectangle(g2d,px,py,bigr,h);
	          break;
	        default:

	      }
			g2d.setColor( color[1]);
	  	    px=x-w;
	  	    py=y-h;	  
	  	    circle(g2d,px,py,bigr);
	  	    px=x+w;
	  	    py=y-h;
			circle(g2d,px,py,bigr);
	  	    px=x-w;
	  	    py=y+h;
	  	    circle(g2d,px,py,bigr);
	  	    px=x+w;
	  	    py=y+h;
		  	circle(g2d,px,py,bigr);

		    g2d.setColor( color[0]);
	  	    px=x;
	  	    py=y-h;
		  	circle(g2d,px,py,smallr);
	  	    px=x+w;
	  	    py=y;
		  	circle(g2d,px,py,smallr);
	  	    px=x;
	  	    py=y+h;
		  	circle(g2d,px,py,smallr);
	  	    px=x-w;
	  	    py=y; 
		  	circle(g2d,px,py,smallr);
	  	}
	  	
	}
	
	static public class QuadTree
	{
		int tier;
		int phase;
		wingtile tile;
		boolean divided;	
		Random rnd;
		
		QuadTree[] divisions=new QuadTree[4];
		Rectangle boundary;
		String[] motiflist=new String[] {"/","\\", "-", "|","+.","x.",  "+", "fne","fsw","fnw","fse","tn","ts","te","tw" };
		Color[] color=new Color[2];
		String motif;
		
		public QuadTree(Random rnd,Rectangle boundary, int tier)
		{
			this.boundary=boundary;
			this.tier=tier;
			this.rnd=rnd;
			divided=false;
			
		    this.phase = this.tier % 2;
		    color[0]=new Color(255,255,255);
		    color[1]=new Color(0,0,0);
			
		    int motifindex = (int)(15.*rnd.nextDouble());
		    this.motif = new String(this.motiflist[motifindex]);
            this.tile=new wingtile(this.motif,this.phase,this.boundary,color);
		}
		
		  public void divide() {
			    int subtier = this.tier + 1;
			    double x = this.boundary.x;
			    double y = this.boundary.y;
			    double w = this.boundary.w;
			    double h = this.boundary.h;

			    Rectangle ne = new Rectangle(x + w / 2, y - h / 2, w / 2, h / 2);
			    this.divisions[0] = new QuadTree(rnd,ne, subtier);
			    Rectangle nw = new Rectangle(x - w / 2, y - h / 2, w / 2, h / 2);
			    this.divisions[1] = new QuadTree(rnd,nw, subtier);
			    Rectangle se = new Rectangle(x + w / 2, y + h / 2, w / 2, h / 2);
			    this.divisions[2] = new QuadTree(rnd,se, subtier);
			    Rectangle sw = new Rectangle(x - w / 2, y + h / 2, w / 2, h / 2);
			    this.divisions[3] = new QuadTree(rnd,sw, subtier);
			    this.divided = true;

			  }
		  
		  public boolean split(Point point) {

			    if (!this.boundary.contains(point)) {
			      return false;
			    }

			    if (!this.divided) {
			      this.divide();
			    } else {
			      for (int i = 0; i < 4; i++) {
			        if (this.divisions[i].split(point)) {
			          return true;
			        }
			      }
			    }
			    return false;
			  }
		  
		  
		  public void drawtiles(Graphics2D g2d) { //this needs to be a breadth first search{

			    Queue<wingtile> drawqueue = new LinkedList();
			    Queue<QuadTree> traverse = new LinkedList();
			    traverse.add(this);
			    drawqueue.add(this.tile);

			    QuadTree node;

			    while (!traverse.isEmpty()) {
			      node = traverse.remove();
			      if (node.divided) {
			        for (int i = 0; i < 4; i++) {
			          traverse.add(node.divisions[i]);
			        }
			      } else {
			        //this is a getaround, sloppy code
			        node.tile.motif = new String(node.motif);
			        drawqueue.add(node.tile);
			      }
			    }

			    while (!drawqueue.isEmpty()) {
			      wingtile tile = drawqueue.remove();
			      tile.drawtile(g2d);
			    }
			    
			  }
	}

	public Graphics2D BuffImage(int width,int height)
	{
		this.bimage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);

		this.g2d = bimage.createGraphics();
		this.g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		return g2d;
	}
	
	public Point randPoint(Random rnd, int canvasX,int canvasS)
	{
		double x= canvasX +canvasS*rnd.nextDouble();
		double y= canvasX +canvasS*rnd.nextDouble();
		return new Point(x,y);
	}
	
	@Override
	public String getName() {
		return "msTruchet";
	}

    public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	    }
	    
	    @Override
	    public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	    	int canvasSize = 2048/ 2;
	    	int canvasW = canvasSize / 2;
	    	int canvasH = canvasSize / 2;

	    	canvasSize *= 5. / 3.;

	    	int canvasX = canvasSize / 2;
	    	int canvasY = canvasSize / 2;

	    	Rectangle bounds = new Rectangle(canvasX,canvasY, canvasW, canvasH);

	    	rnd=new Random(seed);
	    	QuadTree qtree = new QuadTree(rnd, bounds, 0);

	    	Point point = randPoint(rnd,0, 2*canvasW);
	    	qtree.split(point);

	    	for(int i=0;i<complexity;i++)
	    	{ 
	    		point = randPoint(rnd,0,2*canvasW);
	    		qtree.split(point);
	    	}

	    	colorMap = null;
	    	colorIdxMap.clear();

	    	Graphics2D g2d=BuffImage(canvasSize, canvasSize);
	    	qtree.drawtiles(g2d);
	    	g2d.dispose();

	    	image= new SimpleImage(bimage,canvasSize,canvasSize);
	    	colorMap =  image;
	    	renderColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
	    	imgWidth = colorMap.getImageWidth();
	    	imgHeight = colorMap.getImageHeight();
	    }

    @Override
    public String[] getParameterNames() {
        return joinArrays(additionalParamNames, paramNames);
    }

    @Override
    public Object[] getParameterValues() {
        return joinArrays(new Object[] { seed,complexity},super.getParameterValues());
    }

    @Override
    public void setParameter(String pName, double pValue) {
    	if (PARAM_SEED.equalsIgnoreCase(pName))
            seed = (int) pValue;
        else if (PARAM_COMPLEXITY.equalsIgnoreCase(pName))
            complexity = (int) Tools.limitValue(pValue, 1, 100);
		else
			super.setParameter(pName, pValue);
    }

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC};
	}

}
