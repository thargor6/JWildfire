package org.jwildfire.create.tina.variation;


import csk.taprats.geometry.Point;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.awt.*;
import java.util.Random;


public class Mandala2Func extends VariationFunc {


  /*
   * Variation : mandala2
   *
   * Autor: Jesus Sosa
   * Date: September 14, 2018
   * Reference http://www.tiac.net/~sw/2005/03/Mandala/index.html
   */

  private static final long serialVersionUID = 1L;


  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_NUM = "num";
  private static final String PARAM_DENOM = "denom";
  private static final String PARAM_MINSKY = "minsky";
  private static final String PARAM_WOBBLE = "wobble";
  private static final String PARAM_WRAP = "wrap_range";
  private static final String PARAM_HSKEW = "hskew";
  private static final String PARAM_COLORID = "color id";


  private static final String[] paramNames = {PARAM_WIDTH, PARAM_NUM, PARAM_DENOM, PARAM_MINSKY, PARAM_WOBBLE, PARAM_WRAP, PARAM_HSKEW, PARAM_COLORID};


  int width = 500, height = 500;
  int num = 1, denom = 3, minsky = 0;
  int wobble_pick = 0, wrap_range_pick = 0, extra_hskew_pick = 0;
  int colorid = 0;

  int seed = 20000;

