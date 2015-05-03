package org.jwildfire.transform;

import java.awt.Dimension;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.ImagePanel;

public class TestTransformerFrame extends JFrame
{
	public static boolean live=false;
	private static final long serialVersionUID = -1L;
	protected JDesktopPane pane = new JDesktopPane(); 	
	protected SimpleImage si = new SimpleImage(500, 500);
	protected Buffer b;
	public TestTransformerFrame()
	{
		si.fillBackground(0, 255, 0);
		for(int j=0;j<50;j++)
		{	
			for(int i=0;i<500;i++)
			{
				si.setRGB(j,i,0,0,255);
				si.setARGB(j, i, 50,0,0,255);//set alpha channel on left band to 50
			}
		}
		ImagePanel imagePanel = new ImagePanel(si, 0, 0, si.getImageWidth());
	    imagePanel.setSize(si.getImageWidth(), si.getImageHeight());
	    imagePanel.setPreferredSize(new Dimension(si.getImageWidth(), si.getImageHeight()));
	    add(imagePanel);
		b=new Buffer(pane, "pane1", si);
		setTitle("Transformer test");
		setSize(500,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if(live)
			setVisible(true);
	}
	public JDesktopPane getPane() {
		return pane;
	}
	public Buffer getBuffer() {
		return b;
	}
}