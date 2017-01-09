/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JTable;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.create.tina.random.RandomGeneratorType;
import org.jwildfire.create.tina.render.ProgressUpdater;

/**
 * This is a headless controller used for commandline or web server based rendering (Swing components not needed)
 * @author boleslaw
 */
public class HeadlessBatchRendererController implements JobRenderThreadController
{
  JProgressBar pro = new JProgressBar();
  JTable table = new JTable();

  @Override
  public void refreshRenderBatchJobsTable() {
  }

  @Override
  public void onJobFinished() {
    System.out.println("Done");
  }

  @Override
  public JProgressBar getTotalProgressBar() {
    return pro;
  }

  @Override
  public JTable getRenderBatchJobsTable() {

    return table;
  }

  @Override
  public ProgressUpdater getJobProgressUpdater() {
    return new ProgressUpdater() {
      @Override
      public void updateProgress(int pStep) {
      }

      @Override
      public void initProgress(int pMaxSteps) {
      }
    };
  }

  @Override
  public JProgressBar getJobProgressBar() {
    return pro;
  }

  public static void main(String args[]) throws Exception
  {
    //		args = new String[]{"/dev/shm/","400","400","80"};//,"ZIGGURAT"};
    int height;
    int width;
    int quality;
    String filename;
    File f;
    List<File> files = new LinkedList<File>();
    long start = System.nanoTime();
    if (args.length < 4)
    {
      usage(args);
      return;
    }
    else
    {
      try {
        height = Integer.parseInt(args[1]);
        width = Integer.parseInt(args[2]);
        quality = Integer.parseInt(args[3]);
        filename = args[0].trim();
        String was = Prefs.getPrefs().getTinaRandomNumberGenerator().name();
        if (args.length > 4 && Arrays.toString(RandomGeneratorType.values()).contains(args[4]))
        {
          Prefs.getPrefs().setTinaRandomNumberGenerator(RandomGeneratorType.valueOf(args[4]));
          System.out.println(was + " changed to " + Prefs.getPrefs().getTinaRandomNumberGenerator().name());
        }
        f = new File(filename);
        if (!f.exists())
          throw new Exception(filename + " does not exist");
        if (f.isDirectory())
        {
          final File[] listFiles = f.listFiles();
          if (listFiles != null)
            for (File fi : listFiles)
            {
              if (fi.canRead() && fi.exists() && fi.getName().toLowerCase().endsWith(".flame"))
                files.add(fi);
            }
        }
        else if (f.getName().toLowerCase().endsWith(".flame"))
          files.add(f);

      }
      catch (Exception e)
      {
        usage(args);
        System.err.println(e.getMessage());
        return;
      }
    }
    ResolutionProfile respro = new ResolutionProfile(true, width, height);
    JobRenderThreadController controller = new HeadlessBatchRendererController();
    List<Job> joblist = new ArrayList<Job>();
    for (File fi : files)
    {
      Job j = new Job();
      j.setCustomHeight(height);
      j.setCustomWidth(width);
      j.setCustomQuality(quality);
      j.setFlameFilename(fi.getCanonicalPath());
      joblist.add(j);
    }
    QualityProfile qualpro = new QualityProfile();
    qualpro.setQuality(quality);
    JobRenderThread job = new JobRenderThread(controller, joblist, respro, qualpro, true, false);
    job.run();
    System.out.println((System.nanoTime() - start) / 1000 / 1000 + " ms");
  }

  private static void usage(String args[]) {
    System.out.println("Please include the following parameters: \n\tfilename,height,width,quality,[optional:rng]\n" +
        "Example: HeadlessBatchRendererController file1.flame,1024,1280,2000\n" +
        "Example: HeadlessBatchRendererController file1.flame,1024,1280,2000,ZIGGURAT");
    if (args != null)
      System.out.println("You Passed: " + Arrays.toString(args) + "\n");
    System.out.println();
  }
}