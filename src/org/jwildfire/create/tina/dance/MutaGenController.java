package org.jwildfire.create.tina.dance;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;

public class MutaGenController {
  public static final int PAGE_INDEX = 2;
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel flamePanels[];
  private ImagePanel imagePanels[][];
  private final int MUTA_ROWS = 5;
  private final int MUTA_COLS = 5;
  private final List<MutationSet> mutationList = new ArrayList<MutationSet>();
  private MutationSet selectedSet = new MutationSet(MUTA_ROWS, MUTA_COLS, (Flame) null);

  private class MutationSet {
    final int rows, cols;
    final Flame[][] flames;

    public MutationSet(int pRows, int pCols, Flame pFlame) {
      rows = pRows;
      cols = pCols;
      flames = new Flame[rows][cols];
      flames[rows / 2][cols / 2] = pFlame;
    }

    public MutationSet(int pRows, int pCols, List<Flame> pFlames) {
      rows = pRows;
      cols = pCols;
      flames = new Flame[rows][cols];
      int idx = 0;
      for (int i = 0; i < pRows; i++) {
        for (int j = 0; j < pCols; j++) {
          flames[i][j] = pFlames.get(idx++);
        }
      }
    }

    public int getRows() {
      return rows;
    }

    public int getCols() {
      return cols;
    }

    public Flame getFlame(int pRow, int pCol) {
      return flames[pRow][pCol];
    }

  }

  public MutaGenController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pFlamePanels[]) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    flamePanels = pFlamePanels;
    prefs = pPrefs;
  }

  public void importFlame(Flame pFlame) {
    Flame baseFlame = pFlame.makeCopy();
    MutationSet set = new MutationSet(MUTA_ROWS, MUTA_COLS, baseFlame);
    mutationList.add(set);
    selectedSet = set;
    refreshFlameImages();
    //    AnimationModelService.setRandomFlameProperty(flame, 5.0);

  }

  private void refreshFlameImages() {
    createImagePanels();
    if (selectedSet != null) {
      for (int i = 0; i < selectedSet.getRows(); i++) {
        for (int j = 0; j < selectedSet.getCols(); j++) {
          ImagePanel pnl = imagePanels[i][j];
          Dimension size = pnl.getSize();
          int imageWidth = Tools.FTOI(size.getWidth());
          int imageHeight = Tools.FTOI(size.getHeight());
          Flame flame = selectedSet.getFlame(i, j);
          SimpleImage img;
          if (flame != null && imageWidth > 16 && imageHeight > 16) {
            RenderInfo info = new RenderInfo(imageWidth, imageHeight);
            double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
            double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
            flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
            flame.setWidth(imageWidth);
            flame.setHeight(imageHeight);
            FlameRenderer renderer = new FlameRenderer(flame, prefs, false);
            RenderedFlame res = renderer.renderFlame(info);
            img = res.getImage();
          }
          else {
            img = new SimpleImage(imageWidth, imageHeight);
          }
          pnl.setImage(img);
        }
      }
    }
    flamePanels[0].getParent().getParent().invalidate();
    flamePanels[0].getParent().getParent().validate();
    flamePanels[0].getParent().getParent().repaint();
  }

  private void createImagePanels() {
    if (imagePanels == null) {
      try {
        final int rows = MUTA_ROWS;
        final int cols = MUTA_COLS;
        final int BORDER_SIZE = 0;

        imagePanels = new ImagePanel[rows][cols];
        int idx = 0;
        for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
            JPanel panel = flamePanels[idx++];
            Dimension size = panel.getSize();
            SimpleImage img = new SimpleImage(Tools.FTOI(size.getWidth()), Tools.FTOI(size.getHeight()));
            ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
            imagePanels[i][j] = imgPanel;
            imgPanel.setImage(img);
            imgPanel.setLocation(BORDER_SIZE, BORDER_SIZE);
            panel.add(imgPanel);
          }
        }
      }
      catch (Throwable ex) {
        imagePanels = null;
        errorHandler.handleError(ex);
      }
    }
  }
}
