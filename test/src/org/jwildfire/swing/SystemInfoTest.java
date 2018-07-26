package org.jwildfire.swing;

import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JTextPane;

import org.junit.*;

public class SystemInfoTest
{
	@Test
	public void testSystemInfo()
	{
		SystemInfo si = new SystemInfo();
		Assert.assertTrue(0<si.getMaxMemMB());
		Assert.assertNotNull(si.getOsArch());
		Assert.assertNotNull(si.getOsName());
		Assert.assertNotNull(si.getOsVersion());
		Assert.assertNotNull(0<si.getProcessors());
	}
	@Test
	public void testSystemInfoInternalFrame() throws Exception
	{
		//SwingHelper.printTree(sif, 0);
		SystemInfoInternalFrame sif = new SystemInfoInternalFrame();
		sif.setVisible(true);
		
		JButton comp  = (JButton)SwingHelper.match(sif, "siif.okbutton");
		JTextPane text  = (JTextPane)SwingHelper.match(sif, "siif.text");
		String text1=text.getText();
		Assert.assertNotNull(text1);
		
		for(MouseListener ml:(MouseListener[])(comp.getListeners(MouseListener.class)))
		{
			if(ml!=null&&ml.getClass().getName().contains("SystemInfoFrame"))
				ml.mouseClicked(null);
		}
		
		Assert.assertFalse("Expect SystemInfoFrame to no longer be visible after click event",sif.isVisible());
		byte b[] = new byte[4096*1024];//use 4 more MB
		sif.refresh();
		//make sure it is not using same information
		Assert.assertNotEquals("Added "+b.length +"bytes, expect different stats",text1, text.getText());
	}

}