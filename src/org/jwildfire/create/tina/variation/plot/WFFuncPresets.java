package org.jwildfire.create.tina.variation.plot;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathparser.JEPWrapper;
import org.nfunk.jep.Node;

import java.util.HashMap;
import java.util.Map;

public abstract class WFFuncPresets<T extends WFFuncPreset> {
  private Map<Integer, T> presets;
  private final String presets_filename;
  private int minId, maxId;

  private final T dfltPreset = createDefaultPreset();

  public WFFuncPresets(String presets_filename) {
    super();
    this.presets_filename = presets_filename;
  }

  protected abstract T createDefaultPreset();

  public T getPreset(int id) {
    if (presets == null) {
      presets = parsePresets();
    }
    T res = presets.get(id);
    return res != null ? res : dfltPreset;
  }

  private Map<Integer, T> parsePresets() {
    minId = Integer.MAX_VALUE;
    maxId = Integer.MIN_VALUE;
    Map<Integer, T> res = new HashMap<>();
    try {
      String presets_txt = Tools.getRessourceAsString(WFFuncPresets.class, presets_filename);
      int blockStart = -1;
      while (blockStart < presets_txt.length()) {
        blockStart = presets_txt.indexOf("##", blockStart + 1);
        if (blockStart < 0) {
          break;
        }
        int blockEnd = presets_txt.indexOf("##", blockStart + 2);
        if (blockEnd < 0) {
          blockEnd = presets_txt.length();
        }

        try {
          T preset = parsePreset(presets_txt.substring(blockStart - 1, blockEnd));
          if (res.get(preset.getId()) != null) {
            throw new Exception("Preset <" + preset.getId() + "> already exists");
          }
          if (preset.getId() < minId) {
            minId = preset.getId();
          }
          if (preset.getId() > maxId) {
            maxId = preset.getId();
          }
          res.put(preset.getId(), preset);
        } catch (Exception ex) {
          ex.printStackTrace();

        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return res;
  }

  protected abstract T parsePreset(String preset);

  protected int parseId(String preset) {
    int p1 = preset.indexOf("##");
    if (p1 >= 0) {
      int p2 = preset.indexOf("\n", p1 + 1);
      if (p2 < 0)
        p2 = preset.length();
      String token = preset.substring(p1 + 2, p2).trim();
      return Integer.parseInt(token);
    }
    return -1;
  }

  protected String parseToken(String preset, String token) {
    int p1 = preset.indexOf("\n" + token);
    if (p1 >= 0) {
      int p2 = preset.indexOf("\n", p1 + 1);
      if (p2 < 0)
        p2 = preset.length();
      String line = preset.substring(p1 + token.length(), p2).trim();

      int p3 = line.indexOf("=");
      if (p3 >= 0) {
        line = line.substring(p3 + 1, line.length());
        int p4 = line.indexOf("---");
        if (p4 > 0) {
          line = line.substring(0, p4);
        }
        return line.trim();
      }
    }
    return null;
  }

  protected String parseFormula(String preset, String token) {
    return parseToken(preset, token);
  }

  protected double parseParam(String preset, String token) {
    String value = parseToken(preset, token);
    if (value == null) {
      return 0.0;
    }
    JEPWrapper parser = new JEPWrapper();
    Node node = parser.parse(value);
    return (Double) parser.evaluate(node);
  }

  public int getRandomPresetId() {
    return minId + (int) ((maxId - minId) * Math.random());
  }
}
