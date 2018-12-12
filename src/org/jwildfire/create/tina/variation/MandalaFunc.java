package org.jwildfire.create.tina.variation;


import csk.taprats.geometry.Ngon;
import csk.taprats.geometry.Point;
import csk.taprats.geometry.Primitive;
import js.mandala.RotatorMap;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.plot.DrawFunc;

import java.awt.*;
import java.util.Random;


public class MandalaFunc extends DrawFunc {

  /*
   * Variation : mandala
   *
   * Autor: Jesus Sosa
   * Date: September 14, 2018
   * Reference http://www.tiac.net/~sw/2005/03/Mandala/index.html
   */

  private static final long serialVersionUID = 1L;


  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_SIZE = "size";
  private static final String PARAM_NUM = "num";
  private static final String PARAM_DENOM = "denom";
  private static final String PARAM_MINSKY = "minsky";
  private static final String PARAM_WOBBLE = "wobble";
  private static final String PARAM_WRAP = "wrap_range";
  private static final String PARAM_HSKEW = "hskew";
  private static final String PARAM_COLORID = "color id";


  private static final String[] paramNames = {PARAM_WIDTH, PARAM_SIZE, PARAM_NUM, PARAM_DENOM, PARAM_MINSKY, PARAM_WOBBLE, PARAM_WRAP, PARAM_HSKEW, PARAM_COLORID};


  double size = 0.60;
  int width = 300, height = 300;
  int num = 1, denom = 3, minsky = 0;
  int wobble_pick = 0, wrap_range_pick = 0, extra_hskew_pick = 0;
  int colorid = 1;

  int seed = 1000;

  double[] wobble_dat = new double[]{0.0, 0.000003, 0.00001, 0.00003, 0.0001, 0.0003, 0.001, 0.003, 0.01};
  double[] wrap_dat = new double[]{0.0, 12.0, 8.0, 6.0, 4.0, 3.0, 2.0, 1.5, 1.0};
  int[] skew_dat = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 15, 16, 23, 24, 25, 31, 32, 36, 47, 48, 63, 64};

  Random randomize = new Random((long) seed);


  public void build_mandala() {
    boolean boolean_minsky = false;
    double wobble, wrap_range;
    int extra_hskew;
    randomize.nextDouble();

    RotatorMap rm = new RotatorMap(width, height, colorid);
    if (minsky == 1)
      boolean_minsky = true;

    wobble = wobble_dat[wobble_pick];
    if (wobble_pick == 0)
      wobble = 0.0;
    else
      wobble = Math.pow(10.0, -6.0 + .5 * wobble_pick);

    wrap_range = wrap_dat[wrap_range_pick];
    extra_hskew = skew_dat[extra_hskew_pick];

    rm.drawMap(primitives, num, denom, boolean_minsky, wobble, wrap_range, extra_hskew);
  }


  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    randomize = new Random((long) seed);
    build_mandala();
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    Point out = null;

    Primitive primitive = null;
    Color color;
    double col;

    primitive = getPrimitive(pContext);

    if (primitive.gettype() == 4) {
      Ngon polygon = (Ngon) primitive;
      out = plotPolygon(pContext, polygon);

      pVarTP.x += 0.01 * pAmount * (out.getX() * size * polygon.getCosa() + out.getY() * size * polygon.getSina() + polygon.getPos().getX() - width / 2);
      pVarTP.y += 0.01 * pAmount * (-out.getX() * size * polygon.getSina() + out.getY() * size * polygon.getCosa() + polygon.getPos().getY() - width / 2);


      if (pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAmount * pAffineTP.z;
      }

      if (colorid == 0) {
        color = polygon.getRGBColor();
        pVarTP.rgbColor = true;
        ;
        pVarTP.redColor = color.getRed();
        ;
        pVarTP.greenColor = color.getGreen();
        pVarTP.blueColor = color.getBlue();
      } else {
        col = polygon.getColor();
        pVarTP.color = col;
      }
    }

  }

  public String getName() {
    return "mandala";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{width, size, num, denom, minsky, wobble_pick, wrap_range_pick, extra_hskew_pick, colorid};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
      width = (int) Tools.limitValue(pValue, 100, 1000);
      height = width;
    } else if (pName.equalsIgnoreCase(PARAM_SIZE)) {
      size = Tools.limitValue(pValue, 0.0, 1.0);
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
      colorid = (int) Tools.limitValue(pValue, 0, 1);
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
