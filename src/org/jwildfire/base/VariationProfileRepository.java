/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke

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
package org.jwildfire.base;

import org.jwildfire.create.tina.swing.VariationFuncFilter;
import org.jwildfire.create.tina.variation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class VariationProfileRepository {
  private static final VariationProfileFilter EMPTY_FILTER = new EmptyVariationProfile();

  static {
    if(Prefs.getPrefs().getVariationProfiles().isEmpty()) {
      setNewProfiles(createDefaultProfiles());
    }
    else if(Prefs.getPrefs().getTinaFACLRenderPath()!=null && !Prefs.getPrefs().getTinaFACLRenderPath().isEmpty()) {
      VariationProfile gpuProfile = createSupportsGPUProfile();
      if(!Prefs.getPrefs().getVariationProfiles().stream().filter( p -> gpuProfile.getName().equals(p.getName())).findAny().isPresent()) {
        List<VariationProfile> profiles = Prefs.getPrefs().getVariationProfiles().stream().map( p -> p.makeCopy()).collect(Collectors.toList());
        profiles.add(gpuProfile);
        setNewProfiles(profiles);
      }
    }
  }

  private static void setNewProfiles(List<VariationProfile> newProfiles) {
    Prefs.getPrefs().setVariationProfiles(sortEntries(newProfiles));
    try {
      new PrefsWriter().writePrefs(Prefs.getPrefs());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void updateVariationProfiles(List<VariationProfile> newProfiles) {
    Prefs.getPrefs().setVariationProfiles(newProfiles);
    try {
      new PrefsWriter().writePrefs(Prefs.getPrefs());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static List<VariationProfile> getProfiles() {
    return Prefs.getPrefs().getVariationProfiles();
  }

  private static List<VariationProfile> sortEntries(List<VariationProfile> profiles) {
    Collections.sort(profiles, new Comparator<VariationProfile>() {
      @Override
      public int compare(VariationProfile o1, VariationProfile o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    return profiles;
  }

  private static List<VariationProfile> createDefaultProfiles() {
    List<VariationProfile> res = new ArrayList<>();
    res.add(createDefaultProfile());
    res.add(create2DProfile());
    res.add(createPreProfile());
    res.add(createPostProfile());
    res.add(createPrePostProfile());
    res.add(createDCProfile());
    res.add(createBlurProfile());
    res.add(createBaseShapeProfile());
    res.add(createSimulationProfile());
    res.add(createCropProfile());
    res.add(createEditFormulaProfile());
    res.add(create3DProfile());
    res.add(createZTransformProfile());
    res.add(createEscapeTimeFractalProfile());
    res.add(createSupportsExternalShapesProfile());
    res.add(createSupportsGPUProfile());
    return res;
  }

  private static VariationProfile create2DProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("2D");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_2D);
    return res;
  }

  private static VariationProfile createPreProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Pre");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_PRE);
    return res;
  }

  private static VariationProfile createPostProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Post");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_POST);
    return res;
  }

  private static VariationProfile createPrePostProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("PrePost");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_PREPOST);
    return res;
  }

  private static VariationProfile createDCProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Direct Color");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_DC);
    return res;
  }

  private static VariationProfile createBlurProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Blur");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_BLUR);
    return res;
  }

  private static VariationProfile createBaseShapeProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Base shape");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_BASE_SHAPE);
    return res;
  }

  private static VariationProfile createSimulationProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Simulation");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_SIMULATION);
    return res;
  }

  private static VariationProfile createCropProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Crop");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_CROP);
    return res;
  }

  private static VariationProfile createEditFormulaProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Edit formula/code");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_EDIT_FORMULA);
    return res;
  }

  private static VariationProfile create3DProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("3D");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_3D);
    return res;
  }

  private static VariationProfile createZTransformProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("ZTransform");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_ZTRANSFORM);
    return res;
  }

  private static VariationProfile createEscapeTimeFractalProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Escape-time fractal");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_ESCAPE_TIME_FRACTAL);
    return res;
  }

  private static VariationProfile createSupportsExternalShapesProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Ext. images/shapes");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_SUPPORTS_EXTERNAL_SHAPES);
    return res;
  }

  private static VariationProfile createSupportsGPUProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Supports GPU");
    res.setVariationProfileType(VariationProfileType.INCLUDE_TYPES);
    res.getVariationTypes().add(VariationFuncType.VARTYPE_SUPPORTS_GPU);
    return res;
  }

  private static VariationProfile createDefaultProfile() {
    VariationProfile res = new VariationProfile();
    res.setName("Default");
    res.setDefaultProfile(true);
    res.setVariationProfileType(VariationProfileType.EXCLUDE_VARIATIONS);
    return res;
  }

  public static VariationProfileFilter getVariationProfileFilter(String name) {
    if(name==null || name.isEmpty()) {
      return EMPTY_FILTER;
    }
    for(VariationProfile profile: getProfiles()) {
      if(profile.getName().equals(name)) {
        return profile;
      }
    }
    for(VariationProfile profile: getProfiles()) {
      if((VariationFuncFilter.NEGATION_PREFIX+profile.getName()).equals(name)) {
        return profile.negate();
      }
    }
    return EMPTY_FILTER;
  }
}
