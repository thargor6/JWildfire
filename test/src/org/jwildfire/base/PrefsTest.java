package org.jwildfire.base;

import org.junit.*;
import org.jwildfire.base.mathlib.BaseMathLibType;
import org.jwildfire.create.tina.random.RandomGeneratorType;

public class PrefsTest 
{
	@Test
	public void testPrefs()
	{
		Prefs p = Prefs.newInstance();
		Assert.assertNotNull(p.getBaseMathLibType().name());
		p.setBaseMathLibType(BaseMathLibType.JAVA_MATH);
		Assert.assertEquals("JAVA_MATH", p.getBaseMathLibType().name());
		
		Assert.assertTrue(p.isCreateTinaDefaultMacroButtons());
		p.setCreateTinaDefaultMacroButtons(false);
		Assert.assertTrue(!p.isCreateTinaDefaultMacroButtons());
		
		p.setTinaRandomNumberGenerator(RandomGeneratorType.ZIGGURAT);
		Assert.assertEquals(RandomGeneratorType.ZIGGURAT,p.getTinaRandomNumberGenerator());
		
		try {
			new PrefsReader().readPrefs(p);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
