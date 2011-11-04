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
package org.jwildfire.swing;

import java.awt.Point;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Preset;
import org.jwildfire.base.Tools;
import org.jwildfire.create.ImageCreator;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.io.SunFlowWriter;
import org.jwildfire.loader.ImageLoader;
import org.jwildfire.script.Action;
import org.jwildfire.script.ActionList;
import org.jwildfire.script.Parameter;
import org.jwildfire.swing.Buffer.BufferType;
import org.jwildfire.transform.Transformer;

public class MainController {
  private final JDesktopPane desktop;
  private final JMenu windowMenu;
  private final JComboBox transformerInputCmb;
  private final JComboBox transformerPresetCmb;
  private final JComboBox creatorsPresetCmb;
  private final JDialog showMessageDlg;
  private final JTextArea showMessageDlgTextArea;
  private final JTable actionTable;
  private final JTextArea scriptActionTextArea;

  private final JSlider scriptFrameSlider;
  private final JTextField scriptFramesREd;
  private final JTextField scriptFrameREd;

  private final EnvelopeController envelopeController;
  private final RenderController renderController;

  private final ActionList actionList = new ActionList();

  private PropertyPanel currTransformerPropertyPanel;
  private PropertyPanel currCreatorPropertyPanel;
  private PropertyPanel currLoaderPropertyPanel;

  private final ErrorHandler errorHandler;
  private final Prefs prefs;

  private boolean executing = false;

  private final ScriptProcessor scriptProcessor;

  public BufferList getBufferList() {
    return scriptProcessor.getBufferList();
  }

  public ActionList getActionList() {
    return actionList;
  }

  // private JFileChooser jImageFileChooser = null;
  private JFileChooser jScriptFileChooser = null;

  private JFileChooser getImageJFileChooser() {
    /*
     * if (jImageFileChooser == null) { jImageFileChooser = new
     * JFileChooser(); jImageFileChooser.addChoosableFileFilter(new
     * ImageFileFilter());
     * jImageFileChooser.setAcceptAllFileFilterUsed(false); } return
     * jImageFileChooser;
     */
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.addChoosableFileFilter(new ImageFileFilter());
    fileChooser.setAcceptAllFileFilterUsed(false);
    return fileChooser;
  }

  private JFileChooser getScriptJFileChooser() {
    if (jScriptFileChooser == null) {
      jScriptFileChooser = new JFileChooser();
      jScriptFileChooser.addChoosableFileFilter(new ScriptFileFilter());
      jScriptFileChooser.setAcceptAllFileFilterUsed(false);
    }
    return jScriptFileChooser;
  }

  public MainController(
      Prefs pPrefs, ErrorHandler pErrorHandler,
      JDesktopPane pJDesktopPane, JMenu pWindowMenu,
      JComboBox pTransformerInputCmb, JComboBox pTransformerPresetCmb,
      JComboBox pCreatorsPresetCmb, JDialog pShowMessageDlg,
      JTextArea pShowMessageDlgTextArea, JTable pActionTable,
      JTextArea pScriptActionTextArea, JSlider pScriptFrameSlider,
      JTextField pScriptFramesREd, JTextField pScriptFrameREd,
      EnvelopeController pEnvelopeController,
      RenderController pRenderController) {
    prefs = pPrefs;
    errorHandler = pErrorHandler;
    desktop = pJDesktopPane;
    windowMenu = pWindowMenu;
    transformerInputCmb = pTransformerInputCmb;
    transformerPresetCmb = pTransformerPresetCmb;
    creatorsPresetCmb = pCreatorsPresetCmb;
    showMessageDlg = pShowMessageDlg;
    showMessageDlgTextArea = pShowMessageDlgTextArea;
    actionTable = pActionTable;
    scriptActionTextArea = pScriptActionTextArea;
    scriptFrameSlider = pScriptFrameSlider;
    scriptFramesREd = pScriptFramesREd;
    scriptFrameREd = pScriptFrameREd;
    envelopeController = pEnvelopeController;
    renderController = pRenderController;
    scriptProcessor = new ScriptProcessor(pJDesktopPane);
    scriptProcessor.getBufferList().setSyncWithStaticBufferList(true);
    scriptProcessor.setAddBuffersToDesktop(true);
  }

