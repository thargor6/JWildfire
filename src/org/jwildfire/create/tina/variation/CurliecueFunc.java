package org.jwildfire.create.tina.variation;


import csk.taprats.geometry.Line;
import csk.taprats.geometry.Ngon;
import csk.taprats.geometry.Point;
import csk.taprats.geometry.Primitive;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.plot.DrawFunc;

import java.util.Random;

public class CurliecueFunc extends DrawFunc {

  /*
   * Variation : curliecue
   * Autor: Jesus Sosa
   * Date: August 10, 2018
   * Reference https://oolong.co.uk/curlicue.htm
   */

  private static final long serialVersionUID = 1L;


  private static final String PARAM_SIZE = "Points";
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_TYPE = "type";
  private static final String PARAM_SIDES = "sides";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_SYMMETRY = "symmetry";
  private static final String PARAM_ECCE = "eccentricity";
  private static final String PARAM_SHOWLINES = "showlines";


  private static final String[] paramNames = {PARAM_SIZE, PARAM_SEED, PARAM_TYPE, PARAM_SIDES, PARAM_SCALE, PARAM_SYMMETRY, PARAM_ECCE, PARAM_SHOWLINES};


  double size = 500;
  double s = 0.02;
  int type = 0;
  int sides = 4;
  double scale = 0.02;
  int symmetry = 1;
  double m_ecce = 0.0;
  int showlines = 1;
  int seed = 1000;

  Random randomize = new Random((long) seed);

  static double sineArray[] = new double[257];

  static int zoom = 1;

  public static void init() {

    for (int i = 0; i < sineArray.length; i++) {
      sineArray[i] = Math.sin(i * Math.PI / 128);
    }
  }


  public static double sine(double angle) {
    int idx;
    idx = ((int) (256 + angle * 128 / Math.PI)) % 256;
    return sineArray[idx];
  }

  public static double cosine(double angle) {
    int idx;
    idx = ((int) (192 + angle * 128 / Math.PI)) % 256;
    return sineArray[idx];
  }

