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
package org.jwildfire.create.tina.keyframe;

import java.util.Collections;
import java.util.Comparator;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.keyframe.model.AnimationModelService;
import org.jwildfire.create.tina.keyframe.model.PlainProperty;
import org.jwildfire.create.tina.keyframe.model.PropertyModel;

public class KeyFrameService {

  public static boolean hasKeyFrame(Flame pFlame, int pFrame) {
    return getKeyFrame(pFlame, pFrame) != null;
  }

  public static KeyFrame addKeyFrame(Flame pFlame, int pFrame) {
    if (hasKeyFrame(pFlame, pFrame)) {
      throw new RuntimeException("A keyframe at position <" + pFrame + "> already exists");
    }
    KeyFrame keyFrame = new KeyFrame();
    keyFrame.setFrame(pFrame);
    pFlame.getKeyFrames().add(keyFrame);
    Collections.sort(pFlame.getKeyFrames(), new Comparator<KeyFrame>() {
      @Override
      public int compare(KeyFrame o1, KeyFrame o2) {
        Integer frame1 = o1.getFrame();
        Integer frame2 = o2.getFrame();
        return frame1.compareTo(frame2);
      }
    });
    return keyFrame;
  }

  public static KeyFrame getKeyFrame(Flame pFlame, int pFrame) {
    for (KeyFrame keyFrame : pFlame.getKeyFrames()) {
      if (keyFrame.getFrame() == pFrame) {
        return keyFrame;
      }
    }
    return null;
  }

  public static void storeChanges(Flame pFlame, int pFrame, Flame pState) {
    KeyFrame keyFrame = getKeyFrame(pFlame, pFrame);
    if (keyFrame == null) {
      keyFrame = addKeyFrame(pFlame, pFrame);
    }
    PropertyModel model = AnimationModelService.createModel(pFlame);
    processModel(model);
  }

  private static void processModel(PropertyModel pModel) {
    for (PropertyModel subNode : pModel.getChields()) {
      //FlamePropertiesTreeNode child = new FlamePropertiesTreeNode(subNode.getName(), subNode, true);
      //pParentNode.add(child);
      processModel(subNode);
    }
    for (PlainProperty property : pModel.getProperties()) {
      System.out.println(property.getName());
    }
  }

}
/*
 * User Stories:
 * 1. Der Benutzer möchte einen KeyFrame anlegen
 * 2. Der Benutzer möchte einen KeyFrame löschen
 * 3. Der Benutzer möchte einen KeyFrame kopieren
 * 4. Der Benutzer möchte zwischen KeyFrames wechseln
 * 5. Der Benutzer möchte die Parameter an einem KeyFrame editieren 
 * 6. Der Benutzer möchte die Zwischenzustände sehen
 * 7. Der Benutzer möchte die Bewegung als Animation in Echtzeit abspielen
 * 8. Der Benutzer möchte Flames mit KeyFrames speichern
 * 9. Der Benutzer möchte Flames mit KeyFrames laden
 * 10. Der Benutzer möchte die KeyFrames im Movie-Editor verwenden können
 */
