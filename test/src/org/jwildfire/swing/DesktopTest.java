package org.jwildfire.swing;

import java.awt.Window;

import org.junit.Test;

public class DesktopTest 
{
	@Test
	/**
	 * Provide this as a base for Desktop testing, just invoke the main method for now 
	 */
	public void testDesktop() throws Exception
	{
		Desktop.main(new String[]{});
		Window w[] = Window.getWindows();
		for(Window wi:w)
		{
			wi.setVisible(false);
		}
		Thread.sleep(6000);
		w = Window.getWindows();
		for(Window wi:w)
			wi.dispose();
		System.out.println("disposed");
	}
}
