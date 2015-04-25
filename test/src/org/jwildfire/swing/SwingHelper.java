package org.jwildfire.swing;

import java.awt.Component;

import javax.swing.JComponent;

public class SwingHelper 
{
	public static void printTree(JComponent c,int tabs)
	{
		if(c.getName()!=null&&!c.getName().contains("null")||tabs==0)
		{
			for(int i=0;i<tabs;i++)
				System.out.print(" ");
			System.out.println(c.getName()+" ["+c.getClass().getSimpleName()+"]");
		}
		else
			tabs--;
		
		if(c.getComponents()==null)
			return;
		for(Component ci:c.getComponents())
		{
			if(ci instanceof JComponent)
				printTree((JComponent)ci, tabs+1);
		}
	}
	
	public static JComponent match(JComponent c, final String name)
	{
		if(c.getName()!=null&&c.getName().contains(name))
			return c;
		for(Component ci:c.getComponents())
		{
			if(ci instanceof JComponent)
			{
				JComponent ret= match((JComponent)ci, name);
				if(ret!=null)
					return ret;
			}
		}
		return null;
	}
}
