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
package org.jwildfire.create.tina.randomflame;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.script.ScriptParam;
import org.jwildfire.create.tina.script.ScriptRunner;
import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;
import org.jwildfire.create.tina.script.swing.JWFScriptInternalNode;
import org.jwildfire.create.tina.script.swing.JWFScriptUserNode;
import org.jwildfire.create.tina.script.swing.JWFScriptUtils;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

public class RunRandomScriptRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5 + Math.random());
      xForm.addVariation(1.5 + Math.random(), VariationFuncList.getVariationFuncInstance(Math.random() < 0.12 ? VariationFuncList.getRandomVariationname() : "spherical", true));
      xForm.setColorSymmetry(-0.5);

      XFormTransformService.localTranslate(xForm, 0.75 - 5.50 * Math.random(), 0.75 - 1.50 * Math.random(), false);
      XFormTransformService.rotate(xForm, -60.0 + Math.random() * 30.0, false);
      XFormTransformService.scale(xForm, 0.1 + Math.random() * 0.4, true, true, false);

    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(1.0 + Math.random() * 100.0);

      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(Math.random() < 0.8 ? "juliascope" : "julian", true);
      varFunc.setParameter("power", Math.random() < 0.8 ? 2 : 2 + Math.random() * 10.0);
      varFunc.setParameter("dist", Math.random() < 0.8 ? 1.0 : -2.0 + 4.0 * Math.random());
      xForm.addVariation(0.5 + Math.random(), varFunc);
      xForm.setColorSymmetry(0.5);

      XFormTransformService.rotate(xForm, Math.random() * 360.0, false);
      XFormTransformService.localTranslate(xForm, 1.75 - 3.50 * Math.random(), 0.75 - 5.50 * Math.random(), false);
      XFormTransformService.scale(xForm, 1.1 + Math.random() * 2.0, true, true, false);
    }

    layer.getXForms().get(0).getModifiedWeights()[0] = 0.0;
    layer.getXForms().get(0).getModifiedWeights()[1] = 1.0;

    if (Math.random() > 0.667) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(1.0 + Math.random() * 100.0);
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
      xForm.addVariation(0.25 + 1.25 * Math.random(), varFunc);

      XFormTransformService.rotate(xForm, -12.0 + Math.random() * 24.0, true);
      XFormTransformService.localTranslate(xForm, -0.125 + Math.random() * 0.25, -0.125 + Math.random() * 0.25, true);
      XFormTransformService.scale(xForm, 0.9 + Math.random() * 0.2, true, true, true);

      layer.getXForms().get(0).getModifiedWeights()[1] = 0.0;
      layer.getXForms().get(1).getModifiedWeights()[2] = 0.0;
      layer.getXForms().get(2).getModifiedWeights()[2] = 0.0;

      if (Math.random() > 0.667) {
        xForm = new XForm();
        layer.getXForms().add(xForm);
        xForm.setWeight(0.50 + Math.random() * 50.0);
        varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.125 + 0.75 * Math.random(), varFunc);

        XFormTransformService.rotate(xForm, -24.0 + Math.random() * 48.0, true);
        XFormTransformService.localTranslate(xForm, -0.25 + Math.random() * 0.5, -0.25 + Math.random() * 0.5, true);
        XFormTransformService.scale(xForm, 0.5 + Math.random() * 0.25, true, true, true);

        layer.getXForms().get(0).getModifiedWeights()[2] = 0.0;
        layer.getXForms().get(2).getModifiedWeights()[2] = 0.0;
        layer.getXForms().get(1).getModifiedWeights()[3] = 0.0;
        layer.getXForms().get(2).getModifiedWeights()[3] = 0.0;
        layer.getXForms().get(3).getModifiedWeights()[3] = 0.0;
      }
    }

    flame.getFirstLayer().randomizeColors();

    ScriptEntry scriptEntry = getRandomScript();
    if (scriptEntry != null) {
      try {
        runScript(scriptEntry.getScriptPath(), scriptEntry.getScript(), flame);
        flame.setName(getFlameName(flame, scriptEntry.getScriptPath(), scriptEntry.isUserScript()));
      } catch (Exception ex) {
        ex.printStackTrace();
        System.err.println("#############################SCRIPT################################");
        System.err.println(scriptEntry.getScriptPath());
        System.err.println("###################################################################");
        // remove the script from the list in order to avoid to execute it again
        getScripts().remove(scriptEntry);
      }
    }
    return flame;
  }

  private String getFlameName(Flame flame, String scriptPath, boolean isUserScript) {
    String scriptName = new File(scriptPath).getName();
    String fileExt = Tools.getFileExt(scriptName);
    if(fileExt!=null && !fileExt.isEmpty()) {
      scriptName = scriptName.substring(0, scriptName.length() - fileExt.length() - 1);
    }

    String scriptFolder;
    if(isUserScript) {
      String parentFolder = new File(scriptPath).getParentFile().getAbsolutePath();
      if (new File(Prefs.getPrefs().getTinaJWFScriptPath()).getAbsolutePath().equals(parentFolder)) {
        scriptFolder = null;
      } else {
        scriptFolder = new File(scriptPath).getParentFile().getName();
      }
    }
    else {
      scriptFolder = null;
    }

    if(scriptFolder==null) {
      return getName() + "[" + scriptName + "]" + " - " + flame.hashCode();
    }
    else {
      return getName() + "[" + scriptFolder + "/" + scriptName + "]" + " - " + flame.hashCode();
    }
  }

  @Override
  public String getName() {
    return "RunRandomScript";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return true;
  }

  @Override
  protected Flame postProcessFlameBeforeRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }

  @Override
  protected Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }

  @Override
  public boolean supportsSymmetry() {
    return true;
  }

  @Override
  public boolean supportsWeightingField() {
    return true;
  }

  private static class ScriptEntry {
    private final JWFScriptUtils.ScriptNode node;
    private final String scriptPath;
    private String script;
    private boolean userScript;

    public ScriptEntry(JWFScriptUtils.ScriptNode node, String scriptPath, boolean userScript) {
      this.node = node;
      this.scriptPath = scriptPath;
      this.userScript = userScript;
    }

    public String getScriptPath() {
      return scriptPath;
    }

    public boolean isUserScript() {
      return userScript;
    }

    public String getScript() {
      if (script == null) {
        try {
          script = ((JWFScriptUtils.ScriptNode) node).getScript();
          if (!validateScript(script)) {
            script = "";
            System.err.println("INVALID SCRIPT: "+scriptPath);
          }
          script = fixScript(script);
        } catch (Exception ex) {
          script = "";
          ex.printStackTrace();
        }
      }
      return script;
    }

    private String fixScript(String script) {
      // fixes a lot of scripts from Michael Bourne, which use an illegal cast to directly access the TinaController, but do not make anything with this object later
      {
        int p0 = script.indexOf("// Turn preview button off");
        if(p0>0) {
          final String end = "l.doClick();";
          int p1 = script.indexOf(end, p0);
          if(p1>p0) {
            script = script.substring(0, p0) + script.substring(p1+end.length(), script.length());
          }
        }
      }
      return script;
    }

    private boolean validateScript(String script) {
      return script != null && !script.isEmpty() && !script.contains("showInputDialog") && !script.contains("showMessageDialog") &&
              !script.contains("showOpenDialog") &&
              !script.contains("FormBuilder") && !script.contains("getParamByName") && !script.contains("setScriptProperty") &&
              !script.contains("getScriptProperty") && !script.contains("getScriptProperty");
    }

  }

  private static List<ScriptEntry> _scripts = null;

  private static List<ScriptEntry> getScripts() {
    if (_scripts == null) {
      _scripts = new ArrayList<>();
      JTree tree = new JTree();
      JWFScriptUtils.initScriptLibrary(tree, null);
      visitAllScriptNodes(tree, _scripts, getIncludedList(), getExcludedList());
    }
    return _scripts;
  }

  private static List<String> getIncludedList() {
    List<String> included = new ArrayList<>();
    {
      String includedStr = Prefs.getPrefs().getTinaRandGenRunRandomScriptIncludedScripts();
      if(includedStr!=null && !includedStr.isEmpty()) {
        StringTokenizer t= new StringTokenizer(includedStr, ",");
        while(t.hasMoreElements()) {
          String e = t.nextToken().trim();
          if(!e.isEmpty()) {
            included.add(e);
          }
        }
      }
    }
    return included;
  }

  private static List<String> getExcludedList() {
    List<String> excluded = new ArrayList<>();
    {
      String excludedStr = Prefs.getPrefs().getTinaRandGenRunRandomScriptExcludedScripts();
      if (excludedStr != null && !excludedStr.isEmpty()) {
        StringTokenizer t = new StringTokenizer(excludedStr, ",");
        while (t.hasMoreElements()) {
          String e = t.nextToken().trim();
          if(!e.isEmpty()) {
            excluded.add(e);
          }
        }
      }
    }
    return excluded;
  }

  private static void visitAllScriptNodes(JTree tree, List<ScriptEntry> scripts, List<String> included, List<String> excluded) {
    TreeNode root = (TreeNode) tree.getModel().getRoot();
    visitAllScriptNodes(root, scripts, included, excluded);
  }

  public static void visitAllScriptNodes(TreeNode node, List<ScriptEntry> scripts, List<String> included, List<String> excluded) {
    if (node instanceof JWFScriptUserNode) {
      String scriptPath = ((JWFScriptUserNode) node).getFilename();
      if(isIncluded(scriptPath, included, excluded)) {
        scripts.add(new ScriptEntry((JWFScriptUtils.ScriptNode) node, scriptPath, true));
      }
    } else if (node instanceof JWFScriptInternalNode) {
      String scriptPath = ((JWFScriptInternalNode) node).getResFilename();
      if(isIncluded(scriptPath, included, excluded)) {
        scripts.add(new ScriptEntry((JWFScriptUtils.ScriptNode) node, scriptPath, false));
      }
    }
    if (node.getChildCount() >= 0) {
      for (Enumeration e = node.children(); e.hasMoreElements(); ) {
        TreeNode n = (TreeNode) e.nextElement();
        visitAllScriptNodes(n, scripts, included, excluded);
      }
    }
  }

  private static boolean isIncluded(String scriptPath, List<String> included, List<String> excluded) {
    String name = new File(scriptPath).getName();
    boolean _isIncluded;
    if(!included.isEmpty()) {
      _isIncluded = false;
      for(String n:included) {
        if(name.startsWith(n)) {
          _isIncluded = true;
          break;
        }
      }
    }
    else {
      _isIncluded = true;
    }
    if(!_isIncluded) {
      return false;
    }

    if(!excluded.isEmpty()) {
      for(String n:excluded) {
        if(name.startsWith(n)) {
          return false;
        }
      }
    }
    return true;
  }

  private ScriptEntry getRandomScript() {
    List<ScriptEntry> scripts = getScripts();
    while (scripts.size() > 0) {
      int idx = Math.min((int) (Math.random() * scripts.size()), scripts.size() - 1);
      ScriptEntry entry = scripts.get(idx);
      if (entry.getScript().isEmpty()) {
        scripts.remove(idx);
      } else {
        return entry;
      }
    }
    return null;
  }

  private void runScript(String scriptPath, String scriptText, final Flame flame) throws Exception {
      ScriptRunner scriptRunner = ScriptRunner.compile(scriptText);
      scriptRunner.setScriptPath(scriptPath);
      scriptRunner.run(new ScriptRunnerEnvironment() {
        @Override
        public Flame getCurrFlame() {
          return flame;
        }

        @Override
        public Flame getCurrFlame(boolean autoGenerateIfEmpty) {
          return flame;
        }

        @Override
        public void setCurrFlame(Flame pFlame) {

        }

        @Override
        public Layer getCurrLayer() {
          return flame.getFirstLayer();
        }

        @Override
        public void refreshUI() {
          // EMPTY
        }

        @Override
        public ScriptParam getParamByName(String pName) {
          // not supported
          return null;
        }

        @Override
        public void setScriptProperty(ScriptRunner runner, String propName, String propVal) {
          // not supported
        }

        @Override
        public String getScriptProperty(ScriptRunner runner, String propName) {
          // not supported
          return null;
        }

        @Override
        public String getScriptProperty(ScriptRunner runner, String propName, String defaultVal) {
          // not supported
          return null;
        }
      });
  }

}
