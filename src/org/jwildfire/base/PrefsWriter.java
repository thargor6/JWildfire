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
package org.jwildfire.base;

import java.io.File;

public class PrefsWriter {

  public void writePrefs(Prefs pPrefs) throws Exception {
    StringBuilder sb = new StringBuilder();
    addValue(sb, Prefs.KEY_GENERAL_PLAF_STYLE, pPrefs.getPlafStyle());
    addValue(sb, Prefs.KEY_GENERAL_PLAF_THEME, pPrefs.getPlafTheme());
    addValue(sb, Prefs.KEY_GENERAL_PATH_IMAGES, pPrefs.getImagePath());
    addValue(sb, Prefs.KEY_GENERAL_PATH_SCRIPTS, pPrefs.getScriptPath());
    addValue(sb, Prefs.KEY_TINA_RENDER_FAST_MATH, pPrefs.getTinaRenderFastMath());
    addValue(sb, Prefs.KEY_TINA_PATH_FLAMES, pPrefs.getTinaFlamePath());
    addValue(sb, Prefs.KEY_TINA_RENDER_THREADS, pPrefs.getTinaRenderThreads());
    addValue(sb, Prefs.KEY_TINA_RENDER_IMAGE_WIDTH, pPrefs.getTinaRenderImageWidth());
    addValue(sb, Prefs.KEY_TINA_RENDER_IMAGE_HEIGHT, pPrefs.getTinaRenderImageHeight());
    addValue(sb, Prefs.KEY_TINA_RENDER_MOVIE_WIDTH, pPrefs.getTinaRenderMovieWidth());
    addValue(sb, Prefs.KEY_TINA_RENDER_MOVIE_HEIGHT, pPrefs.getTinaRenderMovieHeight());
    addValue(sb, Prefs.KEY_TINA_RENDER_MOVIE_FRAMES, pPrefs.getTinaRenderMovieFrames());
    addValue(sb, Prefs.KEY_TINA_RENDER_PREVIEW_SPATIAL_OVERSAMPLE, pPrefs.getTinaRenderPreviewSpatialOversample());
    addValue(sb, Prefs.KEY_TINA_RENDER_PREVIEW_COLOR_OVERSAMPLE, pPrefs.getTinaRenderPreviewColorOversample());
    addValue(sb, Prefs.KEY_TINA_RENDER_PREVIEW_FILTER_RADIUS, pPrefs.getTinaRenderPreviewFilterRadius());
    addValue(sb, Prefs.KEY_TINA_RENDER_PREVIEW_QUALITY, pPrefs.getTinaRenderPreviewQuality());
    addValue(sb, Prefs.KEY_TINA_RENDER_NORMAL_SPATIAL_OVERSAMPLE, pPrefs.getTinaRenderNormalSpatialOversample());
    addValue(sb, Prefs.KEY_TINA_RENDER_NORMAL_COLOR_OVERSAMPLE, pPrefs.getTinaRenderNormalColorOversample());
    addValue(sb, Prefs.KEY_TINA_RENDER_NORMAL_FILTER_RADIUS, pPrefs.getTinaRenderNormalFilterRadius());
    addValue(sb, Prefs.KEY_TINA_RENDER_NORMAL_QUALITY, pPrefs.getTinaRenderNormalQuality());
    addValue(sb, Prefs.KEY_TINA_RENDER_NORMAL_HDR, pPrefs.isTinaRenderNormalHDR());
    addValue(sb, Prefs.KEY_TINA_RENDER_NORMAL_HDR_INTENSITY_MAP, pPrefs.isTinaRenderNormalHDRIntensityMap());
    addValue(sb, Prefs.KEY_TINA_RENDER_HIGH_SPATIAL_OVERSAMPLE, pPrefs.getTinaRenderHighSpatialOversample());
    addValue(sb, Prefs.KEY_TINA_RENDER_HIGH_COLOR_OVERSAMPLE, pPrefs.getTinaRenderHighColorOversample());
    addValue(sb, Prefs.KEY_TINA_RENDER_HIGH_FILTER_RADIUS, pPrefs.getTinaRenderHighFilterRadius());
    addValue(sb, Prefs.KEY_TINA_RENDER_HIGH_QUALITY, pPrefs.getTinaRenderHighQuality());
    addValue(sb, Prefs.KEY_TINA_RENDER_HIGH_HDR, pPrefs.isTinaRenderHighHDR());
    addValue(sb, Prefs.KEY_TINA_RENDER_HIGH_HDR_INTENSITY_MAP, pPrefs.isTinaRenderHighHDRIntensityMap());
    addValue(sb, Prefs.KEY_TINA_RENDER_MOVIE_SPATIAL_OVERSAMPLE, pPrefs.getTinaRenderMovieSpatialOversample());
    addValue(sb, Prefs.KEY_TINA_RENDER_MOVIE_COLOR_OVERSAMPLE, pPrefs.getTinaRenderMovieColorOversample());
    addValue(sb, Prefs.KEY_TINA_RENDER_MOVIE_FILTER_RADIUS, pPrefs.getTinaRenderMovieFilterRadius());
    addValue(sb, Prefs.KEY_TINA_RENDER_MOVIE_QUALITY, pPrefs.getTinaRenderMovieQuality());
    addValue(sb, Prefs.KEY_TINA_RENDER_REALTIME_QUALITY, pPrefs.getTinaRenderRealtimeQuality());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_SIZE, pPrefs.getTinaRandomBatchSize());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_RED, pPrefs.getTinaRandomBatchBGColorRed());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_GREEN, pPrefs.getTinaRandomBatchBGColorGreen());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_BLUE, pPrefs.getTinaRandomBatchBGColorBlue());

    addValue(sb, Prefs.KEY_SUNFLOW_PATH_SCENES, pPrefs.getSunflowScenePath());
    Tools.writeUTF8Textfile(System.getProperty("user.home") + File.separator + Prefs.PREFS_FILE, sb.toString());
  }

  private void addValue(StringBuilder pSB, String pKey, String pValue) {
    pSB.append(pKey + "=" + (pValue != null ? pValue.replace("\\", "\\\\") : "") + "\n");
  }

  private void addValue(StringBuilder pSB, String pKey, int pValue) {
    pSB.append(pKey + "=" + String.valueOf(pValue) + "\n");
  }

  private void addValue(StringBuilder pSB, String pKey, boolean pValue) {
    pSB.append(pKey + "=" + String.valueOf(pValue) + "\n");
  }

  private void addValue(StringBuilder pSB, String pKey, double pValue) {
    pSB.append(pKey + "=" + Tools.doubleToString(pValue) + "\n");
  }

}
