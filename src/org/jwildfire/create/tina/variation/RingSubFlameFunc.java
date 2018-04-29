/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.max;
import static org.jwildfire.base.mathlib.MathLib.min;
import static org.jwildfire.base.mathlib.MathLib.pow;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqr;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.io.FlameReader;



public class RingSubFlameFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String RESSOURCE_FLAME = "flame";
  public static final String RESSOURCE_FLAME_FILENAME = "flame_filename";

  private static final String PARAM_HIDE = "hide_inner";
  
  private static final String PARAM_RADIUS = "innerradius";
  private static final String PARAM_CROPRADIUS = "outerradius";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_CONTRAST = "contrast";
  private static final String PARAM_POW = "pow";


  private static final String PARAM_SCATTER_AREA = "scatter_area";
  private static final String PARAM_ZERO = "hide_outer";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_ANGLE = "angle";
  
  public static final String PARAM_OFFSETX = "offset_x";
  public static final String PARAM_OFFSETY = "offset_y";
  public static final String PARAM_OFFSETZ = "offset_z";
  public static final String PARAM_COLORSCALE_Z = "colorscale_z";
  public static final String PARAM_COLOR_MODE = "color_mode";

  private static final String PARAM_FLAME_IS_SEQUENCE = "flame_is_sequence";
  private static final String PARAM_FLAME_SEQUENCE_START = "flame_sequence_start";
  private static final String PARAM_FLAME_SEQUENCE_DIGITS = "flame_sequence_digits";

  private static final String[] paramNames = { PARAM_HIDE, PARAM_RADIUS,PARAM_CROPRADIUS, PARAM_THICKNESS,  PARAM_CONTRAST, PARAM_POW, PARAM_SCATTER_AREA, PARAM_ZERO, PARAM_SCALE, PARAM_ANGLE,PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_COLORSCALE_Z, PARAM_COLOR_MODE, PARAM_FLAME_IS_SEQUENCE, PARAM_FLAME_SEQUENCE_START, PARAM_FLAME_SEQUENCE_DIGITS };
  private static final String[] ressourceNames = { RESSOURCE_FLAME, RESSOURCE_FLAME_FILENAME };

  

  private double angle=0.0;
  private double scale=1.0;
  
  private double cosa=cos(angle*M_PI/180.0);
  private double sina=sin(angle*M_PI/180.0);
  
  private int hide=0;
  private double oldx=0,oldy=0;
  
  private double radius = 0.5;
  private double thickness = 0.0;
  private double contrast = 0;
  private double pow = 1.0;

  private double cropradius = 1.0;
  private double scatter_area = 0.0;
  private int zero = 1;
  private double cA;
  
  private double offset_x = 0.0;
  private double offset_y = 0.0;
  private double offset_z = 0.0;
  private double colorscale_z = 0.0;
  private int flame_is_sequence = 0;
  private int flame_sequence_start = 1;
  private int flame_sequence_digits = 4;

  private final static int CM_OFF = -1;
  private final static int CM_DIRECT = 0;
  private final static int CM_RED = 1;
  private final static int CM_GREEN = 2;
  private final static int CM_BLUE = 3;
  private final static int CM_BRIGHTNESS = 4;

  private int color_mode = CM_OFF;

  private String flame_filename = null;

  private Flame flame;

  private XForm xf;
  private XYZPoint p;
  private XYZPoint q = new XYZPoint();
  private XYZPoint a = new XYZPoint();
  private XYZPoint v = new XYZPoint();

  private static class Point implements Serializable {
	    private static final long serialVersionUID = 1L;

	    private double x, y;
	  }
  
	  private void circle2(FlameTransformationContext pContext, Point p) {
		    //    double r = this.radius + this.thickness - this.Gamma * pContext.random();
		    double phi = 2.0 * M_PI * pContext.random();
		    double sinPhi = sin(phi);
		    double cosPhi = cos(phi);
		    double r;
		    if (pContext.random() < this._gamma) {
		      r = this._radius1;
		    }
		    else {
		      r = this._radius2;
		    }
		    p.x = r * cosPhi;
		    p.y = r * sinPhi;
		  }
	  
@Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    int iter = 0;
    final int MAX_ITER = 1000;
    XYZPoint pnew=new XYZPoint();
    double r,Alpha;
  
    if (p.isInfinite() || p.isNaN()) {
      prefuseIter(pContext);
    }

    while (xf != null) {
      if (++iter > MAX_ITER) {
        return;
      }
      xf = xf.getNextAppliedXFormTable()[pContext.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
      //      double parentColor = pAffineTP.color;
      a.clear();
      v.clear();
      xf.transformPoint(pContext, a, v, p, p);
      if (xf.getDrawMode() == DrawMode.HIDDEN)
        continue;
      else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (pContext.random() > xf.getOpacity()))
        continue;
      
      Layer layer = flame.getFirstLayer();
      List<XForm> finalXForms = layer.getFinalXForms();
      if (finalXForms.size() > 0) {
        finalXForms.get(0).transformPoint(pContext, pAffineTP, pnew, p, q);
        for (int i = 1; i < finalXForms.size(); i++) {
          finalXForms.get(i).transformPoint(pContext, pAffineTP, pnew, q, q);
        }
      }
      else {
        q.assign(p);
      }
      break;
    }

    if (!q.doHide) {
      pVarTP.doHide = false;
      pnew.x += scale*q.x + offset_x;
      pnew.y += scale*q.y + offset_y;
      pnew.z += scale*q.z + offset_z + colorscale_z * q.color;

      if (color_mode != CM_OFF) {
        if (q.rgbColor) {
          pVarTP.rgbColor = true;
          pVarTP.redColor = q.redColor;
          pVarTP.greenColor = q.greenColor;
          pVarTP.blueColor = q.blueColor;
        }
        switch (color_mode) {
          case CM_DIRECT:
            pVarTP.color = q.color;
            break;
          case CM_RED:
            pVarTP.color = pVarTP.redColor / 255.0;
            break;
          case CM_GREEN:
            pVarTP.color = pVarTP.greenColor / 255.0;
            break;
          case CM_BLUE:
            pVarTP.color = pVarTP.blueColor / 255.0;
            break;
          case CM_BRIGHTNESS:
            pVarTP.color = (0.2990 * pVarTP.redColor + 0.5880 * pVarTP.greenColor + 0.1130 * pVarTP.blueColor) / 255.0;
            break;
        }
      }
  //    pAffineTP.assign(pVarTP);  //pre_subflame_wfk
      r = sqrt(pnew.x * pnew.x + pnew.y * pnew.y);
      Alpha = this.radius / r;
     
      double x,y;
     
          x=pnew.x;
          y=pnew.y;
      
        pnew.x=  x*cosa - y*sina ;
        pnew.y=  x*sina + y*cosa ;
        pAffineTP.assign(pnew); 
          
           if (r < this._radius1 ) {
        	   if(hide==0)
        	   {
    	         circle2(pContext, toolPoint); // Draw/Hide the circle
       	         pVarTP.x += pAmount * toolPoint.x;
    	         pVarTP.y += pAmount * toolPoint.y;
        	   }
        	   else
        	   {
        	     pVarTP.x=oldx;
        	     pVarTP.y=oldy;
        	   }
    	  }
           else
           {   if (pContext.random() > this.contrast * pow(Alpha, this._absPow)) {
                pVarTP.x += pAmount * pnew.x;
                pVarTP.y += pAmount * pnew.y;

            }
             else {
               pVarTP.x += pAmount * Alpha * Alpha * pnew.x;
               pVarTP.y += pAmount * Alpha * Alpha * pnew.y;
             }
           }
	       oldx=pVarTP.x;
	       oldy=pVarTP.y;
	       
           double x0 = 0.0;
           double y0 = 0.0;
           double cr = cropradius;
           double ca = cA;
           double vv = pAmount;

           pVarTP.x -= x0;
           pVarTP.y -= y0;

           double rad = sqrt(pVarTP.x * pVarTP.x + pVarTP.y * pVarTP.y);
           double ang = atan2(pVarTP.y, pVarTP.x);
           double rdc = cr + (pContext.random() * 0.5 * ca);

           boolean esc = rad > cr;
           boolean cr0 = zero == 1;

           double s = sin(ang);
           double c = cos(ang);

           pVarTP.doHide = false;
           if (cr0 && esc) {
             pVarTP.x = pVarTP.y = 0;
             pVarTP.doHide = true;
           }
           else if (cr0 && !esc) {
             pVarTP.x = vv * pVarTP.x + x0;
             pVarTP.y = vv * pVarTP.y + y0;
           }
           else if (!cr0 && esc) {
             pVarTP.x = vv * rdc * c + x0;
             pVarTP.y = vv * rdc * s + y0;
           }
           else if (!cr0 && !esc) {
             pVarTP.x = vv * pVarTP.x + x0;
             pVarTP.y = vv * pVarTP.y + y0;
           }
           
      if (pContext.isPreserveZCoordinate()) {
    	  pVarTP.z += pAmount * pAffineTP.z;
   	  }
  	}
    else {
      pVarTP.doHide = true;
    }
 }


  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override 
  
  public Object[] getParameterValues() {
    return new Object[] {hide, radius, cropradius,thickness, contrast, pow, scatter_area, zero,scale,angle,offset_x, offset_y, offset_z, colorscale_z, color_mode, flame_is_sequence, flame_sequence_start, flame_sequence_digits };
  }

  @Override
  public void setParameter(String pName, double pValue) {
	  if (PARAM_HIDE.equalsIgnoreCase(pName))
		     hide = (int) limitVal(pValue, 0, 1);
  else if (PARAM_RADIUS.equalsIgnoreCase(pName))
	     radius = pValue;
	    else if (PARAM_CROPRADIUS.equalsIgnoreCase(pName))
	        cropradius = pValue;
	else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
	     thickness = limitVal(pValue, 0.0, 1.0);
	else if (PARAM_CONTRAST.equalsIgnoreCase(pName))
	     contrast = limitVal(pValue, 0.0, 1.0);
	else if (PARAM_POW.equalsIgnoreCase(pName))
	     pow = pValue;  

      else if (PARAM_SCATTER_AREA.equalsIgnoreCase(pName))
        scatter_area = pValue;
      else if (PARAM_ZERO.equalsIgnoreCase(pName))
        zero =  (int) limitVal(pValue, 0, 1);
      else if (PARAM_SCALE.equalsIgnoreCase(pName))
          scale =  pValue;
      else	if (PARAM_ANGLE.equalsIgnoreCase(pName))
		{ angle = pValue;
		  cosa=cos(angle*M_PI/180.0);
		  sina=sin(angle*M_PI/180.0);
		}
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offset_x = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offset_y = pValue;
    else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
      offset_z = pValue;
    else if (PARAM_COLORSCALE_Z.equalsIgnoreCase(pName))
      colorscale_z = pValue;
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName))
      color_mode = limitIntVal(Tools.FTOI(pValue), CM_DIRECT, CM_BRIGHTNESS);
    else if (PARAM_FLAME_IS_SEQUENCE.equalsIgnoreCase(pName))
      flame_is_sequence = Tools.FTOI(pValue);
    else if (PARAM_FLAME_SEQUENCE_START.equalsIgnoreCase(pName))
      flame_sequence_start = Tools.FTOI(pValue);
    else if (PARAM_FLAME_SEQUENCE_DIGITS.equalsIgnoreCase(pName))
      flame_sequence_digits = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "ringsubflame";
  }

  private void parseFlame(FlameTransformationContext pContext) {
    flame = null;
    xf = null;
    p = null;
    try {
      List<Flame> flames;
      String filename = getCurrFlameFilename(pContext);

      if (filename != null && !filename.isEmpty()) {
        if (!new File(filename).exists()) {
          throw new RuntimeException("Flame <" + filename + "> not found");
        }
        flames = new FlameReader(Prefs.getPrefs()).readFlames(filename);
      }
      else {
        flames = new FlameReader(Prefs.getPrefs()).readFlamesfromXML(flameXML);
      }

      if (flames.size() > 0) {
        flame = flames.get(0);
      }
    }
    catch (Throwable ex) {
      System.out.println("##############################################################");
      System.out.println(flameXML);
      System.out.println("##############################################################");
      throw new RuntimeException(ex);
    }
  }

  private Point toolPoint = new Point();
  private double _radius1, _radius2, _gamma, _absPow;
  
  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {


	    this._radius1 = this.radius + this.thickness;
	    this._radius2 = sqr(this.radius) / this._radius1;
	    this._gamma = this._radius1 / (this._radius1 + this._radius2);
	    this._absPow = fabs(this.pow);

	    cA = max(-1.0, min(scatter_area, 1.0));
	    
//	     cosa=cos(angle*M_PI/180.0);
//	     sina=sin(angle*M_PI/180.0);
	  parseFlame(pContext);
      prefuseIter(pContext);
  }


  
  private void prefuseIter(FlameTransformationContext pContext) {
    if (flame != null) {
      Layer layer = flame.getFirstLayer();
      layer.refreshModWeightTables(pContext);
      xf = layer.getXForms().get(0);
      p = new XYZPoint();
      p.x = pContext.random() - 0.5;
      p.y = pContext.random() - 0.5;
      p.z = 0.0;
      p.color = pContext.random();

      for (int i = 0; i < 42; i++) {
        xf = xf.getNextAppliedXFormTable()[pContext.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
        if (xf == null) {
          return;
        }
        a.clear();
        v.clear();
        xf.transformPoint(pContext, a, v, p, p);
      }
    }
  }
  
  public static final String DFLT_FLAME_XML = "<flame smooth_gradient=\"0\" version=\"JWildfire V3.30-JS (21.12.2017)\" size=\"1632 1232\" center=\"0.0 0.0\" scale=\"226.77066666666667\" rotate=\"0.0\" filter=\"0.75\" filter_type=\"ADAPTIVE\" filter_kernel=\"MITCHELL_SINEPOW\" filter_indicator=\"0\" filter_sharpness=\"4.0\" filter_low_density=\"0.025\" oversample=\"2\" post_noise_filter=\"0\" post_noise_filter_threshold=\"0.35\" quality=\"100.0\" background_type=\"GRADIENT_2X2_C\" background_ul=\"0.0 0.0 0.0\" background_ur=\"0.0 0.0 0.0\" background_ll=\"0.0 0.0 0.0\" background_lr=\"0.0 0.0 0.0\" background_cc=\"0.0 0.0 0.0\" bg_transparency=\"0\" brightness=\"4.0\" saturation=\"1.0\" gamma=\"4.0\" gamma_threshold=\"0.01\" vibrancy=\"1.0\" contrast=\"1.0\" white_level=\"220.0\" temporal_samples=\"1.0\" cam_zoom=\"1.0\" cam_pitch=\"0.0\" cam_yaw=\"0.0\" cam_persp=\"0.0\" cam_xfocus=\"0.0\" cam_yfocus=\"0.0\" cam_zfocus=\"0.0\" cam_pos_x=\"0.0\" cam_pos_y=\"0.0\" cam_pos_z=\"0.0\" cam_zpos=\"0.0\" cam_dof=\"0.0\" cam_dof_area=\"0.5\" cam_dof_exponent=\"2.0\" low_density_brightness=\"0.24\" balancing_red=\"1.0\" balancing_green=\"1.0\" balancing_blue=\"1.0\" cam_dof_shape=\"BUBBLE\" cam_dof_scale=\"1.0\" cam_dof_rotate=\"0.0\" cam_dof_fade=\"1.0\" antialias_amount=\"0.5\" antialias_radius=\"0.05\" post_symmetry_type=\"NONE\" post_symmetry_order=\"3\" post_symmetry_centre_x=\"0.0\" post_symmetry_centre_y=\"0.0\" post_symmetry_distance=\"1.25\" post_symmetry_rotation=\"6.0\" frame=\"1\" frame_count=\"300\" fps=\"30\" post_blur_radius=\"0\" post_blur_fade=\"0.95\" post_blur_falloff=\"2.0\" zbuffer_scale=\"1.0\" mixer_mode=\"OFF\">\r\n"+
  "  <xform weight=\"0.5\" color=\"0.0\" symmetry=\"0.0\" mirror_pre_post_translations=\"0\" material=\"0.0\" material_speed=\"0.0\" mod_gamma=\"0.0\" mod_gamma_speed=\"0.0\" mod_contrast=\"0.0\" mod_contrast_speed=\"0.0\" mod_saturation=\"0.0\" mod_saturation_speed=\"0.0\" mod_hue=\"0.0\" mod_hue_speed=\"0.0\" clifford_js=\"1.0\" clifford_js_fx_priority=\"0\" clifford_js_a=\"-1.7080000000000002\" clifford_js_b=\"2.12\" clifford_js_c=\"0.988\" clifford_js_d=\"1.468\" dc_hexes_wf=\"0.0\" dc_hexes_wf_fx_priority=\"0\" dc_hexes_wf_cellsize=\"0.45999999999999996\" dc_hexes_wf_power=\"1.0\" dc_hexes_wf_rotate=\"0.166\" dc_hexes_wf_scale=\"1.0\" dc_hexes_wf_color_scale=\"0.5\" dc_hexes_wf_color_offset=\"0.0\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" chaos=\"1.0\"/>\r\n"+
  "  <palette count=\"256\" format=\"RGB\">\r\n"+
  "F54F16ED4C18E5491ADD461DD4431FCC4021C43D23BC3A26B43728AC342AA3312C9B2E2E\r\n"+
  "932B318B28338325357B223772203A6A1D3C621A3E5A17405214434A1145410E47390B49\r\n"+
  "31084B29054E25074F27104F291A4F2B244F2D2D502F3750314050334A50355350375D50\r\n"+
  "3967503B70503D7A503F8351418D5143965145A05147AA5149B3514BBD514DC6514FD052\r\n"+
  "51D95253E35255ED5257F65259F2505AEB4D5CE44A5EDD4760D64462CF4164C83E66C13B\r\n"+
  "68BA386AB3356CAC326EA52F709E2C719729739026758923778220797B1D7B741A7D6D17\r\n"+
  "7F6614815F1183580E85510B864A08884406884106893F06893D06893B068939068A3606\r\n"+
  "8A34068A32068B30068B2E068B2C068B29068C27068C25068C23068C21068D1E068D1C06\r\n"+
  "8D1A068E18068E16068E13068E11068F0F068F0D068E0F068C13068A1706871C06852006\r\n"+
  "8325068129067F2E067D32067B3606793B06773F06744406724805704C056E51056C5505\r\n"+
  "6A5A05685E05666305646705616B055F70055D74055B7905597D055B79055D76055F7205\r\n"+
  "616F05646B056668056864056A61056C5D056E5A05705605725305744F06774C06794806\r\n"+
  "7B45067D41067F3E06813A068337068533068730068A2C068C29068E25068D2306892406\r\n"+
  "8524068025057C25057826057426056F26056B27056727056228045E28045A2904552904\r\n"+
  "512A044D2A04492B03442B03402C033C2C03372D03332D032F2E032A2E02262E02222F02\r\n"+
  "2535032B3D04304505354D063B5407405C0945640A4B6C0B50730C557B0D5B830E608B0F\r\n"+
  "6693116B9A1270A21376AA147BB21580B91686C1178BC91890D11A96D81B9BE01CA0E81D\r\n"+
  "A6F01EAAF620ACF623AEF626AFF629B1F62BB3F72EB4F731B6F734B8F737B9F73ABBF73D\r\n"+
  "BDF740BEF743C0F846C2F849C4F84CC5F84FC7F852C9F855CAF858CCF85BCEF85ECFF961\r\n"+
  "D1F964D3F967D4F96AD6F76ED7F474D9F179DAEE7FDCEB85DDE88ADFE490E0E195E2DE9B\r\n"+
  "E3DBA0E5D8A6E6D5ABE8D2B1E9CFB6EBCBBCECC8C2EEC5C7EFC2CDF1BFD2F2BCD8F4B9DD\r\n"+
  "F5B6E3F7B2E8F8AFEEFAACF3  </palette>"+
  "</flame>\r\n"+ "";
  
  private String flameXML = DFLT_FLAME_XML;

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] { (flameXML != null ? flameXML.getBytes() : null), (flame_filename != null ? flame_filename.getBytes() : null) };
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_FLAME_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.FLAME_FILENAME;
    }
    else if (RESSOURCE_FLAME.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_FLAME.equalsIgnoreCase(pName)) {
      flameXML = pValue != null ? new String(pValue) : "";
    }
    else if (RESSOURCE_FLAME_FILENAME.equalsIgnoreCase(pName)) {
      flame_filename = pValue != null ? new String(pValue) : "";
    }
    else
      throw new IllegalArgumentException(pName);
  }

  private String getCurrFlameFilename(FlameTransformationContext pContext) {
    if (flame_is_sequence > 0 && flame_filename != null && !flame_filename.isEmpty()) {
      int frame = pContext.getFrame() - 1 + flame_sequence_start;
      String baseFilename;
      String fileExt;
      int p = flame_filename.lastIndexOf(".");
      if (p < 0 || p <= flame_sequence_digits || p == flame_filename.length() - 1)
        return flame_filename;
      baseFilename = flame_filename.substring(0, p - flame_sequence_digits);
      fileExt = flame_filename.substring(p, flame_filename.length());

      String number = String.valueOf(frame);
      while (number.length() < flame_sequence_digits) {
        number = "0" + number;
      }
      return baseFilename + number + fileExt;

    }
    else {
      return flame_filename;
    }
  }

}
