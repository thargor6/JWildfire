/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.eden.scene;

import java.util.HashMap;
import java.util.Map;

import org.jwildfire.create.eden.base.Color3f;
import org.jwildfire.create.eden.scene.material.Glass;
import org.jwildfire.create.eden.scene.material.Material;
import org.jwildfire.create.eden.scene.material.Shiny;

public class MaterialGroup extends SceneElementGroup {
  private Map<String, Material> materialMap = new HashMap<String, Material>();

  public MaterialGroup(PositionableSceneElement pParent) {
    super(pParent);
    addDefaultMaterials();
  }

  public void addMaterial(Material pMaterial) {
    String name = makeUniqueName(pMaterial);
    pMaterial.setName(name);
    materialMap.put(name, pMaterial);
    getElements().add(pMaterial);
  }

  private String makeUniqueName(Material pMaterial) {
    String baseName = pMaterial.getName();
    if (baseName == null || baseName.length() == 0) {
      baseName = pMaterial.getClass().getSimpleName().toLowerCase();
    }
    int p = baseName.lastIndexOf('_');
    if (p > 0 && p < baseName.length() - 1) {
      String ext = baseName.substring(p + 1, baseName.length());
      if (isNumber(ext)) {
        baseName = baseName.substring(0, p);
      }
    }
    int counter = 1;
    String name = baseName;
    while (true) {
      if (materialMap.get(name) == null) {
        return name;
      }
      name = baseName + "_" + (counter++);
    }
  }

  private boolean isNumber(String pValue) {
    try {
      Integer.parseInt(pValue);
      return true;
    }
    catch (Exception ex) {
      return false;
    }
  }

  private void addDefaultMaterials() {
    {
      Glass glass = new Glass(Material.MATERIAL_GLASS);
      addMaterial(glass);
    }
    {
      Shiny shiny = new Shiny(Material.MATERIAL_SHINY);
      addMaterial(shiny);
    }
    {
      Shiny shiny = new Shiny(Material.MATERIAL_SHINY_RED);
      shiny.setColor(new Color3f(0.95, 0.1, 0.05));
      addMaterial(shiny);
    }
    {
      Shiny shiny = new Shiny(Material.MATERIAL_SHINY_GREEN);
      shiny.setColor(new Color3f(0.290196, 0.909804, 0));
      addMaterial(shiny);
    }
    {
      Shiny shiny = new Shiny(Material.MATERIAL_SHINY_BLUE);
      shiny.setColor(new Color3f(0, 0.190196, 0.909804));
      addMaterial(shiny);
    }
    {
      Shiny shiny = new Shiny(Material.MATERIAL_MIRROR);
      shiny.setColor(new Color3f(0.2, 0.2, 0.2));
      shiny.setRefl(3.5);
      addMaterial(shiny);
    }
  }

}
