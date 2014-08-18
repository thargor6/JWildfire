/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.render.ChannelMixerMode;

public class ChannelMixerControlsDelegate {
  private final TinaController owner;
  private final TinaControllerData data;
  private final JTabbedPane rootTabbedPane;
  private final boolean useUndoManager;
  private List<ChannelMixerPanelDelegate> channelMixerPanels;

  public ChannelMixerControlsDelegate(TinaController pOwner, TinaControllerData pData, JTabbedPane pRootTabbedPane, boolean pUseUndoManager) {
    owner = pOwner;
    data = pData;
    rootTabbedPane = pRootTabbedPane;
    useUndoManager = pUseUndoManager;
    channelMixerPanels = createMixerPanels();
  }

  private List<ChannelMixerPanelDelegate> createMixerPanels() {
    List<ChannelMixerPanelDelegate> res = new ArrayList<ChannelMixerPanelDelegate>();
    res.add(new ChannelMixerPanelDelegate(this, data.channelMixerRRRootPanel) {

      @Override
      public MotionCurve getCurve(Flame pFlame) {
        return pFlame.getMixerRRCurve();
      }

    });
    res.add(new ChannelMixerPanelDelegate(this, data.channelMixerRGRootPanel) {

      @Override
      public MotionCurve getCurve(Flame pFlame) {
        return pFlame.getMixerRGCurve();
      }

    });
    res.add(new ChannelMixerPanelDelegate(this, data.channelMixerRBRootPanel) {

      @Override
      public MotionCurve getCurve(Flame pFlame) {
        return pFlame.getMixerRBCurve();
      }

    });
    res.add(new ChannelMixerPanelDelegate(this, data.channelMixerGRRootPanel) {

      @Override
      public MotionCurve getCurve(Flame pFlame) {
        return pFlame.getMixerGRCurve();
      }

    });
    res.add(new ChannelMixerPanelDelegate(this, data.channelMixerGGRootPanel) {

      @Override
      public MotionCurve getCurve(Flame pFlame) {
        return pFlame.getMixerGGCurve();
      }

    });
    res.add(new ChannelMixerPanelDelegate(this, data.channelMixerGBRootPanel) {

      @Override
      public MotionCurve getCurve(Flame pFlame) {
        return pFlame.getMixerGBCurve();
      }

    });
    res.add(new ChannelMixerPanelDelegate(this, data.channelMixerBRRootPanel) {

      @Override
      public MotionCurve getCurve(Flame pFlame) {
        return pFlame.getMixerBRCurve();
      }

    });
    res.add(new ChannelMixerPanelDelegate(this, data.channelMixerBGRootPanel) {

      @Override
      public MotionCurve getCurve(Flame pFlame) {
        return pFlame.getMixerBGCurve();
      }

    });
    res.add(new ChannelMixerPanelDelegate(this, data.channelMixerBBRootPanel) {

      @Override
      public MotionCurve getCurve(Flame pFlame) {
        return pFlame.getMixerBBCurve();
      }

    });

    return res;
  }

  public void enableControls() {
    boolean hasFlame = owner.getCurrFlame() != null;
    data.channelMixerModeCmb.setEnabled(hasFlame);
    data.channelMixerResetBtn.setEnabled(hasFlame);
  }

  public void refreshValues() {
    boolean oldRefreshing = owner.refreshing;
    owner.refreshing = true;
    try {
      Flame flame = owner.getCurrFlame();
      data.channelMixerModeCmb.setSelectedItem(flame.getChannelMixerMode());
      for (ChannelMixerPanelDelegate mixerPanel : channelMixerPanels) {
        mixerPanel.refreshCurve(flame);
      }
    }
    finally {
      owner.refreshing = oldRefreshing;
    }
  }

  public void channelMixerModeCmb_changed() {
    if (!owner.refreshing) {
      Flame flame = owner.getCurrFlame();
      if (useUndoManager) {
        owner.undoManager.saveUndoPoint(flame);
      }
      flame.setChannelMixerMode((ChannelMixerMode) data.channelMixerModeCmb.getSelectedItem());
      enableControls();
      refreshValues();
      owner.refreshFlameImage(false);
    }
  }

  public void resetBtn_clicked() {
    Flame flame = owner.getCurrFlame();
    if (useUndoManager) {
      owner.undoManager.saveUndoPoint(flame);
    }
    flame.resetMixerCurves();
    refreshValues();
    owner.refreshFlameImage(false);
  }

  protected TinaController getOwner() {
    return owner;
  }

  protected boolean isUseUndoManager() {
    return useUndoManager;
  }

}
