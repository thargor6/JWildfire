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

/*************************************************************************************************
 * @author Jesus Sosa
 * @date April 13, 2018
 *
 * Variation: terrain3D
 *
 * Adapted from Java code found in
 * http://www.cs.ukzn.ac.za/~hughm/ds/ls/FractalLandscapes.html
 *
 *
 * Also I´m using SimpleMesh.java object by Andreas Maschke
 * included in source code of Java WildFire.
 *************************************************************************************************/

package org.jwildfire.create.tina.variation.mesh;


import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceManager;

import java.util.Random;


public class FracTerrain3DFunc extends AbstractOBJMeshWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ROUGHNESS = "roughness";
  private static final String PARAM_EXAGGERATION = "z_exaggeration";
  private static final String PARAM_N = "Size (N PowersOf 2)";
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_DC = "dc";

  public static final String PARAM_SCALEX = "scale_x";
  public static final String PARAM_SCALEY = "scale_y";
  protected static final String PARAM_SCALEZ = "scale_z";
  protected static final String PARAM_OFFSETX = "offset_x";
  protected static final String PARAM_OFFSETY = "offset_y";
  protected static final String PARAM_OFFSETZ = "offset_z";


  protected static final String PARAM_SUBDIV_LEVEL = "subdiv_level";
  protected static final String PARAM_SUBDIV_SMOOTH_PASSES = "subdiv_smooth_passes";
  protected static final String PARAM_SUBDIV_SMOOTH_LAMBDA = "subdiv_smooth_lambda";
  protected static final String PARAM_SUBDIV_SMOOTH_MU = "subdiv_smooth_mu";

  protected static final String PARAM_BLEND_COLORMAP = "blend_colormap";
  protected static final String PARAM_DISPL_AMOUNT = "displ_amount";
  protected static final String PARAM_BLEND_DISPLMAP = "blend_displ_map";

  protected static final String PARAM_RECEIVE_ONLY_SHADOWS = "receive_only_shadows";


  private static final String[] paramNames = {PARAM_ROUGHNESS, PARAM_EXAGGERATION, PARAM_N, PARAM_SEED, PARAM_DC, PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_SUBDIV_LEVEL, PARAM_SUBDIV_SMOOTH_PASSES, PARAM_SUBDIV_SMOOTH_LAMBDA, PARAM_SUBDIV_SMOOTH_MU, PARAM_BLEND_COLORMAP, PARAM_DISPL_AMOUNT, PARAM_BLEND_DISPLMAP, PARAM_RECEIVE_ONLY_SHADOWS};


  double roughness = 0.5;
  double exaggeration = 0.7;
  int n = 5;
  private int seed = (int) (1000 * Math.random());
  private int dc = 1;

  static public class FractalTerrain {
    private double[][] terrain;
    private double min, max;
    private int divisions;
    private Random rng;
    int lod;


    SimpleMesh mesh = new SimpleMesh();

    public FractalTerrain(int lod, double roughness, double seed) {
      this.lod = lod;
//	    this.roughness = roughness;
      this.divisions = 1 << lod;
      terrain = new double[divisions + 1][divisions + 1];
//	    rng = new Random ();

      rng = new Random((long) seed);
      terrain[0][0] = rnd();
      terrain[0][divisions] = rnd();
      terrain[divisions][divisions] = rnd();
      terrain[divisions][0] = rnd();
      double rough = roughness;
      for (int i = 0; i < lod; ++i) {
        int q = 1 << i, r = 1 << (lod - i), s = r >> 1;
        for (int j = 0; j < divisions; j += r)
          for (int k = 0; k < divisions; k += r)
            diamond(j, k, r, rough);
        if (s > 0)
          for (int j = 0; j <= divisions; j += s)
            for (int k = (j + s) % r; k <= divisions; k += r)
              square(j - s, k - s, r, rough);
        rough *= roughness;
      }
      min = max = terrain[0][0];
      for (int i = 0; i <= divisions; ++i)
        for (int j = 0; j <= divisions; ++j)
          if (terrain[i][j] < min) min = terrain[i][j];
          else if (terrain[i][j] > max) max = terrain[i][j];
    }

    private void diamond(int x, int y, int side, double scale) {
      if (side > 1) {
        int half = side / 2;
        double avg = (terrain[x][y] + terrain[x + side][y] +
                terrain[x + side][y + side] + terrain[x][y + side]) * 0.25;
        terrain[x + half][y + half] = avg + rnd() * scale;
      }
    }

    private void square(int x, int y, int side, double scale) {
      int half = side / 2;
      double avg = 0.0, sum = 0.0;
      if (x >= 0) {
        avg += terrain[x][y + half];
        sum += 1.0;
      }
      if (y >= 0) {
        avg += terrain[x + half][y];
        sum += 1.0;
      }
      if (x + side <= divisions) {
        avg += terrain[x + side][y + half];
        sum += 1.0;
      }
      if (y + side <= divisions) {
        avg += terrain[x + half][y + side];
        sum += 1.0;
      }
      terrain[x + half][y + half] = avg / sum + rnd() * scale;
    }

    private double rnd() {
      return 2. * rng.nextDouble() - 1.0;
    }

    public double getAltitude(double i, double j) {
      double alt = terrain[(int) (i * divisions)][(int) (j * divisions)];
      return (alt - min) / (max - min);
    }

    public void setTerrainData(double exaggeration) {
//	   double exaggeration = .7;
      int steps = 1 << lod;

      for (int i = 0; i <= steps; ++i) {
        for (int j = 0; j <= steps; ++j) {
          double x = 1.0 * i / steps, z = 1.0 * j / steps;
          double altitude = getAltitude(x, z);
          mesh.addVertex(x - 0.5, altitude * exaggeration, z - 0.5);
        }
      }
      for (int i = 0; i < steps; ++i) {
        for (int j = 0; j < steps; ++j) {
          int v1 = (steps + 1) * i + j;
          int v2 = (steps + 1) * (i + 1) + j;
          int v3 = (steps + 1) * i + j + 1;
          mesh.addFace(v1, v2, v3);
          v1 = (steps + 1) * (i + 1) + j;
          v2 = (steps + 1) * (i + 1) + j + 1;
          v3 = (steps + 1) * i + j + 1;
          mesh.addFace(v1, v2, v3);
        }
      }

    }

    public SimpleMesh getMesh() {
      return this.mesh;
    }

  }


  @Override
  public Object[] getParameterValues() {
    return new Object[]{roughness, exaggeration, n, seed, dc, scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, subdiv_level, subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu, colorMapHolder.getBlend_colormap(), displacementMapHolder.getDispl_amount(), displacementMapHolder.getBlend_displ_map(), receive_only_shadows};
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ROUGHNESS.equalsIgnoreCase(pName)) {
      roughness = pValue;
    } else if (PARAM_EXAGGERATION.equalsIgnoreCase(pName)) {
      exaggeration = pValue;

    } else if (PARAM_N.equalsIgnoreCase(pName)) {
      n = (int) Tools.limitValue(pValue, 1, 8);
    } else if (PARAM_SEED.equalsIgnoreCase(pName)) {
      seed = (int) Tools.FTOI(pValue);

    } else if (PARAM_DC.equalsIgnoreCase(pName)) {
      dc = (int) Tools.limitValue(pValue, 0, 1);
    } else if (PARAM_SCALEX.equalsIgnoreCase(pName))
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
    else if (PARAM_SUBDIV_LEVEL.equalsIgnoreCase(pName))
      subdiv_level = limitIntVal(Tools.FTOI(pValue), 0, 6);
    else if (PARAM_SUBDIV_SMOOTH_PASSES.equalsIgnoreCase(pName))
      subdiv_smooth_passes = limitIntVal(Tools.FTOI(pValue), 0, 24);
    else if (PARAM_SUBDIV_SMOOTH_LAMBDA.equalsIgnoreCase(pName))
      subdiv_smooth_lambda = pValue;
    else if (PARAM_SUBDIV_SMOOTH_MU.equalsIgnoreCase(pName))
      subdiv_smooth_mu = pValue;
    else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName))
      colorMapHolder.setBlend_colormap(limitIntVal(Tools.FTOI(pValue), 0, 1));
    else if (PARAM_DISPL_AMOUNT.equalsIgnoreCase(pName))
      displacementMapHolder.setDispl_amount(pValue);
    else if (PARAM_BLEND_DISPLMAP.equalsIgnoreCase(pName))
      displacementMapHolder.setBlend_displ_map(limitIntVal(Tools.FTOI(pValue), 0, 1));
    else if (PARAM_RECEIVE_ONLY_SHADOWS.equalsIgnoreCase(pName))
      receive_only_shadows = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "terrain3D";
  }

  private String calcMeshKey() {
    return n + "#" + roughness + "#" + seed + "#" + exaggeration + "#" + subdiv_level + "#" + subdiv_smooth_passes + "#" + subdiv_smooth_lambda + "#" + subdiv_smooth_mu;
  }

  @Override
  public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.initOnce(pContext, pLayer, pXForm, pAmount);
    String key = calcMeshKey();
    mesh = (SimpleMesh) RessourceManager.getRessource(key);
    if (mesh == null) {
      mesh = null;
      FractalTerrain fterrain = new FractalTerrain(n, roughness, seed);
      fterrain.setTerrainData(exaggeration);
      mesh = fterrain.getMesh();
      for (int i = 0; i < Math.min(2, subdiv_level); i++) {
        mesh = mesh.interpolate();
        mesh.taubinSmooth(subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu);
      }
      RessourceManager.putRessource(key, mesh);
    }
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    mesh = (SimpleMesh) RessourceManager.getRessource(calcMeshKey());
  }


  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    super.transform(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    if (dc == 1)
      pVarTP.color = MathLib.fmod(pVarTP.y, 1.0);
  }
}
