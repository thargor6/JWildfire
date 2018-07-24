package org.jwildfire.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.*;

import org.jwildfire.create.CreatorsList;
import org.jwildfire.loader.LoadersList;
import org.jwildfire.transform.Transformer;
import org.jwildfire.transform.TransformersList;

public class OperatorsFrame extends JFrame {
  private MainController mainController = null; // @jve:decl-index=0:
  private JWildfire desktop = null;// @jve:decl-index=0:
  private Object currTransformerPropertyPanel = null; // @jve:decl-index=0:
  private Object currCreatorPropertyPanel = null; // @jve:decl-index=0:
  private Object currLoaderPropertyPanel = null; // @jve:decl-index=0:
  private boolean refreshing = false;
  boolean presetRefreshing = false;

  private static final long serialVersionUID = 1L;
  private JComboBox transformerPresetCmb = null;

  private JPanel transformerPropertiesPanel = null;
  private JPanel jContentPane = null;
  private JList loadersList = null;

  private JButton loaderExecuteButton = null;

  private JPanel operatorsPresetPanel = null;

  private JLabel transformerPresetLbl = null;
  private JPanel creatorPropertiesPanel = null;

  private JSplitPane transformersSplitPane = null;

  private JLabel transformerInputLabel = null;

  private JLabel creatorPresetLabel = null;

  private JComboBox creatorSizePresetCmb = null;

  private JPanel loadersContentPanel = null;

  private JPanel loadersBottomPanel = null;

  private JSplitPane loadersSplitPane = null;

  private JScrollPane loadersScrollPane = null;

  private JPanel loadersPropertiesPanel = null;
  private JSplitPane creatorsSplitPane = null;
  private JTabbedPane operatorsTabbedPane = null;
  private JPanel transformersContentPanel = null;
  private JPanel creatorsContentPanel = null;
  private JPanel transformersBottomPanel = null;
  private JPanel creatorsPresetPanel = null;

  private JCheckBox transformerOutputMesh3DCBx = null;
  private JLabel creatorPresetLbl = null;
  private JPanel creatorsBottomPanel = null;
  private JComboBox creatorPresetCmb = null;

  private JScrollPane creatorsCenterScrollPane = null;
  private JScrollPane transformersCenterScrollPane = null;

  private JTextField creatorWidthREd = null;

  private JTextField creatorHeightREd = null;
  private JLabel creatorXLabel = null;

  private JPanel transformersTopPanel = null;

  private JLabel transformersFilterLabel = null;

  private JTextField transformersFilterREd = null;
  private JList creatorsList = null;

  private JList transformersList = null;

  private JComboBox transformerInputCmb = null;

  private JButton transformerExecuteButton = null;

  private JButton creatorExecuteButton = null;

  private JLabel creatorSizeLabel = null;

