/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2022 Andreas Maschke

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
package org.jwildfire.create.tina.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class MainEditorEvents {
  private final TinaController tinaController;
  private final TinaControllerParameter fields;

  public MainEditorEvents(TinaController tinaController, TinaControllerParameter fields) {
    this.tinaController = tinaController;
    this.fields = fields;
  }

  private void setupSlider(
      JLabel label,
      Runnable onReset,
      JWFNumberField editField,
      Consumer<ChangeEvent> onEditFieldChange,
      JSlider slider,
      Consumer<ChangeEvent> onSliderChange,
      String motionPropertyName) {
    final String labelName = motionPropertyName + "Lbl";
    final String sliderName = motionPropertyName + "Slider";

    label.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
              tinaController.saveUndoPoint();
              onReset.run();
            }
          }
        });
    label.setName(labelName);

    editField.setLinkedLabelControlName(labelName);
    editField.setLinkedMotionControlName(sliderName);
    editField.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            tinaController.getFlameControls().editMotionCurve(e);
          }
        });
    editField.setMotionPropertyName(motionPropertyName);
    editField.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (!editField.isMouseAdjusting() || editField.getMouseChangeCount() == 0) {
              if (!slider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            onEditFieldChange.accept(e);
          }
        });

    slider.setName(sliderName);
    slider.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            tinaController.saveUndoPoint();
          }
        });
    slider.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            onSliderChange.accept(e);
          }
        });
  }

  public void setupEvents() {
    setupCameraTabEvents();
  }
  private void setupCameraTabEvents() {
    setupSlider(
            fields.cameraRollLbl,
            () -> tinaController.getFlameControls().cameraRollREd_reset(),
            fields.pCameraRollREd,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraRollREd_changed(),
            fields.pCameraRollSlider,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraRollSlider_stateChanged(e),
            "camRoll");
    setupSlider(
            fields.cameraPitchLbl,
            () -> tinaController.getFlameControls().cameraPitchREd_reset(),
            fields.pCameraPitchREd,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraPitchREd_changed(),
            fields.pCameraPitchSlider,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraPitchSlider_stateChanged(e),
            "camPitch");
    setupSlider(
            fields.cameraYawLbl,
            () -> tinaController.getFlameControls().cameraYawREd_reset(),
            fields.pCameraYawREd,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraYawREd_changed(),
            fields.pCameraYawSlider,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraYawSlider_stateChanged(e),
            "camYaw");
    setupSlider(
            fields.cameraBankLbl,
            () -> tinaController.getFlameControls().cameraBankREd_reset(),
            fields.pCameraBankREd,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraBankREd_changed(),
            fields.pCameraBankSlider,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraBankSlider_stateChanged(e),
            "camBank");
    setupSlider(
            fields.cameraPerspectiveLbl,
            () -> tinaController.getFlameControls().cameraPerspectiveREd_reset(),
            fields.pCameraPerspectiveREd,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraPerspectiveREd_changed(),
            fields.pCameraPerspectiveSlider,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraPerspectiveSlider_stateChanged(e),
            "camPerspective");

    setupSlider(
        fields.pCameraCentreXLbl,
        () -> tinaController.getFlameControls().cameraCentreXREd_reset(),
        fields.pCameraCentreXREd,
        (ChangeEvent e) -> tinaController.getFlameControls().cameraCentreXREd_changed(),
        fields.pCameraCentreXSlider,
        (ChangeEvent e) -> tinaController.getFlameControls().cameraCentreXSlider_stateChanged(e),
        "centreX");
    setupSlider(
            fields.pCameraCentreYLbl,
            () -> tinaController.getFlameControls().cameraCentreYREd_reset(),
            fields.pCameraCentreYREd,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraCentreYREd_changed(),
            fields.pCameraCentreYSlider,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraCentreYSlider_stateChanged(e),
            "centreY");
    setupSlider(
            fields.pCameraZoomLbl,
            () -> tinaController.getFlameControls().cameraCentreXREd_reset(),
            fields.pCameraZoomREd,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraZoomREd_changed(),
            fields.pCameraZoomSlider,
            (ChangeEvent e) -> tinaController.getFlameControls().cameraZoomSlider_stateChanged(e),
            "camZoom");
    setupSlider(
            fields.pixelsPerUnitLbl,
            () -> tinaController.getFlameControls().pixelsPerUnitREd_reset(),
            fields.pPixelsPerUnitREd,
            (ChangeEvent e) -> tinaController.getFlameControls().pixelsPerUnitREd_changed(),
            fields.pPixelsPerUnitSlider,
            (ChangeEvent e) -> tinaController.getFlameControls().pixelsPerUnitSlider_stateChanged(e),
            "pixelsPerUnit");
    setupSlider(
            fields.camPosXLbl,
            () -> tinaController.getFlameControls().camPosXREd_reset(),
            fields.camPosXREd,
            (ChangeEvent e) -> tinaController.getFlameControls().camPosXREd_changed(),
            fields.camPosXSlider,
            (ChangeEvent e) -> tinaController.getFlameControls().camPosXSlider_stateChanged(e),
            "camPosX");
    setupSlider(
            fields.camPosYLbl,
            () -> tinaController.getFlameControls().camPosYREd_reset(),
            fields.camPosYREd,
            (ChangeEvent e) -> tinaController.getFlameControls().camPosYREd_changed(),
            fields.camPosYSlider,
            (ChangeEvent e) -> tinaController.getFlameControls().camPosYSlider_stateChanged(e),
            "camPosY");
    setupSlider(
            fields.camPosZLbl,
            () -> tinaController.getFlameControls().camPosZREd_reset(),
            fields.camPosZREd,
            (ChangeEvent e) -> tinaController.getFlameControls().camPosZREd_changed(),
            fields.camPosZSlider,
            (ChangeEvent e) -> tinaController.getFlameControls().camPosZSlider_stateChanged(e),
            "camPosZ");


  }
}
