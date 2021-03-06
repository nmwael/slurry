package org.slurry.cache4guice;

import java.util.List;

import junit.framework.Assert;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.BlockingCache;

import org.apache.commons.lang.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slurry.cache4guice.cache.util.Cache4GuiceHelper;
import org.slurry.cache4guice.module.CacheModule;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class Cache4GuiceBenchMarkTest {

	private Injector injector;

	private Calculator cacheCalculator;

	private Calculator nonCacheCalculator = new CalculatorImplParent();

	private Cache4GuiceHelper cache4GuiceHelper;

	private Logger logger;

	@Before
	public void beforeTest() {

		logger = LoggerFactory.getLogger(Cache4GuiceBenchMarkTest.class);

		injector = Guice.createInjector(new GuiceModule(), new CacheModule());

		injector.injectMembers(this);

	}

	@After
	public void afterTest() throws SchedulerException {
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.shutdown();
		CacheManager.getInstance().shutdown();
	}

	@Test
	public void minimalTest() throws InterruptedException {

		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		// Got to cache!
		Assert.assertEquals(2, getCacheCalculator().calculateSomethingWild(2));

		logger.debug("Done calculating Cached Warmup {} ms ",
				stopwatch.getTime());

		stopwatch = new StopWatch();
		stopwatch.start();
		Assert.assertEquals(2, getCacheCalculator().calculateSomethingWild(2));

		logger.debug("Done calculating Cached {} ms ", stopwatch.getTime());

		stopwatch = new StopWatch();
		stopwatch.start();

		Assert.assertEquals(2, nonCacheCalculator.calculateSomethingWild(2));

		logger.debug("Done calculating  Non Cached {} ms ", stopwatch.getTime());

	}

	@Test
	public void runTimeCacheConfigurationTest() throws InterruptedException {

		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		// Got to cache!
		Assert.assertEquals(2, getCacheCalculator().calculateSomethingWild(2));

		logger.debug("Done calculating Cached Warmup {} ms ",
				stopwatch.getTime());

		stopwatch = new StopWatch();
		stopwatch.start();
		Assert.assertEquals(2, getCacheCalculator().calculateSomethingWild(2));

		logger.debug("Done calculating Cached {} ms ", stopwatch.getTime());

		stopwatch = new StopWatch();
		List<Ehcache> findCaches = getCache4GuiceHelper().findCaches(
				CalculatorImplParent.class);

		for (Ehcache cache : findCaches) {
			cache.getCacheConfiguration().setTimeToLiveSeconds(1);
		}

		// wait for cache to expire
		Thread.sleep(1500);

		stopwatch.start();
		Assert.assertEquals(2, getCacheCalculator().calculateSomethingWild(2));
		logger.debug("Done calculating Cached {} ms ", stopwatch.getTime());

	}

	@Test
	public void runTimeCacheConfigurationBlockingTest()
			throws InterruptedException {

		List<Ehcache> findCaches = getCache4GuiceHelper().findCaches(
				CalculatorImplParent.class);
		for (Ehcache cache : findCaches) {
			BlockingCache blockingCache = new BlockingCache(cache);
			cache.getCacheConfiguration().setTimeToLiveSeconds(1);
			Boolean test = cache.equals(blockingCache);
			cache.getCacheManager().replaceCacheWithDecoratedCache(cache,
					blockingCache);
		}
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		// Got to cache!
		Assert.assertEquals(2, getCacheCalculator().calculateSomethingWild(2));

		logger.debug("Done calculating Cached Warmup {} ms ",
				stopwatch.getTime());

		stopwatch = new StopWatch();
		stopwatch.start();
		Assert.assertEquals(2, getCacheCalculator().calculateSomethingWild(2));

		logger.debug("Done calculating Cached {} ms ", stopwatch.getTime());

		stopwatch = new StopWatch();

		// wait for cache to expire
		Thread.sleep(1500);

		stopwatch.start();
		Assert.assertEquals(2, getCacheCalculator().calculateSomethingWild(2));
		logger.debug("Done calculating Cached {} ms ", stopwatch.getTime());

	}

	@Test
	public void checkingCacheCollision() throws InterruptedException {

		minimalTest();

		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		// Got to cache!
		Assert.assertEquals(4, getCacheCalculator()
				.calculateSomethingWild(2, 2));

		logger.debug("Done calculating Cached Warmup {} ms ",
				stopwatch.getTime());

		stopwatch = new StopWatch();
		stopwatch.start();

		Assert.assertEquals(4, getCacheCalculator()
				.calculateSomethingWild(2, 2));

		logger.debug("Done calculating Cached {} ms ", stopwatch.getTime());

		stopwatch = new StopWatch();
		stopwatch.start();

		Assert.assertEquals(4, nonCacheCalculator.calculateSomethingWild(2, 2));

		logger.debug("Done calculating  Non Cached {} ms ", stopwatch.getTime());

	}

	@Test
	public void getCacheFromName() throws InterruptedException {
		getCacheCalculator().imNamed(1);
		Ehcache cache = getCache4GuiceHelper().getCache(Names.cacheNameOne);
		Assert.assertNotNull(cache);

	}

	@Test
	public void getCachesFromCategoryName() throws InterruptedException {
		List<Ehcache> caches = getCache4GuiceHelper().getCaches(
				Names.cacheCategoryA);
		Assert.assertNotNull(caches);
		Assert.assertEquals(2, caches.size());

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
	public void setCacheCalculator(Calculator cacheCalculator) {
		this.cacheCalculator = cacheCalculator;
	}

}