  /**
   * This is the xxx default constructor
   */
  public OperatorsFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(300, 200);
    this.setBounds(new Rectangle(813, 14, 355, 652));
    this.setTitle("Operators (<F2>/<DblClick> to edit)");
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setVisible(false);
    this.setResizable(true);
    this.setContentPane(getJContentPane());
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.setLayout(new BorderLayout());
      jContentPane.add(getOperatorsTabbedPane(),
          BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes operatorsTabbedPane
   * 
   * @return javax.swing.JTabbedPane
   */
  private JTabbedPane getOperatorsTabbedPane() {
    if (operatorsTabbedPane == null) {
      operatorsTabbedPane = new JTabbedPane();
      operatorsTabbedPane.addTab("Transform", null,
          getTransformersContentPanel(), null);
      operatorsTabbedPane.addTab("Create image", null,
          getCreatorsContentPanel(), null);
      operatorsTabbedPane.addTab("Load image", null,
          getLoadersContentPanel(), "");
    }
    return operatorsTabbedPane;
  }

  /**
   * This method initializes transformersContentPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getTransformersContentPanel() {
    if (transformersContentPanel == null) {
      transformersContentPanel = new JPanel();
      transformersContentPanel.setLayout(new BorderLayout());
      transformersContentPanel.setBorder(BorderFactory.createEmptyBorder(
          10, 10, 10, 10));
      transformersContentPanel.setToolTipText("");
      transformersContentPanel.add(getTransformersBottomPanel(),
          BorderLayout.SOUTH);
      transformersContentPanel.add(getTransformersSplitPane(),
          BorderLayout.CENTER);
      transformersContentPanel.add(getTransformersTopPanel(),
          BorderLayout.NORTH);
    }
    return transformersContentPanel;
  }

  /**
   * This method initializes creatorsContentPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getCreatorsContentPanel() {
    if (creatorsContentPanel == null) {
      creatorsContentPanel = new JPanel();
      creatorsContentPanel.setLayout(new BorderLayout());
      creatorsContentPanel.setBorder(BorderFactory.createEmptyBorder(10,
          10, 10, 10));
      creatorsContentPanel.add(getCreatorsBottomPanel(),
          BorderLayout.SOUTH);
      creatorsContentPanel.add(getCreatorsSplitPane(),
          BorderLayout.CENTER);
    }
    return creatorsContentPanel;
  }

  /**
   * This method initializes transformersBottomPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getTransformersBottomPanel() {
    if (transformersBottomPanel == null) {
      transformerInputLabel = new JLabel();
      transformerInputLabel.setPreferredSize(new Dimension(28, 26));
      transformerInputLabel.setLocation(new Point(5, 5));
      transformerInputLabel.setSize(new Dimension(52, 26));
      transformerInputLabel.setText("Input");
      transformersBottomPanel = new JPanel();
      transformersBottomPanel.setLayout(null);
      transformersBottomPanel.setPreferredSize(new Dimension(0, 90));
      transformersBottomPanel.add(getTransformerOutputMesh3DCBx(), null);
      transformersBottomPanel.add(getTransformerExecuteButton(), null);
      transformersBottomPanel.add(getTransformerInputCmb(), null);
      transformersBottomPanel.add(transformerInputLabel, null);
    }
    return transformersBottomPanel;
  }

  /**
   * This method initializes creatorsBottomPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getCreatorsBottomPanel() {
    if (creatorsBottomPanel == null) {
      creatorPresetLabel = new JLabel();
      creatorPresetLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      creatorPresetLabel.setText("Size-Preset");
      creatorPresetLabel.setSize(new Dimension(72, 26));
      creatorPresetLabel.setLocation(new Point(5, 5));
      creatorPresetLabel.setPreferredSize(new Dimension(72, 26));
      creatorXLabel = new JLabel();
      creatorXLabel.setText("x");
      creatorXLabel.setSize(new Dimension(11, 20));
      creatorXLabel.setPreferredSize(new Dimension(7, 18));
      creatorXLabel.setLocation(new Point(146, 34));
      creatorSizeLabel = new JLabel();
      creatorSizeLabel.setPreferredSize(new Dimension(52, 26));
      creatorSizeLabel.setLocation(new Point(5, 36));
      creatorSizeLabel.setSize(new Dimension(72, 26));
      creatorSizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      creatorSizeLabel.setText("Size");
      creatorsBottomPanel = new JPanel();
      creatorsBottomPanel.setLayout(null);
      creatorsBottomPanel.setPreferredSize(new Dimension(0, 100));
      creatorsBottomPanel.add(getCreatorExecuteButton(), null);
      creatorsBottomPanel.add(creatorSizeLabel, null);
      creatorsBottomPanel.add(getCreatorWidthREd(), null);
      creatorsBottomPanel.add(creatorXLabel, null);
      creatorsBottomPanel.add(getCreatorHeightREd(), null);
      creatorsBottomPanel.add(creatorPresetLabel, null);
      creatorsBottomPanel.add(getCreatorSizePresetCmb(), null);
    }
    return creatorsBottomPanel;
  }

  /**
   * This method initializes creatorsCenterScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getCreatorsCenterScrollPane() {
    if (creatorsCenterScrollPane == null) {
      creatorsCenterScrollPane = new JScrollPane();
      creatorsCenterScrollPane.setViewportView(getCreatorsList());
    }
    return creatorsCenterScrollPane;
  }

  /**
   * This method initializes creatorExecuteButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getCreatorExecuteButton() {
    if (creatorExecuteButton == null) {
      creatorExecuteButton = new JButton();
      creatorExecuteButton.setText("Create");
      creatorExecuteButton.setSize(new Dimension(155, 26));
      creatorExecuteButton.setLocation(new Point(94, 68));
      creatorExecuteButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              creatorExecuteButton_actionPerformed(e);
            }
          });
    }
    return creatorExecuteButton;
  }

  /**
   * This method initializes creatorWidthREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getCreatorWidthREd() {
    if (creatorWidthREd == null) {
      creatorWidthREd = new JTextField();
      creatorWidthREd.setPreferredSize(new Dimension(60, 26));
      creatorWidthREd.setLocation(new Point(82, 34));
      creatorWidthREd.setSize(new Dimension(60, 26));
      creatorWidthREd.setText("800");
    }
    return creatorWidthREd;
  }

  /**
   * This method initializes creatorHeightREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getCreatorHeightREd() {
    if (creatorHeightREd == null) {
      creatorHeightREd = new JTextField();
      creatorHeightREd.setText("600");
      creatorHeightREd.setLocation(new Point(159, 34));
      creatorHeightREd.setPreferredSize(new Dimension(60, 26));
      creatorHeightREd.setSize(new Dimension(60, 26));
    }
    return creatorHeightREd;
  }

  /**
   * This method initializes transformerOutputMesh3DCBx
   * 
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getTransformerOutputMesh3DCBx() {
    if (transformerOutputMesh3DCBx == null) {
      transformerOutputMesh3DCBx = new JCheckBox();
      transformerOutputMesh3DCBx
          .setText("Create 3D object and render image");
      transformerOutputMesh3DCBx.setSize(new Dimension(255, 20));
      transformerOutputMesh3DCBx
          .setActionCommand("Create 3D object and render image");
      transformerOutputMesh3DCBx.setLocation(new Point(62, 34));
    }
    return transformerOutputMesh3DCBx;
  }

  /**
   * This method initializes transformersTopPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getTransformersTopPanel() {
    if (transformersTopPanel == null) {
      transformersFilterLabel = new JLabel();
      transformersFilterLabel.setPreferredSize(new Dimension(43, 26));
      transformersFilterLabel.setText("Filter");
      transformersFilterLabel.setSize(new Dimension(43, 26));
      transformersFilterLabel.setLocation(new Point(5, 5));
      transformersFilterLabel
          .setHorizontalAlignment(SwingConstants.RIGHT);
      transformersTopPanel = new JPanel();
      transformersTopPanel.setLayout(null);
      transformersTopPanel.setPreferredSize(new Dimension(0, 36));
      transformersTopPanel.add(transformersFilterLabel, null);
      transformersTopPanel.add(getTransformersFilterREd(), null);
    }
    return transformersTopPanel;
  }

  /**
   * This method initializes transformersFilterREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getTransformersFilterREd() {
    if (transformersFilterREd == null) {
      transformersFilterREd = new JTextField();
      transformersFilterREd.setText("");
      transformersFilterREd.setSize(new Dimension(235, 26));
      transformersFilterREd.setLocation(new Point(50, 5));
      transformersFilterREd.setFont(new Font("Dialog", Font.PLAIN, 12));
      transformersFilterREd.setPreferredSize(new Dimension(200, 26));
      transformersFilterREd
          .addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
              Vector<String> list = TransformersList
                  .getItemVector();
              Vector<String> filteredList;
              String filter = transformersFilterREd.getText();
              if ((filter != null) && (filter.length() > 0)) {
                filteredList = new Vector<String>();
                filter = filter.toLowerCase();
                for (String item : list) {
                  if (((filter.length() <= 2) && (item
                      .toLowerCase().indexOf(filter) == 0))
                      || ((filter.length() > 2) && (item
                          .toLowerCase().indexOf(
                              filter) >= 0))) {
                    filteredList.add(item);
                  }
                }
              }
              else
                filteredList = list;
              String selected = (String) transformersList
                  .getSelectedValue();
              if ((selected != null)
                  && (filteredList.indexOf(selected) < 0))
                selected = null;
              transformersList.setListData(filteredList);
              if (selected != null)
                transformersList.setSelectedValue(selected,
                    true);
              else if (filteredList.size() == 1)
                transformersList.setSelectedIndex(0);
              else
                transformersList.setSelectedIndex(-1);
            }

          });
    }
    return transformersFilterREd;
  }

  /**
   * This method initializes creatorSizePresetCmb
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getCreatorSizePresetCmb() {
    if (creatorSizePresetCmb == null) {
      creatorSizePresetCmb = new JComboBox();
      creatorSizePresetCmb.setPreferredSize(new Dimension(233, 26));
      creatorSizePresetCmb.setSize(new Dimension(233, 26));
      creatorSizePresetCmb.setLocation(new Point(82, 5));
      creatorSizePresetCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          FrameSizePresets.FrameSizePreset preset = (FrameSizePresets.FrameSizePreset) creatorSizePresetCmb
              .getSelectedItem();
          if (preset != null) {
            creatorWidthREd.setText(String.valueOf(preset
                .getWidth()));
            creatorHeightREd.setText(String.valueOf(preset
                .getHeight()));
          }
        }
      });
    }
    return creatorSizePresetCmb;
  }

  /**
   * This method initializes loadersContentPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getLoadersContentPanel() {
    if (loadersContentPanel == null) {
      loadersContentPanel = new JPanel();
      loadersContentPanel.setLayout(new BorderLayout());
      loadersContentPanel.setBorder(BorderFactory.createEmptyBorder(10,
          10, 10, 10));
      loadersContentPanel
          .add(getLoadersBottomPanel(), BorderLayout.SOUTH);
      loadersContentPanel.add(getLoadersSplitPane(), BorderLayout.CENTER);
    }
    return loadersContentPanel;
  }

  /**
   * This method initializes transformersSplitPane
   * 
   * @return javax.swing.JSplitPane
   */
  private JSplitPane getTransformersSplitPane() {
    if (transformersSplitPane == null) {
      transformersSplitPane = new JSplitPane();
      transformersSplitPane.setPreferredSize(new Dimension(78, 78));
      transformersSplitPane.setDividerLocation(200);
      transformersSplitPane
          .setTopComponent(getTransformersCenterScrollPane());
      transformersSplitPane
          .setBottomComponent(getTransformerPropertiesPanel());
      transformersSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    }
    return transformersSplitPane;
  }

  /**
   * This method initializes creatorsSplitPane
   * 
   * @return javax.swing.JSplitPane
   */
  private JSplitPane getCreatorsSplitPane() {
    if (creatorsSplitPane == null) {
      creatorsSplitPane = new JSplitPane();
      creatorsSplitPane.setDividerLocation(200);
      creatorsSplitPane.setTopComponent(getCreatorsCenterScrollPane());
      creatorsSplitPane.setBottomComponent(getCreatorPropertiesPanel());
      creatorsSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    }
    return creatorsSplitPane;
  }

  /**
   * This method initializes loaderExecuteButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getLoaderExecuteButton() {
    if (loaderExecuteButton == null) {
      loaderExecuteButton = new JButton();
      loaderExecuteButton.setText("Load image");
      loaderExecuteButton.setLocation(new Point(90, 8));
      loaderExecuteButton.setPreferredSize(new Dimension(145, 26));
      loaderExecuteButton.setSize(new Dimension(145, 26));
      loaderExecuteButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              loaderExecuteButton_actionPerformed(e);
            }
          });
    }
    return loaderExecuteButton;
  }

  /**
   * This method initializes creatorPropertiesPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getCreatorPropertiesPanel() {
    if (creatorPropertiesPanel == null) {
      creatorPropertiesPanel = new JPanel();
      creatorPropertiesPanel.setLayout(new BorderLayout());
      creatorPropertiesPanel.add(getCreatorsPresetPanel(), BorderLayout.NORTH);
    }
    return creatorPropertiesPanel;
  }

  /**
   * This method initializes transformerExecuteButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getTransformerExecuteButton() {
    if (transformerExecuteButton == null) {
      transformerExecuteButton = new JButton();
      transformerExecuteButton.setText("Transform");
      transformerExecuteButton.setSize(new Dimension(145, 26));
      transformerExecuteButton.setPreferredSize(new Dimension(145, 26));
      transformerExecuteButton.setLocation(new Point(97, 56));
      transformerExecuteButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              transformerExecuteButton_actionPerformed(e);
            }
          });
    }
    return transformerExecuteButton;
  }

  /**
   * This method initializes creatorsPresetPanel  
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getCreatorsPresetPanel() {
    if (creatorsPresetPanel == null) {
      creatorPresetLbl = new JLabel();
      creatorPresetLbl.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
      creatorPresetLbl.setText("Preset");
      creatorPresetLbl.setLocation(new Point(2, 0));
      creatorPresetLbl.setSize(new Dimension(72, 26));
      creatorPresetLbl.setPreferredSize(new Dimension(72, 26));
      creatorsPresetPanel = new JPanel();
      creatorsPresetPanel.setLayout(null);
      creatorsPresetPanel.setPreferredSize(new Dimension(0, 26));
      creatorsPresetPanel.add(creatorPresetLbl, null);
      creatorsPresetPanel.add(getCreatorPresetCmb(), null);
    }
    return creatorsPresetPanel;
  }

  /**
   * This method initializes creatorPresetCmb 
   *  
   * @return javax.swing.JComboBox  
   */
  JComboBox getCreatorPresetCmb() {
    if (creatorPresetCmb == null) {
      creatorPresetCmb = new JComboBox();
      creatorPresetCmb.setPreferredSize(new Dimension(233, 26));
      creatorPresetCmb.setLocation(new Point(82, 0));
      creatorPresetCmb.setSize(new Dimension(233, 26));
      creatorPresetCmb
          .addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent e) {
              creatorPresetCmb_itemStateChanged(e);
            }
          });
    }
    return creatorPresetCmb;
  }

  /**
   * This method initializes loadersBottomPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getLoadersBottomPanel() {
    if (loadersBottomPanel == null) {
      loadersBottomPanel = new JPanel();
      loadersBottomPanel.setLayout(null);
      loadersBottomPanel.setPreferredSize(new Dimension(0, 40));
      loadersBottomPanel.add(getLoaderExecuteButton(), null);
    }
    return loadersBottomPanel;
  }

  /**
   * This method initializes loadersSplitPane
   * 
   * @return javax.swing.JSplitPane
   */
  private JSplitPane getLoadersSplitPane() {
    if (loadersSplitPane == null) {
      loadersSplitPane = new JSplitPane();
      loadersSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
      loadersSplitPane.setTopComponent(getLoadersScrollPane());
      loadersSplitPane.setBottomComponent(getLoadersPropertiesPanel());
      loadersSplitPane.setDividerLocation(200);
    }
    return loadersSplitPane;
  }

  /**
   * This method initializes loadersScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getLoadersScrollPane() {
    if (loadersScrollPane == null) {
      loadersScrollPane = new JScrollPane();
      loadersScrollPane.setViewportView(getLoadersList());
    }
    return loadersScrollPane;
  }

  /**
   * This method initializes loadersPropertiesPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getLoadersPropertiesPanel() {
    if (loadersPropertiesPanel == null) {
      loadersPropertiesPanel = new JPanel();
      loadersPropertiesPanel.setLayout(new BorderLayout());
    }
    return loadersPropertiesPanel;
  }

  /**
   * This method initializes loadersList
   * 
   * @return javax.swing.JList
   */
  JList getLoadersList() {
    if (loadersList == null) {
      loadersList = new JList();
      loadersList
          .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(
                javax.swing.event.ListSelectionEvent e) {
              loadersList_valueChanged(e);
            }
          });
      loadersList.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          loadersList_mouseClicked(e);
        }
      });
    }
    return loadersList;
  }

  /**
   * This method initializes transformersCenterScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getTransformersCenterScrollPane() {
    if (transformersCenterScrollPane == null) {
      transformersCenterScrollPane = new JScrollPane();
      transformersCenterScrollPane.setViewportView(getTransformersList());
    }
    return transformersCenterScrollPane;
  }

  /**
   * This method initializes creatorsList
   * 
   * @return javax.swing.JList
   */
  JList getCreatorsList() {
    if (creatorsList == null) {
      creatorsList = new JList();
      creatorsList
          .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(
                javax.swing.event.ListSelectionEvent e) {
              creatorsList_valueChanged(e);
            }
          });
      creatorsList.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          creatorsList_mouseClicked(e);
        }
      });
    }
    return creatorsList;
  }

  /**
   * This method initializes transformersList
   * 
   * @return javax.swing.JList
   */
  JList getTransformersList() {
    if (transformersList == null) {
      transformersList = new JList();
      transformersList
          .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      transformersList
          .addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
              transformersList_mouseClicked(e);
            }
          });
      transformersList
          .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(
                javax.swing.event.ListSelectionEvent e) {
              transformersList_valueChanged(e);
            }
          });
    }
    return transformersList;
  }

  /**
   * This method initializes transformerInputCmb
   * 
   * @return javax.swing.JComboBox
   */
  JComboBox getTransformerInputCmb() {
    if (transformerInputCmb == null) {
      transformerInputCmb = new JComboBox();
      transformerInputCmb.setSize(new Dimension(251, 26));
      transformerInputCmb.setPreferredSize(new Dimension(251, 26));
      transformerInputCmb.setLocation(new Point(61, 5));
      transformerInputCmb
          .addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent e) {
              transformerInputCmb_itemStateChanged(e);
            }
          });
    }
    return transformerInputCmb;
  }

  private void transformerInputCmb_itemStateChanged(java.awt.event.ItemEvent e) {
    if (refreshing)
      return;
    String pName = (String) transformerInputCmb.getSelectedItem();
    mainController.setTransformerInput(pName);
    desktop.enableControls();
  }

  private void transformerPresetCmb_itemStateChanged(
      java.awt.event.ItemEvent e) {
    if (presetRefreshing)
      return;
    String presetName = (String) transformerPresetCmb.getSelectedItem();
    mainController.applyTransformerPreset(presetName);
    switchTransformerPropertiesPanel();
  }

  private void creatorPresetCmb_itemStateChanged(
      java.awt.event.ItemEvent e) {
    if (presetRefreshing)
      return;
    String presetName = (String) creatorPresetCmb.getSelectedItem();
    mainController.applyCreatorPreset(presetName);
    switchCreatorPropertiesPanel();
  }

  private void creatorsList_mouseClicked(java.awt.event.MouseEvent e) {
    if (e.getClickCount() == 2) {
      creatorExecuteButton_actionPerformed(null);
    }
  }

  private void loadersList_valueChanged(javax.swing.event.ListSelectionEvent e) {
    String name = (String) loadersList.getSelectedValue();
    mainController.selectLoader(name);
    switchLoaderPropertiesPanel();
    desktop.enableControls();
  }

  private void loadersList_mouseClicked(java.awt.event.MouseEvent e) {
    if (e.getClickCount() == 2) {
      loaderExecuteButton_actionPerformed(null);
    }
  }

  private void loaderExecuteButton_actionPerformed(
      java.awt.event.ActionEvent e) {
    try {
      mainController.executeLoader(null, true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    desktop.enableControls();
  }

  private void creatorExecuteButton_actionPerformed(
      java.awt.event.ActionEvent e) {
    try {
      int width = Integer.parseInt(creatorWidthREd.getText());
      int height = Integer.parseInt(creatorHeightREd.getText());
      mainController.executeCreator(width, height, null, true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    desktop.enableControls();
  }

  public void setMainController(MainController mainController) {
    this.mainController = mainController;
  }

  public void setDesktop(JWildfire desktop) {
    this.desktop = desktop;
  }

  /**
   * This method initializes transformerPresetCmb
   * 
   * @return javax.swing.JComboBox
   */
  JComboBox getTransformerPresetCmb() {
    if (transformerPresetCmb == null) {
      transformerPresetCmb = new JComboBox();
      transformerPresetCmb.setPreferredSize(new Dimension(251, 26));
      transformerPresetCmb.setSize(new Dimension(251, 26));
      transformerPresetCmb.setLocation(new Point(61, 0));
      transformerPresetCmb
          .addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent e) {
              transformerPresetCmb_itemStateChanged(e);
            }
          });
    }
    return transformerPresetCmb;
  }

  private void transformersList_valueChanged(
      javax.swing.event.ListSelectionEvent e) {
    String name = (String) transformersList.getSelectedValue();
    Transformer currTransformer = mainController.getTransformer();
    if ((currTransformer == null)
        || (!currTransformer.getName().equals(name))) {
      mainController.selectTransformer(name);
      mainController.fillInputBufferCmb();
      presetRefreshing = true;
      try {
        mainController.fillTransformerPresetCmb();
      }
      finally {
        presetRefreshing = false;
      }
      switchTransformerPropertiesPanel();
      transformerOutputMesh3DCBx.setSelected(false);
      desktop.enableControls();
    }
  }

  private void transformerExecuteButton_actionPerformed(
      java.awt.event.ActionEvent e) {
    try {
      String pName = (String) transformerInputCmb.getSelectedItem();
      mainController.executeTransformer(pName,
          transformerOutputMesh3DCBx.isSelected(), null, null, true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    desktop.enableControls();
  }

  private void creatorsList_valueChanged(
      javax.swing.event.ListSelectionEvent e) {
    String name = (String) creatorsList.getSelectedValue();
    mainController.selectCreator(name);
    presetRefreshing = true;
    try {
      mainController.fillCreatorsPresetCmb();
    }
    finally {
      presetRefreshing = false;
    }
    switchCreatorPropertiesPanel();
    desktop.enableControls();
  }

  void switchTransformerPropertiesPanel() {
    if (currTransformerPropertyPanel != null)
      transformerPropertiesPanel
          .remove((JPanel) currTransformerPropertyPanel);
    currTransformerPropertyPanel = mainController
        .createTransformerPropertyPanel();
    transformerPropertiesPanel.add((JPanel) currTransformerPropertyPanel,
        BorderLayout.CENTER);
    transformerPropertiesPanel.invalidate();
    transformerPropertiesPanel.validate();
    // System.out.println("SWITCH PANEL");
  }

  void switchCreatorPropertiesPanel() {
    if (currCreatorPropertyPanel != null)
      creatorPropertiesPanel.remove((JPanel) currCreatorPropertyPanel);
    currCreatorPropertyPanel = mainController.createCreatorPropertyPanel();
    creatorPropertiesPanel.add((JPanel) currCreatorPropertyPanel,
        BorderLayout.CENTER);
    creatorPropertiesPanel.invalidate();
    creatorPropertiesPanel.validate();
  }

  void switchLoaderPropertiesPanel() {
    if (currLoaderPropertyPanel != null)
      loadersPropertiesPanel.remove((JPanel) currLoaderPropertyPanel);
    currLoaderPropertyPanel = mainController.createLoaderPropertyPanel();
    loadersPropertiesPanel.add((JPanel) currLoaderPropertyPanel,
        BorderLayout.CENTER);
    loadersPropertiesPanel.invalidate();
    loadersPropertiesPanel.validate();
  }

  /* This method initializes transformerPropertiesPanel
    * 
    * @return javax.swing.JPanel
    */
  private JPanel getTransformerPropertiesPanel() {
    if (transformerPropertiesPanel == null) {
      transformerPropertiesPanel = new JPanel();
      transformerPropertiesPanel.setLayout(new BorderLayout());
      transformerPropertiesPanel.add(getOperatorsPresetPanel(),
          BorderLayout.NORTH);
    }
    return transformerPropertiesPanel;
  }

  private void transformersList_mouseClicked(java.awt.event.MouseEvent e) {
    if (e.getClickCount() == 2) {
      transformerExecuteButton_actionPerformed(null);
    }
  }

  /**
   * This method initializes operatorsPresetPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getOperatorsPresetPanel() {
    if (operatorsPresetPanel == null) {
      transformerPresetLbl = new JLabel();
      transformerPresetLbl.setBounds(new Rectangle(7, 0, 52, 26));
      transformerPresetLbl.setText("Preset");
      transformerPresetLbl.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
      transformerPresetLbl.setPreferredSize(new Dimension(28, 26));
      operatorsPresetPanel = new JPanel();
      operatorsPresetPanel.setLayout(null);
      operatorsPresetPanel.setPreferredSize(new Dimension(0, 26));
      operatorsPresetPanel.add(transformerPresetLbl, null);
      operatorsPresetPanel.add(getTransformerPresetCmb(), null);
    }
    return operatorsPresetPanel;
  }

  public void initApp() {
    creatorsList.setListData(CreatorsList.getItemVector());
    if (creatorsList.getModel().getSize() > 0)
      creatorsList.setSelectedIndex(0);
    transformersList.setListData(TransformersList.getItemVector());
    if (transformersList.getModel().getSize() > 0)
      transformersList.setSelectedIndex(0);
    loadersList.setListData(LoadersList.getItemVector());
    if (loadersList.getModel().getSize() > 0)
      loadersList.setSelectedIndex(0);
    for (FrameSizePresets.FrameSizePreset preset : FrameSizePresets
        .getPresets()) {
      creatorSizePresetCmb.addItem(preset);
      if (preset.getWidth() == 800)
        creatorSizePresetCmb.setSelectedItem(preset);
    }
  }

  public void enableControls() {
    Transformer t = mainController.getTransformer();
    transformerOutputMesh3DCBx.setEnabled((t != null)
          && t.supports3DOutput());
  }

}
