package org.jwildfire.create.tina.io;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;

public class Flam3Reader extends AbstractFlameReader {

  protected Flam3Reader(Prefs pPrefs) {
    super(pPrefs);
  }

  public List<Flame> readFlamesfromXML(String pXML) {
    List<Flame> res = new ArrayList<Flame>();
    int pFlames = 0;
    while (true) {
      String flameXML;
      {
        int ps = pXML.indexOf("<flame ", pFlames);
        if (ps < 0)
          break;
        int pe = pXML.indexOf("</flame>", ps + 1);
        if (pe < 0)
          break;
        pFlames = pe + 8;
        flameXML = pXML.substring(ps, pFlames);
      }

      Flame flame = new Flame();
      res.add(flame);
      // Flame attributes
      {
        int ps = flameXML.indexOf("<flame ");
        int pe = -1;
        boolean qt = false;
        for (int i = ps + 1; i < flameXML.length(); i++) {
          if (flameXML.charAt(i) == '\"') {
            qt = !qt;
          }
          else if (!qt && flameXML.charAt(i) == '>') {
            pe = i;
            break;
          }
        }
        String hs = flameXML.substring(ps + 7, pe);
        parseFlameAttributes(flame, hs);
      }
      Layer layer = flame.getFirstLayer();
      readXForms(flameXML, flame, layer);
      readFinalXForms(flameXML, flame, layer);
      readColors(flameXML, layer);
    }
    return res;
  }

}
