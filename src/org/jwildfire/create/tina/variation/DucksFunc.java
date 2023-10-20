package org.jwildfire.create.tina.variation;


import java.io.StringReader;
import js.colordomain.Complex;
import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class DucksFunc extends VariationFunc {

  /*
   * Variation : ducks
   *
   * Autor: Jesus Sosa
   * Date: October 22, 2018
   * Reference http://www.algorithmic-worlds.net/blog/blog.php?Post=20110227
   */

  public static class ComplexFuncRunner {

    public static ComplexFuncRunner compile(String pScript) throws Exception {
      ComplexFuncRunner res = (ComplexFuncRunner) ClassBodyEvaluator.createFastClassBodyEvaluator(new Scanner(null, new StringReader(pScript)), ComplexFuncRunner.class, (ClassLoader) null);
      return res;
    }

    public Complex f(Complex z, Complex c) {
      Complex tmp = new Complex(z.re(), Math.abs(z.im()));
      return tmp.Log().plus(new Complex(c.re(), c.im()));
    }
  }


  private static final long serialVersionUID = 1L;

  private static final String PARAM_SIZE = "size";
  private static final String PARAM_RMIN = "re_min";
  private static final String PARAM_RMAX = "re_max";
  private static final String PARAM_IMIN = "im_min";
  private static final String PARAM_IMAX = "im_max";
  private static final String PARAM_ZOOMIN = "zoomIn";
  private static final String PARAM_ZOOMOUT = "zoomOut";
  private static final String PARAM_PANMX = "Pan -Re";
  private static final String PARAM_PANPX = "Pan +Re";
  private static final String PARAM_PANMY = "Pan -Im";
  private static final String PARAM_PANPY = "Pan +Im";
  private static final String PARAM_JULIAMODE = "julia Mode";
  private static final String PARAM_C_REAL = "c_re";
  private static final String PARAM_C_IMAG = "c_im";
  private static final String PARAM_MAXITERS = "MaxIters";
  private static final String PARAM_ITERS = "MinIters";
  private static final String PARAM_COLORTYPE = "ColorType"; //		                int colortype=0; // 0, 1, 2 , 3, 4
  //colortype=0 colordomain
  //colortype=1 Fragmentarium 0
  //colortype=2 Fragmentarium 1
  //colortype=3 Fragmentarium 2
  //colortype=4 FractalLab

  private static final String PARAM_ESCAPE = "ScapeSize";
  private static final String PARAM_CONTRAST = "Contrast";
  private static final String PARAM_COLORFACTOR = "ColorFactor";

  private static final String PARAM_GRADIENTMODE = "Gradient";

  private static final String PARAM_C_SCALE = "colorScale";
  private static final String PARAM_C_CYCLE = "colorCycle";
  private static final String PARAM_C_OFFSET = "colorOffset";
  private static final String PARAM_C_MIRROR = "colorMirror";
  private static final String PARAM_C_RAINBOW = "colorRainbow";


  double escape = 5.0; //0.0-11.0
  double C = 1.0;       //0.0-2.0
  double colorFactor = 1.0;  //0.0-1.0

  private static final String[] paramNames = {PARAM_SIZE, PARAM_RMIN, PARAM_RMAX, PARAM_IMIN, PARAM_IMAX, PARAM_ZOOMIN, PARAM_ZOOMOUT,
          PARAM_PANMX, PARAM_PANPX, PARAM_PANMY, PARAM_PANPY, PARAM_JULIAMODE, PARAM_C_REAL, PARAM_C_IMAG, PARAM_MAXITERS, PARAM_ITERS, PARAM_COLORTYPE, PARAM_ESCAPE,
          PARAM_CONTRAST, PARAM_COLORFACTOR, PARAM_GRADIENTMODE, PARAM_C_SCALE, PARAM_C_CYCLE, PARAM_C_OFFSET, PARAM_C_MIRROR, PARAM_C_RAINBOW};

  private static final String RESSOURCE_CODE = "code";
  private static final String RESSOURCE_COLOR1 = "Col1 R,G,B";
  private static final String RESSOURCE_COLOR2 = "Col2 R,G,B";

  private static final String[] ressourceNames = {RESSOURCE_CODE, RESSOURCE_COLOR1, RESSOURCE_COLOR2};

  int size = 1500;
  int zoomIn = 1;
  int zoomOut = 1;
  double c_re = 0.672;
  double c_im = -0.378;

  double colorScale = 1.0;
  double colorCycle = 2.0;
  double colorOffset = 0.0;


  int colortype = 4;
  int colorMirror = 1;
  int colorRainbow = 0;
  int juliamode = 0;

  int panmx = 1;
  int panpx = 1;

  int panmy = 1;
  int panpy = 1;


  private String Col1 = new String("0,102,178");
  private String Col2 = new String("  0,  0,  0");

  double color1[] = new double[3];
  double color2[] = new double[3];
  double R = 0.0, G = 0.4, B = 0.7;


  int nIters = 1;
  int MaxIters = 50;

  int Gradient = 1;
					  
/*					  double re_min=-1.5,re_max=0.75;
					  double im_min=-3.1416,im_max=-0.25;*/

  double re_min = -2, re_max = 2;
  double im_min = -3.2, im_max = 0.8;


  String code_func = "import js.colordomain.Complex;\r\n" +
          "public Complex f(Complex z, Complex c)\r\n" +
          "		    {\r\n" +
          "	             Complex tmp = new Complex(z.re(),Math.abs(z.im()));\r\n" +
          "	             return tmp.Log().plus(new Complex(c.re(),c.im()));\r\n" +
          "	        }";

  ComplexFuncRunner cf_runner = null;

  @Override
  public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.initOnce(pContext, pLayer, pXForm, pAmount);
    // store result to cache
/*					        if (cf_runner == null) {
						          compile();
						        }*/
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    color1 = convColor(Col1, "white");
    color2 = convColor(Col2, "black");

    R = color1[0];
    G = color1[1];
    B = color1[2];
    if (cf_runner == null) {
      compile();
    }

  }

  public void compile() {
    try {
      cf_runner = ComplexFuncRunner.compile(code_func);
    } catch (Throwable ex) {
      System.out.println("##############################################################");
      System.out.println(ex.getMessage());
      System.out.println("##############################################################");
      System.out.println(code_func);
      System.out.println("##############################################################");
    }
  }

  @Override
  public void validate() {
    try {
      if (code_func != null) {
        ComplexFuncRunner.compile(code_func);
      }
    } catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  public double[] convColor(String Col, String defcolor) {
    int RGB[];
    double[] color = new double[3];
    String p[] = null;
    p = Col.split(",");
    RGB = new int[3];
    boolean fail = false;

    if (p.length >= 3) {

      for (int k = 0; k < 3; k++) {
        try {
          RGB[k] = Integer.parseInt(p[k]);
        } catch (NumberFormatException numberFormatException) {
          fail = true;
        }
      }
    } else
      fail = true;

    if (fail) {
      if ("black".equals(defcolor)) {
        color[0] = 0.0;
        color[1] = 0.0;
        color[2] = 0.0;
      } else {
        color[0] = 1.0;
        color[1] = 1.0;
        color[2] = 1.0;
      }
    } else {
      color[0] = (double) RGB[0] / 255.0;
      color[1] = (double) RGB[1] / 255.0;
      color[2] = (double) RGB[2] / 255.0;
    }
    return color;
  }
		              
/*		              public Complex f(Complex z, Complex c)
		              {
	            	        Complex tmp = new Complex(z.re(),Math.abs(z.im()));
	              	        return tmp.Log().plus(new Complex(c.re(),c.im()));
	            	             
	            	  }*/

  public double[] SetHSV(double h, double s, double v) {
    double r = 0.0D;
    double g = 0.0D;
    double b = 0.0D;
    double color[] = new double[3];
    if (s == 0.0D) {
      r = g = b = v;
    } else {
      if (h == 1.0D)
        h = 0.0D;
      double z = Math.floor(h * 6D);
      int i = (int) z;
      double f = h * 6D - z;
      double p = v * (1.0D - s);
      double q = v * (1.0D - s * f);
      double t = v * (1.0D - s * (1.0D - f));
      switch (i) {
        case 0: // '\0'
          r = v;
          g = t;
          b = p;
          break;

        case 1: // '\001'
          r = q;
          g = v;
          b = p;
          break;

        case 2: // '\002'
          r = p;
          g = v;
          b = t;
          break;

        case 3: // '\003'
          r = p;
          g = q;
          b = v;
          break;

        case 4: // '\004'
          r = t;
          g = p;
          b = v;
          break;

        case 5: // '\005'
          r = v;
          g = p;
          b = q;
          break;
      }
    }
    color[0] = r;
    color[1] = g;
    color[2] = b;
    return color;
  }


  public double clamp(double x, double minVal, double maxVal) {
    return Math.min(Math.max(x, minVal), maxVal);
  }

  public double[] mix(double[] x, double[] y, double a) {
    double[] z = new double[3];
    z[0] = (x[0] * (1 - a) + y[0] * a);
    z[1] = (x[1] * (1 - a) + y[1] * a);
    z[2] = (x[2] * (1 - a) + y[2] * a);
    return z;
  }

  public double mix(double x, double y, double a) {
    double z;
    z = (x * (1 - a) + y * a);
    return z;
  }


  public static double log2(double d) {
    return Math.log(d) / Math.log(2.0);
  }

  public double[] hsv2rgb(double[] hsv) {
    double h, s, v, r = 0, g = 0, b = 0, j, p, q, t;
    int i;
    double[] color = new double[3];

    color[0] = 0.0;
    color[1] = 0.0;
    color[2] = 0.0;

    h = hsv[0];
    s = hsv[1];
    v = hsv[2];

    if (h == 1.0) {
      h = 0.0;
    }

    if (v == 0.0) {
      // No brightness so return black
      color[0] = 0.0;
      color[1] = 0.0;
      color[2] = 0.0;

    } else if (s == 0.0) {
      // No saturation so return grey
      color[0] = hsv[2];
      color[1] = hsv[2];
      color[2] = hsv[2];

    } else {
      // RGB color
      h *= 6.0;
      i = (int) (Math.floor(h));
      j = h - (double) i;
      p = v * (1.0 - s);
      q = v * (1.0 - (s * j));
      t = v * (1.0 - (s * (1.0 - j)));

      if (i == 0) {
        r = v;
        g = t;
        b = p;
      } else if (i == 1) {
        r = q;
        g = v;
        b = p;
      } else if (i == 2) {
        r = p;
        g = v;
        b = t;
      } else if (i == 3) {
        r = p;
        g = q;
        b = v;
      } else if (i == 4) {
        r = t;
        g = p;
        b = v;
      } else if (i == 5) {
        r = v;
        g = p;
        b = q;
      }
      color[0] = r;
      color[1] = g;
      color[2] = b;
    }

    return color;
  }


  // RGB to HSV
  // http://www.easyrgb.com/index.php?X=MATH&H=20#text20
  public double[] rgb2hsv(double[] color) {
    double rgb_min = Math.min(color[0], Math.min(color[1], color[2]));
    double rgb_max = Math.max(color[0], Math.max(color[1], color[2]));
    double rgb_delta = rgb_max - rgb_min;

    double v = rgb_max;
    double h = 0, s;

    if (rgb_delta == 0.0) {
      // Grey
      h = 0.0;
      s = 0.0;
    } else {
      // Colour
      s = rgb_delta / rgb_max;
      double r_delta = (((rgb_max - color[0]) / 6.0) + (rgb_delta / 2.0)) / rgb_delta;
      double g_delta = (((rgb_max - color[1]) / 6.0) + (rgb_delta / 2.0)) / rgb_delta;
      double b_delta = (((rgb_max - color[2]) / 6.0) + (rgb_delta / 2.0)) / rgb_delta;

      if (color[0] == rgb_max) {
        h = b_delta - g_delta;
      } else if (color[1] == rgb_max) {
        h = 1.0 / 3.0 + r_delta - b_delta;
      } else if (color[2] == rgb_max) {
        h = 2.0 / 3.0 + g_delta - r_delta;
      }

      if (h < 0.0) h += 1.0;
      if (h > 1.0) h -= 1.0;
    }
    double[] theColor = new double[3];
    theColor[0] = h;
    theColor[1] = s;
    theColor[2] = v;
    return color;
  }
			      	
/*				    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
					{
	       
		                 int j=(int) (pContext.random()*size);
		                 int i=(int) (pContext.random()*size);
		                 
		                
		 	            double im = im_max - ((im_max - im_min) * (double)j) / (double)(size- 1);
			            double re = re_min - ((re_min - re_max) * (double)i) / (double)(size - 1);
			            
		                Complex c = new Complex(re, im);
		                
		                Complex z = c;

		                for(int k=0;k< nIters;k++)
		                {
		                	n+=1.0;
		                	z= f( z);
		                }
		                
		                double a;
		                for(a = z.arg(); a < 0.0D; a += 6.2831853071795862D);
		                a /= 6.2831853071795862D;
		                double m = z.mod();
		                double ranges = 0.0D;
		                double rangee;
		                for(rangee = 1.0D; m > rangee; rangee *= 2.7182818284590451D)
		                    ranges = rangee;

		                double k = (m - ranges) / (rangee - ranges);
		                double sat = k >= 0.5D ? 1.0D - (k - 0.5D) * 2D : k * 2D;
		                sat = 1.0D - Math.pow(1.0D - sat, 3D);
		                sat = 0.40000000000000002D + sat * 0.59999999999999998D;
		                double val = k >= 0.5D ? 1.0D - (k - 0.5D) * 2D : k * 2D;
		                val = 1.0D - val;
		                val = 1.0D - Math.pow(1.0D - val, 3D);
		                val = 0.59999999999999998D + val * 0.40000000000000002D;
		                int theColor[] = SetHSV(a, sat, val);
		                
			            
				        pVarTP.rgbColor  =true;;
				        pVarTP.redColor  =theColor[0];
				        pVarTP.greenColor=theColor[1];
				        pVarTP.blueColor =theColor[2];
		  
						pVarTP.x+= pAmount*((double)(i)/size - 0.5);
			  			pVarTP.y+= pAmount*((double)(j)/size - 0.5 );
			  			
			  		    if (pContext.isPreserveZCoordinate()) {
			  		      pVarTP.z += pAmount * pAffineTP.z;
			  		    }
						
					}   */

  public double[] int2dbl(int[] color) {
    double[] theColor = new double[3];

    theColor[0] = (double) color1[0] / 255.0;
    theColor[1] = (double) color1[1] / 255.0;
    theColor[2] = (double) color1[2] / 255.0;
    return theColor;
  }

  public int[] dbl2int(double[] theColor) {
    int[] color = new int[3];

    int col = (int) (256D * theColor[0]);
    if (col > 255)
      col = 255;
    color[0] = col;
    col = (int) (256D * theColor[1]);
    if (col > 255)
      col = 255;
    color[1] = col;
    col = (int) (256D * theColor[2]);
    if (col > 255)
      col = 255;
    color[2] = col;
    return color;
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    int j = (int) (pContext.random() * size);
    int i = (int) (pContext.random() * size);

    double n = 0.0;
    double d = 0.0;
    double v;

    double theColor[] = new double[3];

    double ci = 0.0;


    double im = im_max - ((im_max - im_min) * (double) j) / (double) (size - 1);
    double re = re_min - ((re_min - re_max) * (double) i) / (double) (size - 1);

    Complex c = new Complex(re, im);
    Complex z = c;

    double col = 0.5;

    if (juliamode == 1)
      c = new Complex(c_re, c_im);

    if (colortype == 0)  // colordomain
    {
      Gradient = 0;
      for (int k = 0; k < MaxIters; k++) {
        n += 1.0;
        //     	z= f( z, c);
        z = cf_runner.f(z, c);
        if (n >= (double) nIters) {
          // float  dot(vec 2 z,vec 2 z) dot product z.z  (z.x*z.x + z.y*z.y)
          d += Complex.norm(z);
        }
      }
      double a;
      for (a = z.arg(); a < 0.0D; a += 6.2831853071795862D) ;
      a /= 6.2831853071795862D;
      double m = z.mod();
      double ranges = 0.0D;
      double rangee;
      for (rangee = 1.0D; m > rangee; rangee *= 2.7182818284590451D)
        ranges = rangee;
      double k = (m - ranges) / (rangee - ranges);
      double sat = k >= 0.5D ? 1.0D - (k - 0.5D) * 2D : k * 2D;
      sat = 1.0D - Math.pow(1.0D - sat, 3D);
      sat = 0.40000000000000002D + sat * 0.59999999999999998D;
      double val = k >= 0.5D ? 1.0D - (k - 0.5D) * 2D : k * 2D;
      val = 1.0D - val;
      val = 1.0D - Math.pow(1.0D - val, 3D);
      val = 0.59999999999999998D + val * 0.40000000000000002D;
      theColor = SetHSV(a, sat, val);
    }

    if (colortype == 1) //Fragmentarium color schema 0
    {
      double mean = 0;
      int it = 0;
      for (int k = 0; k < MaxIters; k++) {
        //	z= f( z, c);
        z = cf_runner.f(z, c);
        if (k > nIters) {
          mean += z.mod();
          if (Complex.norm(z) > escape && k > nIters) {
            it = k;
            break;

          }
        }
      }
      mean /= (double) (it - nIters);
      ci = 1.0 - log2(0.5 * log2(mean / C));
      col = ci / (double) MaxIters;

      theColor[0] = 0.5 + 0.5 * Math.cos(6.0 * ci + R);
      theColor[1] = 0.5 + 0.5 * Math.cos(6.0 * ci + G);
      theColor[2] = 0.5 + 0.5 * Math.cos(6.0 * ci + B);
    }

    if (colortype == 2) //Fragmentarium color schema 1
    {
      double m = 0.0;
      for (int k = 0; k < MaxIters; k++) {
        //	z= f( z, c);
        z = cf_runner.f(z, c);
        if (k > nIters)
          m = mix(m, z.mod(), colorFactor);
        if (Complex.norm(z) > escape && k > nIters)
          break;
      }
      ci = 1.0 - log2(0.5 * log2(m / C));
      col = ci / (double) MaxIters;
      theColor[0] = 0.5 + 0.5 * Math.cos(6.0 * ci + R);
      theColor[1] = 0.5 + 0.5 * Math.cos(6.0 * ci + G);
      theColor[2] = 0.5 + 0.5 * Math.cos(6.0 * ci + B);
    }

    if (colortype == 3) //Fragmentarium color schema 2
    {
      int m = 0;
      for (int k = 0; k < MaxIters; k++) {
        //   	z= f( z, c);
        z = cf_runner.f(z, c);
        if (Complex.norm(z) > escape && k > nIters) {
          m = k;
          break;
        }
      }
      ci = (double) m + 1.0 - log2(0.5 * log2(Complex.norm(z)));
      col = ci / (double) MaxIters;
      theColor[0] = 0.5 + 0.5 * Math.cos(6.0 * ci + R);
      theColor[1] = 0.5 + 0.5 * Math.cos(6.0 * ci + G);
      theColor[2] = 0.5 + 0.5 * Math.cos(6.0 * ci + B);
    }

    if (colortype == 4) //FractalLab
    {
      Gradient = 0;
      for (int k = 0; k < MaxIters; k++) {
        n += 1.0;
        // 	z= f( z, c);
        z = cf_runner.f(z, c);
        if (n >= (double) nIters) {
          // float  dot(vec 2 z,vec 2 z) dot product z.z  (z.x*z.x + z.y*z.y)
          d += Complex.norm(z);
        }
      }

      v = Math.sqrt(d / n);
      v = Math.pow(v, colorScale);
      v *= colorCycle;
      v += colorOffset;

      if (colorMirror == 1) {
        boolean even = (v % 2.0) < 1.0 ? true : false;
        if (even) {
          v = 1.0 - (v % 1.0);
        } else {
          v = (v % 1.0);
        }
      } else {
        v = 1.0 - (v % 1.0);
      }

      if (colorRainbow == 1)  // vec3 mix (vec3 x, vec3 y, float a)  return x(1-a) + ya (per component)
      {
        double[] color = new double[3];
        double a = clamp(v, 0.0, 1.0);
        double[] col1 = new double[3];
        double[] col2 = new double[3];
		                		
/*		                		double [] black= new double[3];
			            		  black[0]=0.0;
			            		  black[1]=0.0;
			            		  black[2]=0.0;
			            		double[] white=new double[3];
			            		  white[0]=1.0;
			            		  white[1]=1.0;
			            		  white[2]=1.0;
			                    col1=rgb2hsv(black);
			                	col2=rgb2hsv(white);*/

        col1 = rgb2hsv(color1);
        col2 = rgb2hsv(color2);

        color = mix(col1, col2, a);
        theColor = hsv2rgb(color);
      } else {
        // clamp(x,x1,x2) return x if (x1<x<x2)
        double a = clamp(v, 0.0, 1.0);
        theColor = mix(color1, color2, a);
      }
    }

// convert r,b,g to integer

    if (Gradient == 0) {
      int[] color = new int[3];
      color = dbl2int(theColor);


      pVarTP.rgbColor = true;
      ;
      pVarTP.redColor = color[0];
      pVarTP.greenColor = color[1];
      pVarTP.blueColor = color[2];
    } else {
      pVarTP.rgbColor = false;
      pVarTP.color = col;
    }

    pVarTP.x += pAmount * ((double) (i) / size - 0.5);
    pVarTP.y += pAmount * ((double) (j) / size - 0.5);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }


  public String getName() {
    return "ducks";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
    return new Object[]{size, re_min, re_max, im_min, im_max, zoomIn, zoomOut, panmx, panpx, panmy, panpy, juliamode, c_re, c_im,
            MaxIters, nIters, colortype, escape, C, colorFactor, Gradient, colorScale, colorCycle, colorOffset, colorMirror, colorRainbow};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_SIZE)) {
      size = (int) Tools.limitValue(pValue, 100, 10000);
    } else if (pName.equalsIgnoreCase(PARAM_RMIN)) {
      re_min = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_RMAX)) {
      re_max = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_IMIN)) {
      im_min = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_IMAX)) {
      im_max = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_ZOOMIN)) {
      zoomIn = (int) Tools.limitValue(pValue, 0, 1);
      double rrange = Math.abs(re_max - re_min);
      double irange = Math.abs(im_max - im_min);
      rrange /= 4D;
      irange /= 4D;
      im_max = (im_max - irange);
      re_max = (re_max - rrange);
      im_min = (im_min + irange);
      re_min = (re_min + rrange);

    } else if (pName.equalsIgnoreCase(PARAM_ZOOMOUT)) {
      zoomOut = (int) Tools.limitValue(pValue, 1, 1);
      double rrange = Math.abs(re_max - re_min);
      double irange = Math.abs(im_max - im_min);
      rrange /= 2D;
      irange /= 2D;
      re_max = (re_max + rrange);
      re_min = (re_min - rrange);
      im_max = (im_max + irange);
      im_min = (im_min - irange);
    } else if (pName.equalsIgnoreCase(PARAM_PANMX)) {
      panmx = (int) Tools.limitValue(pValue, 1, 1);
      double rrange = Math.abs(re_max - re_min);
      //			        double sign=(panx==0)?-1.0:1.0;
      //			        re_min=(re_min +  sign* 0.10* rrange);
      re_min = (re_min + 0.10 * rrange);
      re_max = (re_min + rrange);
      im_max = im_max;
      im_min = im_min;
    } else if (pName.equalsIgnoreCase(PARAM_PANPX)) {
      panpx = (int) Tools.limitValue(pValue, 1, 1);
      double rrange = Math.abs(re_max - re_min);
      //			        double sign=(panx==0)?-1.0:1.0;
      //			        re_min=(re_min +  sign* 0.10* rrange);
      re_min = (re_min - 0.10 * rrange);
      re_max = (re_min + rrange);
      im_max = im_max;
      im_min = im_min;
    } else if (pName.equalsIgnoreCase(PARAM_PANMY)) {
      panmy = (int) Tools.limitValue(pValue, 1, 1);
      double irange = Math.abs(im_max - im_min);
//						        double sign=(pany==0)?-1.0:1.0;
      im_min = (im_min + 0.10 * irange);
      im_max = (im_min + irange);
      re_min = re_min;
      re_max = re_max;
    } else if (pName.equalsIgnoreCase(PARAM_PANPY)) {
      panpy = (int) Tools.limitValue(pValue, 1, 1);
      double irange = Math.abs(im_max - im_min);
//					        double sign=(pany==0)?-1.0:1.0;
      im_min = (im_min - 0.10 * irange);
      im_max = (im_min + irange);
      re_min = re_min;
      re_max = re_max;
    } else if (pName.equalsIgnoreCase(PARAM_JULIAMODE)) {
      juliamode = (int) Tools.limitValue(pValue, 0, 1);
    } else if (pName.equalsIgnoreCase(PARAM_C_REAL)) {
      c_re = Tools.limitValue(pValue, -15, 8);
    } else if (pName.equalsIgnoreCase(PARAM_C_IMAG)) {
      c_im = Tools.limitValue(pValue, -15, 8);
    } else if (pName.equalsIgnoreCase(PARAM_MAXITERS)) {
      MaxIters = (int) Tools.limitValue(pValue, 1, 50);
    } else if (pName.equalsIgnoreCase(PARAM_ITERS)) {
      nIters = (int) Tools.limitValue(pValue, 1, 50);
    } else if (pName.equalsIgnoreCase(PARAM_COLORTYPE)) {
      colortype = (int) Tools.limitValue(pValue, 0, 4);
    } else if (pName.equalsIgnoreCase(PARAM_ESCAPE)) {
      escape = Tools.limitValue(pValue, 0.0, 11.0);
    } else if (pName.equalsIgnoreCase(PARAM_CONTRAST)) {
      C = Tools.limitValue(pValue, 0.0, 2.0);
    } else if (pName.equalsIgnoreCase(PARAM_COLORFACTOR)) {
      colorFactor = (int) Tools.limitValue(pValue, 0.0, 1.0);
    } else if (pName.equalsIgnoreCase(PARAM_GRADIENTMODE)) {
      Gradient = (int) Tools.limitValue(pValue, 0, 1);
    } else if (pName.equalsIgnoreCase(PARAM_C_SCALE)) {
      colorScale = Tools.limitValue(pValue, 0, 10);
    } else if (pName.equalsIgnoreCase(PARAM_C_CYCLE)) {
      colorCycle = Tools.limitValue(pValue, 1, 10);
    } else if (pName.equalsIgnoreCase(PARAM_C_OFFSET)) {
      colorOffset = Tools.limitValue(pValue, -10, 10);
    } else if (pName.equalsIgnoreCase(PARAM_C_MIRROR)) {
      colorMirror = (int) Tools.limitValue(pValue, 0, 1);
    } else if (pName.equalsIgnoreCase(PARAM_C_RAINBOW)) {
      colorRainbow = (int) Tools.limitValue(pValue, 0, 1);
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public boolean dynamicParameterExpansion() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String pName) {
    // preset_id doesn't really expand parameters, but it changes them; this will make them refresh
    if (pName.equalsIgnoreCase(PARAM_RMIN)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_RMAX)) {
      return true;
    } else if (PARAM_IMIN.equalsIgnoreCase(pName))
      return true;
    else if (pName.equalsIgnoreCase(PARAM_IMAX)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_ZOOMIN)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_ZOOMOUT)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_PANMX)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_PANPX)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_PANMY)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_PANPY)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_C_REAL)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_C_IMAG)) {
      return true;
    } else
      return false;
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }


  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(code_func != null ? code_func.getBytes() : null), (Col1 != null ? Col1.getBytes() : null), (Col2 != null ? Col2.getBytes() : null)};
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (pName.equals(RESSOURCE_CODE)) {
      return RessourceType.JAVA_CODE;
    } else if (pName.equals(RESSOURCE_COLOR1)) {
      return RessourceType.BYTEARRAY;
    } else if (pName.equals(RESSOURCE_COLOR2)) {
      return RessourceType.BYTEARRAY;
    } else {
      return super.getRessourceType(pName);
    }
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_CODE.equalsIgnoreCase(pName)) {
      code_func = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_COLOR1.equalsIgnoreCase(pName)) {
      Col1 = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_COLOR2.equalsIgnoreCase(pName)) {
      Col2 = pValue != null ? new String(pValue) : "";
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_BASE_SHAPE};
  }

}
