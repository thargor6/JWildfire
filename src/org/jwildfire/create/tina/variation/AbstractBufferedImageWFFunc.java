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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.GradientCreator;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.variation.MSTruchetFunc.Point;
import org.jwildfire.create.tina.variation.MSTruchetFunc.QuadTree;
import org.jwildfire.create.tina.variation.MSTruchetFunc.Rectangle;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public abstract class AbstractBufferedImageWFFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	public static final String PARAM_SCALEX = "scale_x";
	public static final String PARAM_SCALEY = "scale_y";
	private static final String PARAM_SCALEZ = "scale_z";
	private static final String PARAM_OFFSETX = "offset_x";
	private static final String PARAM_OFFSETY = "offset_y";
	private static final String PARAM_OFFSETZ = "offset_z";
	private static final String PARAM_TILEX = "tile_x";
	private static final String PARAM_TILEY = "tile_y";
	private static final String PARAM_RESETZ = "reset_z";
	private static final String PARAM_DC_COLOR = "dc_color";
	private static final String PARAM_BLEND_COLORMAP = "blend_colormap";



	protected static final String[] paramNames = { PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_TILEX, PARAM_TILEY, PARAM_RESETZ};

	private double scaleX = 1.0;
	private double scaleY = 1.0;
	private double scaleZ = 0.0;
	private double offsetX = 0.0;
	private double offsetY = 0.0;
	private double offsetZ = 0.0;
	private int tileX = 0;
	private int tileY = 0;
	private int resetZ = 1;
	private int dc_color = 1;
	private int blend_colormap = 0;
	private String imageFilename = null;
	private byte[] inlinedImage = null;
	private String imageDescSrc = null;
	private String imageSrc = null;
	private int inlinedImageHash = 0;



	
	// derived params
	protected int imgWidth;
	protected int imgHeight;
	
	protected Pixel toolPixel = new Pixel();
	protected float[] rgbArray = new float[3];


	@Override
	public String[] getParameterNames() {
		return paramNames;
	}

	@Override
	public Object[] getParameterValues() {
		return new Object[] { scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, tileX, tileY, resetZ};
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_SCALEX.equalsIgnoreCase(pName))
			scaleX = pValue;
		else if (PARAM_SCALEY.equalsIgnoreCase(pName))
			scaleY = pValue;
		else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
			scaleZ = pValue;
		else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
			offsetX = pValue;
		else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
			offsetY = pValue;
		else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
			offsetZ = pValue;
		else if (PARAM_TILEX.equalsIgnoreCase(pName))
			tileX = Tools.FTOI(pValue);
		else if (PARAM_TILEY.equalsIgnoreCase(pName))
			tileY = Tools.FTOI(pValue);
		else if (PARAM_RESETZ.equalsIgnoreCase(pName))
			resetZ = Tools.FTOI(pValue);
