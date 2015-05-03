package org.jwildfire.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Vector;

import javax.swing.JDesktopPane;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jwildfire.image.SimpleImage;

public class LoaderTest 
{
	@BeforeClass
	public static void before()
	{
		try {
			File f = new File("/dev/shm");
			if(!f.isDirectory())
				System.err.println("Error, test needs a /dev/shm");
			if(!f.exists())
			{
				f.mkdir();
				System.out.println("created a /dev/shm");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	@Test
	public void testLoadersListNull()
	{	//default should return null since there are no loaders
		String name = "test";
		JDesktopPane desk = new JDesktopPane();
		ImageLoader imageloader = LoadersList.getLoaderInstance(desk,name);
		Assert.assertNull(imageloader);
	}
	
	@Test
	public void testLoadersList()
	{
		//Note at init the name must be ImageSequenceLoader to return from getLoaderInstance
		//I don't think it is possible to add any other instances to LoadersList.items, there is only
		//one ImageSequenceLoader, which is created statically
		String name = "ImageSequenceLoader";
		JDesktopPane desk = new JDesktopPane();
		ImageLoader imageloader = LoadersList.getLoaderInstance(desk,name);
		Vector<String> loadVec = LoadersList.getItemVector();
		Assert.assertNotNull(imageloader);
		for(String item:loadVec)
			System.out.println(item);
	}
	@Test
	public void testImageLoader() throws Exception
	{
		//use an image we have available already in classpath
		FileInputStream fs = new FileInputStream("Delphi/jwildfire_icon.png");
		FileOutputStream fo = new FileOutputStream(new File("/dev/shm/test007"));
		IOUtils.copyLarge(fs, fo);
		fs.close();
		fo.close();
		String fn="/dev/shm/test001";
		String name = "ImageSequenceLoader";
		JDesktopPane desk = new JDesktopPane();
		ImageSequenceLoader imageloader = (ImageSequenceLoader)LoadersList.getLoaderInstance(null,name);
		imageloader.setFilename(fn);
		imageloader.setFrame(7);
		Assert.assertEquals(fn,imageloader.getFilename());
		Assert.assertEquals(7,imageloader.getFrame());
		imageloader.setDesktop(desk);
		Assert.assertEquals(desk,imageloader.getDesktop());
		Assert.assertEquals(name,imageloader.getName());
		Assert.assertEquals(name, imageloader.getBeanInfo().getBeanDescriptor().getName());
		SimpleImage si = imageloader.loadImage();
		Assert.assertEquals(48, si.getImageHeight());
		Assert.assertEquals(48, si.getImageWidth());
		Assert.assertEquals(1.0, si.getAspect(),.000001);
		imageloader.execute();
		new File("/dev/shm/test007").delete();
	}
}
