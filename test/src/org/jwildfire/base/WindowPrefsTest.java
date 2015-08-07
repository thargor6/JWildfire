package org.jwildfire.base;

import org.junit.Assert;

import org.junit.Test;

public class WindowPrefsTest
{
	@Test
	public void testDiffNames()
	{
		WindowPrefs p1 = new WindowPrefs("eigenName");
		p1.setHeight(100);
		p1.setWidth(200);
		p1.setLeft(300);
		p1.setTop(0);
		p1.setMaximized(true);
		WindowPrefs p2 = new WindowPrefs("copy");
		p1.assign(p2);
		Assert.assertFalse(p1.isEqual(p2));//different names
		Assert.assertFalse(p2.isEqual(p1));
	}
	@Test
	public void testSameNames()
	{
		WindowPrefs p1 = new WindowPrefs("eigenName");
		p1.setHeight(100);
		p1.setWidth(200);
		p1.setLeft(300);
		p1.setTop(0);
		p1.setMaximized(true);
		WindowPrefs p2 = new WindowPrefs("eigenName");
		p1.assign(p2);
		Assert.assertTrue(p1.isEqual(p2));//same names
		Assert.assertTrue(p2.isEqual(p1));
	}
	@Test
	public void testNull()
	{
		WindowPrefs p1 = new WindowPrefs(null);
		p1.setHeight(100);
		p1.setWidth(200);
		p1.setLeft(300);
		p1.setTop(0);
		p1.setMaximized(true);
		WindowPrefs p2 = p1.makeCopy();
		p1.assign(p2);
		Assert.assertTrue(p1.isEqual(p2));//same names - null
		Assert.assertTrue(p2.isEqual(p1));
	}
	@Test
	public void testDiffNamesN1()
	{
		WindowPrefs p1 = new WindowPrefs(null);
		p1.setHeight(100);
		p1.setWidth(200);
		p1.setLeft(300);
		p1.setTop(0);
		p1.setMaximized(true);
		WindowPrefs p2 = p1.makeCopy();
		p1.assign(p2);
		p1 = new WindowPrefs("newpref");
		Assert.assertFalse(p1.isEqual(p2));//same names - null
		Assert.assertFalse(p2.isEqual(p1));
	}
	@Test
	public void testDiffNamesN2()
	{
		WindowPrefs p1 = new WindowPrefs(null);
		p1.setHeight(100);
		p1.setWidth(200);
		p1.setLeft(300);
		p1.setTop(0);
		p1.setMaximized(true);
		WindowPrefs p2 = p1.makeCopy();
		p1.assign(p2);
		p2 = new WindowPrefs("newpref"); 
		Assert.assertFalse(p1.isEqual(p2));//same names - null
		Assert.assertFalse(p2.isEqual(p1));
	}
}
