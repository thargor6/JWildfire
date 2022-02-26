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


import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.create.tina.variation.VariationFuncType;

import java.util.HashSet;
import java.util.Set;

public class VariationProfile implements VariationProfileFilter, Assignable<VariationProfile> {
  public static final String KEY_COUNT = "varProfiles.count.2";
  public static final String KEY_NAME = "varProfiles.name";
  public static final String KEY_DEFAULT = "varProfiles.default";
  public static final String KEY_PROFILE_TYPE = "varProfiles.profileType";
  public static final String KEY_VARIATIONS = "varProfiles.variations";
  public static final String KEY_VARIATION_TYPES = "varProfiles.variationTypes";

  private String name;
  private boolean defaultProfile;
  private boolean negatedProfile;
  private VariationProfileType variationProfileType;
  private final Set<String> variations = new HashSet<>();
  private final Set<VariationFuncType> variationTypes = new HashSet<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isDefaultProfile() {
    return defaultProfile;
  }

  public boolean isNegatedProfile() {
    return negatedProfile;
  }

  public void setDefaultProfile(boolean defaultProfile) {
    this.defaultProfile = defaultProfile;
  }

  public VariationProfileType getVariationProfileType() {
    return variationProfileType;
  }

  public void setVariationProfileType(VariationProfileType variationProfileType) {
    this.variationProfileType = variationProfileType;
  }

  public Set<String> getVariations() {
    return variations;
  }

  public Set<VariationFuncType> getVariationTypes() {
    return variationTypes;
  }

  @Override
  public boolean evaluate(String variationName) {
    switch(variationProfileType) {
      case INCLUDE_TYPES:
        {
          Set<VariationFuncType> varTypes = VariationFuncList.getVariationTypes(variationName);
          for(VariationFuncType ourType: getVariationTypes()) {
            if(!varTypes.contains(ourType)) {
              return false;
            }
          }
          return true;
        }
      case EXCLUDE_TYPES:
        {
          Set<VariationFuncType> varTypes = VariationFuncList.getVariationTypes(variationName);
          for(VariationFuncType ourType: getVariationTypes()) {
            if(varTypes.contains(ourType)) {
              return false;
            }
          }
          return true;
        }
      case INCLUDE_VARIATIONS:
        return getVariations().contains(variationName);
      case EXCLUDE_VARIATIONS:
        return !getVariations().contains(variationName);
    }
    return false;
  }

  @Override
  public boolean isNegative() {
    return negatedProfile;
  }

  @Override
  public void assign(VariationProfile pSrc) {
    name = pSrc.getName();
    defaultProfile = pSrc.isDefaultProfile();
    negatedProfile = pSrc.isNegatedProfile();
    variationProfileType = pSrc.getVariationProfileType();
    variations.clear();
    variations.addAll(pSrc.getVariations());
    variationTypes.clear();
    variationTypes.addAll(pSrc.getVariationTypes());
  }

  @Override
  public VariationProfile makeCopy() {
    VariationProfile res = new VariationProfile();
    res.assign(this);
    return res;
  }

  public VariationProfileFilter negate() {
    VariationProfile negatedCopy = makeCopy();
    negatedCopy.negatedProfile = !negatedCopy.negatedProfile;
    return negatedCopy;
  }

  @Override
  public boolean isEqual(VariationProfile pSrc) {
    return (pSrc.getName()==null && name == null) || pSrc.getName().equals(name);
  }

}
