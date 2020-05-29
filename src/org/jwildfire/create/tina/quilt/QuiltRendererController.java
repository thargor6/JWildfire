/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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
package org.jwildfire.create.tina.quilt;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.*;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.render.*;
import org.jwildfire.create.tina.swing.FlameFileChooser;
import org.jwildfire.create.tina.swing.FlameHolder;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.TinaControllerData;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.transform.RectangleTransformer;

public class QuiltRendererController implements FlameHolder {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final TinaControllerData data;
  private FlamePanel previewFlamePanel;
  private Flame currFlame;

  public QuiltRendererController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel, TinaControllerData pData) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    data = pData;

    data.quiltRendererRenderWidthEdit.setValue(7680);
    data.quiltRendererRenderHeightEdit.setValue(4320);
    data.quiltRendererXSegmentationLevelEdit.setValue(3);
    data.quiltRendererYSegmentationLevelEdit.setValue(2);
    data.quiltRendererQualityEdit.setValue(1000);
    recalcSizes();
    enableControls();
  }

  public void recalcSizes() {
    data.quiltRendererSegmentWidthEdit.setValue(data.quiltRendererRenderWidthEdit.getIntValue()/data.quiltRendererXSegmentationLevelEdit.getIntValue());
    data.quiltRendererSegmentHeightEdit.setValue(data.quiltRendererRenderHeightEdit.getIntValue()/data.quiltRendererYSegmentationLevelEdit.getIntValue());
    refreshPreviewImage();
  }

  private QuiltRenderThread currRenderThread;

  public void setSize4K() {
    data.quiltRendererRenderWidthEdit.setText(String.valueOf(3840));
    data.quiltRendererRenderHeightEdit.setText(String.valueOf(2160));
    recalcSizes();
  }

  public void setSize8K() {
    data.quiltRendererRenderWidthEdit.setText(String.valueOf(7680));
    data.quiltRendererRenderHeightEdit.setText(String.valueOf(4320));
    recalcSizes();
  }

  public void setSize32K() {
    data.quiltRendererRenderWidthEdit.setText(String.valueOf(30720));
    data.quiltRendererRenderHeightEdit.setText(String.valueOf(17280));
    recalcSizes();
  }

  public void setSize16K() {
    data.quiltRendererRenderWidthEdit.setText(String.valueOf(15360));
    data.quiltRendererRenderHeightEdit.setText(String.valueOf(8640));
    recalcSizes();
  }

  public class ProgressUpdater implements org.jwildfire.create.tina.render.ProgressUpdater {
    private final JProgressBar progressBar;
    private final boolean refreshPreview;

    public ProgressUpdater(JProgressBar progressBar, boolean refreshPreview) {
      this.progressBar = progressBar;
      this.refreshPreview = refreshPreview;
    }

    @Override
    public void initProgress(int pMaxSteps) {
      try {
        progressBar.setValue(0);
        progressBar.setMinimum(0);
        progressBar.setMaximum(pMaxSteps);
        progressBar.invalidate();
        progressBar.validate();
      }
      catch (Throwable ex) {
        // empty
      }
    }

    @Override
    public void updateProgress(int pStep) {
      try {
        progressBar.setValue(pStep);
        progressBar.invalidate();
        progressBar.validate();
        RepaintManager manager = RepaintManager.currentManager(progressBar);
        manager.markCompletelyDirty(progressBar);
        manager.paintDirtyRegions();
        if(refreshPreview) {
          refreshPreviewImage();
        }
      }
      catch (Throwable ex) {
        // empty
      }
    }
  }

  public class QuiltRenderThread implements Runnable {
    private boolean done;
    private boolean cancelSignalled;
    private final QuiltRendererController controller;
    private QuiltFlameRenderer renderer;

    public QuiltRenderThread(QuiltRendererController controller) {
      this.controller = controller;
    }

    @Override
    public void run() {
      done = cancelSignalled = false;
      try {
        try {
          renderer = new QuiltFlameRenderer();
          Flame flame = controller.getFlame().makeCopy();
          int destWidth = controller.data.quiltRendererRenderWidthEdit.getIntValue();
          int destHeight = controller.data.quiltRendererRenderHeightEdit.getIntValue();
          double quality = controller.data.quiltRendererQualityEdit.getDoubleValue();
          int xSegmentationLevel = controller.data.quiltRendererXSegmentationLevelEdit.getIntValue();
          int ySegmentationLevel = controller.data.quiltRendererYSegmentationLevelEdit.getIntValue();
          int qualityLevel = (int)(quality+0.5); // only used for the filename
          String outputFilename = controller.data.quiltRendererOutputFilenameEdit.getText();

          double wScl = (double) destWidth / (double) flame.getWidth();
          double hScl = (double) destHeight / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(destWidth);
          flame.setHeight(destHeight);
          flame.setSampleDensity(quality);
          renderer.renderFlame(flame, destWidth, destHeight, xSegmentationLevel, ySegmentationLevel, qualityLevel, outputFilename, new ProgressUpdater(data.quiltRendererTotalProgressBar, true), new ProgressUpdater(data.quiltRendererSegmentProgressBar, false));
        }
        catch (Throwable ex) {
          throw new RuntimeException(ex);
        }
      }
      finally {
        done = true;
        controller.onRenderFinished();
      }
    }

    public boolean isDone() {
      return done;
    }

    public boolean imageWasCreated() {
      return renderer!=null && renderer.finalImageWasMerged();
    }

    public Throwable getRenderException() {
      return renderer!=null ? renderer.getRenderException() : null;
    }

    public void cancel() {
      this.cancelSignalled = true;
      if (cancelSignalled && renderer != null) {
        try {
          renderer.cancel();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

  }

  private void onRenderFinished() {
    if(currRenderThread!=null) {
      if(currRenderThread.getRenderException()!=null) {
        errorHandler.handleError(currRenderThread.getRenderException());
      }
      else if(currRenderThread.imageWasCreated()) {
        JOptionPane.showMessageDialog(data.quiltRendererPreviewRootPanel, "Final image \"" + data.quiltRendererOutputFilenameEdit.getText() + "\" was successfully saved");
      }
    }
    currRenderThread = null;
    refreshUI();
  }

  public void quiltRenderStartButton_clicked() {
    if (currRenderThread != null) {
      try {
        currRenderThread.cancel();
        currRenderThread = null;
      }
      catch(Exception ex) {
        //
      }
    }
    else {
      currRenderThread = new QuiltRenderThread(this);
      Thread thread = new Thread(currRenderThread);
      thread.setPriority(Thread.MIN_PRIORITY);
      thread.start();
    }
    refreshUI();
  }

  private FlamePanel getPreviewFlamePanel() {
    if (previewFlamePanel == null) {
      int width = Math.max(data.quiltRendererPreviewRootPanel.getWidth(), 32);
      int height = Math.max(data.quiltRendererPreviewRootPanel.getHeight(), 32);
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      previewFlamePanel = new FlamePanel(prefs, img, 0, 0, data.quiltRendererPreviewRootPanel.getWidth(), this, null, null);
      previewFlamePanel.setRenderWidth(data.quiltRendererRenderWidthEdit.getIntValue());
      previewFlamePanel.setRenderHeight(data.quiltRendererRenderHeightEdit.getIntValue());
      previewFlamePanel.setDrawTriangles(false);
      data.quiltRendererPreviewRootPanel.add(previewFlamePanel, BorderLayout.CENTER);
      data.quiltRendererPreviewRootPanel.getParent().validate();
      data.quiltRendererPreviewRootPanel.repaint();
    }
    return previewFlamePanel;
  }

  public void enableControls() {
    boolean isRendering = currRenderThread != null  && !currRenderThread.isDone();
    if(isRendering) {
      data.quiltRendererRenderButton.setText("Cancel");
    }
    else {
      data.quiltRendererRenderButton.setText("Render");
    }
    data.quiltRendererSegmentWidthEdit.setEnabled(false);
    data.quiltRendererSegmentHeightEdit.setEnabled(false);
    data.quiltRendererRenderWidthEdit.setEnabled(!isRendering);
    data.quiltRendererRenderHeightEdit.setEnabled(!isRendering);
    data.quiltRendererQualityEdit.setEnabled(!isRendering);
    data.quiltRendererXSegmentationLevelEdit.setEnabled(!isRendering);
    data.quiltRendererYSegmentationLevelEdit.setEnabled(!isRendering);
    data.quiltRendererOutputFilenameEdit.setEnabled(!isRendering);
    data.quiltRendererOpenFlameButton.setEnabled(!isRendering);
    data.quiltRendererImportFlameFromClipboardButton.setEnabled(!isRendering);
    data.quiltRendererImportFlameFromEditorButton.setEnabled(!isRendering);
    data.quiltRenderer4KButton.setEnabled(!isRendering);
    data.quiltRenderer8KButton.setEnabled(!isRendering);
    data.quiltRenderer16KButton.setEnabled(!isRendering);
    data.quiltRenderer32Button.setEnabled(!isRendering);
    data.quiltRendererRenderButton.setEnabled(true);
  }

  private void refreshUI() {
    enableControls();
    refreshPreviewImage();
  }

  public void openFlameButton_clicked() {
    try {
      JFileChooser chooser = new FlameFileChooser(prefs);
      if (prefs.getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputFlamePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(data.quiltRendererPreviewRootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
        Flame flame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        currFlame = flame;
        refreshOutputFilename();
        refreshUI();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlameFromEditorButton_clicked() {
    try {
      Flame newFlame = tinaController.exportFlame();
      if (newFlame != null) {
        currFlame = newFlame;
        refreshOutputFilename();
        refreshUI();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlameFromClipboardButton_clicked() {
    Flame newFlame = null;
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
                  DataFlavor.stringFlavor));
          List<Flame> flames = new FlameReader(prefs).readFlamesfromXML(xml);
          if (flames.size() > 0) {
            newFlame = flames.get(0);
          }
        }
      }
      if (newFlame == null) {
        throw new Exception("There is currently no valid flame in the clipboard");
      }
      else {
        currFlame = newFlame;
        refreshOutputFilename();
        refreshUI();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void refreshOutputFilename() {
    int extension = 0;
    String pathname = null;
    while(pathname==null) {
      String flamename = currFlame != null ? currFlame.getName() : null;
      if (flamename != null) {
        flamename = flamename.replaceAll("[ \\/:]", "");
      }
      if (flamename == null || flamename.isEmpty()) {
        flamename = "quilt";
      }

      String imagename = (extension++>0 ? flamename +"_"+extension : flamename) + ".png";

      String folder = prefs.getOutputImagePath();
      if (folder == null || folder.isEmpty()) {
        pathname = imagename;
      } else {
        pathname = new File(folder, imagename).getAbsolutePath();
      }
      if(new File(pathname).exists()) {
        pathname=null;
      }
    }
    data.quiltRendererOutputFilenameEdit.setText(pathname);
  }

  public void refreshPreviewImage() {
    FlamePanel imgPanel = getPreviewFlamePanel();
    imgPanel.setRenderWidth(data.quiltRendererRenderWidthEdit.getIntValue());
    imgPanel.setRenderHeight(data.quiltRendererRenderHeightEdit.getIntValue());

    Rectangle bounds = imgPanel.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;
    Flame flame = (getFlame()!=null ? getFlame().makeCopy(): null);
    if (flame!=null && width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
      if (flame != null) {
        double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
        double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
        flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
        flame.setWidth(info.getImageWidth());
        flame.setHeight(info.getImageHeight());

        FlameRenderer renderer = new FlameRenderer(flame, prefs, data.toggleTransparencyButton.isSelected(), false);
        flame.setSampleDensity(Math.min(prefs.getTinaRenderRealtimeQuality(), 5.0));

        flame.setSpatialFilterRadius(0.0);
        RenderedFlame res = renderer.renderFlame(info);

        SimpleImage image = res.getImage();
        addSegmentBorders(image);
        imgPanel.setImage(image);
      }
      else {
        imgPanel.setImage(new SimpleImage(width, height));
      }
    }
    data.quiltRendererPreviewRootPanel.invalidate();
    data.quiltRendererPreviewRootPanel.validate();
  }

  private void addSegmentBorders(SimpleImage image) {
    RectangleTransformer rect = new RectangleTransformer();
    int xSegmentationLevel = data.quiltRendererXSegmentationLevelEdit.getIntValue();
    int ySegmentationLevel = data.quiltRendererYSegmentationLevelEdit.getIntValue();
    String filename = data.quiltRendererOutputFilenameEdit.getText();
    int destWidth = data.quiltRendererRenderWidthEdit.getIntValue();
    int destHeight = data.quiltRendererRenderHeightEdit.getIntValue();
    int qualityLevel = (int)(data.quiltRendererQualityEdit.getDoubleValue()+0.5);

    QuiltFlameRenderer renderer = new QuiltFlameRenderer();

    int spentHeight = 0;
    for(int i=0;i<ySegmentationLevel;i++) {
      int height = (int)((double)image.getImageHeight()/(double)ySegmentationLevel + 0.5);
      int spentWidth = 0;
      for(int j=0;j<xSegmentationLevel;j++) {
        int width = (int)((double)image.getImageWidth()/(double)xSegmentationLevel + 0.5);

        String segmentFilename = renderer.getSegmentFilename(filename, destWidth, destHeight, xSegmentationLevel, ySegmentationLevel, qualityLevel, j, i);
        boolean isRendered = new File(segmentFilename).exists();

        if(isRendered) {
          rect.setColor(new Color(128, 255, 32));
          rect.setThickness(7);
        }
        else {
          rect.setColor(new Color(255, 128, 32));
          rect.setThickness(5);
        }

        rect.setLeft(spentWidth);
        rect.setTop(spentHeight);
        rect.setWidth(j < xSegmentationLevel - 1 ? width + 1 : image.getImageWidth() - spentWidth);
        rect.setHeight(i < ySegmentationLevel - 1 ? height + 1: image.getImageHeight() - spentHeight );
        rect.transformImage(image);
        spentWidth += width;
      }
      spentHeight += height;
    }
  }

  @Override
  public Flame getFlame() {
    return currFlame;
  }

}
