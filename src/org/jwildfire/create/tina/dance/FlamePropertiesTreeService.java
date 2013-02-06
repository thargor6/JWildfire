/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.dance;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.model.AbstractProperty;
import org.jwildfire.create.tina.dance.model.AnimationModelService;
import org.jwildfire.create.tina.dance.model.FlamePropertyPath;
import org.jwildfire.create.tina.dance.model.PlainProperty;
import org.jwildfire.create.tina.dance.model.PropertyModel;
import org.jwildfire.create.tina.dance.motion.Motion;

public class FlamePropertiesTreeService {

  public String getFlameCaption(Flame pFlame) {
    return pFlame.getName().equals("") ? String.valueOf(pFlame.hashCode()) : pFlame.getName();
  }

  public void refreshFlamePropertiesTree(JTree pFlamePropertiesTree, DancingFlameProject pProject) {
    FlamePropertiesTreeNode<Object> root = new FlamePropertiesTreeNode<Object>("Flames", null, true);
    for (Flame flame : pProject.getFlames()) {
      FlamePropertiesTreeNode<Flame> flameNode = new FlamePropertiesTreeNode<Flame>(getFlameCaption(flame), flame, true);
      PropertyModel model = AnimationModelService.createModel(flame);
      addNodesToTree(model, flameNode);
      root.add(flameNode);
    }
    pFlamePropertiesTree.setModel(new DefaultTreeModel(root));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void addNodesToTree(PropertyModel pModel, FlamePropertiesTreeNode<?> pParentNode) {
    for (PropertyModel subNode : pModel.getChields()) {
      FlamePropertiesTreeNode child = new FlamePropertiesTreeNode(subNode.getName(), subNode, true);
      pParentNode.add(child);
      addNodesToTree(subNode, child);
    }
    for (PlainProperty property : pModel.getProperties()) {
      FlamePropertiesTreeNode<?> child = new FlamePropertiesTreeNode(property.getName(), property, false);
      pParentNode.add(child);
    }
  }

  public FlamePropertiesTreeNode<?> getSelectedLeaf(JTree pFlamePropertiesTree) {
    if (pFlamePropertiesTree.getSelectionPath() != null) {
      Object[] selection = pFlamePropertiesTree.getSelectionPath().getPath();
      if (selection != null && selection.length > 0) {
        return (FlamePropertiesTreeNode<?>) selection[selection.length - 1];
      }
    }
    return null;
  }

  public boolean isPlainPropertySelected(JTree pFlamePropertiesTree) {
    FlamePropertiesTreeNode<?> selectedLeaf = getSelectedLeaf(pFlamePropertiesTree);
    if (selectedLeaf != null) {
      Object data = selectedLeaf.getNodeData();
      return data != null && data instanceof PlainProperty;
    }
    return false;
  }

  public FlamePropertyPath getSelectedPropertyPath(JTree pFlamePropertiesTree) {
    if (pFlamePropertiesTree.getSelectionPath() != null) {
      Object[] selection = pFlamePropertiesTree.getSelectionPath().getPath();
      if (selection != null && selection.length > 1) {
        @SuppressWarnings("unchecked")
        FlamePropertiesTreeNode<Flame> root = (FlamePropertiesTreeNode<Flame>) selection[1]; // skip root node which is only a text node
        List<String> path = new ArrayList<String>();
        for (int i = 2; i < selection.length; i++) {
          FlamePropertiesTreeNode<?> node = (FlamePropertiesTreeNode<?>) selection[i];
          path.add(((AbstractProperty) node.getNodeData()).getName());
        }
        FlamePropertyPath res = new FlamePropertyPath(root.getNodeData(), path);

        return res;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public int linkMotionToFlame(Motion pMotion, FlamePropertiesTreeNode<Flame> pFlameNode, FlamePropertyPath pSelPath) {
    //    int added = 0;
    //    for (int i = 0; i < pFlameNode.getChildCount(); i++) {
    //      FlamePropertiesTreeNode<AbstractProperty> node = (FlamePropertiesTreeNode<AbstractProperty>) pFlameNode.getChildAt(i);
    //      List<FlamePropertiesTreeNode<AbstractProperty>> currPath = new ArrayList<FlamePropertiesTreeNode<AbstractProperty>>();
    //      currPath.add(node);
    //
    //      FlamePropertiesTreeNode<AbstractProperty> child = node;
    //      while (child.getChildCount() > 0) {
    //        child = (FlamePropertiesTreeNode<AbstractProperty>) child.getChildAt(0);
    //        currPath.add(child);
    //      }
    //
    //      if (pSelPath.getPropertyPath().size() == currPath.size()) {
    //        boolean equal = true;
    //        for (int j = currPath.size() - 1; j >= 0; j--) {
    //          if (!pSelPath.getPropertyPath().get(j).getName().equals(currPath.get(j).getNodeData().getName())) {
    //            equal = false;
    //            break;
    //          }
    //        }
    //        if (equal) {
    //          FlamePropertyPath newPath = new FlamePropertyPath(pFlameNode.getNodeData());
    //          for (FlamePropertiesTreeNode<AbstractProperty> currNode : currPath) {
    //            newPath.getPropertyPath().add(currNode.getNodeData());
    //          }
    //          if (!pMotion.hasLink(newPath)) {
    //            pMotion.getMotionLinks().add(new MotionLink(newPath));
    //            added++;
    //          }
    //        }
    //      }
    //    }
    //    return added;
    return 0;
  }

}
