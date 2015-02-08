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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.model.AbstractProperty;
import org.jwildfire.create.tina.dance.model.AnimationModelService;
import org.jwildfire.create.tina.dance.model.FlamePropertyPath;
import org.jwildfire.create.tina.dance.model.PlainProperty;
import org.jwildfire.create.tina.dance.model.PropertyModel;

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

  public void refreshFlamePropertiesTree(JTree pFlamePropertiesTree, Flame pFlame) {
    FlamePropertiesTreeNode<Object> root = new FlamePropertiesTreeNode<Object>("Flames", null, true);
    if (pFlame != null) {
      FlamePropertiesTreeNode<Flame> flameNode = new FlamePropertiesTreeNode<Flame>(getFlameCaption(pFlame), pFlame, true);
      PropertyModel model = AnimationModelService.createModel(pFlame);
      addNodesToTree(model, flameNode);
      root.add(flameNode);
      pFlamePropertiesTree.setModel(new DefaultTreeModel(root));
    }
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
        FlamePropertiesTreeNode<Flame> flameNode = (FlamePropertiesTreeNode<Flame>) selection[1]; // skip root node which is only a text node
        List<String> path = new ArrayList<String>();
        for (int i = 2; i < selection.length; i++) {
          FlamePropertiesTreeNode<?> node = (FlamePropertiesTreeNode<?>) selection[i];
          path.add(((AbstractProperty) node.getNodeData()).getName());
        }
        FlamePropertyPath res = new FlamePropertyPath(flameNode.getNodeData(), path);
        return res;
      }
    }
    return null;
  }

  public void selectPropertyPath(JTree pTree, FlamePropertyPath pPath) {
    if (pPath != null) {
      TreeNode selNode = null;
      TreeNode root = (TreeNode) pTree.getModel().getRoot();
      List<String> pathNodes = pPath.getPathComponents();
      // skip root
      if (pathNodes.size() > 0 && root != null && root.getChildCount() == 1) {
        root = root.getChildAt(0);
        int d = 0;
        while (d < pathNodes.size()) {
          boolean found = false;
          for (int i = 0; i < root.getChildCount(); i++) {
            TreeNode node = root.getChildAt(i);
            if (node instanceof FlamePropertiesTreeNode) {
              FlamePropertiesTreeNode<?> fNode = (FlamePropertiesTreeNode<?>) node;
              if (pathNodes.get(d).equals(fNode.getUserObject())) {
                root = node;
                found = true;
                break;
              }
            }
          }
          if (!found) {
            break;
          }
          else if (d == pathNodes.size() - 1) {
            selNode = root;
          }
          d++;
        }
        if (selNode != null) {
          TreeNode[] nodes = ((DefaultTreeModel) pTree.getModel()).getPathToRoot(selNode);
          TreePath path = new TreePath(nodes);
          pTree.expandPath(path);
          pTree.setSelectionPath(path);
        }
      }
    }
  }
}
