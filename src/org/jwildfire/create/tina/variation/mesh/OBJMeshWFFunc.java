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
  private static final String RESSOURCE_UVMAP_FILENAME = "uvmap_filename";

  private static final String[] ressourceNames = { RESSOURCE_OBJ_FILENAME, RESSOURCE_UVMAP_FILENAME };

  private String objFilename = null;

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] { (objFilename != null ? objFilename.getBytes() : null), (uvMapFilename != null ? uvMapFilename.getBytes() : null) };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_OBJ_FILENAME.equalsIgnoreCase(pName)) {
      objFilename = pValue != null ? new String(pValue) : "";
    }
    else if (RESSOURCE_UVMAP_FILENAME.equalsIgnoreCase(pName)) {
      uvMapFilename = pValue != null ? new String(pValue) : "";
      clearCurrUVMap();
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_OBJ_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.OBJ_MESH;
    }
    else if (RESSOURCE_UVMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    }
    else
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
        String meshKey = this.getClass().getName() + "_" + getMeshname(objFilename);
        mesh = (SimpleMesh) RessourceManager.getRessource(meshKey);
        if (mesh == null) {
          mesh = loadMeshFromFile(objFilename);
          RessourceManager.putRessource(meshKey, mesh);
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (mesh == null) {
      mesh = createDfltMesh();
    }
  }

}
