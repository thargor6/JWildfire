package org.jwildfire.create.tina.browser;

import org.jwildfire.swing.ImagePanel;

public class RenderJobInfo {
  private final ImagePanel destPnl;
  private final FlameFlatNode flame;
  private final int renderWidth, renderHeight;
  private final int locationX, locationY;

  public RenderJobInfo(ImagePanel pDestPnl, FlameFlatNode pFlame, int pRenderWidth, int pRenderHeight, int pLocationX, int pLocationY) {
    destPnl = pDestPnl;
    flame = pFlame;
    renderWidth = pRenderWidth;
    renderHeight = pRenderHeight;
    locationX = pLocationX;
    locationY = pLocationY;
  }

  public ImagePanel getDestPnl() {
    return destPnl;
  }

  public FlameFlatNode getFlame() {
    return flame;
  }

  public int getRenderWidth() {
    return renderWidth;
  }

  public int getRenderHeight() {
    return renderHeight;
  }

  public int getLocationX() {
    return locationX;
  }

  public int getLocationY() {
    return locationY;
  }

}
