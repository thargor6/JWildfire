package org.jwildfire.log;

import java.util.Date;

import org.junit.*;

public class LogTest 
{
	private static Date testDate;
	private static LogEntry entry;
	private static Exception cannedException;
	@BeforeClass
	public static void setUp()
	{
		testDate = new Date(); 
		entry = new LogEntry();
		entry.setCategory(Category.INFO);
		entry.setMessage("test");
		entry.setElapsedTime(12L);
		entry.setStackTrace("st");
		entry.setTime(testDate);
		try 
		{
			Integer.parseInt("PQRS");
		} catch (Exception e) {
			cannedException=e;
		}
	}
	@Test
	public void testLogEntry()
	{
		Assert.assertEquals("test", entry.getMessage());
		Assert.assertEquals("st", entry.getStackTrace());
		Assert.assertEquals(Category.INFO, entry.getCategory());
		Assert.assertEquals(12L, entry.getElapsedTime());
		Assert.assertEquals(testDate.getTime(), entry.getTime().getTime());
	}
	@Test
	public void testLogList()
	{
		LogEntry entry2 = new LogEntry();
		entry2.setCategory(Category.ERROR);
		entry2.setMessage("test2");
		entry2.setElapsedTime(System.currentTimeMillis());
		entry2.setStackTrace("st2");
		entry2.setTime(testDate);
		LogList list = new LogList();
		list.add(entry);
		list.add(entry2);
		Assert.assertEquals(Category.INFO,list.get(0).getCategory());
		Assert.assertEquals(Category.ERROR,list.get(1).getCategory());
		list.add(list.addErrorEntry(cannedException));
		list.add(list.addInfoEntry("fractals"));
		Assert.assertNotNull(list.get(2));
		Assert.assertTrue(list.get(2).getStackTrace().contains("NumberFormatException"));
		Assert.assertEquals("fractals",list.get(3).getMessage());
		Assert.assertNotNull(Category.values().toString());
		Assert.assertEquals(Category.INFO, Category.valueOf("INFO"));
	}
	@Test
	//TODO eats the exception and dumps to console
	public void testLogListNullError() throws Exception
	{
		new LogList().addErrorEntry(null);
	}
}
