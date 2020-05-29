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
package org.jwildfire.create.tina.batch;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.faclrender.FACLRenderTools;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.FlameFileChooser;
import org.jwildfire.create.tina.swing.FlameHolder;
import org.jwildfire.create.tina.swing.StandardDialogs;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.TinaControllerData;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;

public class BatchRendererController implements JobRenderThreadController {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final TinaControllerData data;
  private final List<Job> batchRenderList = new ArrayList<Job>();
  private final ProgressUpdater jobProgressUpdater;
  private FlamePanel batchPreviewFlamePanel;
  private final JCheckBox batchRenderOverrideCBx;
  private final JButton batchRenderShowImageBtn;
  private final JToggleButton enableOpenClBtn;

  public BatchRendererController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel, TinaControllerData pData, ProgressUpdater pJobProgressUpdater,
      JCheckBox pBatchRenderOverrideCBx, JButton pBatchRenderShowImageBtn, JToggleButton pEnableOpenClBtn) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    data = pData;
    jobProgressUpdater = pJobProgressUpdater;
    batchRenderOverrideCBx = pBatchRenderOverrideCBx;
    batchRenderShowImageBtn = pBatchRenderShowImageBtn;
    enableOpenClBtn = pEnableOpenClBtn;
    if (!FACLRenderTools.isFaclRenderAvalailable()) {
      enableOpenClBtn.setSelected(false);
      enableOpenClBtn.setEnabled(false);
      enableOpenClBtn.setVisible(false);
    }
  }

  private JobRenderThread jobRenderThread = null;

  public void batchRenderStartButton_clicked() {
    if (jobRenderThread != null) {
      jobRenderThread.setCancelSignalled(true);
      return;
    }
    List<Job> activeJobList = new ArrayList<Job>();
    for (Job job : batchRenderList) {
      if (batchRenderOverrideCBx.isSelected() || !job.isFinished()) {
        activeJobList.add(job);
      }
    }
    if (activeJobList.size() > 0) {
      jobRenderThread = new JobRenderThread(this, activeJobList, (ResolutionProfile) data.batchResolutionProfileCmb.getSelectedItem(), (QualityProfile) data.batchQualityProfileCmb.getSelectedItem(), batchRenderOverrideCBx.isSelected(), enableOpenClBtn.isSelected());
      new Thread(jobRenderThread).start();
    }
    enableJobRenderControls();
  }

  public void batchRenderFilesRemoveAllButton_clicked() {
    if (jobRenderThread != null) {
      return;
    }
    batchRenderList.clear();
    refreshRenderBatchJobsTable();
  }

  public void batchRenderFilesRemoveButton_clicked() {
    if (jobRenderThread != null) {
      return;
    }
    int row = data.renderBatchJobsTable.getSelectedRow();
    if (row >= 0 && row < batchRenderList.size()) {
      batchRenderList.remove(row);
      refreshRenderBatchJobsTable();
      if (row >= batchRenderList.size()) {
        row--;
      }
      if (row >= 0 && row < batchRenderList.size()) {
        data.renderBatchJobsTable.getSelectionModel().setSelectionInterval(row, row);
      }
    }
  }

  public void enableJobRenderControls() {
    boolean idle = jobRenderThread == null;
    data.batchRenderAddFilesButton.setEnabled(idle);
    data.batchRenderFilesMoveDownButton.setEnabled(idle);
    data.batchRenderFilesMoveUpButton.setEnabled(idle);
    data.batchRenderFilesRemoveButton.setEnabled(idle);
    data.batchRenderFilesRemoveAllButton.setEnabled(idle);
    data.batchRenderStartButton.setText(idle ? "Render" : "Stop");
    data.batchRenderStartButton.invalidate();
    data.batchRenderStartButton.validate();
    //    rootPanel.setEnabled(idle);
  }

  public void batchRenderAddFilesButton_clicked() {
    if (jobRenderThread != null) {
      return;
    }
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
      int jobCount = batchRenderList.size();
      chooser.setMultiSelectionEnabled(true);
      if (chooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
        for (File file : chooser.getSelectedFiles()) {
          addFlameToBatchRenderer(file.getPath(), false);
        }
      }
      if (jobCount != batchRenderList.size()) {
        refreshRenderBatchJobsTable();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void addFlameToBatchRenderer(String filename, boolean refreshTable) {
    boolean hasFile = false;
    for (Job job : batchRenderList) {
      if (job.getFlameFilename().equals(filename)) {
        hasFile = true;
        break;
      }
    }
    if (!hasFile) {
      Job job = new Job();
      job.setFlameFilename(filename);
      batchRenderList.add(job);
      if (refreshTable) {
        refreshRenderBatchJobsTable();
      }
    }
  }

  public void batchRenderFilesMoveUpButton_clicked() {
    if (jobRenderThread != null) {
      return;
    }
    int row = data.renderBatchJobsTable.getSelectedRow();
    if (row < 0 && batchRenderList.size() > 0) {
      row = 0;
      data.renderBatchJobsTable.getSelectionModel().setSelectionInterval(row, row);
    }
    else if (row > 0 && row < batchRenderList.size()) {
      Job t = batchRenderList.get(row - 1);
      batchRenderList.set(row - 1, batchRenderList.get(row));
      batchRenderList.set(row, t);
      refreshRenderBatchJobsTable();
      data.renderBatchJobsTable.getSelectionModel().setSelectionInterval(row - 1, row - 1);
    }
  }

  public void batchRenderFilesMoveDownButton_clicked() {
    if (jobRenderThread != null) {
      return;
    }
    int row = data.renderBatchJobsTable.getSelectedRow();
    if (row < 0 && batchRenderList.size() > 0) {
      row = 0;
      data.renderBatchJobsTable.getSelectionModel().setSelectionInterval(row, row);
    }
    else if (row >= 0 && row < batchRenderList.size() - 1) {
      Job t = batchRenderList.get(row + 1);
      batchRenderList.set(row + 1, batchRenderList.get(row));
      batchRenderList.set(row, t);
      refreshRenderBatchJobsTable();
      data.renderBatchJobsTable.getSelectionModel().setSelectionInterval(row + 1, row + 1);
    }
  }

  @Override
  public void onJobFinished() {
    jobRenderThread = null;
    enableJobRenderControls();
  }

  @Override
  public JTable getRenderBatchJobsTable() {
    return data.renderBatchJobsTable;
  }

  @Override
  public void refreshRenderBatchJobsTable() {
    final int COL_FLAME = 0;
    final int COL_CUSTOM_SIZE = 1;
    final int COL_CUSTOM_QUALITY = 2;
    final int COL_STATE = 3;
    final int COL_ELAPSED = 4;
    final int COL_LAST_ERROR = 5;
    data.renderBatchJobsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return batchRenderList.size();
      }

      @Override
      public int getColumnCount() {
        return 6;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_FLAME:
            return "Flame";
          case COL_CUSTOM_SIZE:
            return "Custom size";
          case COL_CUSTOM_QUALITY:
            return "Custom quality";
          case COL_STATE:
            return "State";
          case COL_ELAPSED:
            return "Elapsed time (seconds)";
          case COL_LAST_ERROR:
            return "Last error";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        Job job = rowIndex < batchRenderList.size() ? batchRenderList.get(rowIndex) : null;
        if (job != null) {
          switch (columnIndex) {
            case COL_FLAME:
              return new File(job.getFlameFilename()).getName();
            case COL_CUSTOM_SIZE:
              return job.getCustomWidth() > 0 || job.getCustomHeight() > 0 ? job.getCustomWidth() + "x" + job.getCustomHeight() : "";
            case COL_CUSTOM_QUALITY:
              return job.getCustomQuality() > 0 ? Integer.valueOf(job.getCustomQuality()) : "";
            case COL_STATE:
              return job.isFinished() ? "ready" : "";
            case COL_ELAPSED:
              return job.isFinished() ? Tools.doubleToString(job.getElapsedSeconds()) : "";
            case COL_LAST_ERROR:
              return job.getLastErrorMsg();
          }
        }
        return null;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return column == COL_CUSTOM_SIZE || column == COL_CUSTOM_QUALITY;
      }

      @Override
      public void setValueAt(Object aValue, int row, int column) {
        Job job = getCurrJob();
        if (job != null) {
          String valStr = (String) aValue;
          switch (column) {
            case COL_CUSTOM_QUALITY:
              try {
                if (valStr == null || valStr.trim().length() == 0) {
                  job.setCustomQuality(0);
                }
                else {
                  int quality = Integer.parseInt(valStr);
                  if (quality < 16)
                    throw new RuntimeException();
                  job.setCustomQuality(quality);
                }
              }
              catch (Throwable ex) {
                errorHandler.handleError(new Exception("Invalid quality value <" + valStr + ">"));
              }
              break;
            case COL_CUSTOM_SIZE:
              try {
                if (valStr == null || valStr.trim().length() == 0) {
                  job.setCustomWidth(0);
                  job.setCustomHeight(0);
                }
                else {
                  int p = valStr.toLowerCase().indexOf("x");
                  if (p < 0)
                    throw new RuntimeException();
                  int width = Integer.parseInt(valStr.substring(0, p).trim());
                  int height = Integer.parseInt(valStr.substring(p + 1, valStr.length()).trim());
                  if (width < 16 || height < 16)
                    throw new RuntimeException();
                  job.setCustomWidth(width);
                  job.setCustomHeight(height);
                }
              }
              catch (Throwable ex) {
                errorHandler.handleError(new Exception("Invalid size <" + valStr + ">, size must be specified in the format <width>x<height>, e.g. 1920x1080"));
              }
              break;
            default: // nothing to do
              break;
          }
        }
        super.setValueAt(aValue, row, column);
      }

    });
    data.renderBatchJobsTable.getTableHeader().setFont(data.transformationsTable.getFont());
    data.renderBatchJobsTable.getColumnModel().getColumn(COL_FLAME).setWidth(120);
    data.renderBatchJobsTable.getColumnModel().getColumn(COL_CUSTOM_QUALITY).setWidth(40);
    data.renderBatchJobsTable.getColumnModel().getColumn(COL_CUSTOM_SIZE).setWidth(60);
    data.renderBatchJobsTable.getColumnModel().getColumn(COL_STATE).setPreferredWidth(10);
    data.renderBatchJobsTable.getColumnModel().getColumn(COL_ELAPSED).setWidth(10);
    data.renderBatchJobsTable.getColumnModel().getColumn(COL_LAST_ERROR).setWidth(120);
    if (batchRenderList.size() > 0)
      data.renderBatchJobsTable.setRowSelectionInterval(0, 0);
  }

  @Override
  public JProgressBar getTotalProgressBar() {
    return data.batchRenderTotalProgressBar;
  }

  @Override
  public JProgressBar getJobProgressBar() {
    return data.batchRenderJobProgressBar;
  }

  @Override
  public ProgressUpdater getJobProgressUpdater() {
    return jobProgressUpdater;
  }

  private Job getCurrJob() {
    int row = data.renderBatchJobsTable.getSelectedRow();
    if (row >= 0 && row < batchRenderList.size()) {
      return batchRenderList.get(row);
    }
    return null;
  }

  private class BatchRenderPreviewFlameHolder implements FlameHolder {

    @Override
    public Flame getFlame() {
      try {
        Job job = getCurrJob();
        if (job != null) {
          List<Flame> flames = new FlameReader(prefs).readFlames(job.getFlameFilename());
          return flames.size() > 0 ? flames.get(0) : null;
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      return null;
    }

  }

  private BatchRenderPreviewFlameHolder batchRenderPreviewFlameHolder = null;

  private BatchRenderPreviewFlameHolder getBatchRenderPreviewFlameHolder() {
    if (batchRenderPreviewFlameHolder == null) {
      batchRenderPreviewFlameHolder = new BatchRenderPreviewFlameHolder();
    }
    return batchRenderPreviewFlameHolder;
  }

  private ResolutionProfile getBatchRenderResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) data.batchResolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  private FlamePanel getBatchPreviewFlamePanel() {
    if (batchPreviewFlamePanel == null) {
      int width = Math.max(data.batchPreviewRootPanel.getWidth(), 32);
      int height = Math.max(data.batchPreviewRootPanel.getHeight(), 32);
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      batchPreviewFlamePanel = new FlamePanel(prefs, img, 0, 0, data.batchPreviewRootPanel.getWidth(), getBatchRenderPreviewFlameHolder(), null, null);
      ResolutionProfile resProfile = getBatchRenderResolutionProfile();
      batchPreviewFlamePanel.setRenderWidth(resProfile.getWidth());
      batchPreviewFlamePanel.setRenderHeight(resProfile.getHeight());
      batchPreviewFlamePanel.setDrawTriangles(false);
      data.batchPreviewRootPanel.add(batchPreviewFlamePanel, BorderLayout.CENTER);
      data.batchPreviewRootPanel.getParent().validate();
      data.batchPreviewRootPanel.repaint();
    }
    return batchPreviewFlamePanel;
  }

  public void renderBatchJobsTableClicked() {
    FlamePanel imgPanel = getBatchPreviewFlamePanel();
    Rectangle bounds = imgPanel.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;
    Flame flame = getBatchRenderPreviewFlameHolder().getFlame();
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
      if (flame != null) {
        double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
        double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
        flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
        flame.setWidth(info.getImageWidth());
        flame.setHeight(info.getImageHeight());

        FlameRenderer renderer = new FlameRenderer(flame, prefs, data.toggleTransparencyButton.isSelected(), false);
        flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
        flame.setSpatialFilterRadius(0.0);
        RenderedFlame res = renderer.renderFlame(info);
        imgPanel.setImage(res.getImage());
      }
      else {
        imgPanel.setImage(new SimpleImage(width, height));
      }
    }
    data.batchPreviewRootPanel.invalidate();
    data.batchPreviewRootPanel.validate();
  }

  public void batchRendererResolutionProfileCmb_changed() {
    if (batchPreviewFlamePanel != null) {
      data.batchPreviewRootPanel.remove(batchPreviewFlamePanel);
      batchPreviewFlamePanel = null;
    }
    renderBatchJobsTableClicked();
  }

  public void showImageBtn_clicked() {
    try {
      Job job = getCurrJob();
      if (job != null) {
        List<Flame> flames = new FlameReader(Prefs.getPrefs()).readFlames(job.getFlameFilename());
        Flame flame = flames.get(0);
        String primaryFilename = job.getImageFilename(flame.getStereo3dMode());
        File imageFile = new File(primaryFilename);
        if (imageFile.exists()) {
          tinaController.getMainController().loadImage(imageFile.getAbsolutePath(), false);
        }
        else {
          StandardDialogs.message(rootPanel, "No rendered image found");
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlame(String pFilename, ResolutionProfile pResolutionProfile, QualityProfile pQualityProfile) {
    Job job = new Job();
    job.setFlameFilename(pFilename);
    if (pResolutionProfile != null) {
      job.setCustomWidth(pResolutionProfile.getWidth());
      job.setCustomHeight(pResolutionProfile.getHeight());
    }
    if (pQualityProfile != null) {
      job.setCustomQuality(pQualityProfile.getQuality());
    }
    batchRenderList.add(job);
    refreshRenderBatchJobsTable();
  }
}
