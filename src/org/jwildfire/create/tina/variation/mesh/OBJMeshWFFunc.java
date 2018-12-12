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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.create.tina.variation.RessourceType;

public class OBJMeshWFFunc extends AbstractOBJMeshWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String RESSOURCE_OBJ_FILENAME = "obj_filename";

  private String objFilename = null;

  private static final String[] ressourceNames = {RESSOURCE_OBJ_FILENAME, RESSOURCE_COLORMAP_FILENAME, RESSOURCE_DISPL_MAP_FILENAME};

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(objFilename != null ? objFilename.getBytes() : null), (colorMapHolder.getColormap_filename() != null ? colorMapHolder.getColormap_filename().getBytes() : null), (displacementMapHolder.getDispl_map_filename() != null ? displacementMapHolder.getDispl_map_filename().getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_OBJ_FILENAME.equalsIgnoreCase(pName)) {
      objFilename = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
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
    if (RESSOURCE_OBJ_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.OBJ_MESH;
    } else if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "obj_mesh_wf";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    if (objFilename != null && objFilename.length() > 0) {
      try {
        String meshKey = this.getClass().getName() + "_" + OBJMeshUtil.getMeshname(objFilename, subdiv_level, subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu);
        mesh = (SimpleMesh) RessourceManager.getRessource(meshKey);
        if (mesh == null) {
          mesh = OBJMeshUtil.loadAndSmoothMeshFromFile(objFilename, subdiv_smooth_passes, subdiv_level, subdiv_smooth_lambda, subdiv_smooth_mu);
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

}
