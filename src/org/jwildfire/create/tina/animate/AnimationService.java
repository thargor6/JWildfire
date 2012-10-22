/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
package org.jwildfire.create.tina.animate;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.render.CRendererInterface;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.render.RendererType;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.image.SimpleImage;

public class AnimationService {

  public static enum GlobalScript {
    NONE,
    ROTATE_PITCH,
    ROTATE_PITCH_YAW,
    ROTATE_ROLL,
    ROTATE_YAW
  }

  public static enum XFormScript {
    NONE,
    ROTATE_FULL,
    ROTATE_SLIGHTLY,
    ROTATE_LAST_XFORM,
    ROTATE_FIRST_XFORM
  }

  public static Flame createFlame(int pFrame, int pFrames, Flame pFlame, GlobalScript pGlobalScript, XFormScript pXFormScript, Prefs pPrefs) throws Exception {
    Flame flame = pFlame.makeCopy();
    switch (pGlobalScript) {
      case ROTATE_PITCH: {
        double camPitch = 360.0 / (double) pFrames * (double) (pFrame - 1);
        flame.setCamPitch(camPitch);
      }
        break;
      case ROTATE_YAW: {
        double camYaw = 360.0 / (double) pFrames * (double) (pFrame - 1);
        flame.setCamYaw(camYaw);
      }
        break;
      case ROTATE_PITCH_YAW: {
        double camRoll = 86;
        double camPitch = 360.0 / (double) pFrames * (double) (pFrame - 1);
        double camYaw = -180 - camPitch;
        flame.setCamRoll(camRoll);
        flame.setCamPitch(camPitch);
        flame.setCamYaw(camYaw);
        flame.setCamPerspective(0.2);
      }
        break;
      case ROTATE_ROLL: {
        double camRoll = 360.0 / (double) pFrames * (double) (pFrame - 1);
        flame.setCamRoll(camRoll);
      }
        break;
    }

    switch (pXFormScript) {
      case ROTATE_FULL: {
        int idx = 0;
        for (XForm xForm : flame.getXForms()) {
          idx++;
          double angle = 360.0 / (double) pFrames * (double) (pFrame - 1);
          if (idx % 2 == 0) {
            angle = -angle;
          }
          XFormTransformService.rotate(xForm, angle);
        }
      }
        break;
      case ROTATE_SLIGHTLY: {
        int idx = 0;
        for (XForm xForm : flame.getXForms()) {
          double maxAngle = (++idx * 3.0) + 90;
          double angle = maxAngle / (double) pFrames * (double) (pFrame - 1);
          angle = maxAngle * (1.0 - Math.cos((double) (pFrame - 1) / (double) (pFrames - 1) * 2.0 * Math.PI)) * 0.5;
          if (idx % 2 == 0) {
            angle = -angle;
          }
          XFormTransformService.rotate(xForm, angle);
        }
      }
        break;
      case ROTATE_LAST_XFORM: {
        XForm xForm = flame.getXForms().get(flame.getXForms().size() - 1);
        double angle = 360.0 / (double) pFrames * (double) (pFrame - 1);
        XFormTransformService.rotate(xForm, angle);
      }
        break;
      case ROTATE_FIRST_XFORM: {
        XForm xForm = flame.getXForms().get(0);
        double angle = 360.0 / (double) pFrames * (double) (pFrame - 1);
        XFormTransformService.rotate(xForm, angle);
      }
        break;
    }
    return flame;
    //          flame.setCamRoll(86 - 20 * Math.sin((imgIdx - 1) * 4.0 * Math.PI / 72.0));
    //          flame.setCamYaw(-180 + 60 * Math.sin((imgIdx - 1) * 2.0 * Math.PI / 72.0));

  }

  public static SimpleImage renderFrame(int pFrame, int pFrames, Flame pFlame, GlobalScript pGlobalScript, XFormScript pXFormScript, int pWidth, int pHeight, Prefs pPrefs, RendererType pRendererType) throws Exception {
    Flame flame = createFlame(pFrame, pFrames, pFlame, pGlobalScript, pXFormScript, pPrefs);
    RenderInfo info = new RenderInfo(pWidth, pHeight);
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(info.getImageWidth());
    flame.setHeight(info.getImageHeight());
    switch (pRendererType) {
      case JAVA: {
        FlameRenderer renderer = new FlameRenderer(flame, pPrefs, flame.isBGTransparency());
        RenderedFlame res = renderer.renderFlame(info);
        return res.getImage();
      }
      case C32:
      case C64: {
        flame.setSpatialOversample(1);
        flame.setColorOversample(1);
        CRendererInterface cudaRenderer = new CRendererInterface(pRendererType, flame.isBGTransparency());
        CRendererInterface.checkFlameForCUDA(flame);
        RenderedFlame res = cudaRenderer.renderFlame(info, flame, pPrefs);
        return res.getImage();
      }
      default:
        return null;
    }
  }
}