  public void build_pattern() {
    double theta, phi, x0, y0, x1, y1;
    theta = 0;
    phi = 0;
    x0 = m_ecce;
    y0 = m_ecce;

    s = randomize.nextDouble();
    for (int i = 0; i < size; i++) {
      x1 = x0 + 0.01 * Math.cos(phi);
      y1 = y0 + 0.01 * Math.sin(phi);
      if (type == 0)  // Draw circle Blur
      {
        if (symmetry == 1) {
          primitives.add(new Point((float) x0, (float) -y0, (double) i / size));
          primitives.add(new Point((float) x1, (float) -y1, (double) i / size));
          if (showlines == 1)
            primitives.add(new Line((float) x0, (float) -y0, (float) x1, (float) -y1, (double) i / size));
        }
        if (symmetry == 2) {
          primitives.add(new Point(x0, -y0, (double) i / size));
          primitives.add(new Point(x1, -y1, (double) i / size));


          primitives.add(new Point(-x0, y0, (double) i / size));
          primitives.add(new Point(-x1, y1, (double) i / size));


          primitives.add(new Point(y0, x0, (double) i / size));
          primitives.add(new Point(y1, x1, (double) i / size));


          primitives.add(new Point(-y0, -x0, (double) i / size));
          primitives.add(new Point(-y1, -x1, (double) i / size));

          if (showlines == 1) {
            primitives.add(new Line((float) -y0, (float) -x0, (float) -y1, (float) -x1, (double) i / size));
            primitives.add(new Line((float) y0, (float) x0, (float) y1, (float) x1, (double) i / size));
            primitives.add(new Line((float) -x0, (float) y0, (float) -x1, (float) y1, (double) i / size));
            primitives.add(new Line((float) x0, (float) -y0, (float) x1, (float) -y1, (double) i / size));
          }
        }
        if (symmetry == 3) {
          primitives.add(new Point(x0, -y0, (double) i / size));
          primitives.add(new Point(x1, -y1, (double) i / size));


          primitives.add(new Point(-x0, y0, (double) i / size));
          primitives.add(new Point(-x1, y1, (double) i / size));


          primitives.add(new Point(-x0, -y0, (double) i / size));
          primitives.add(new Point(-x1, -y1, (double) i / size));


          primitives.add(new Point(x0, y0, (double) i / size));
          primitives.add(new Point(x1, y1, (double) i / size));
          if (showlines == 1) {
            primitives.add(new Line((float) x0, (float) y0, (float) x1, (float) y1, (double) i / size));
            primitives.add(new Line((float) -x0, (float) -y0, (float) -x1, (float) -y1, (double) i / size));
            primitives.add(new Line((float) -x0, (float) y0, (float) -x1, (float) y1, (double) i / size));
            primitives.add(new Line((float) x0, (float) -y0, (float) x1, (float) -y1, (double) i / size));
          }
        }
        if (symmetry == 4) {
          primitives.add(new Point(x0, -y0, (double) i / size));
          primitives.add(new Point(x1, -y1, (double) i / size));


          primitives.add(new Point(-x0, y0, (double) i / size));
          primitives.add(new Point(-x1, y1, (double) i / size));


          primitives.add(new Point(y0, x0, (double) i / size));
          primitives.add(new Point(y1, x1, (double) i / size));


          primitives.add(new Point(-y0, -x0, (double) i / size));
          primitives.add(new Point(-y1, -x1, (double) i / size));


          primitives.add(new Point(x0, y0, (double) i / size));
          primitives.add(new Point(x1, y1, (double) i / size));


          primitives.add(new Point(-x0, -y0, (double) i / size));
          primitives.add(new Point(-x1, -y1, (double) i / size));


          primitives.add(new Point(y0, -x0, (double) i / size));
          primitives.add(new Point(y1, -x1, (double) i / size));


          primitives.add(new Point(-y0, x0, (double) i / size));
          primitives.add(new Point(-y1, x1, (double) i / size));
          if (showlines == 1) {
            primitives.add(new Line((float) -y0, (float) x0, (float) -y1, (float) x1, (double) i / size));
            primitives.add(new Line((float) y0, (float) -x0, (float) y1, (float) -x1, (double) i / size));
            primitives.add(new Line((float) -x0, (float) -y0, (float) -x1, (float) -y1, (double) i / size));
            primitives.add(new Line((float) x0, (float) y0, (float) x1, (float) y1, (double) i / size));
            primitives.add(new Line((float) -y0, (float) -x0, (float) -y1, (float) -x1, (double) i / size));
            primitives.add(new Line((float) y0, (float) x0, (float) y1, (float) x1, (double) i / size));
            primitives.add(new Line((float) -x0, (float) y0, (float) -x1, (float) y1, (double) i / size));
            primitives.add(new Line((float) x0, (float) -y0, (float) x1, (float) -y1, (double) i / size));
          }
        }
      } else if (type == 1)  // Draw Polygons
      {
        if (symmetry == 1) {
          primitives.add(new Ngon(sides, scale, 0.0, new Point(x0, -y0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(x1, -y1), (double) i / size, 0.0));
          if (showlines == 1)
            primitives.add(new Line((float) x0, (float) -y0, (float) x1, (float) -y1, (double) i / size));
        }
        if (symmetry == 2) {


          primitives.add(new Ngon(sides, scale, 0.0, new Point(x0, -y0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(x1, -y1), (double) i / size, 0.0));

          primitives.add(new Ngon(sides, scale, 0.0, new Point(-x0, y0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(-x1, y1), (double) i / size, 0.0));

          primitives.add(new Ngon(sides, scale, 0.0, new Point(y0, x0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(y1, x1), (double) i / size, 0.0));

          primitives.add(new Ngon(sides, scale, 0.0, new Point(-y0, -x0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(-y1, -x1), (double) i / size, 0.0));

          if (showlines == 1) {
            primitives.add(new Line((float) x0, (float) -y0, (float) x1, (float) -y1, (double) i / size));
            primitives.add(new Line((float) -x0, (float) y0, (float) -x1, (float) y1, (double) i / size));
            primitives.add(new Line((float) y0, (float) x0, (float) y1, (float) x1, (double) i / size));
            primitives.add(new Line((float) -y0, (float) -x0, (float) -y1, (float) -x1, (double) i / size));

          }
        }
        if (symmetry == 3) {

          primitives.add(new Ngon(sides, scale, 0.0, new Point(x0, -y0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(x1, -y1), (double) i / size, 0.0));

          primitives.add(new Ngon(sides, scale, 0.0, new Point(-x0, y0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(-x1, y1), (double) i / size, 0.0));

          primitives.add(new Ngon(sides, scale, 0.0, new Point(-x0, -y0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(-x1, -y1), (double) i / size, 0.0));

          primitives.add(new Ngon(sides, scale, 0.0, new Point(x0, y0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(x1, y1), (double) i / size, 0.0));
          if (showlines == 1) {
            primitives.add(new Line((float) x0, (float) -y0, (float) x1, (float) -y1, (double) i / size));
            primitives.add(new Line((float) -x0, (float) y0, (float) -x1, (float) y1, (double) i / size));
            primitives.add(new Line((float) -x0, (float) -y0, (float) -x1, (float) -y1, (double) i / size));
            primitives.add(new Line((float) x0, (float) y0, (float) x1, (float) y1, (double) i / size));
          }
        }
        if (symmetry == 4) {

          primitives.add(new Ngon(sides, scale, 0.0, new Point(x0, -y0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(x1, -y1), (double) i / size, 0.0));


          primitives.add(new Ngon(sides, scale, 0.0, new Point(-x0, y0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(-x1, y1), (double) i / size, 0.0));


          primitives.add(new Ngon(sides, scale, 0.0, new Point(y0, x0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(y1, x1), (double) i / size, 0.0));


          primitives.add(new Ngon(sides, scale, 0.0, new Point(-y0, -x0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(-y1, -x1), (double) i / size, 0.0));


          primitives.add(new Ngon(sides, scale, 0.0, new Point(x0, y0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(x1, y1), (double) i / size, 0.0));


          primitives.add(new Ngon(sides, scale, 0.0, new Point(-x0, -y0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(-x1, -y1), (double) i / size, 0.0));


          primitives.add(new Ngon(sides, scale, 0.0, new Point(y0, -x0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(y1, -x1), (double) i / size, 0.0));


          primitives.add(new Ngon(sides, scale, 0.0, new Point(-y0, x0), (double) i / size, 0.0));
          primitives.add(new Ngon(sides, scale, 0.0, new Point(-y1, x1), (double) i / size, 0.0));
          if (showlines == 1) {
            primitives.add(new Line((float) -y0, (float) x0, (float) -y1, (float) x1, (double) i / size));
            primitives.add(new Line((float) y0, (float) -x0, (float) y1, (float) -x1, (double) i / size));
            primitives.add(new Line((float) -x0, (float) -y0, (float) -x1, (float) -y1, (double) i / size));
            primitives.add(new Line((float) x0, (float) y0, (float) x1, (float) y1, (double) i / size));
            primitives.add(new Line((float) -y0, (float) -x0, (float) -y1, (float) -x1, (double) i / size));
            primitives.add(new Line((float) y0, (float) x0, (float) y1, (float) x1, (double) i / size));
            primitives.add(new Line((float) -x0, (float) y0, (float) -x1, (float) y1, (double) i / size));
            primitives.add(new Line((float) x0, (float) -y0, (float) x1, (float) -y1, (double) i / size));
          }
        }
      }

      x0 = x1;
      y0 = y1;

      phi = (theta + phi) % (2 * Math.PI);
      theta = (theta + 2 * Math.PI * s) % (2 * Math.PI);
    }
  }

/*
public  void draw (int x,int y,int ox,int oy,int type, double color) {
	double x1=(double)x/leng;
	double y1=(double)y/leng;
	double x2=(double)ox/leng;
	double y2=(double)oy/leng;
	
    if (type==0) 
    {
    //	System.out.println("line : " + x1 +" , " + y1 + " - " + x2 + " , " + y2 + " color: "+ color);
        primitives.add(new Line((float)x1, (float)y1,(float)x2, (float)y2,color));
    }
    else
    {
      //  System.out.println("Square : " + x1 +" , " + y1 + " - " + drawer + " , " + type + " color: "+ color);
	     primitives.add(new Ngon(sides,scale,0.0,new Point(x1,y1),color,0.0));
    }
}


public void build_pattern1()
{
	 int count=0, number=0, xcentre=0,ycentre=0, direction=1;	
	 
	double f,realx,realy,d,seed=0.0001;
   double color;
   int  x,y,ox,oy;


   init();  
   seed=seed+0.00000004*rate*direction;
   ox=ecce;
   oy=ecce;
   x=ox;
   y=oy;
   realx=ecce;
   realy=ecce;
   f=0;
   d=0;    
       while (count < leng){
           count+=1;
           f+=d;
           d+=seed;
           if (f>Math.PI*2) f-=Math.PI*2;
           if (d>Math.PI*2) d-=Math.PI*2;
           if (f<0) f+=Math.PI*2;
           if (d<0) d+=Math.PI*2;
           realx+=zoom*(cosine(f));
           realy+=zoom*(sine(f));
           if (Math.abs(f)<0.0001 && Math.abs(d)<0.0001 ) {

           }
           x=(int)realx;
           y=(int)realy;
          color=((count+number)%255)/255.0;

           if ((count+number)%leng < prop){                   
                   if (symmetry==1){
                       draw(xcentre+x,ycentre+y,xcentre+ox,ycentre+oy,type,color);
                       draw(xcentre-x,ycentre-y,xcentre-ox,ycentre-oy,type,color);
                   }
                   if (symmetry==2){
                       draw(xcentre+x,ycentre+y,xcentre+ox,ycentre+oy,type,color);
                       draw(xcentre-x,ycentre-y,xcentre-ox,ycentre-oy,type,color);
                       draw(xcentre-y,ycentre+x,xcentre-oy,ycentre+ox,type,color);
                       draw(xcentre+y,ycentre-x,xcentre+oy,ycentre-ox,type,color);
                   }
                   if (symmetry==3){
                       draw(xcentre+x,ycentre+y,xcentre+ox,ycentre+oy,type,color);
                       draw(xcentre-x,ycentre-y,xcentre-ox,ycentre-oy,type,color);
                       draw(xcentre-x,ycentre+y,xcentre-ox,ycentre+oy,type,color);
                       draw(xcentre+x,ycentre-y,xcentre+ox,ycentre-oy,type,color);                            
                   }
                   if (symmetry==4){
                       draw(xcentre+x,ycentre+y,xcentre+ox,ycentre+oy,type,color);
                       draw(xcentre-x,ycentre-y,xcentre-ox,ycentre-oy,type,color);
                       draw(xcentre-y,ycentre+x,xcentre-oy,ycentre+ox,type,color);
                       draw(xcentre+y,ycentre-x,xcentre+oy,ycentre-ox,type,color);
                       
                       draw(xcentre+x,ycentre-y,xcentre+ox,ycentre-oy,type,color);
                       draw(xcentre-x,ycentre+y,xcentre-ox,ycentre+oy,type,color);
                       draw(xcentre-y,ycentre-x,xcentre-oy,ycentre-ox,type,color);
                       draw(xcentre+y,ycentre+x,xcentre+oy,ycentre+ox,type,color);                        
                   }                    
           }            
           ox=x;
           oy=y;

       }	
}
			 

*/


  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    randomize = new Random((long) seed);
    build_pattern();
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    Point out = null;

    Primitive primitive = null;
    double color = 0.0;

    primitive = getPrimitive(pContext);

    if (primitive.gettype() == 1) {
      Point point = (Point) primitive;
      out = plotBlur(pContext, point.getX(), point.getY(), scale);
      color = point.getColor();

      pVarTP.x += pAmount * out.getX();
      pVarTP.y += pAmount * out.getY();
      pVarTP.color = color;
      if (pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAmount * pAffineTP.z;
      }
    }
    if (primitive.gettype() == 2) {
      Line line = (Line) primitive;
      out = plotLine(pContext, line.getX1(), line.getY1(), line.getX2(), line.getY2());
      color = line.getColor();

      pVarTP.x += pAmount * out.getX();
      pVarTP.y += pAmount * out.getY();
      pVarTP.color = color;
      if (pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAmount * pAffineTP.z;
      }
    }
    if (primitive.gettype() == 4) {
      Ngon polygon = (Ngon) primitive;
      out = plotPolygon(pContext, polygon);
      color = polygon.getColor();

      pVarTP.x += pAmount * (out.getX() * polygon.getScale() * polygon.getCosa() + out.getY() * polygon.getScale() * polygon.getSina() + polygon.getPos().getX());
      pVarTP.y += pAmount * (-out.getX() * polygon.getScale() * polygon.getSina() + out.getY() * polygon.getScale() * polygon.getCosa() + polygon.getPos().getY());
      pVarTP.color = color;

      if (pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAmount * pAffineTP.z;
      }
    }

  }

  public String getName() {
    return "curliecue";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{size, seed, type, sides, scale, symmetry, m_ecce, showlines};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_SIZE)) {
      size = (int) Tools.limitValue(pValue, 1, 10000);
    } else if (pName.equalsIgnoreCase(PARAM_SEED)) {
      seed = (int) pValue;
    } else if (pName.equalsIgnoreCase(PARAM_TYPE)) {
      type = (int) Tools.limitValue(pValue, 0, 1);
    } else if (pName.equalsIgnoreCase(PARAM_SIDES)) {
      sides = (int) Tools.limitValue(pValue, 3, 20);
    } else if (pName.equalsIgnoreCase(PARAM_SCALE)) {
      scale = Tools.limitValue(pValue, 0.0, 1.0);
    } else if (pName.equalsIgnoreCase(PARAM_SYMMETRY)) {
      symmetry = (int) Tools.limitValue(pValue, 1, 4);
    } else if (pName.equalsIgnoreCase(PARAM_ECCE)) {
      m_ecce = Tools.limitValue(pValue, 0.0, 1.0);
    } else if (pName.equalsIgnoreCase(PARAM_SHOWLINES)) {
      showlines = (int) Tools.limitValue(pValue, 0, 1);
    } else
      throw new IllegalArgumentException(pName);
  }
}
