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
package org.jwildfire.swing;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Preset;
import org.jwildfire.base.Tools;
import org.jwildfire.create.ImageCreator;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.io.SunFlowWriter;
import org.jwildfire.loader.ImageLoader;
import org.jwildfire.script.Action;
import org.jwildfire.script.ActionList;
import org.jwildfire.swing.Buffer.BufferType;
import org.jwildfire.transform.Transformer;

public class MainController {
  private final JFrame mainFrame;
  private final JMenu windowMenu;
  private final JComboBox transformerInputCmb;
  private final JComboBox transformerPresetCmb;
  private final JComboBox creatorsPresetCmb;
  private final JDialog showMessageDlg;
  private final JTextArea showMessageDlgTextArea;

  private final JTextField scriptFrameREd;

  private final ActionList actionList = new ActionList();

  private PropertyPanel currTransformerPropertyPanel;
  private PropertyPanel currCreatorPropertyPanel;
  private PropertyPanel currLoaderPropertyPanel;

  private final ErrorHandler errorHandler;
  private final Prefs prefs;

  private boolean executing = false;

  private final ScriptProcessor scriptProcessor;

  private final int designedWindowCount;

  public BufferList getBufferList() {
    return scriptProcessor.getBufferList();
  }

  public ActionList getActionList() {
    return actionList;
  }

  private JFileChooser jScriptFileChooser = null;

  private JFileChooser getScriptJFileChooser() {
    if (jScriptFileChooser == null) {
      jScriptFileChooser = new DefaultFileChooser() {
        private static final long serialVersionUID = 1L;

        @Override
        protected String getDefaultExtension() {
          return Tools.FILEEXT_JFX;
        }

      };
      jScriptFileChooser.addChoosableFileFilter(new ScriptFileFilter());
      jScriptFileChooser.setAcceptAllFileFilterUsed(false);
    }
    return jScriptFileChooser;
  }

  public MainController(
      Prefs pPrefs, ErrorHandler pErrorHandler,
      JFrame pMainFrame, JMenu pWindowMenu,
      JComboBox pTransformerInputCmb, JComboBox pTransformerPresetCmb,
      JComboBox pCreatorsPresetCmb, JDialog pShowMessageDlg,
      JTextArea pShowMessageDlgTextArea, JTable pActionTable,
      JTextArea pScriptActionTextArea, JSlider pScriptFrameSlider,
      JTextField pScriptFramesREd, JTextField pScriptFrameREd,
      EnvelopeController pEnvelopeController,
      int pDesignedWindowCount) {
    designedWindowCount = pDesignedWindowCount;
    prefs = pPrefs;
    errorHandler = pErrorHandler;
    mainFrame = pMainFrame;
    windowMenu = pWindowMenu;
    transformerInputCmb = pTransformerInputCmb;
    transformerPresetCmb = pTransformerPresetCmb;
    creatorsPresetCmb = pCreatorsPresetCmb;
    showMessageDlg = pShowMessageDlg;
    showMessageDlgTextArea = pShowMessageDlgTextArea;
    scriptFrameREd = pScriptFrameREd;
    scriptProcessor = new ScriptProcessor(pMainFrame);
    scriptProcessor.getBufferList().setSyncWithStaticBufferList(true);
    scriptProcessor.setAddBuffersToDesktop(true);
  }

  public Buffer loadImage(String pFilename, boolean pRecordAction)
      throws Exception {
    Buffer buffer = scriptProcessor.loadImage(pFilename);
    if (pRecordAction)
      actionList.addLoadImageAction(pFilename);
    addEvents(buffer);
    refreshWindowMenu();
    return buffer;
  }

  public void loadMovie(String pFilename) {
    try {
      Desktop.getDesktop().open(new File(pFilename));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Buffer loadImage(boolean pRecordAction) throws Exception {
    JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
    if (prefs.getInputImagePath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
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
      if (buffers.getOutHDRBuffer() != null)
        addEvents(buffers.getOutHDRBuffer());
      if (buffers.getOutBuffer3D() != null)
        addEvents(buffers.getOutBuffer3D());
      if (pRecordAction)
        actionList.addExecuteTransformerAction(
            scriptProcessor.getTransformer(),
            buffers.getInBuffer(), buffers.getOutBuffer(), buffers.getOutHDRBuffer(),
            buffers.getOutBuffer3D());
      refreshWindowMenu();
      mainFrame.repaint();
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
      return buffer;
    }
    finally {
      executing = false;
    }
  }

  private void addEvents(Buffer pBuffer) {
    JFrame frame = pBuffer.getFrame();
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosed(
          WindowEvent e) {
        JFrame frame = (JFrame) e.getSource();
        BufferList bufferList = getBufferList();
        for (Buffer buffer : bufferList) {
          if (frame == buffer.getFrame()) {
            bufferList.remove(buffer);
            actionList.removeBuffer(buffer, getBufferList());
            buffer.flush();
            refreshWindowMenu();
            break;
          }
        }
      }
    });
  }

