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
package org.jwildfire.create.tina.variation.mesh;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.create.tina.variation.RessourceType;

import java.io.File;
import java.io.InputStream;

public class OBJMeshPrimitiveWFFunc extends AbstractOBJMeshWFFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_PRIMITIVE = "primitive";

  private static final String[] paramNames = {PARAM_PRIMITIVE, PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_SUBDIV_LEVEL, PARAM_SUBDIV_SMOOTH_PASSES, PARAM_SUBDIV_SMOOTH_LAMBDA, PARAM_SUBDIV_SMOOTH_MU, PARAM_BLEND_COLORMAP, PARAM_DISPL_AMOUNT, PARAM_BLEND_DISPLMAP, PARAM_RECEIVE_ONLY_SHADOWS};

  private int primitive = 0;

  public static final int LOWPOLY_SHAPE_COUNT = 14;

  private String primitives[] = {"ball", "capsule", "cone", "diamond", "torus", "box", "gear15", "icosahedron", "tetrahedron",
          "octahedron", "dodecahedron", "wedge", "icosidodecahedron", "cubeoctahedron", "gears6a", "gears6s", "gears8a", "gears8s",
          "gears12a", "gears12s", "gears16a", "gears16s", "gears24a", "gears24s", "mandelbulb", "drop"};

  private static final String[] ressourceNames = {RESSOURCE_COLORMAP_FILENAME, RESSOURCE_DISPL_MAP_FILENAME};

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{primitive, scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, subdiv_level, subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu, colorMapHolder.getBlend_colormap(), displacementMapHolder.getDispl_amount(), displacementMapHolder.getBlend_displ_map(), receive_only_shadows};
  }

  @Override
  public String getName() {
    return "obj_mesh_primitive_wf";
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRIMITIVE.equalsIgnoreCase(pName))
      primitive = Tools.FTOI(pValue);
    else
      super.setParameter(pName, pValue);
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    String meshName = primitive >= 0 && primitive < primitives.length ? primitives[primitive] : null;

    if (meshName != null && meshName.length() > 0) {
      try {
        String meshKey = this.getClass().getName() + "_" + OBJMeshUtil.getMeshname(meshName, subdiv_level, subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu);
        mesh = (SimpleMesh) RessourceManager.getRessource(meshKey);
        if (mesh == null) {
          String resourceObj = meshName + ".obj";
          InputStream is = this.getClass().getResourceAsStream(resourceObj);
          File objFile = OBJMeshUtil.obj2file(is);
          try {
            mesh = OBJMeshUtil.loadAndSmoothMeshFromFile(objFile.getAbsolutePath(), subdiv_smooth_passes, subdiv_level, subdiv_smooth_lambda, subdiv_smooth_mu);
          } finally {
            objFile.delete();
          }
          RessourceManager.putRessource(meshKey, mesh);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (mesh == null) {
      mesh = OBJMeshUtil.createDfltMesh();
    }
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(colorMapHolder.getColormap_filename() != null ? colorMapHolder.getColormap_filename().getBytes() : null), (displacementMapHolder.getDispl_map_filename() != null ? displacementMapHolder.getDispl_map_filename().getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      colorMapHolder.setColormap_filename(pValue != null ? new String(pValue) : "");
      colorMapHolder.clear();
      uvColorMapper.clear();
    } else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      displacementMapHolder.setDispl_map_filename(pValue != null ? new String(pValue) : "");
      displacementMapHolder.clear();
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else
      throw new IllegalArgumentException(pName);
  }
}
