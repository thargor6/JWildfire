/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

import java.util.List;

import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.render.AffineZStyle;

public class SubFlameWFFunc extends VariationFunc {
  private static final String RESSOURCE_FLAME = "flame";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String[] paramNames = { PARAM_A, PARAM_B, PARAM_C };
  private static final String[] ressourceNames = { RESSOURCE_FLAME };

  private double a = 0.0;
  private double b = 0.0;
  private double c = 0.0;

  private Flame flame;
  private XForm xf;
  private XYZPoint p;
  private XYZPoint q = new XYZPoint();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (xf != null) {
      xf = xf.getNextAppliedXFormTable()[pContext.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
      double parentColor = pAffineTP.color;
      xf.transformPoint(pContext, pAffineTP, pVarTP, p, p, AffineZStyle.FLAT);
      if (xf.getDrawMode() == DrawMode.HIDDEN)
        return;
      else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (pContext.random() > xf.getOpacity()))
        return;

      XForm finalXForm = flame.getFinalXForm();
      if (finalXForm != null) {
        finalXForm.transformPoint(pContext, pAffineTP, pVarTP, p, q, AffineZStyle.FLAT);
      }

      pVarTP.color += parentColor;
      while (pVarTP.color < 0.0)
        pVarTP.color += 1.0;
      while (pVarTP.color > 1.0)
        pVarTP.color -= 1.0;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { a, b, c };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "subflame_wf";
  }

  private void parseFlame() {
    flame = null;
    xf = null;
    p = null;
    try {
      List<Flame> flames = new Flam3Reader().readFlamesfromXML(flameXML);
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

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm) {
    if (flame != null) {
      flame.refreshModWeightTables(pContext);
      xf = flame.getXForms().get(0);
      p = new XYZPoint();
      p.x = 2.0 * pContext.random() - 1.0;
      p.y = 2.0 * pContext.random() - 1.0;
      p.z = 0.0;
      p.color = pContext.random();
    }
  }

  private String flameXML = "<flame name=\"JWildfire\" version=\"0.35 (15.01.2012)\" size=\"581 327\" center=\"0.0 0.0\" scale=\"63.5625\" rotate=\"0.0\" oversample=\"1\" color_oversample=\"1\" filter=\"1.0\" quality=\"50.0\" background=\"0.0 0.0 0.0\" brightness=\"4.0\" gamma=\"4.0\" gamma_threshold=\"0.04\" estimator_radius=\"9\" estimator_minimum=\"0\" estimator_curve=\"0.4\" temporal_samples=\"1.0\" cam_zoom=\"1.0\" cam_pitch=\"0.0\" cam_yaw=\"0.0\" cam_persp=\"0.0\" cam_zpos=\"0.0\" cam_dof=\"0.0\" shading_shading=\"FLAT\" >\r\n" +
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

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] { (flameXML != null ? flameXML.getBytes() : null) };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_FLAME.equalsIgnoreCase(pName)) {
      flameXML = pValue != null ? new String(pValue) : "";
      parseFlame();
    }
    else
      throw new IllegalArgumentException(pName);
  }

}
