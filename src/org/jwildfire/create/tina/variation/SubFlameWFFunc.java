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

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.*;
import org.jwildfire.create.tina.io.FlameReader;

import java.io.File;
import java.util.List;

import static org.jwildfire.base.mathlib.MathLib.*;

public class SubFlameWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String RESSOURCE_FLAME = "flame";
  public static final String RESSOURCE_FLAME_FILENAME = "flame_filename";

  public static final String PARAM_SCALE = "scale";
  public static final String PARAM_ANGLE = "angle";
  public static final String PARAM_OFFSETX = "offset_x";
  public static final String PARAM_OFFSETY = "offset_y";
  public static final String PARAM_OFFSETZ = "offset_z";
  public static final String PARAM_COLORSCALE_Z = "colorscale_z";
  public static final String PARAM_COLOR_MODE = "color_mode";

  protected static final String PARAM_FLAME_IS_SEQUENCE = "flame_is_sequence";
  protected static final String PARAM_FLAME_SEQUENCE_START = "flame_sequence_start";
  protected static final String PARAM_FLAME_SEQUENCE_END = "flame_sequence_end";
  protected static final String PARAM_FLAME_SEQUENCE_REPEAT = "flame_sequence_repeat";
  protected static final String PARAM_FLAME_SEQUENCE_DIGITS = "flame_sequence_digits";

  protected static final String[] paramNames = {PARAM_SCALE, PARAM_ANGLE, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ,
          PARAM_COLORSCALE_Z, PARAM_COLOR_MODE, PARAM_FLAME_IS_SEQUENCE,
          PARAM_FLAME_SEQUENCE_START, PARAM_FLAME_SEQUENCE_END, PARAM_FLAME_SEQUENCE_REPEAT, PARAM_FLAME_SEQUENCE_DIGITS};
  protected static final String[] ressourceNames = {RESSOURCE_FLAME, RESSOURCE_FLAME_FILENAME};

  protected double angle = 0.0;
  protected double scale = 1.0;
  protected double cosa = cos(angle * M_PI / 180.0);
  protected double sina = sin(angle * M_PI / 180.0);
  protected double offset_x = 0.0;
  protected double offset_y = 0.0;
  protected double offset_z = 0.0;
  protected double colorscale_z = 0.0;

  protected final static int SEQ_OFF = 0;
  protected final static int SEQ_FILES = 1;
  protected final static int SEQ_CURVE = 2;

  protected int flame_is_sequence = 0;
  protected int flame_sequence_start = 1;
  protected int flame_sequence_end = 0;
  protected int flame_sequence_repeat = 1;
  protected int flame_sequence_digits = 4;

  protected final static int CM_FLAME = -2;
  protected final static int CM_OFF = -1;
  protected final static int CM_DIRECT = 0;
  protected final static int CM_RED = 1;
  protected final static int CM_GREEN = 2;
  protected final static int CM_BLUE = 3;
  protected final static int CM_BRIGHTNESS = 4;

  protected int color_mode = CM_OFF;

  protected String flame_filename = null;

  protected Flame flame;

  protected XForm xf;
  protected XYZPoint p;
  protected XYZPoint q = new XYZPoint();
  protected XYZPoint a = new XYZPoint();
  protected XYZPoint v = new XYZPoint();
  protected FlameTransformationContext sfContext;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    subflameIter(pContext);
    pVarTP.x += q.x;
    pVarTP.y += q.y;
    pVarTP.z += q.z;

    setColor(pVarTP);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{scale, angle, offset_x, offset_y, offset_z, colorscale_z, color_mode,
            flame_is_sequence, flame_sequence_start, flame_sequence_end, flame_sequence_repeat, flame_sequence_digits};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offset_x = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offset_y = pValue;
    else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
      offset_z = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_ANGLE.equalsIgnoreCase(pName)) {
      angle = pValue;
      cosa = cos(angle * M_PI / 180.0);
      sina = sin(angle * M_PI / 180.0);
    } else if (PARAM_COLORSCALE_Z.equalsIgnoreCase(pName))
      colorscale_z = pValue;
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName))
      color_mode = limitIntVal(Tools.FTOI(pValue), CM_FLAME, CM_BRIGHTNESS);
    else if (PARAM_FLAME_IS_SEQUENCE.equalsIgnoreCase(pName))
      flame_is_sequence = limitIntVal(Tools.FTOI(pValue), SEQ_OFF, SEQ_CURVE);
    else if (PARAM_FLAME_SEQUENCE_START.equalsIgnoreCase(pName))
      flame_sequence_start = Tools.FTOI(pValue);
    else if (PARAM_FLAME_SEQUENCE_END.equalsIgnoreCase(pName))
      flame_sequence_end = Tools.FTOI(pValue);
    else if (PARAM_FLAME_SEQUENCE_REPEAT.equalsIgnoreCase(pName))
      flame_sequence_repeat = Tools.FTOI(pValue);
    else if (PARAM_FLAME_SEQUENCE_DIGITS.equalsIgnoreCase(pName))
      flame_sequence_digits = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "subflame_wf";
  }

  protected void parseFlame(FlameTransformationContext pContext) {
    flame = null;
    xf = null;
    p = null;
    try {
      List<Flame> flames;
      String filename = getCurrFlameFilename(pContext);

      if (filename != null && !filename.isEmpty() && new File(filename).exists()) {
        flames = new FlameReader(Prefs.getPrefs()).readFlames(filename);
      } else {
        flames = new FlameReader(Prefs.getPrefs()).readFlamesfromXML(flameXML);
      }

      if (flames.size() > 0) {
        flame = flames.get(0);
      }
    } catch (Throwable ex) {
      System.out.println("##############################################################");
      System.out.println(ex.getMessage());
      System.out.println("##############################################################");
      flame = null;
    }
  }

  int frame;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    int seq_start = flame_sequence_start < 1 ? 1 : flame_sequence_start;
    int seq_end = flame_sequence_end <= 0 ? 0 : flame_sequence_end < seq_start ? seq_start : flame_sequence_end;
    int seq_repeat = flame_sequence_repeat < 1 || flame_sequence_repeat > seq_end ? seq_end : flame_sequence_repeat;
    int seq_period = seq_end - seq_repeat + 1;
    frame = pContext.getFrame() + flame_sequence_start - 1;
    if (seq_end > 0 && frame > seq_end) frame = (frame - seq_repeat) % seq_period + seq_repeat;

    parseFlame(pContext);
    int time = flame_is_sequence == SEQ_CURVE ? frame : flame.getFrame() > 0 ? flame.getFrame() : 0;
    flame = AnimationService.evalMotionCurves(flame.makeCopy(), time);
    sfContext = new FlameTransformationContext(pContext.getFlameRenderer(), pContext.getRandGen(), 0, time);
    sfContext.setPreserveZCoordinate(flame.isPreserveZ());
    prefuseIter(pContext);
  }

  protected void prefuseIter(FlameTransformationContext pContext) {
    if (flame != null) {
      Layer layer = flame.getFirstLayer();
      // a compatibility quirk: default for subflame final xforms is DIFFUSION
      for (XForm xForm: layer.getFinalXForms()) {
        if (xForm.getColorType() == ColorType.UNSET) xForm.setColorType(ColorType.DIFFUSION);
      }
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
        xf.transformPoint(sfContext, a, v, p, p);
      }
    }
  }

  protected void subflameIter(FlameTransformationContext pContext) {
    int iter = 0;
    final int MAX_ITER = 1000;

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
      a.clear();
      v.clear();
      xf.transformPoint(sfContext, a, v, p, p);
      if (xf.getDrawMode() == DrawMode.HIDDEN)
        continue;
      else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (pContext.random() > xf.getOpacity()))
        continue;

      Layer layer = flame.getFirstLayer();
      List<XForm> finalXForms = layer.getFinalXForms();
      if (finalXForms.size() > 0) {
        finalXForms.get(0).transformPoint(sfContext, a, v, p, q);
        for (int i = 1; i < finalXForms.size(); i++) {
          finalXForms.get(i).transformPoint(sfContext, a, v, q, q);
        }
      } else {
        q.assign(p);
      }
      break;
    }

    double x = scale * q.x;
    double y = scale * q.y;
    q.x = x * cosa - y * sina + offset_x;
    q.y = x * sina + y * cosa + offset_y;
    q.z = scale * q.z + offset_z + colorscale_z * q.color;

  }

  protected void setColor(XYZPoint pVarTP) {
    if (!q.doHide) {
      pVarTP.doHide = false;
      if (color_mode != CM_OFF) {
        if (q.rgbColor || color_mode == CM_FLAME) {
          pVarTP.rgbColor = true;
          pVarTP.redColor = q.redColor;
          pVarTP.greenColor = q.greenColor;
          pVarTP.blueColor = q.blueColor;
          pVarTP.color = q.color;
        }
        switch (color_mode) {
          case CM_DIRECT:
            pVarTP.color = q.color;
            break;
          case CM_RED:
            pVarTP.color = q.redColor / 255.0;
            break;
          case CM_GREEN:
            pVarTP.color = q.greenColor / 255.0;
            break;
          case CM_BLUE:
            pVarTP.color = q.blueColor / 255.0;
            break;
          case CM_BRIGHTNESS:
            pVarTP.color = (0.2990 * q.redColor + 0.5880 * q.greenColor + 0.1130 * q.blueColor) / 255.0;
            break;
        }
      }
    } else {
      pVarTP.doHide = true;
    }
  }

  public static final String DFLT_FLAME_XML = "<flame name=\"JWildfire\" version=\"0.35 (15.01.2012)\" size=\"581 327\" center=\"0.0 0.0\" scale=\"63.5625\" rotate=\"0.0\" oversample=\"1\" color_oversample=\"1\" filter=\"1.0\" quality=\"50.0\" background=\"0.0 0.0 0.0\" brightness=\"4.0\" gamma=\"4.0\" gamma_threshold=\"0.04\" estimator_radius=\"9\" estimator_minimum=\"0\" estimator_curve=\"0.4\" temporal_samples=\"1.0\" cam_zoom=\"1.0\" cam_pitch=\"0.0\" cam_yaw=\"0.0\" cam_persp=\"0.0\" cam_zpos=\"0.0\" cam_dof=\"0.0\" shading_shading=\"FLAT\" >\r\n" +
          "  <xform weight=\"37.974195875650885\" color=\"0.0\" symmetry=\"0.6363142683575415\" waves2_wf=\"1.0\" waves2_wf_scalex=\"0.05411632642405888\" waves2_wf_scaley=\"0.07140430473672771\" waves2_wf_freqx=\"5.665411884739101\" waves2_wf_freqy=\"3.5622214535317194\" waves2_wf_use_cos_x=\"0\" waves2_wf_use_cos_y=\"0\" waves2_wf_dampx=\"0.0\" waves2_wf_dampy=\"-0.0749313500620006\" popcorn2=\"1.1747945422649702E-4\" popcorn2_x=\"1.0\" popcorn2_y=\"0.5\" popcorn2_c=\"1.5\" coefs=\"0.29869999951569876 0.9193040710637221 -0.9193040710637221 0.29869999951569876 -1.6842788839099072 2.0224216083110305\" chaos=\"1.0 1.0 1.0\" />\r\n" +
          "  <xform weight=\"0.5\" color=\"0.0\" symmetry=\"-1.0\" spherical3D=\"0.43660175471191676\" coefs=\"1.0 0.0 0.0 1.0 0.0 0.0\" chaos=\"1.0 1.0 1.0\" />\r\n" +
          "  <xform weight=\"0.5\" color=\"0.0\" symmetry=\"-1.0\" linear3D=\"1.0\" coefs=\"0.9321767990927353 -0.36200333594211836 0.36200333594211836 0.9321767990927353 -0.1636856703682093 0.5528632251910492\" chaos=\"1.0 1.0 1.0\" />\r\n" +
          "  <palette count=\"256\" format=\"RGB\" >\r\n" +
          "D45334CF5635CA5936C55C38C05F39BC623AB7653BB2683DAD6B3EA86E3FA371409E7442\r\n" +
          "997743947A448F7D458B80478683488186497C894A778C4C728F4D6D924E68944F639751\r\n" +
          "5E9A525A9D5355A05450A3564BA65746A95841AC593CAF5B37B25C32B55D2DB85E29BB5F\r\n" +
          "24BE611FC1621AC46315C76410CA660BCD6706D06804D26A07D16C0AD06E0DCF710FCE73\r\n" +
          "12CD7615CC7818CB7A1BCA7D1EC97F21C88124C78427C68629C5882CC48B2FC38D32C28F\r\n" +
          "35C19238C0943BBF963EBE9941BE9B43BD9D46BCA049BBA24CBAA44FB9A752B8A955B7AB\r\n" +
          "58B6AE5AB5B05DB4B260B3B563B2B766B1B969B0BC6CAFBE6FAEC172ADC374ACC577ABC8\r\n" +
          "7AAACA7DA9CC80A9CD82A9CD85A8CD87A8CD8AA8CD8DA7CD8FA7CD92A7CD94A7CD97A6CD\r\n" +
          "9AA6CD9CA6CD9FA5CDA1A5CDA4A5CDA6A5CDA9A4CDACA4CDAEA4CDB1A3CDB3A3CDB6A3CC\r\n" +
          "B8A3CCBBA2CCBEA2CCC0A2CCC3A2CCC5A1CCC8A1CCCAA1CCCDA0CCD0A0CCD2A0CCD5A0CC\r\n" +
          "D79FCCDA9FCCDD9FCCDF9ECCE29ECCE49ECCE79ECCE99DCCEC9DCCEA9BC8E798C4E596C0\r\n" +
          "E394BCE091B7DE8FB3DB8DAFD98AABD788A7D486A3D2839FD0819BCD7F97CB7C93C87A8E\r\n" +
          "C6788AC47586C17382BF707EBD6E7ABA6C76B86972B6676EB3656AB16265AE6061AC5E5D\r\n" +
          "AA5B59A75955A55751A3544DA052499E50459C4D41994B3C97493894463492443090422C\r\n" +
          "8D3F288B3D24893B2087391E89381F8A37218B36238D35258E34268F332891322A92312B\r\n" +
          "93302D952F2F962E31972D32992D349A2C369B2B389C2A399E293B9F283DA0273FA22640\r\n" +
          "A32542A42444A62345A72247A82149AA204BAB1F4CAC1E4EAE1E50AF1D52B01C53B11B55\r\n" +
          "B31A57B41959B5185AB7175CB8165EB9155FBB1461BC1363BD1265BF1166BE1166BC1066\r\n" +
          "BA1065B71064B50F63B30F63B10F62AF0E61AD0E60AB0E60A90E5FA70D5EA50D5DA30D5C\r\n" +
          "A10C5C9F0C5B9D0C5A9B0B59990B59960B58940A57920A56900A558E09558C09548A0953\r\n" +
          "88085286085284085182075080074F7E074F7C064E7A064D78064C75054B73054B71054A\r\n" +
          "6F04496D04486B0448690347</palette>\r\n" +
          "</flame>\r\n" +
          "";

  protected String flameXML = DFLT_FLAME_XML;

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(flameXML != null ? flameXML.getBytes() : null), (flame_is_sequence == SEQ_FILES && flame_filename != null ? flame_filename.getBytes() : null)};
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_FLAME_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.FLAME_FILENAME;
    } else if (RESSOURCE_FLAME.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_FLAME.equalsIgnoreCase(pName)) {
      flameXML = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_FLAME_FILENAME.equalsIgnoreCase(pName)) {
      flame_filename = pValue != null ? new String(pValue) : "";
      if (!flame_filename.isEmpty()) {
        try {
          flameXML = Tools.readUTF8Textfile(flame_filename);
        }
        catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
     } else
      throw new IllegalArgumentException(pName);
  }

  protected String getCurrFlameFilename(FlameTransformationContext pContext) {
    if (flame_is_sequence == SEQ_FILES && flame_filename != null && !flame_filename.isEmpty()) {
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

    } else {
      return null;
    }
  }
  
  /* Override makeCopy to avoid raising exception if specified file doesn't exist when copy is made */

  public SubFlameWFFunc makeCopy() {
    SubFlameWFFunc varCopy = (SubFlameWFFunc) VariationFuncList.getVariationFuncInstance(this.getName());
    // params (safe to copy automatically)
    String[] paramNames = this.getParameterNames();
    if (paramNames != null) {
      for (int i = 0; i < paramNames.length; i++) {
        Object val = this.getParameterValues()[i];
        if (val instanceof Number) {
          varCopy.setParameter(paramNames[i], ((Number) val).doubleValue());
        } else {
          throw new IllegalStateException();
        }
      }
    }
    // ressources (copy manually; don't try to read file)
    varCopy.flameXML = flameXML;
    varCopy.flame_filename = flame_filename;

    return varCopy;
  }

}
