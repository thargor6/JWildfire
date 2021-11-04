/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.jwildfire.base.Prefs;
import org.nfunk.jep.function.Str;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.text.SimpleDateFormat;

@SuppressWarnings("serial")
public class MessageLogFrame extends JFrame implements MessageLogEventObserver {
  private JPanel jContentPane = null;

  public MessageLogFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(1188, 740);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT, JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Message log");
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
      jContentPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      jContentPane.setSize(new Dimension(1097, 617));
      jContentPane.add(getScrollPane(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  private JScrollPane scrollPane;
  private JTextPane logMessagesPane;

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setPreferredSize(new Dimension(6, 400));
      scrollPane.setViewportView(getLogMessagesPane());
    }
    return scrollPane;
  }

  JTextPane getLogMessagesPane() {
    if (logMessagesPane == null) {
      logMessagesPane = new JTextPane();
      logMessagesPane.setBackground(SystemColor.menu);
      logMessagesPane.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 14));
      logMessagesPane.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
              java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });
      logMessagesPane.setEditable(false);
    }
    return logMessagesPane;
  }

  public void initLogMessagePane() {
    logMessagesPane.setContentType("text/html");
    try {
      //logMessagesPane.setText("<pre>" + "" + "</pre>");
      logMessagesPane.setSelectionStart(0);
      logMessagesPane.setSelectionEnd(0);
      MessageLogMapHolder.create().registerObserver(this);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void update(ILoggingEvent event) {
    StringBuilder builder = new StringBuilder();
    builder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.getTimeStamp())+" "+event.getLevel()+"\n");
    builder.append("    "+event.getLoggerName()+ ": " + event.getFormattedMessage()+"\n");
    for(StackTraceElement element: event.getCallerData()) {
      builder.append("        "+element.toString()+"\n");
    }
    builder.append("\n\n");
    try {
      Document doc = logMessagesPane.getDocument();
      doc.insertString(0, builder.toString(), null);
      logMessagesPane.setCaretPosition(0);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }
}
