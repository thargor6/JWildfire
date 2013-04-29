package org.jwildfire.create.tina.dance;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.io.Flam3PaletteReader;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.ErrorHandler;

public class GradientController {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final JTree gradientLibTree;
  private DefaultMutableTreeNode userGradientsRootNode;

  public GradientController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel, JTree pGradientLibTree) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    gradientLibTree = pGradientLibTree;
    enableControls();
    initGradientsLibrary();
  }

  private static class GradientInternalNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;
    private final String resFilename;
    private final String caption;
    private String script;
    private String description;

    public GradientInternalNode(String pCaption, String pResFilename) {
      super(pCaption, false);
      caption = pCaption;
      resFilename = pResFilename;
    }

  }

  private static class GradientUserNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;
    private String caption;
    private final String filename;
    private String script;
    private String description;

    public GradientUserNode(String pCaption, String pFilename) {
      super(pCaption, false);
      filename = pFilename;
      caption = pCaption;
    }

  }

  private static class GradientFolderNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;
    private final boolean userDir;
    private final String directory;

    public GradientFolderNode(String pCaption, String pDirectory, boolean pUserDir) {
      super(pCaption, true);
      userDir = pUserDir;
      directory = pDirectory;
    }

    @Override
    public boolean isLeaf() {
      return false;
    }

    public boolean isUserDir() {
      return userDir;
    }

    public String getDirectory() {
      return directory;
    }

  }

  private static class InvalidGradientFolderNode extends GradientFolderNode {
    private static final long serialVersionUID = 1L;

    public InvalidGradientFolderNode() {
      super("(gradient-path is empty, check the Prefs)", null, false);
    }
  }

  private void initGradientsLibrary() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Gradient library", true);
    // Internal gradients
    {
      String[] ressources = { "Escher Flux", "Mobius Dragon", "Soft Julian", "Wrap into Bubble", "Wrap into Heart", "Wrap into SubFlame",
          "HypertilePoincare_Rev2", "Bwraps-bubble-Julian2", "Bwraps and bubbles", "Oily_Juliascope_Rev1", "Oily_Rev3",
          "Plastic", "SphericalCross_Rev2", "SuperSusan_Rev1", "TomsSpiralSpiral_Rev3", "Wedge_Sph_Marble" };
      List<String> resLst = Arrays.asList(ressources);
      Collections.sort(resLst);
      ressources = (String[]) resLst.toArray();

      // for the base path inside the jar file
      RGBPaletteReader reader = new Flam3PaletteReader();
      DefaultMutableTreeNode defaultFolderNode = null;
      for (String ressource : ressources) {
        try {
          String resFilename = "scripts/" + ressource + "." + Tools.FILEEXT_JWFSCRIPT;
          InputStream is = reader.getClass().getResourceAsStream(resFilename);
          if (is != null) {

            GradientInternalNode node = new GradientInternalNode(ressource, resFilename);

            if (defaultFolderNode == null) {
              defaultFolderNode = new GradientFolderNode("Built-in scripts (read-only)", null, false);
              root.add(defaultFolderNode);
            }
            defaultFolderNode.add(node);
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

    // External flames
    {
      String baseDrawer = prefs.getTinaJWFScriptPath();
      DefaultMutableTreeNode parentNode = userGradientsRootNode = new GradientFolderNode("Your scripts", baseDrawer, true);
      root.add(parentNode);
      if (baseDrawer == null || baseDrawer.equals("") || baseDrawer.equals(".") || baseDrawer.equals("/") || baseDrawer.equals("\\")) {
        parentNode.add(new InvalidGradientFolderNode());
      }
      else {
        //        scanUserGradients(baseDrawer, parentNode);
      }
    }
    gradientLibTree.setRootVisible(false);
    gradientLibTree.setModel(new DefaultTreeModel(root));
  }

  public void enableControls() {
    // TODO Auto-generated method stub

  }

}
