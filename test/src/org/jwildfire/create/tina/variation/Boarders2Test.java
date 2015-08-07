package org.jwildfire.create.tina.variation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.batch.HeadlessBatchRendererController;
import org.jwildfire.create.tina.batch.Job;
import org.jwildfire.create.tina.batch.JobRenderThread;
import org.jwildfire.create.tina.batch.JobRenderThreadController;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.random.RandomGeneratorType;
import org.jwildfire.create.tina.random.ZigguratRandomGenerator;
import org.jwildfire.create.tina.render.FlameRenderer;

public class Boarders2Test 
{
	@Test
	public void testBean()
	{
		Boarders2Func bf = new Boarders2Func();
		bf.setParameter("c", 0);
		bf.setParameter("left", 0);
		bf.setParameter("right", 0);
		bf.init(null, null, new XForm(), 10);
		
		Assert.assertNotNull(bf.getName());
		bf.setParameter("c", 5);
		bf.setParameter("left", 6);
		bf.setParameter("right", 7);
		try {
			bf.setParameter("doesn't exist", 5);
			Assert.fail("Expected exception");
		} catch (Exception e) {
		}
		Assert.assertEquals(3,bf.getParameterValues().length);
		Assert.assertEquals(5.0,bf.getParameter("c"));
		Assert.assertEquals(6.0,bf.getParameter("left"));
		Assert.assertEquals(7.0,bf.getParameter("right"));
		
	}
	@Test
	public void testTransformXbigXPos()
	{
		XYZPoint pt = new XYZPoint();
		pt.y=.4;
		pt.x=.5;
		pt.z=1;
		XYZPoint pt2 = new XYZPoint();
		pt2.y=.4;
		pt2.x=.5;
		pt2.z=1;
		Boarders2Func bf = new Boarders2Func();
		bf.init(null, null, new XForm(), 10);
		Flame f = new Flame();
		f.setHeight(500);
		f.setWidth(500);
		f.setName("test1");
		bf.transform(new FlameTransformationContext(
				new FlameRenderer(f, Prefs.getPrefs(), true, false), new ZigguratRandomGenerator(), 1),
				new XForm(), pt,  pt2, 10);
	}
	@Test
	public void testTransformYbigYPos()
	{
		XYZPoint pt = new XYZPoint();
		pt.y=.4;
		pt.x=.2;
		pt.z=1;
		XYZPoint pt2 = new XYZPoint();
		pt2.y=.4;
		pt2.x=.2;
		pt2.z=1;
		Boarders2Func bf = new Boarders2Func();
		bf.init(null, null, new XForm(), 10);
		Flame f = new Flame();
		f.setHeight(500);
		f.setWidth(500);
		f.setName("test1");
		bf.transform(new FlameTransformationContext(
				new FlameRenderer(f, Prefs.getPrefs(), true, false), new ZigguratRandomGenerator(), 1),
				new XForm(), pt,  pt2, 10);
	}
	@Test
	public void testTransformYbigYNeg()
	{
		XYZPoint pt = new XYZPoint();
		pt.y=-.4;
		pt.x=.2;
		pt.z=1;
		XYZPoint pt2 = new XYZPoint();
		pt2.y=-.4;
		pt2.x=.2;
		pt2.z=1;
		Boarders2Func bf = new Boarders2Func();
		bf.init(null, null, new XForm(), 10);
		Flame f = new Flame();
		f.setHeight(500);
		f.setWidth(500);
		f.setName("test1");
		bf.transform(new FlameTransformationContext(
				new FlameRenderer(f, Prefs.getPrefs(), true, false), new ZigguratRandomGenerator(), 1),
				new XForm(), pt,  pt2, 10);
	}
	@Test
	public void testTransformXbigXNeg()
	{
		XYZPoint pt = new XYZPoint();
		pt.y=.4;
		pt.x=-.5;
		pt.z=1;
		XYZPoint pt2 = new XYZPoint();
		pt2.y=.4;
		pt2.x=-.5;
		pt2.z=1;
		Boarders2Func bf = new Boarders2Func();
		bf.setParameter("right", 0);
		bf.init(null, null, new XForm(), 10);
		Flame f = new Flame();
		f.setHeight(500);
		f.setWidth(500);
		f.setName("test1");
		bf.transform(new FlameTransformationContext(
				new FlameRenderer(f, Prefs.getPrefs(), true, false), new ZigguratRandomGenerator(), 1),
				new XForm(), pt,  pt2, 10);
	}
	@Test// @Ignore
	public void visual() throws Exception
	{
		Prefs.getPrefs().setTinaRandomNumberGenerator(RandomGeneratorType.JAVA_INTERNAL);
		System.out.println(Prefs.getPrefs().getTinaRandomNumberGenerator().name());
		int side = 600;
		ResolutionProfile respro = new ResolutionProfile(true, side, side);
		int quality=80;
		Flame f = new Flame();
		f.setWidth(side);
		f.setHeight(side);
		f.setGamma(4.0);
		f.setBGTransparency(false);
		f.setAntialiasAmount(1.0);
		f.setAntialiasRadius(.1);
		Layer l = new Layer();
		XForm xf = new XForm();
		Boarders2Func bf = new Boarders2Func();
		bf.setParameter("c", .6);
		bf.setParameter("left", .65);
		bf.setParameter("right", .35);
		xf.addVariation(1.0, bf);
		f.setCamZoom(6.461);
		xf.setWeight(5.5);
		l.getXForms().add(xf);
		RGBPalette pal = new RGBPalette();
		for(int i=0;i<256;i++)
			pal.addColor(0, i, 255);
		pal.setFlam3Name("test");
		l.setPalette(pal);
		f.setResolutionProfile(respro);
		f.getLayers().clear();
		f.getLayers().add(l);
		f.setPixelsPerUnit(43.75);//this is "scale"
		new FlameWriter().writeFlame(f, "/dev/shm/test.flame");
		JobRenderThreadController controller = new HeadlessBatchRendererController();
		List<Job> joblist = new ArrayList<>();
		Job j = new Job();
		j.setCustomHeight(side);
		j.setCustomWidth(side);
		j.setCustomQuality(quality);
		j.setFlameFilename("/dev/shm/test.flame");
		joblist.add(j);
		
		QualityProfile qualpro = new QualityProfile();
		qualpro.setQuality(quality);
		JobRenderThread job = new JobRenderThread(controller, joblist, respro, qualpro, true);
		job.run();
	}
}
