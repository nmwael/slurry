package org.slurry.cache4guice;

import junit.framework.Assert;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slurry.cache4guice.aop.CacheInterceptor;
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
	public void afterTest() throws SchedulerException{
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.shutdown();
		CacheManager.getInstance().shutdown();;

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
	public void SelfPopulatingScheduledCacheTestMultipleCalls() throws InterruptedException {
		int result = getCacheCalculator().serveStaleAndRefreshedDataSometimesThrowError(1, 1);
		int resultAfterSleep = getCacheCalculator().serveStaleAndRefreshedDataSometimesThrowError(1, 1);
		Assert.assertEquals("Should be stale", result, resultAfterSleep);
		//Thread.sleep(2000);
		resultAfterSleep = getCacheCalculator()
				.serveStaleAndRefreshedDataSometimesThrowError(2, 1);
		Assert.assertEquals("2+1=3", 3, resultAfterSleep);

		resultAfterSleep = getCacheCalculator()
				.serveStaleAndRefreshedDataSometimesThrowError(3, 1);
		Assert.assertEquals("3+1=4", 4, resultAfterSleep);
		resultAfterSleep = getCacheCalculator()
				.serveStaleAndRefreshedDataSometimesThrowError(4, 1);
		Assert.assertEquals("4+1=5", 5, resultAfterSleep);
		resultAfterSleep = getCacheCalculator()
				.serveStaleAndRefreshedDataSometimesThrowError(5, 1);
		Assert.assertEquals("5+1=6", 6, resultAfterSleep);
		resultAfterSleep = getCacheCalculator()
				.serveStaleAndRefreshedDataSometimesThrowError(6, 1);
		Assert.assertEquals("6+1=7", 7, resultAfterSleep);
		resultAfterSleep = getCacheCalculator()
				.serveStaleAndRefreshedDataSometimesThrowError(7, 1);
		Assert.assertEquals("7+1=8", 8, resultAfterSleep);
		resultAfterSleep = getCacheCalculator()
				.serveStaleAndRefreshedDataSometimesThrowError(8, 1);
		Assert.assertEquals("8+1=9", 9, resultAfterSleep);
		resultAfterSleep = getCacheCalculator()
				.serveStaleAndRefreshedDataSometimesThrowError(9, 1);



	}


// EHcache does not shutdown correctly between tests and this one fails as a consequence of it.
//	@Test
//	public void SelfPopulatingScheduledRuntimeCacheTest()
//			throws InterruptedException {
//		CacheInterceptor instance = injector
//				.getInstance(CacheInterceptor.class);
//
//
//		instance.putSpecialConfiguration(Names.specialCache, "1000000");
//		int result = getCacheCalculator().serveStaleAndRefreshedData(1, 1);
//		int resultAfterSleep = getCacheCalculator().serveStaleAndRefreshedData(
//				1, 1);
//		Assert.assertEquals("Should be stale", result, resultAfterSleep);
//		Thread.sleep(2000);
//		resultAfterSleep = getCacheCalculator()
//				.serveStaleAndRefreshedData(1, 1);
//		Assert.assertEquals("Should be stale", result, resultAfterSleep);
//
// 	}
//

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
