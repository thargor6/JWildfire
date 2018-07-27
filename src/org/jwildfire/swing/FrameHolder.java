package org.jwildfire.swing;

import javax.swing.*;

public interface FrameHolder {
    void saveWindowPrefs();

    JCheckBoxMenuItem getMenuItem();

    void enableMenu();
}

