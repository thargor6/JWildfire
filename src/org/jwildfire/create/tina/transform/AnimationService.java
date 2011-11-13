package org.jwildfire.create.tina.transform;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.AffineZStyle;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;

public class AnimationService {

  public static enum Script {
    ROTATE_PITCH,
    ROTATE_PITCH_YAW,
  }

  public static void renderFrame(int pFrame, int pFrames, Flame pFlame1, Flame pFlame2, boolean pDoMorph, Script pScript, String pImagePath, int pWidth, int pHeight, int pQuality, RenderMode pRenderMode, AffineZStyle pAffineZStyle) throws Exception {
    String imgFilename = String.valueOf(pFrame);
    while (imgFilename.length() < 3) {
      imgFilename = "0" + imgFilename;
    }
    imgFilename = pImagePath + imgFilename + ".png";
    SimpleImage img = new SimpleImage(pWidth, pHeight);
    Flame flame;
    if (pDoMorph) {
      int morphFrames = pFrames / 2;
      int morphFrame;
      if (pFrame <= morphFrames) {
        morphFrame = pFrame;
      }
      else {
        morphFrame = morphFrames - (pFrame - morphFrames);
        if (morphFrame < 1) {
          morphFrame = 1;
        }
        else if (morphFrame > morphFrames) {
          morphFrame = morphFrames;
        }
      }
      flame = FlameMorphService.morphFlames(pFlame1, pFlame2, morphFrame, morphFrames);
    }
    else {
      flame = pFlame1;
    }
    double wScl = (double) img.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) img.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(img.getImageWidth());
    flame.setHeight(img.getImageHeight());
    flame.setSampleDensity(pQuality);
    double camPitch, camRoll, camYaw, camPerspective;
    switch (pScript) {
      case ROTATE_PITCH:
      default:
        camRoll = 86;
        camPitch = 360.0 / (double) pFrames * (double) (pFrame - 1);
        camYaw = -180;
        camPerspective = 0.2;
        break;
      case ROTATE_PITCH_YAW:
        camRoll = 86;
        camPitch = 360.0 / (double) pFrames * (double) (pFrame - 1);
        camYaw = -180 - camPitch;
        camPerspective = 0.2;
        break;
    }
    //          flame.setCamRoll(86 - 20 * Math.sin((imgIdx - 1) * 4.0 * Math.PI / 72.0));
    //          flame.setCamYaw(-180 + 60 * Math.sin((imgIdx - 1) * 2.0 * Math.PI / 72.0));
    flame.setCamRoll(camRoll);
    flame.setCamPitch(camPitch);
    flame.setCamYaw(camYaw);
    flame.setCamPerspective(camPerspective);

    FlameRenderer renderer = new FlameRenderer();
    renderer.setRenderMode(pRenderMode);
    renderer.setAffineZStyle(pAffineZStyle);
    renderer.renderFlame(flame, img);
    new ImageWriter().saveImage(img, imgFilename);
  }

}
