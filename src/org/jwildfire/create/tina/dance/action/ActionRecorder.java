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
package org.jwildfire.create.tina.dance.action;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.RealtimeAnimRenderThread;

public class ActionRecorder {
  private final RealtimeAnimRenderThread thread;
  private List<RecordedAction> recordedActions = new ArrayList<RecordedAction>();

  public ActionRecorder(RealtimeAnimRenderThread pThread) {
    thread = pThread;
  }

  public void recordFlameChange(Flame pFlame, int pMorphFrameCount) {
    getRecordedActions().add(new FlameChangeAction(System.currentTimeMillis() - thread.getTimeRenderStarted(), pFlame, pMorphFrameCount));
  }

  public void recordStart(Flame pFlame) {
    getRecordedActions().clear();
    getRecordedActions().add(new StartAction(pFlame));
  }

  public void recordStop() {
    getRecordedActions().add(new StopAction(System.currentTimeMillis() - thread.getTimeRenderStarted()));
  }

  public List<RecordedAction> getRecordedActions() {
    return recordedActions;
  }

}
