package org.jwildfire.create.tina.dance;

import javax.swing.tree.DefaultMutableTreeNode;

public class FlamePropertiesTreeNode<T> extends DefaultMutableTreeNode {
  private static final long serialVersionUID = 1L;
  private final T nodeData;

  public FlamePropertiesTreeNode(String pCaption, T pNodeData, boolean pAllowsChildren) {
    super(pCaption, pAllowsChildren);
    nodeData = pNodeData;
  }

  public T getNodeData() {
    return nodeData;
  }
}
