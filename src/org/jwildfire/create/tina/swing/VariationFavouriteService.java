/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2020 Andreas Maschke

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

import org.jwildfire.base.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.HashSet;
import java.util.Set;

public class VariationFavouriteService {
  private static final Logger logger = LoggerFactory.getLogger(VariationFavouriteService.class);

  private static Set<String> favourites;

  private VariationFavouriteService(){
    // EMPTY
  }

  static {
    favourites = new HashSet<>();
    favourites.addAll(Prefs.getPrefs().getVariationFavourites());
  }

  public static void setFavourite(String variationName) {
    if(!favourites.contains(variationName)) {
      favourites.add(variationName);
      Prefs.getPrefs().setVariationFavourites(favourites);
      try {
        Prefs.getPrefs().saveToFile();
      } catch (Exception e) {
        logger.error(Marker.ANY_MARKER, "Error writing preferences", e);
      }
    }
  }

  public static void clearFavourite(String variationName) {
    if(favourites.contains(variationName)) {
      favourites.remove(variationName);
      Prefs.getPrefs().setVariationFavourites(favourites);
      try {
        Prefs.getPrefs().saveToFile();
      } catch (Exception e) {
        logger.error(Marker.ANY_MARKER, "Error writing preferences", e);
      }
    }
  }

  public static boolean isFavourite(String variationName) {
    return favourites.contains(variationName);
  }

}