  public void saveImage(Buffer buffer) throws Exception {
    if (buffer != null) {
      JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
      if (prefs.getOutputImagePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(windowMenu) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastOutputImageFile(file);
        if (buffer.getBufferType() == BufferType.IMAGE) {
          new ImageWriter().saveImage(buffer.getImage(),
              file.getAbsolutePath());
        }
        else if (buffer.getBufferType() == BufferType.HDR_IMAGE) {
          new ImageWriter().saveImage(buffer.getHDRImage(),
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

  public void refreshWindowMenu() {
    while (windowMenu.getItemCount() > designedWindowCount)
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
      int off = designedWindowCount + 1; // +1 because of the
      // separator
      for (int i = off; i < windowMenu.getItemCount(); i++) {
        if (windowMenu.getItem(i) == menuItem) {
          int idx = i - off;
          Buffer buffer = getBufferList().get(idx);
          buffer.getFrame().toFront();
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
        transformer.initDefaultParams(buffer.getImage());
        currTransformerPropertyPanel.writeToObject(transformer);
      }
    }
  }

  private void showMessage(String pMessage) {
    Point dPos = mainFrame.getRootPane().getLocation();
    int dWidth = mainFrame.getWidth();
    int dHeight = mainFrame.getHeight();
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
      JFrame frame = buffer.getFrame();
      mainFrame.remove(frame);
      bufferList.remove(buffer);
    }
  }

  public boolean closeAll() {
    String msg = "Do you really want to close all buffers?";
    String title = msg;
    if (JOptionPane.showConfirmDialog(mainFrame, msg, title,
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      clearAllBuffers();
      refreshWindowMenu();
      actionList.clear();
      mainFrame.repaint();
      return true;
    }
    else
      return false;
  }

  public boolean clearScript() {
    if (actionList.size() > 0) {
      String msg = "Do you really want to clear the current script?";
      String title = msg;
      if (JOptionPane.showConfirmDialog(mainFrame, msg, title,
          JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        actionList.clear();
        return true;
      }
    }
    return false;
  }

  public void saveScript() throws Exception {
    if (actionList.size() > 0) {
      JFileChooser chooser = getScriptJFileChooser();
      if (prefs.getOutputScriptPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getOutputScriptPath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(windowMenu) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastOutputScriptFile(file);
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
      if (JOptionPane.showConfirmDialog(mainFrame, msg, title,
          JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        clearAllBuffers();
        refreshWindowMenu();
        // dummy image just to call the init-method of transformers
        // (which makes sense if script lack some parameters)
        SimpleImage paramInitImg = null;
        mainFrame.repaint();
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
          mainFrame.repaint();
        }
      }
    }
  }

  public void loadScript() throws Exception {
    String msg = "Do you really want to clear all buffers and the current script?";
    String title = msg;
    if (((getBufferList().size() == 0) && (actionList.size() == 0))
        || (JOptionPane.showConfirmDialog(mainFrame, msg, title,
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
      JFileChooser chooser = getScriptJFileChooser();
      if (prefs.getInputScriptPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputScriptPath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(windowMenu) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        try {
          prefs.setLastInputScriptFile(file);
          clearAllBuffers();
          actionList.clear();
          actionList.loadFromFile(file.getAbsolutePath());
        }
        finally {
          refreshWindowMenu();
          mainFrame.repaint();
        }
      }
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

  public void saveImageBuffer(JFrame frame) {
    BufferList bufferList = getBufferList();
    for (Buffer buffer : bufferList) {
      if (buffer.getFrame() == frame) {
        try {
          saveImage(buffer);
        }
        catch(Exception ex) {
          errorHandler.handleError(ex);
        }
        break;
      }
    }
  }
}
