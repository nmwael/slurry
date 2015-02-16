package org.slurry.cache4guice;

import junit.framework.Assert;
import net.sf.ehcache.CacheManager;

import org.apache.commons.lang.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slurry.cache4guice.cache.util.Cache4GuiceHelper;
import org.slurry.cache4guice.module.CacheModule;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

public class Cache4SelfPopulatingScheduledTest {

	private Injector injector;

	private Calculator cacheCalculator;

	private Cache4GuiceHelper cache4GuiceHelper;

	@Before
	public void beforeTest() {

		injector = Guice.createInjector(new GuiceMultibinderModule(),
				new CacheModule());

		injector.injectMembers(this);

	}

	@After
	public void afterTest() throws SchedulerException {
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.shutdown();
		CacheManager.getInstance().shutdown();
		;

	}

	@Test
	public void SelfPopulatingScheduledCacheTest() throws InterruptedException {
		int result = getCacheCalculator().serveStaleAndRefreshedData(1, 1);
		int resultAfterSleep = getCacheCalculator().serveStaleAndRefreshedData(
				1, 1);
		Assert.assertEquals("Should be stale", result, resultAfterSleep);
		Thread.sleep(2000);
		resultAfterSleep = getCacheCalculator()
				.serveStaleAndRefreshedData(1, 1);
		Assert.assertTrue("Should be updated", result != resultAfterSleep);

	}

	@Test
	public void SelfPopulatingScheduledCacheTestMultipleCallsException()
			throws InterruptedException {
		int result = getCacheCalculator()
				.serveStaleAndRefreshedDataSometimesThrowError(1, 1);
		for (Integer i = 0; i < 50; i++) {
			int resultAfterSleep = getCacheCalculator().serveStaleAndRefreshedDataSometimesThrowError(1, 1);
					Thread.sleep(100);
			Assert.assertTrue("1+1=2", 5>=resultAfterSleep);
			
		}
		Thread.sleep(6000);
		int resultAfterSleep = getCacheCalculator().serveStaleAndRefreshedDataSometimesThrowError(1, 1);
		
		Assert.assertTrue("data did not recover. was<"+resultAfterSleep+">", 5<resultAfterSleep);

	}

	@Test
	public void SelfPopulatingScheduledCacheTestMultipleCallsWithWait()
			throws InterruptedException {
		StopWatch stopwatch = new StopWatch();
		Integer result = getCacheCalculator()
				.serveStaleAndRefreshedDataSometimesThrowError();

		StopWatch stopwatch2 = new StopWatch();
		stopwatch2.start();
		for (Integer i = 0; i < 50; i++) {
			Thread.sleep(100);
			stopwatch.start();
			result = getCacheCalculator()
					.serveStaleAndRefreshedDataSometimesThrowError();
			stopwatch.stop();
			Assert.assertTrue("Refresh took over <200>, was <"+stopwatch.getTime()+">", stopwatch.getTime()<200);
			stopwatch.reset();
		}
		stopwatch2.stop();
		Assert.assertTrue(stopwatch2.getTime()< 50*105);
		Assert.assertTrue("cache should have been updated",5<result);
		

	}

	@Test
	public void tet() throws InterruptedException{
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		Integer result = getCacheCalculator()
				.sloowOperation(5);
		stopwatch.stop();
		Assert.assertTrue("Refresh took has to be over <10000>, was <"+stopwatch.getTime()+">", stopwatch.getTime()>10000);
		
		stopwatch.reset();
		Thread.sleep(1000*60*60);
		stopwatch.start();
		result = getCacheCalculator()
				.sloowOperation(5);
		stopwatch.stop();
		Assert.assertTrue("Refresh took over <200>, was <"+stopwatch.getTime()+">", stopwatch.getTime()<200);
	}
	

	public Cache4GuiceHelper getCache4GuiceHelper() {
		return cache4GuiceHelper;
	}

	@Inject
	public void setCache4GuiceHelper(Cache4GuiceHelper cache4GuiceHelper) {
		this.cache4GuiceHelper = cache4GuiceHelper;
	}

	public Calculator getCacheCalculator() {
		return cacheCalculator;
	}

	@Inject
	public void setCacheCalculator(
			@Named(value = GuiceMultibinderModule.calculatorId01) Calculator cacheCalculator) {
		this.cacheCalculator = cacheCalculator;
	}

}
