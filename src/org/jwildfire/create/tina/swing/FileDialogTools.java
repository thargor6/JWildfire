/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2020 Andreas Maschke

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

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.swing.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileDialogTools {
  private static final Set<String> visitedFolders = new HashSet<>();

  private static String getFolderPath(File file) {
    if (file.isDirectory()) {
      return file.getAbsolutePath();
    } else {
      return file.getParentFile() != null ? file.getParentFile().getAbsolutePath() : "";
    }
  }

  private static void addToVisitedFolders(File file) {
    String path = getFolderPath(file);
    visitedFolders.add(path);
  }

  public static void ensureFileAccess(Frame frame, Component parent, String path) {
    ensureFileAccess(frame, parent, new File(path));
  }

  public static void ensureFileAccess(Frame frame, Component parent, File file) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      String path = getFolderPath(file);
      if (!visitedFolders.contains(path)) {
        int i = 0;
        int maxTries = 3;
        while (i < maxTries) {
          StandardDialogs.message(
              parent,
              "Please select the folder <"
                  + path
                  + "> in the following dialog in order to obtain write access to it");
          String selectPath =
              selectDirectory(frame, parent, "Please select the folder <" + path + ">", path);
          if (selectPath != null) {
            String selectedFolderPath = getFolderPath(new File(selectPath));
            visitedFolders.add(selectedFolderPath);
            if (selectedFolderPath.equals(path)) {
              break;
            } else {
              i++;
            }
          }
        }
      }
    }
  }

  public static File selectFlameFileForOpen(
      Frame frame, Component centerPanel, String defaultFilename) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Open a flame-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new FlameFileFilter().accept(new File(dir, name));
            }
          });
      if (defaultFilename != null && defaultFilename.length() > 0) {
        try {
          fileDialog.setDirectory(new File(defaultFilename).getAbsoluteFile().getParent());
          fileDialog.setFile(defaultFilename);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      } else if (Prefs.getPrefs().getInputFlamePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getInputFlamePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setMode(FileDialog.LOAD);
      fileDialog.setModal(true);
      fileDialog.setVisible(true);
      if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_FLAME});
        Prefs.getPrefs().setLastInputFlameFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new FlameFileChooser(Prefs.getPrefs());
      if (defaultFilename != null && defaultFilename.length() > 0) {
        try {
          chooser.setCurrentDirectory(new File(defaultFilename).getAbsoluteFile().getParentFile());
          chooser.setSelectedFile(new File(defaultFilename));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      } else if (Prefs.getPrefs().getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputFlamePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastInputFlameFile(file);
        return file;
      }
    }
    return null;
  }

  // append the first given file-extension if the file has not any of the given file-extensions
  private static File enforceFileExt(File file, String[] extensions) {
    if (file == null || extensions == null || extensions.length == 0) {
      throw new IllegalArgumentException();
    }
    String name = file.getName();
    String currFileExt = Tools.getFileExt(name);
    for (String ext : extensions) {
      if (ext != null && ext.length() > 0 && ext.equals(currFileExt)) {
        return file;
      }
    }
    for (String ext : extensions) {
      if (ext != null && ext.length() > 0) {
        return new File(file.getParent(), name + '.' + ext);
      }
    }
    return file;
  }

  public static File selectFlameFileForSave(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Save flame-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new FlameFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getOutputFlamePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getOutputFlamePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(createFileNamePreset(Tools.FILEEXT_FLAME));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, Tools.FILEEXT_FLAME))  {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_FLAME});
        Prefs.getPrefs().setLastOutputFlameFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new FlameFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getOutputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getOutputFlamePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastOutputFlameFile(file);
        return file;
      }
    }
    return null;
  }

  private static boolean openSaveDialog(Component parent, FileDialog fileDialog, String fileExt) {
    fileDialog.setVisible(true);
    if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
      if(fileDialog.getFilenameFilter()!=null) {
        if(!fileDialog.getFilenameFilter().accept(fileDialog.getFiles()[0].getParentFile(), fileDialog.getFiles()[0].getName())) {
          StandardDialogs.message(
                  parent,
                  "Please specify a filename including a file-extension, e.g. \"" + createFileNamePreset(fileExt) +"\"");
          return false;
        }
        else {
          return true;
        }
      } else {
        return true;
      }
    }
    return false;
  }

  private static String createFileNamePreset(String fileExt) {
    if (fileExt != null && fileExt.length() > 0) {
      return "Untitled" + "." + fileExt;
    } else {
      return "Untitled";
    }
  }

  public static File selectMapFileForSave(
      Frame frame, Component centerPanel, String previousGradientPath) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Open a map-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new MapFileFilter().accept(new File(dir, name));
            }
          });
      if (previousGradientPath != null) {
        try {
          fileDialog.setDirectory(previousGradientPath);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      } else if (Prefs.getPrefs().getTinaGradientPath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getTinaGradientPath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(createFileNamePreset(Tools.FILEEXT_MAP));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, Tools.FILEEXT_MAP))  {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_MAP});
        return file;
      }
    } else {
      JFileChooser chooser = new MapFileChooser(Prefs.getPrefs());
      if (previousGradientPath != null) {
        try {
          chooser.setCurrentDirectory(new File(previousGradientPath));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      } else if (Prefs.getPrefs().getTinaGradientPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getTinaGradientPath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        return file;
      }
    }
    return null;
  }

  public static File selectRessourceForOpen(
      Frame frame, Component centerPanel, RessourceDialog.ContentType contentType) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Open a ressource");
      try {
        if (contentType == RessourceDialog.ContentType.JAVA)
          fileDialog.setDirectory(Prefs.getPrefs().getTinaCustomVariationsPath());
        else fileDialog.setDirectory(new File(Prefs.getPrefs().getInputFlamePath()).getParent());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      fileDialog.setMode(FileDialog.LOAD);
      fileDialog.setModal(true);
      fileDialog.setVisible(true);
      if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
        File file = fileDialog.getFiles()[0];
        Prefs.getPrefs().setLastInputFlameFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new JFileChooser();
      if (Prefs.getPrefs().getInputFlamePath() != null) {
        try {
          if (contentType == RessourceDialog.ContentType.JAVA)
            chooser.setCurrentDirectory(new File(Prefs.getPrefs().getTinaCustomVariationsPath()));
          else
            chooser.setCurrentDirectory(
                new File(Prefs.getPrefs().getInputFlamePath()).getParentFile());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          return file;
        }
      }
    }
    return null;
  }

  public static File selectSoundFileForOpen(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Open a sound-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new SoundFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getInputSoundFilePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getInputSoundFilePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setMode(FileDialog.LOAD);
      fileDialog.setModal(true);
      fileDialog.setVisible(true);
      if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
        File file =
            enforceFileExt(
                fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_MP3, Tools.FILEEXT_WAV});
        Prefs.getPrefs().setLastInputSoundFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new SoundFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getInputSoundFilePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputSoundFilePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastInputSoundFile(file);
        return file;
      }
    }
    return null;
  }

  public static List<File> selectFlameFilesForOpen(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Open flame-files...");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new FlameFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getInputFlamePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getInputFlamePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setMultipleMode(true);
      fileDialog.setMode(FileDialog.LOAD);
      fileDialog.setModal(true);
      fileDialog.setVisible(true);
      if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < fileDialog.getFiles().length; i++) {
          File file = fileDialog.getFiles()[i];
          Prefs.getPrefs().setLastInputFlameFile(file);
          files.add(file);
        }
        return files;
      }
    } else {
      JFileChooser chooser = new FlameFileChooser(Prefs.getPrefs());
      chooser.setMultiSelectionEnabled(true);
      if (Prefs.getPrefs().getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputFlamePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        List<File> files = new ArrayList<>();
        for (File file : chooser.getSelectedFiles()) {
          Prefs.getPrefs().setLastInputFlameFile(file);
          files.add(file);
        }
        return files;
      }
    }
    return null;
  }

  public static File selectJWFDanceFileForOpen(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Open a JWFDance-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new JWFDanceFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getInputJWFMoviePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getInputJWFMoviePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setMode(FileDialog.LOAD);
      fileDialog.setModal(true);
      fileDialog.setVisible(true);
      if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_JWFDANCE});
        Prefs.getPrefs().setLastInputSoundFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new JWFDanceFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getInputJWFMoviePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputJWFMoviePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastInputSoundFile(file);
        return file;
      }
    }
    return null;
  }

  public static File selectJWFDanceFileForSave(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Save JWFDance-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new JWFDanceFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getOutputJWFMoviePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getOutputJWFMoviePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(createFileNamePreset(Tools.FILEEXT_JWFDANCE));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, Tools.FILEEXT_JWFDANCE))  {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_JWFDANCE});
        Prefs.getPrefs().setLastOutputJWFMovieFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new JWFDanceFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getOutputJWFMoviePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getOutputJWFMoviePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastOutputJWFMovieFile(file);
        return file;
      }
    }
    return null;
  }

  public static File selectImageFileForOpen(
      Frame frame, Component centerPanel, String defaultExtension, String defaultFilename) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Open an image");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new ImageFileFilter().accept(new File(dir, name))
                  || new HDRImageFileFilter().accept(new File(dir, name));
            }
          });
      if (defaultFilename != null && defaultFilename.length() > 0) {
        try {
          fileDialog.setDirectory(new File(defaultFilename).getAbsoluteFile().getParent());
          fileDialog.setFile(defaultFilename);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      } else if (Prefs.getPrefs().getInputImagePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getInputImagePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setMode(FileDialog.LOAD);
      fileDialog.setModal(true);
      fileDialog.setVisible(true);
      if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
        File file =
            enforceFileExt(
                fileDialog.getFiles()[0],
                new String[] {
                  defaultExtension,
                  Tools.FILEEXT_PNG,
                  Tools.FILEEXT_PNS,
                  Tools.FILEEXT_JPEG,
                  Tools.FILEEXT_JPG,
                  Tools.FILEEXT_HDR,
                  Tools.FILEEXT_GIF,
                  Tools.FILEEXT_JPS
                });
        Prefs.getPrefs().setLastInputImageFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new ImageFileChooser(defaultExtension);
      if (defaultFilename != null && defaultFilename.length() > 0) {
        try {
          chooser.setCurrentDirectory(new File(defaultFilename).getAbsoluteFile().getParentFile());
          chooser.setSelectedFile(new File(defaultFilename));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      } else if (Prefs.getPrefs().getInputImagePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputImagePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastInputImageFile(file);
        return file;
      }
    }
    return null;
  }

  public static File selectImageFileForSave(
      Frame frame, Component centerPanel, String defaultExtension) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Save an image");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new ImageFileFilter().accept(new File(dir, name))
                  || new HDRImageFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getOutputImagePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getOutputImagePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(
          createFileNamePreset(
              defaultExtension != null && defaultExtension.length() > 0
                  ? defaultExtension
                  : Tools.FILEEXT_PNG));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, defaultExtension != null && defaultExtension.length() > 0
              ? defaultExtension
              : Tools.FILEEXT_PNG))  {
        File file =
            enforceFileExt(
                fileDialog.getFiles()[0],
                new String[] {
                  defaultExtension,
                  Tools.FILEEXT_PNG,
                  Tools.FILEEXT_PNS,
                  Tools.FILEEXT_JPEG,
                  Tools.FILEEXT_JPG,
                  Tools.FILEEXT_HDR,
                  Tools.FILEEXT_GIF,
                  Tools.FILEEXT_JPS
                });
        Prefs.getPrefs().setLastOutputImageFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new ImageFileChooser(defaultExtension);
      if (Prefs.getPrefs().getOutputImagePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getOutputImagePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastOutputImageFile(file);
        return file;
      }
    }
    return null;
  }

  public static File selectPointCloudFileForSave(
      Frame frame, Component centerPanel, String defaultExtension) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Save pointcloud-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new PointCloudOutputFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getTinaMeshPath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getTinaMeshPath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(
          createFileNamePreset(
              defaultExtension != null && defaultExtension.length() > 0
                  ? defaultExtension
                  : Tools.FILEEXT_OBJ));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, defaultExtension != null && defaultExtension.length() > 0
              ? defaultExtension
              : Tools.FILEEXT_OBJ))  {
        File file =
            enforceFileExt(
                fileDialog.getFiles()[0],
                new String[] {defaultExtension, Tools.FILEEXT_OBJ, Tools.FILEEXT_PLY});
        Prefs.getPrefs().setLastMeshFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new PointCloudOutputFileChooser(defaultExtension);
      if (Prefs.getPrefs().getTinaMeshPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getTinaMeshPath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastMeshFile(file);
        return file;
      }
    }
    return null;
  }

  public static File selectMeshFileForSave(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Save mesh-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new MeshFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getTinaMeshPath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getTinaMeshPath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(createFileNamePreset(Tools.FILEEXT_OBJ));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, Tools.FILEEXT_OBJ))  {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_OBJ});
        Prefs.getPrefs().setLastMeshFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new MeshFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getTinaMeshPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getTinaMeshPath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastMeshFile(file);
        return file;
      }
    }
    return null;
  }

  public static File selectMeshFileForOpen(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Open mesh-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new MeshFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getTinaMeshPath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getTinaMeshPath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setMode(FileDialog.LOAD);
      fileDialog.setModal(true);
      fileDialog.setVisible(true);
      if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_OBJ});
        Prefs.getPrefs().setLastMeshFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new MeshFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getTinaMeshPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getTinaMeshPath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastMeshFile(file);
        return file;
      }
    }
    return null;
  }

  public static File selectSvgFileForOpen(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Open svg-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new SvgFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getTinaSVGPath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getTinaSVGPath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setMode(FileDialog.LOAD);
      fileDialog.setModal(true);
      fileDialog.setVisible(true);
      if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_SVG});
        return file;
      }
    } else {
      JFileChooser chooser = new SvgFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getTinaSVGPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getTinaSVGPath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        return file;
      }
    }
    return null;
  }

  public static String selectDirectory(
      Frame frame, Component centerPanel, String caption, String directory) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      String oldProp = System.getProperty("apple.awt.fileDialogForDirectories");
      try {
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        FileDialog fileDialog = new FileDialog(frame, caption);
        fileDialog.setModal(true);
        fileDialog.setVisible(true);
        if (directory != null && !directory.isEmpty()) {
          try {
            fileDialog.setDirectory(directory);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (fileDialog.getFile() != null && !fileDialog.getFile().isEmpty()) {
          File file = fileDialog.getFiles()[0];
          // return file.getParentFile().getAbsolutePath();
          return file.getAbsolutePath();
        }
      } finally {
        System.setProperty(
            "apple.awt.fileDialogForDirectories", (oldProp != null) ? oldProp : "false");
      }
    } else {
      JFileChooser chooser = new JFileChooser();
      chooser = new JFileChooser();
      chooser.setDialogTitle(caption);
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      if (directory != null && !directory.isEmpty()) {
        try {
          chooser.setCurrentDirectory(new File(directory));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        return chooser.getSelectedFile().getAbsolutePath();
      }
    }
    return null;
  }

  public static File selectFileForOpen(
      Frame frame, Component centerPanel, String caption, String defaultFilename) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, caption);
      if (defaultFilename != null && defaultFilename.length() > 0) {
        try {
          fileDialog.setDirectory(new File(defaultFilename).getAbsoluteFile().getParent());
          fileDialog.setFile(defaultFilename);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setMode(FileDialog.LOAD);
      fileDialog.setModal(true);
      fileDialog.setVisible(true);
      if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
        File file = fileDialog.getFiles()[0];
        return file;
      }
    } else {
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle(caption);
      if (defaultFilename != null && defaultFilename.length() > 0) {
        try {
          chooser.setCurrentDirectory(new File(defaultFilename).getAbsoluteFile().getParentFile());
          chooser.setSelectedFile(new File(defaultFilename));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        return file;
      }
    }
    return null;
  }

  public static File selectJWFRenderFileForSave(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Save render-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new JWFRenderFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getOutputFlamePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getOutputFlamePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(createFileNamePreset(Tools.FILEEXT_JWFRENDER));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, Tools.FILEEXT_JWFRENDER))  {
        File file =
            enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_JWFRENDER});
        Prefs.getPrefs().setLastOutputFlameFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new JWFRenderFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getOutputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getOutputFlamePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastOutputFlameFile(file);
        return file;
      }
    }
    return null;
  }

  public static File selectJWFRenderFileForOpen(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Open render-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new JWFRenderFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getInputFlamePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getInputFlamePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setMode(FileDialog.LOAD);
      fileDialog.setModal(true);
      fileDialog.setVisible(true);
      if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
        File file =
            enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_JWFRENDER});
        Prefs.getPrefs().setLastInputFlameFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new JWFRenderFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputFlamePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastInputFlameFile(file);
        return file;
      }
    }
    return null;
  }

  public static File selectRenderFileForSave(
      Frame frame, Component centerPanel, String defaultExtension) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Render an image/video");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new ImageFileFilter().accept(new File(dir, name))
                  || new VideoFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getOutputImagePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getOutputImagePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(
          createFileNamePreset(
              defaultExtension != null && defaultExtension.length() > 0
                  ? defaultExtension
                  : Tools.FILEEXT_PNG));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, defaultExtension != null && defaultExtension.length() > 0
              ? defaultExtension
              : Tools.FILEEXT_PNG))  {
        File file =
            enforceFileExt(
                fileDialog.getFiles()[0],
                new String[] {
                  defaultExtension,
                  Tools.FILEEXT_PNG,
                  Tools.FILEEXT_JPG,
                  Tools.FILEEXT_JPEG,
                  Tools.FILEEXT_MP4
                });
        Prefs.getPrefs().setLastOutputImageFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new RenderOutputFileChooser(defaultExtension);
      if (Prefs.getPrefs().getOutputImagePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getOutputImagePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastOutputImageFile(file);
        return file;
      }
    }
    return null;
  }

  public static File selectAnbFileForSave(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Render an ANB-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new ANBFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getOutputImagePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getOutputImagePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(createFileNamePreset(Tools.FILEEXT_ANB));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, Tools.FILEEXT_ANB))  {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_ANB});
        Prefs.getPrefs().setLastOutputImageFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileFilter(new ANBFileFilter());
      if (Prefs.getPrefs().getOutputImagePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getOutputImagePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        return file;
      }
    }
    return null;
  }

  public static File selectMp4FileForSave(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Render a MP4-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new VideoFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getOutputImagePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getOutputImagePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(createFileNamePreset(Tools.FILEEXT_MP4));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, Tools.FILEEXT_MP4))  {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_MP4});
        Prefs.getPrefs().setLastOutputImageFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileFilter(new VideoFileFilter());
      if (Prefs.getPrefs().getOutputImagePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getOutputImagePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        return file;
      }
    }
    return null;
  }

  public static File selectFlameSequenceFileForSave(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Save flame-file-sequence");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new FlameFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getMovieFlamesPath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getMovieFlamesPath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(createFileNamePreset(Tools.FILEEXT_FLAME));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, Tools.FILEEXT_FLAME))  {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_FLAME});
        return file;
      }
    } else {
      JFileChooser chooser = new FlameFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getMovieFlamesPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getMovieFlamesPath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        return file;
      }
    }
    return null;
  }

  public static File selectJWFMovieFileForOpen(
      Frame frame, Component centerPanel, String defaultFilename) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Open a jwfmovie-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new JWFMovieFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getInputJWFMoviePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getInputJWFMoviePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setMode(FileDialog.LOAD);
      fileDialog.setModal(true);
      fileDialog.setVisible(true);
      if (fileDialog.getFiles() != null && fileDialog.getFiles().length > 0) {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_JWFMOVIE});
        Prefs.getPrefs().setLastOutputJWFMovieFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new JWFMovieFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getInputJWFMoviePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputJWFMoviePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        return file;
      }
    }
    return null;
  }

  public static File selectJWFMovieFileForSave(Frame frame, Component centerPanel) {
    if (Tools.OSType.MAC == Tools.getOSType()) {
      FileDialog fileDialog = new FileDialog(frame, "Save jwfmovie-file");
      fileDialog.setFilenameFilter(
          new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new JWFMovieFileFilter().accept(new File(dir, name));
            }
          });
      if (Prefs.getPrefs().getOutputJWFMoviePath() != null) {
        try {
          fileDialog.setDirectory(Prefs.getPrefs().getOutputJWFMoviePath());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      fileDialog.setFile(createFileNamePreset(Tools.FILEEXT_JWFMOVIE));
      fileDialog.setMode(FileDialog.SAVE);
      fileDialog.setModal(true);
      if(openSaveDialog(centerPanel, fileDialog, Tools.FILEEXT_JWFMOVIE))  {
        File file = enforceFileExt(fileDialog.getFiles()[0], new String[] {Tools.FILEEXT_JWFMOVIE});
        Prefs.getPrefs().setLastOutputJWFMovieFile(file);
        return file;
      }
    } else {
      JFileChooser chooser = new JWFMovieFileChooser(Prefs.getPrefs());
      if (Prefs.getPrefs().getOutputJWFMoviePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(Prefs.getPrefs().getOutputJWFMoviePath()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Prefs.getPrefs().setLastOutputJWFMovieFile(file);
        return file;
      }
    }
    return null;
  }
}