  double[] wobble_dat = new double[]{0.0, 0.000003, 0.00001, 0.00003, 0.0001, 0.0003, 0.001, 0.003, 0.01};
  double[] wrap_dat = new double[]{0.0, 12.0, 8.0, 6.0, 4.0, 3.0, 2.0, 1.5, 1.0};
  int[] skew_dat = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 15, 16, 23, 24, 25, 31, 32, 36, 47, 48, 63, 64};

  Random randomize = new Random((long) seed);


  int skew_maps_size, skew_maps_base, skew_maps_min, skew_maps_max;
  int show_base = -32;  //  Lowest value for both x and y on screen--upper left corner.
  double angle, c, s, epsilon;
  int hskew1[], vskew[], hskew2[];

  int max_n_steps = 20000;
  Color colors[] = new Color[max_n_steps + 2];
  int step_counts[][];
  int biggest_size;


  void setColorFor_n_steps(int n_steps) {
    float z, h, s, b, r, gr;

    Color color = null;  // Convince the compiler that it will be initialized.

    if (n_steps <= max_n_steps + 1 && colors[n_steps] != null) {
//				  			g.setColor( colors[ n_steps ] );
    } else {
      if (n_steps == max_n_steps + 1) {
        color = Color.black;
      } else {
        if (colorid == 0) {
          b = (float) (n_steps % 17) / 16;
          r = (float) (n_steps % 91) / 90;
          gr = (float) (n_steps % 123) / 122;
          color = new Color(r, gr, b);
        } else if (colorid == 1) {
          z = (float) Math.log(n_steps);
          h = z - (float) Math.floor(z);
          b = (float) (1 - .5 * (z / Math.log(max_n_steps)));
          if (b > .5) s = (float) ((1.0 - b) * 2 /* + .5 */);
          else s = 1;
          //		        color = Color.getHSBColor( h, s, b );
          color = Color.getHSBColor(h, (float) 0.85, 1);
        } else if (colorid == 2) {  // wrapping--assume all paths cycle
          z = (float) (n_steps / 6.333333);
          h = z - (float) Math.floor(z);
          b = (float) (.25 + .75 / (1 + (float) n_steps / max_n_steps));
          if (b > .5) s = (float) ((1.0 - b) + .5);
          else s = 1;
          color = Color.getHSBColor(h, s, b);
          color = Color.getHSBColor(h, (float) 0.85, 1);
        } else if (colorid == 3) {
          b = (float) (n_steps % 17) / 16;
          r = (float) (n_steps % 91) / 90;
          gr = (float) (n_steps % 123) / 122;
          color = new Color(r, gr, b);
        }

      }
      if (n_steps <= max_n_steps + 1) colors[n_steps] = color;
//				  			g.setColor( color );
    }
  }


  public void RotatorMap(int width, int height, int colorid) {
    this.width = width;
    this.height = height;
    this.colorid = colorid;
    step_counts = new int[width][height];
    show_base = -width / 2;

  }


  // Here is the rotation transformation expressed as a procedure that changes
  // the x and y of a Point in-place.  It's not actually used because calling
  // this procedure inside the inner loop was a lot slower than calculating
  // in-line.
  // Two wrongs don't make a right but three skews make a rotate.
  void rotate(Point p) {
    double x, y;
    x = p.getX();
    y = p.getY();
    x += (int) Math.round(c * y);
    y += (int) Math.round(s * x);
    x += (int) Math.round(c * y);
    p.setX(x);
    p.setY(y);
  }


  // This procedure sets up hskew[] and vskew[] to have the same effect as
  // either rotate(), or Minsky's eliptical variation of it.
  // Plus sets up for the wobble and wrap effects.
  void set_up_skews(boolean minsky, double wobble, double wrap_range, int extra_hskew) {


    this.minsky = (minsky) ? 1 : 0;
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

    if (width > height) biggest_size = width;
    else biggest_size = height;

    if (wrap_range == 0.0) skew_maps_size = biggest_size * 2;
    else skew_maps_size = (int) (biggest_size * wrap_range);

    // Make skew_maps_size odd so that it can have a center value:
    skew_maps_size |= 1;

    // Try to center the wrap range around zero, but if it's too tight for that,
    // adjust the center as little as possible to get the screen inside the range
    // --but the range is kept the same for x and y.
    skew_maps_base = skew_maps_size / 2;
    if (skew_maps_base + (show_base + biggest_size) > skew_maps_size) {
      skew_maps_base = -(show_base + biggest_size) + skew_maps_size;
      // System.out.println( "skew_maps_base = " + skew_maps_base );
    }

    hskew1 = null;  // Having this null triggers recreating them.
    int x_or_y = adjust_skew_maps(0);
  }


  int adjust_skew_maps(int x_or_y) {
    if (wrap_range == 0.0) {
      // Can't count on skew_maps_min or skew_maps_max here.
      while (skew_maps_base + x_or_y < 0 ||
              skew_maps_base + x_or_y >= skew_maps_size) {
        skew_maps_size *= 2;
        skew_maps_base = skew_maps_size / 2;
        hskew1 = null;
      }
    }
    if (hskew1 == null) {
      // System.out.println( "adjust_skew_maps( " + x_or_y + " ) skew_maps_size == " + skew_maps_size );
      skew_maps_min = -skew_maps_base;
      skew_maps_max = skew_maps_min + skew_maps_size - 1;
      // System.out.println( "skew_maps_min = " + skew_maps_min + ", _max = " + skew_maps_max );


      hskew1 = new int[skew_maps_size];
      vskew = new int[skew_maps_size];
      hskew2 = new int[skew_maps_size];

      double lowangle = num / ((1 + wobble) * denom);
      double hiangle = num / ((1 - wobble) * denom);
      double wobble_frequency = 1 / 512.0;

      for (int i = -skew_maps_base; i + skew_maps_base < skew_maps_size; i++) {
        double x = i * 2 * Math.PI * wobble_frequency;
        angle = Math.PI * 2 * (lowangle - (Math.cos(x) - 1) / 2 * (hiangle - lowangle));
        if (minsky == 1) { // eliptical  HAKMEM items 149 (Minsky), 151 (Gosper)
          epsilon = Math.sqrt(2 * (1 - Math.cos(angle)));  //
          // See below about the .00000001
          hskew1[i + skew_maps_base] = (int) -Math.round(epsilon * i + .00000001) + extra_hskew;
          vskew[i + skew_maps_base] = (int) Math.round(epsilon * i + .00000001);
          hskew2[i + skew_maps_base] = 0;
        } else { // Normal, circular, "Givens":
          s = Math.sin(angle);
          c = (Math.cos(angle) - 1) / s;
          hskew1[i + skew_maps_base] = (int) Math.round(c * i);  // indexed by y
          // In Java the following things are true:
          // s = sin( Math.PI * 2 * ( 1 / 12 ) ) = .49999999999999994
          // Math.round(s)       = 1.0   (!?)
          // Math.round( s * 2 ) = 1.0
          // Math.round( s * 3 ) = 1.0
          // This causes a mess in the picture when denom == 12,
          // so this is the fix I came up with:
          vskew[i + skew_maps_base] = (int) Math.round(s * i + .00000001);  // indexed by x
          hskew2[i + skew_maps_base] = hskew1[i + skew_maps_base] + extra_hskew;
        }
      }
    }
    return (x_or_y - skew_maps_size *
            (int) Math.floor((double) (skew_maps_base + x_or_y) / skew_maps_size)
    );
  }


  public int intrandom_range(int a, int b) {
    return a + (int) (Math.random() * (b - a));
  }

  boolean boolean_minsky = false;
  double wobble, wrap_range;
  int extra_hskew;

  @Override
  public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.initOnce(pContext, pLayer, pXForm, pAmount);
    // store result to cache
    RotatorMap(width, height, colorid);
    if (minsky == 1)
      boolean_minsky = true;

    wobble = wobble_dat[wobble_pick];
    if (wobble_pick == 0)
      wobble = 0.0;
    else
      wobble = Math.pow(10.0, -6.0 + .5 * wobble_pick);

    wrap_range = wrap_dat[wrap_range_pick];
    extra_hskew = skew_dat[extra_hskew_pick];

    drawMap(num, denom, boolean_minsky, wobble, wrap_range, extra_hskew);
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    //            	randomize=new Random((long)seed);
    //		        randomize.nextDouble();
  }


  public void drawMap(int num, int denom, boolean minsky, double wobble, double wrap_range,
                      int extra_hskew) {
//			Graphics g = canvas.image.getGraphics();
    int n_steps;
    int min_show_x, max_show_x, min_show_y, max_show_y;

    this.num = num;
    this.denom = denom;

    // angle = Math.PI * 2 * num / denom ... on average, but see:
    set_up_skews(minsky, wobble, wrap_range, extra_hskew);

    int offs_x = -show_base;
    int offs_y = -show_base;
    min_show_x = show_base;
    max_show_x = min_show_x + width - 1;
    min_show_y = show_base;
    max_show_y = min_show_y + height - 1;
    colors = new Color[max_n_steps + 2];

    for (int x = min_show_x; x <= max_show_x; x++) {
      for (int y = min_show_y; y <= max_show_y; y++) {
        if (step_counts[x + offs_x][y + offs_y] == 0) {
          int px = x;
          int py = y;
//						g.setColor( Color.black );  // show some kind of progress...
          for (n_steps = 1; n_steps <= max_n_steps || wrap_range > 0.0; n_steps++) {
            for (int i_denom = 0; i_denom < denom; i_denom++) {
              px += hskew1[py + skew_maps_base];
              if (px < skew_maps_min || px > skew_maps_max) px = adjust_skew_maps(px);
              py += vskew[px + skew_maps_base];
              if (py < skew_maps_min || py > skew_maps_max) py = adjust_skew_maps(py);
              px += hskew2[py + skew_maps_base];
              if (px < skew_maps_min || px > skew_maps_max) px = adjust_skew_maps(px);
            }
            if (px == x && py == y) {  // Found a cycle...
              break;
            }
            if (n_steps > max_n_steps) {
//							 	g.fillRect( px + offs_x, py + offs_y, 1, 1 );
              //						Ngon poly=new Ngon(4,1.0/(float)biggest_size,0.0,new Point(px + offs_x, py + offs_y),colors[n_steps],0.0);
              //					    primitives.add(poly);
              n_steps = max_n_steps;
            }
          }
          // Now retrace that path, knowing the (non-)cycle length:
          px = x;
          py = y;
          setColorFor_n_steps(n_steps);
          for (int n = 1; n <= n_steps; n++) {
            if (px >= min_show_x && px <= max_show_x &&
                    py >= min_show_y && py <= max_show_y) {
              if (step_counts[px + offs_x][py + offs_y] != 0) {
                break;  //  been there done...something...
              }
              step_counts[px + offs_x][py + offs_y] = n_steps;
            }
            for (int i_denom = 0; i_denom < denom; i_denom++) {
              px += hskew1[py + skew_maps_base];
              if (px < skew_maps_min || px > skew_maps_max) px = adjust_skew_maps(px);
              py += vskew[px + skew_maps_base];
              if (py < skew_maps_min || py > skew_maps_max) py = adjust_skew_maps(py);
              px += hskew2[py + skew_maps_base];
              if (px < skew_maps_min || px > skew_maps_max) px = adjust_skew_maps(px);
            }
            if (px == x && py == y) {  // End of the cycle...
              break;
            }
          } // for n_steps retracing path
        } // if step_count was zero
      } // for y
    } // for x
//			canvas.paint( canvas.getGraphics() );
  } // drawMap

  public boolean inrange(int i1, int i, int i2) {
    if ((i <= i2) && (i >= i1))
      return true;
    else
      return false;
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    int x, y;
    Color col;
    double color;

    x = intrandom_range(0, width);
    y = intrandom_range(0, height);

    Point p = new Point(x, y);
    if(step_counts!=null) {
      col = colors[step_counts[x][y]];

      if (inrange(1, colorid, 3)) {
        pVarTP.rgbColor = true;
        ;
        pVarTP.redColor = col.getRed();
        ;
        pVarTP.greenColor = col.getGreen();
        pVarTP.blueColor = col.getBlue();
      } else {
        float[] hsbValues = new float[3];
        hsbValues = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), hsbValues);
        color = (double) hsbValues[0];
        pVarTP.color = color;
      }
      pVarTP.x += pAmount * ((double) (p.getX()) / width - 0.5);
      pVarTP.y += pAmount * ((double) (p.getY()) / width - 0.5);

      if (pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAmount * pAffineTP.z;
      }
    }
  }

  public String getName() {
    return "mandala2";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{width, num, denom, minsky, wobble_pick, wrap_range_pick, extra_hskew_pick, colorid};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
      width = (int) Tools.limitValue(pValue, 100, 2000);
      height = width;
    } else if (pName.equalsIgnoreCase(PARAM_NUM)) {
      if (pValue < 1)
        pValue = 1;
      if ((pValue * 2) % denom == 0)
        pValue++;
      num = (int) (Tools.limitValue(pValue, 1, 15));
    } else if (pName.equalsIgnoreCase(PARAM_DENOM)) {
      if (pValue < 3)
        pValue = 3;
      if ((num * 2) % pValue == 0)
        pValue++;
      denom = (int) (Tools.limitValue(pValue, 3, 16));
    } else if (pName.equalsIgnoreCase(PARAM_MINSKY)) {
      minsky = (int) Tools.limitValue(pValue, 0, 1);
    } else if (pName.equalsIgnoreCase(PARAM_WOBBLE)) {
      if (wobble_pick < 0)
        wobble_pick = 0;
      wobble_pick = (int) Tools.limitValue(pValue, 0, wobble_dat.length) % wobble_dat.length;
    } else if (pName.equalsIgnoreCase(PARAM_WRAP)) {
      if (wrap_range_pick < 0)
        wrap_range_pick = 0;
      wrap_range_pick = (int) Tools.limitValue(pValue, 0, wrap_dat.length) % wrap_dat.length;
    } else if (pName.equalsIgnoreCase(PARAM_HSKEW)) {
      if (extra_hskew_pick < 0)
        extra_hskew_pick = 0;
      extra_hskew_pick = (int) Tools.limitValue(pValue, 0, skew_dat.length) % skew_dat.length;
    } else if (pName.equalsIgnoreCase(PARAM_COLORID)) {
      colorid = (int) Tools.limitValue(pValue, 0, 3);
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
    if (pName.equalsIgnoreCase(PARAM_NUM)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_DENOM)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_MINSKY)) {
      return true;
    } else if (PARAM_WOBBLE.equalsIgnoreCase(pName))
      return true;
    else if (pName.equalsIgnoreCase(PARAM_WRAP)) {
      return true;
    } else if (pName.equalsIgnoreCase(PARAM_HSKEW)) {
      return true;
    } else
      return false;
  }
}
