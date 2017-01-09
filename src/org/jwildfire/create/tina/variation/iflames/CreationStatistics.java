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
package org.jwildfire.create.tina.variation.iflames;

import java.util.ArrayList;
import java.util.List;

public class CreationStatistics {
  private final List<Action> actions = new ArrayList<Action>();
  private int structureMapWidth, structureMapHeight;
  private int flameCount;
  private int iteratorCount;
  private long totalDuration;

  public enum ActionType {
    SCALE, ERODE, CONV_NORTH, CONV_EAST, CONV_SOUTH, CONV_WEST, CONV_MERGE, CREATE_STRUCTURE
  }

  public static class Action {
    private final ActionType actionType;
    private long duration;

    public Action(ActionType pActionType) {
      actionType = pActionType;
    }

    public long getDuration() {
      return duration;
    }

    public void setDuration(long pDuration) {
      duration = pDuration;
    }

    public ActionType getActionType() {
      return actionType;
    }
  }

  public void clear() {
    actions.clear();
  }

  public void addAction(ActionType pActionType) {
    actions.add(new Action(pActionType));
  }

  public int getStructureMapWidth() {
    return structureMapWidth;
  }

  public void setStructureMapWidth(int pStructureMapWidth) {
    structureMapWidth = pStructureMapWidth;
  }

  public int getStructureMapHeight() {
    return structureMapHeight;
  }

  public void setStructureMapHeight(int pStructureMapHeight) {
    structureMapHeight = pStructureMapHeight;
  }

  public List<Action> getActions() {
    return actions;
  }

  public Action getAction(ActionType pActionType) {
    for (Action action : actions) {
      if (action.getActionType().equals(pActionType)) {
        return action;
      }
    }
    return null;
  }

  public void setFlameCount(int pFlameCount) {
    flameCount = pFlameCount;
  }

  public void setTotalDuration(long pTotalDuration) {
    totalDuration = pTotalDuration;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("iflame-generation statistics\n");
    sb.append("------------------------------------------\n");
    sb.append("structure width: " + structureMapWidth + "\n");
    sb.append("structure height: " + structureMapHeight + "\n");
    sb.append("number of flames: " + flameCount + "\n");
    sb.append("number of iterators: " + iteratorCount + "\n");
    sb.append("total duration: " + totalDuration / 1000.0 + "s\n");
    sb.append("actions:\n");
    for (Action action : actions) {
      sb.append("  " + action.getActionType() + ": " + action.getDuration() / 1000.0 + "s\n");
    }

    return sb.toString();
  }

  public void setIteratorCount(int pIteratorCount) {
    iteratorCount = pIteratorCount;
  }

}