  public Buffer loadImage(String pFilename, boolean pRecordAction)
      throws Exception {
    Buffer buffer = scriptProcessor.loadImage(pFilename);
    if (pRecordAction)
      actionList.addLoadImageAction(pFilename);
    refreshActionTable();
    addEvents(buffer);
    refreshWindowMenu();
    return buffer;
  }

  public Buffer loadImage(boolean pRecordAction) throws Exception {
    JFileChooser chooser = getImageJFileChooser();
    try {
      chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    if (chooser.showOpenDialog(windowMenu) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      prefs.setLastInputImageFile(file);
      return loadImage(file.getAbsolutePath(), pRecordAction);
    }
    else
      return null;
  }

  public void selectTransformer(String pName) {
    if (!executing) {
      scriptProcessor.selectTransformer(pName);
      lastInitBuffer = null;
    }
  }

  public void executeTransformer(String pInputName, boolean pStoreMesh3D,
      String pOutputName, String pOutput3DName, boolean pRecordAction) {
    executing = true;
    try {
      ScriptProcessor.TransformResult buffers = scriptProcessor
          .executeTransformer(pInputName, pStoreMesh3D, pOutputName,
              pOutput3DName, pRecordAction);
      if (buffers.getOutBuffer() != null)
        addEvents(buffers.getOutBuffer());
      if (buffers.getOutBuffer3D() != null)
        addEvents(buffers.getOutBuffer3D());
      if (pRecordAction)
        actionList.addExecuteTransformerAction(
            scriptProcessor.getTransformer(),
            buffers.getInBuffer(), buffers.getOutBuffer(),
            buffers.getOutBuffer3D());
      refreshActionTable();
      refreshWindowMenu();
      desktop.repaint();
    }
    finally {
      executing = false;
    }
  }

  public Transformer getTransformer() {
    return scriptProcessor.getTransformer();
  }

  public void selectCreator(String pName) {
    if (!executing)
      scriptProcessor.selectCreator(pName);
  }

  public ImageCreator getCreator() {
    return scriptProcessor.getCreator();
  }

  public Buffer executeCreator(int pWidth, int pHeight, String pOutputName,
      boolean pRecordAction) {
    executing = true;
    try {
      Buffer buffer = scriptProcessor.executeCreator(pWidth, pHeight,
          pOutputName, pRecordAction);
      addEvents(buffer);
      refreshWindowMenu();
      if (pRecordAction)
        actionList.addExecuteImageCreatorAction(
            scriptProcessor.getCreator(), buffer, pWidth, pHeight);
      refreshActionTable();
      return buffer;
    }
    finally {
      executing = false;
    }
  }

  public void selectLoader(String pName) {
    if (!executing)
      scriptProcessor.selectLoader(pName);
  }

  public ImageLoader getLoader() {
    return scriptProcessor.getLoader();
  }

  public Buffer executeLoader(String pOutputName, boolean pRecordAction) {
    executing = true;
    try {
      Buffer buffer = scriptProcessor.executeLoader(pOutputName,
          pRecordAction);
      addEvents(buffer);
      refreshWindowMenu();
      if (pRecordAction)
        actionList.addExecuteImageLoaderAction(
            scriptProcessor.getLoader(), buffer);
      refreshActionTable();
      return buffer;
    }
    finally {
      executing = false;
    }
  }

  private void addEvents(Buffer pBuffer) {
    JInternalFrame frame = pBuffer.getInternalFrame();
    frame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
      public void internalFrameClosed(
          javax.swing.event.InternalFrameEvent e) {
        JInternalFrame frame = (JInternalFrame) e.getSource();
        BufferList bufferList = getBufferList();
        for (Buffer buffer : bufferList) {
          if (frame == buffer.getInternalFrame()) {
            bufferList.remove(buffer);
            actionList.removeBuffer(buffer, getBufferList());
            refreshWindowMenu();
            refreshActionTable();
            break;
          }
        }
      }
    });
  }

  private Buffer getActiveBuffer() {
    BufferList bufferList = getBufferList();
    for (Buffer buffer : bufferList) {
      if (buffer.getInternalFrame().isSelected())
        return buffer;
    }
    return null;
  }

  public void saveImage() throws Exception {
    Buffer buffer = getActiveBuffer();
    if (buffer != null) {
      JFileChooser chooser = getImageJFileChooser();
      try {
        chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      if (chooser.showSaveDialog(windowMenu) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastOutputImageFile(file);
        if (buffer.getBufferType() == BufferType.IMAGE) {
          new ImageWriter().saveImage(buffer.getImage(),
              file.getAbsolutePath());
        }
        else if (buffer.getBufferType() == BufferType.MESH3D) {
          new SunFlowWriter().saveMesh(buffer.getMesh3D(), file.getAbsolutePath());

        }
        else {
          showMessage("Not supported");
          return;
        }
      }
    }
  }

  public void handleError(Throwable pThrowable) {
    errorHandler.handleError(pThrowable);
  }

  private final static int DESIGNED_WINDOW_MENU_COUNT = 5;

  public void refreshWindowMenu() {
    while (windowMenu.getItemCount() > DESIGNED_WINDOW_MENU_COUNT)
      windowMenu.remove(windowMenu.getItemCount() - 1);
    if (getBufferList().size() > 0) {
      windowMenu.addSeparator();
      BufferList buffers = getBufferList();
      for (int i = 0; i < buffers.size(); i++) {
        Buffer buffer = buffers.get(i);
        JMenuItem menuItem = new JMenuItem();
        menuItem.setText(buffer.getName());
        menuItem.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent e) {
            windowMenuItem_actionPerformed(e);
          }
        });
        windowMenu.add(menuItem);
      }
    }
    fillInputBufferCmb();
  }

  private void windowMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    try {
      JMenuItem menuItem = (JMenuItem) e.getSource();
      int off = DESIGNED_WINDOW_MENU_COUNT + 1; // +1 because of the
      // separator
      for (int i = off; i < windowMenu.getItemCount(); i++) {
        if (windowMenu.getItem(i) == menuItem) {
          int idx = i - off;
          Buffer buffer = getBufferList().get(idx);
          buffer.getInternalFrame().toFront();
          buffer.getInternalFrame().setSelected(true);
          break;
        }
      }
    }
    catch (Throwable ex) {
      handleError(ex);
    }
  }

  public void fillInputBufferCmb() {
    Object selItem = transformerInputCmb.getSelectedItem();
    transformerInputCmb.removeAllItems();
    Transformer transformer = scriptProcessor.getTransformer();
    if (transformer != null) {
      for (Buffer buffer : getBufferList()) {
        if (transformer.acceptsInputBufferType(buffer.getBufferType()))
          transformerInputCmb.addItem(buffer.getName());
      }
    }
    if (transformerInputCmb.getItemCount() == 1)
      transformerInputCmb.setSelectedIndex(0);
    else if (selItem != null)
      transformerInputCmb.setSelectedItem(selItem);
  }

  private String lastInitBuffer = null;

  public void setTransformerInput(String pName) {
    if (executing)
      return;
    Transformer transformer = scriptProcessor.getTransformer();
    if ((transformer != null)
        && ((lastInitBuffer == null) || !lastInitBuffer.equals(pName))) {
      lastInitBuffer = pName;
      Buffer buffer = getBufferList().bufferByName(pName);
      if ((buffer != null)
          && (buffer.getBufferType() == BufferType.IMAGE)) {
        System.out.println(transformer + ".initDefaultParams(" + pName
            + ")");
        transformer.initDefaultParams(buffer.getImage());
        currTransformerPropertyPanel.writeToObject(transformer);
      }
    }
  }

  private void showMessage(String pMessage) {
    Point dPos = desktop.getRootPane().getLocation();
    int dWidth = desktop.getWidth();
    int dHeight = desktop.getHeight();
    int wWidth = showMessageDlg.getWidth();
    int wHeight = showMessageDlg.getHeight();
    showMessageDlg.setLocation(dPos.x + (dWidth - wWidth) / 2, dPos.y
        + (dHeight - wHeight) / 2);
    showMessageDlgTextArea.setText(pMessage);
    showMessageDlgTextArea.select(0, 0);
    showMessageDlg.setVisible(true);
  }

  public void closeShowMessageDlg() {
    showMessageDlg.setVisible(false);
  }

  private void clearAllBuffers() {
    BufferList bufferList = getBufferList();
    while (bufferList.size() > 0) {
      Buffer buffer = bufferList.get(bufferList.size() - 1);
      JInternalFrame frame = buffer.getInternalFrame();
      if (frame.isIcon()) {
        try {
          frame.setIcon(false);
        }
        catch (Throwable e) {
          e.printStackTrace();
        }
      }
      desktop.remove(frame);
      bufferList.remove(buffer);
    }
  }

  public boolean closeAll() {
    String msg = "Do you really want to close all buffers?";
    String title = msg;
    if (JOptionPane.showConfirmDialog(desktop, msg, title,
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      clearAllBuffers();
      refreshWindowMenu();
      actionList.clear();
      refreshActionTable();
      desktop.repaint();
      return true;
    }
    else
      return false;
  }

  public boolean clearScript() {
    if (actionList.size() > 0) {
      String msg = "Do you really want to clear the current script?";
      String title = msg;
      if (JOptionPane.showConfirmDialog(desktop, msg, title,
          JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        actionList.clear();
        refreshActionTable();
        return true;
      }
    }
    return false;
  }

  private ListSelectionListener listener = null;

  private void refreshActionTable() {
    scriptActionTextArea.setText(null);
    if (listener != null) {
      actionTable.getSelectionModel().removeListSelectionListener(
          listener);
      listener = null;
    }
    actionTable.setModel(new ActionTableModel());
    listener = new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        revertScriptAction();
      }
    };
    actionTable.getSelectionModel().addListSelectionListener(listener);
  }

  private class ActionTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private String[] columnNames = { "Action", "Parameter", "Input",
        "Output", "Output3D" };

    @Override
    public int getColumnCount() {
      return columnNames.length;
    }

    @Override
    public int getRowCount() {
      return actionList.size();
    }

    @Override
    public String getColumnName(int col) {
      return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
      if ((row >= 0) && (row < actionList.size())) {
        Action a = actionList.get(row);
        switch (col) {
          case 0:
            return a.getActionType().toString();
          case 1:
            return a.getParameter();
          case 2:
            return a.getInputBuffer();
          case 3:
            return a.getOutputBuffer();
          case 4:
            return a.getOutputBuffer3D();
        }
      }
      return null;
    }
  }

  public void saveScript() throws Exception {
    if (actionList.size() > 0) {
      JFileChooser chooser = getScriptJFileChooser();
      try {
        chooser.setCurrentDirectory(new File(prefs.getScriptPath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      if (chooser.showSaveDialog(windowMenu) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastScriptFile(file);
        String filename = Tools.forceFileExt(file.getAbsolutePath(),
            Tools.FILEEXT_JFX);
        actionList.saveToFile(filename);
      }
    }
  }

  public void replayScript() throws Exception {
    if (actionList.size() > 0) {
      String msg = "Do you really want to clear all buffers and replay the current script?";
      String title = msg;
      if (JOptionPane.showConfirmDialog(desktop, msg, title,
          JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        clearAllBuffers();
        refreshWindowMenu();
        // dummy image just to call the init-method of transformers
        // (which makes sense if script lack some parameters)
        SimpleImage paramInitImg = null;
        desktop.repaint();
        for (Action action : actionList) {
          switch (action.getActionType()) {
            case EXECUTE_CREATOR:
              selectCreator(action.getParameter());
              action.setProperties(getCreator(), getBufferList());
              executeCreator(action.getWidth(), action.getHeight(),
                  action.getOutputBuffer(), false);
              break;
            case EXECUTE_LOADER:
              selectLoader(action.getParameter());
              action.setProperties(getLoader(), getBufferList());
              executeLoader(action.getOutputBuffer(), false);
              break;
            case EXECUTE_TRANSFORMER:
              selectTransformer(action.getParameter());
              if (paramInitImg == null)
                paramInitImg = new SimpleImage(320, 256);
              getTransformer().initDefaultParams(paramInitImg);
              action.setProperties(getTransformer(), getBufferList());
              executeTransformer(action.getInputBuffer(),
                  action.getOutputBuffer3D() != null,
                  action.getOutputBuffer(),
                  action.getOutputBuffer3D(), false);
              break;
            case LOAD_IMAGE: {
              Buffer buffer = loadImage(action.getParameter(), false);
              if (action.getOutputBuffer() != null
                  && action.getOutputBuffer().length() > 0)
                buffer.setName(action.getOutputBuffer());
            }
              break;
          }
          desktop.repaint();
        }
      }
    }
  }

  public void loadScript() throws Exception {
    String msg = "Do you really want to clear all buffers and the current script?";
    String title = msg;
    if (((getBufferList().size() == 0) && (actionList.size() == 0))
        || (JOptionPane.showConfirmDialog(desktop, msg, title,
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
      JFileChooser chooser = getScriptJFileChooser();
      try {
        chooser.setCurrentDirectory(new File(prefs.getScriptPath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      if (chooser.showOpenDialog(windowMenu) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        try {
          prefs.setLastScriptFile(file);
          clearAllBuffers();
          actionList.clear();
          actionList.loadFromFile(file.getAbsolutePath());
        }
        finally {
          refreshWindowMenu();
          refreshActionTable();
          desktop.repaint();
        }
      }
    }
  }

  public void revertScriptAction() {
    String actionStr = null;
    int row = actionTable.getSelectedRow();
    if ((row >= 0) && (row < actionList.size())) {
      Action action = actionList.get(row);
      StringBuffer b = new StringBuffer();
      action.saveToBuffer(b, "\n");
      actionStr = b.toString();
      envelopeController.setCurrAction(action);
    }
    else {
      envelopeController.setCurrAction(null);
    }
    envelopeController.enableControls();
    envelopeController.refreshEnvelope();
    scriptActionTextArea.setText(actionStr);
    scriptActionTextArea.select(0, 0);
  }

  public void saveScriptAction() throws Exception {
    int row = actionTable.getSelectedRow();
    if ((row >= 0) && (row < actionList.size())) {
      ActionList lActionList = new ActionList();
      lActionList.loadFromString(scriptActionTextArea.getText());
      actionList.remove(row);
      for (Action action : lActionList) {
        if (row < actionList.size())
          actionList.add(row, action);
        else
          actionList.add(action);
      }
      refreshActionTable();
    }
  }

  public PropertyPanel createTransformerPropertyPanel() {
    currTransformerPropertyPanel = new PropertyPanel(getTransformer());
    return currTransformerPropertyPanel;
  }

  public PropertyPanel createCreatorPropertyPanel() {
    currCreatorPropertyPanel = new PropertyPanel(getCreator());
    return currCreatorPropertyPanel;
  }

  public PropertyPanel createLoaderPropertyPanel() {
    currLoaderPropertyPanel = new PropertyPanel(getLoader());
    return currLoaderPropertyPanel;
  }

  private boolean scriptRefreshing = false;

  public void scriptFrameChanged(int pSliderPos, String pFrameStr,
      String pFramesStr) {
    if (scriptRefreshing)
      return;
    scriptRefreshing = true;
    try {
      int frames;
      try {
        frames = Integer.parseInt(pFramesStr);
      }
      catch (Exception ex) {
        frames = 60;
      }
      if (frames < 1)
        frames = 1;

      int frame = pSliderPos;
      if (frame < 0) {
        try {
          frame = Integer.parseInt(pFrameStr);
        }
        catch (Exception ex) {
          frames = 1;
        }
      }
      if (frame > frames)
        frame = frames;
      else if (frame < 1)
        frame = 1;

      scriptFrameSlider.setMinimum(1);
      scriptFrameSlider.setMaximum(frames);
      scriptFrameSlider.setValue(frame);
      scriptFrameREd.setText(String.valueOf(frame));
      scriptFramesREd.setText(String.valueOf(frames));

      int row = actionTable.getSelectedRow();
      if ((row >= 0) && (row < actionList.size())) {
        Action action = actionList.get(row);
        if (action.hasEnvelopes()) {
          for (Parameter parameter : action.getParameterList()) {
            Envelope envelope = parameter.getEnvelope();
            if (envelope != null) {
              double val = envelope.evaluate(frame);
              parameter.setValue(Tools.doubleToString(val));
            }
          }

          StringBuffer b = new StringBuffer();
          action.saveToBuffer(b, "\n");
          String actionStr = b.toString();
          scriptActionTextArea.setText(actionStr);
          scriptActionTextArea.select(0, 0);
        }
      }
    }
    finally {
      scriptRefreshing = false;
    }
  }

  public void renderFrame() {
    try {
      int frame = Integer.parseInt(scriptFrameREd.getText());
      SimpleImage img = renderController.renderFrame(frame);
      new Buffer(desktop, frame, img);
    }
    catch (Throwable ex) {
      handleError(ex);
    }

  }

  public void syncActionAction() throws Exception {
    int row = actionTable.getSelectedRow();
    if ((row >= 0) && (row < actionList.size())) {
      Action action = actionList.get(row);
      switch (action.getActionType()) {
        case EXECUTE_CREATOR:
          selectCreator(action.getParameter());

          action.setProperties(scriptProcessor.getCreator(),
              scriptProcessor.getBufferList());
          break;
        case EXECUTE_TRANSFORMER:
          selectTransformer(action.getParameter());
          action.setProperties(scriptProcessor.getTransformer(),
              scriptProcessor.getBufferList());
          break;
        case EXECUTE_LOADER:
          selectLoader(action.getParameter());
          action.setProperties(scriptProcessor.getLoader(),
              scriptProcessor.getBufferList());
          break;
      }
    }
  }

  public void applyTransformerPreset(String presetName) {
    Transformer transformer = scriptProcessor.getTransformer();
    if (transformer != null) {
      SimpleImage img = null;
      String inputName = (String) transformerInputCmb.getSelectedItem();
      if (inputName != null && inputName.length() > 0) {
        Buffer inBuffer = getBufferList().bufferByName(inputName);
        if (inBuffer != null && inBuffer.getBufferType() == BufferType.IMAGE) {
          img = inBuffer.getImage();
        }
      }
      transformer.applyPreset(presetName, img);
    }
  }

  public void applyCreatorPreset(String presetName) {
    ImageCreator creator = scriptProcessor.getCreator();
    if (creator != null) {
      creator.applyPreset(presetName);
    }
  }

  public void fillTransformerPresetCmb() {
    Object selItem = transformerPresetCmb.getSelectedItem();
    transformerPresetCmb.removeAllItems();
    Transformer transformer = scriptProcessor.getTransformer();
    if (transformer != null) {
      if (transformer.getPresets().size() > 0) {
        transformerPresetCmb.addItem("(restore defaults)");
      }
      for (Preset preset : transformer.getPresets()) {
        transformerPresetCmb.addItem(preset.getName());
      }
    }
    if (selItem != null) {
      transformerPresetCmb.setSelectedItem(selItem);
    }
  }

  public void fillCreatorsPresetCmb() {
    Object selItem = creatorsPresetCmb.getSelectedItem();
    creatorsPresetCmb.removeAllItems();
    ImageCreator creator = scriptProcessor.getCreator();
    if (creator != null) {
      for (Preset preset : creator.getPresets()) {
        creatorsPresetCmb.addItem(preset.getName());
      }
    }
    if (selItem != null) {
      creatorsPresetCmb.setSelectedItem(selItem);
    }
  }
}