/*		else if (PARAM_DC_COLOR.equalsIgnoreCase(pName))
			dc_color = Tools.FTOI(pValue);
		else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName))
			blend_colormap = Tools.FTOI(pValue);*/
		else
			throw new IllegalArgumentException(pName);
	}

	protected WFImage colorMap;
	protected RenderColor[] renderColors;
	protected Map<RenderColor, Double> colorIdxMap = new HashMap<RenderColor, Double>();

	protected void clearCurrColorMap() {
		colorMap = null;
		colorIdxMap.clear();
	}
	
	protected double getColorIdx(int pR, int pG, int pB) {
		RenderColor pColor = new RenderColor(pR, pG, pB);
		Double res = colorIdxMap.get(pColor);
		if (res == null) {

			int nearestIdx = 0;
			RenderColor color = renderColors[0];
			double dr, dg, db;
			dr = (color.red - pR);
			dg = (color.green - pG);
			db = (color.blue - pB);
			double nearestDist = sqrt(dr * dr + dg * dg + db * db);
			for (int i = 1; i < renderColors.length; i++) {
				color = renderColors[i];
				dr = (color.red - pR);
				dg = (color.green - pG);
				db = (color.blue - pB);
				double dist = sqrt(dr * dr + dg * dg + db * db);
				if (dist < nearestDist) {
					nearestDist = dist;
					nearestIdx = i;
				}
			}
			res = (double) nearestIdx / (double) (renderColors.length - 1);
			colorIdxMap.put(pColor, res);
		}
		return res;
	}
	
	 public void transformImage(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount, double pInputX, double pInputY) {
	        double x = (pInputX - (offsetX + 0.5) + 1.0) / scaleX * (double) (imgWidth - 2);
	        double y = (pInputY - (offsetY + 0.5) + 1.0) / scaleY * (double) (imgHeight - 2);
	        int ix, iy;
	        if (blend_colormap > 0) {
	            ix = (int) MathLib.trunc(x);
	            iy = (int) MathLib.trunc(y);
	        } else {
	            ix = Tools.FTOI(x);
	            iy = Tools.FTOI(y);
	        }
	        if (this.tileX == 1) {
	            if (ix < 0) {
	                int nx = ix / imgWidth - 1;
	                ix -= nx * imgWidth;
	            } else if (ix >= imgWidth) {
	                int nx = ix / imgWidth;
	                ix -= nx * imgWidth;
	            }
	        }
	        if (this.tileY == 1) {
	            if (iy < 0) {
	                int ny = iy / imgHeight - 1;
	                iy -= ny * imgHeight;
	            } else if (iy >= imgHeight) {
	                int ny = iy / imgHeight;
	                iy -= ny * imgHeight;
	            }
	        }

	        double r, g, b;
	        if (ix >= 0 && ix < imgWidth && iy >= 0 && iy < imgHeight) {
	            if (colorMap instanceof SimpleImage) {
	                if (blend_colormap > 0) {
	                    double iufrac = MathLib.frac(x);
	                    double ivfrac = MathLib.frac(y);
	                    toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
	                            ix, iy));
	                    int lur = toolPixel.r;
	                    int lug = toolPixel.g;
	                    int lub = toolPixel.b;
	                    toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
	                            ix + 1, iy));
	                    int rur = toolPixel.r;
	                    int rug = toolPixel.g;
	                    int rub = toolPixel.b;
	                    toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
	                            ix, iy + 1));
	                    int lbr = toolPixel.r;
	                    int lbg = toolPixel.g;
	                    int lbb = toolPixel.b;
	                    toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
	                            ix + 1, iy + 1));
	                    int rbr = toolPixel.r;
	                    int rbg = toolPixel.g;
	                    int rbb = toolPixel.b;
	                    r = GfxMathLib.blerp(lur, rur, lbr, rbr, iufrac, ivfrac);
	                    g = GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac);
	                    b = GfxMathLib.blerp(lub, rub, lbb, rbb, iufrac, ivfrac);
	                } else {
	                    toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
	                            ix, iy));
	                    r = toolPixel.r;
	                    g = toolPixel.g;
	                    b = toolPixel.b;
	                }
	                if (dc_color > 0) {
	                    pVarTP.rgbColor = true;
	                    pVarTP.redColor = r;
	                    pVarTP.greenColor = g;
	                    pVarTP.blueColor = b;
	                }
	            } else {
	                if (blend_colormap > 0) {
	                    double iufrac = MathLib.frac(x);
	                    double ivfrac = MathLib.frac(y);

	                    ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
	                    double lur = rgbArray[0];
	                    double lug = rgbArray[1];
	                    double lub = rgbArray[2];
	                    ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix + 1, iy);
	                    double rur = rgbArray[0];
	                    double rug = rgbArray[1];
	                    double rub = rgbArray[2];
	                    ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy + 1);
	                    double lbr = rgbArray[0];
	                    double lbg = rgbArray[1];
	                    double lbb = rgbArray[2];
	                    ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix + 1, iy + 1);
	                    double rbr = rgbArray[0];
	                    double rbg = rgbArray[1];
	                    double rbb = rgbArray[2];
	                    r = GfxMathLib.blerp(lur, rur, lbr, rbr, iufrac, ivfrac);
	                    g = GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac);
	                    b = GfxMathLib.blerp(lub, rub, lbb, rbb, iufrac, ivfrac);
	                } else {
	                    ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
	                    r = rgbArray[0];
	                    g = rgbArray[1];
	                    b = rgbArray[2];
	                }
	                if (dc_color > 0) {
	                    pVarTP.rgbColor = true;
	                    pVarTP.redColor = r;
	                    pVarTP.greenColor = g;
	                    pVarTP.blueColor = b;
	                }
	            }
	        } else {
	            r = g = b = 0.0;
	            if (dc_color > 0) {
	                pVarTP.rgbColor = true;
	                pVarTP.redColor = r;
	                pVarTP.greenColor = g;
	                pVarTP.blueColor = b;
	            }
	        }
	        double dz = this.offsetZ;
	        if (fabs(scaleZ) > EPSILON) {
	            double intensity = (0.299 * r + 0.588 * g + 0.113 * b) / 255.0;
	            dz += scaleZ * intensity;
	        }
	        if (resetZ != 0) {
	            pVarTP.z = dz;
	        } else {
	            pVarTP.z += dz;
	        }
	        if (dc_color > 0) {
	            pVarTP.color = getColorIdx(Tools.FTOI(r), Tools.FTOI(g), Tools.FTOI(b));
	        }
	    }

	    @Override
	    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
	        pVarTP.x += pAmount * (pContext.random() - 0.5);
	        pVarTP.y += pAmount * (pContext.random() - 0.5);

	        transformImage(pContext, pXForm, pAffineTP, pVarTP, pAmount, pVarTP.x, pVarTP.y);

	        if (pContext.isPreserveZCoordinate()) {
	            pVarTP.z += pAmount * pAffineTP.z;
	        }
	    }
	    
	    @Override
	    public void randomize() {
	      if (Math.random() < 0.5) {
	        scaleX = 1.0;
	        scaleY = 1.0;
	        scaleZ = 0.0;
	      } else {
	        scaleX = Math.random() * 1.8 + 0.2;
            scaleY = Math.random() * 1.8 + 0.2;
            scaleZ = Math.random();
	      }
          offsetX = Math.random();
          offsetY = Math.random();
          offsetZ = Math.random() * 2.0 - 1.0;
          tileX = (int) (Math.random() * 2);
          tileY = (int) (Math.random() * 2);
          resetZ = (int) (Math.random() * 2);
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

}

