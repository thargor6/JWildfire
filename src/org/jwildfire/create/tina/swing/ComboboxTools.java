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
package org.jwildfire.create.tina.swing;

import org.jwildfire.base.VariationProfile;
import org.jwildfire.base.VariationProfileRepository;

import javax.swing.*;

public final class ComboboxTools {

  public static void initVariationProfileCmb(JComboBox pCmb, boolean pSelectDefault, boolean pWithNegatedOptions) {
    pCmb.removeAllItems();
    pCmb.setMaximumRowCount(16);
    int selectedIdx = -1;
    int currIdx = 0;
    pCmb.addItem("");

    for (VariationProfile profile : VariationProfileRepository.getProfiles()) {
      pCmb.addItem(profile.getName());
      if (pSelectDefault && profile.isDefaultProfile() && selectedIdx < 0) {
        selectedIdx = currIdx;
      }
      currIdx++;
    }

    if(pWithNegatedOptions) {
      pCmb.addItem("");
      for (VariationProfile profile : VariationProfileRepository.getProfiles()) {
        pCmb.addItem(VariationFuncFilter.NEGATION_PREFIX+profile.getName());
      }
    }

    if (pSelectDefault && pCmb.getItemCount() > 0) {
      pCmb.setSelectedIndex(selectedIdx >= 0 ? selectedIdx + 1 : 1);
    }
    else {
      pCmb.setSelectedIndex(0);
    }
  }


}
