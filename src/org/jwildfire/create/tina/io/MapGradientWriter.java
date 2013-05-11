package org.jwildfire.create.tina.io;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

public class MapGradientWriter {

  public void writeGradient(RGBPalette pGradient, String pFilename) throws Exception {
    Tools.writeUTF8Textfile(pFilename, getGradientAsMap(pGradient));
  }

  public String getGradientAsMap(RGBPalette pGradient) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
      RGBColor color = pGradient.getRawColor(i);
      sb.append(alignColValue(color.getRed()));
      sb.append(" ");
      sb.append(alignColValue(color.getGreen()));
      sb.append(" ");
      sb.append(alignColValue(color.getBlue()));
      if (i == 0) {
        sb.append(" \"");
        sb.append(pGradient.getFlam3Name());
        sb.append("\", created with " + Tools.APP_TITLE + " " + Tools.APP_VERSION);
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  private String alignColValue(int pValue) {
    String res = String.valueOf(pValue);
    while (res.length() < 3) {
      res = " " + res;
    }
    return res;
  }
}
