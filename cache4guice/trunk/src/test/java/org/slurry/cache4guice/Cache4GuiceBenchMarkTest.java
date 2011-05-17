package org.slurry.cache4guice;

import junit.framework.Assert;

import org.apache.commons.lang.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slurry.cache4guice.module.CacheModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Cache4GuiceBenchMarkTest {

	private Injector injector;

	private Calculator cacheCalculator;

	private Calculator nonCacheCalculator = new CalculatorImpl();

	private Logger logger;

	@Before
	public void beforeTest() {

		logger = LoggerFactory.getLogger(Cache4GuiceBenchMarkTest.class);

		injector = Guice.createInjector(new CacheModule(), new GuiceModule());

		cacheCalculator = injector.getInstance(Calculator.class);

	}

	@Test
	public void minimalTest() throws InterruptedException {

		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		// Got to cache!
		Assert.assertEquals(2, cacheCalculator.calculateSomethingWild(2));

		logger.debug("Done calculating Cached Warmup {} ms ",
				stopwatch.getTime());

		stopwatch = new StopWatch();
		stopwatch.start();
		Assert.assertEquals(2, cacheCalculator.calculateSomethingWild(2));

		logger.debug("Done calculating Cached {} ms ", stopwatch.getTime());

		stopwatch = new StopWatch();
		stopwatch.start();

		Assert.assertEquals(2, nonCacheCalculator.calculateSomethingWild(2));

		logger.debug("Done calculating  Non Cached {} ms ", stopwatch.getTime());

	}

	@Test
	public void checkingCacheCollision() throws InterruptedException {

		minimalTest();

		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		// Got to cache!
		Assert.assertEquals(4, cacheCalculator.calculateSomethingWild(2, 2));

		logger.debug("Done calculating Cached Warmup {} ms ",
				stopwatch.getTime());

		stopwatch = new StopWatch();
		stopwatch.start();

		Assert.assertEquals(4, cacheCalculator.calculateSomethingWild(2, 2));

		logger.debug("Done calculating Cached {} ms ", stopwatch.getTime());

		stopwatch = new StopWatch();
		stopwatch.start();

		Assert.assertEquals(4, nonCacheCalculator.calculateSomethingWild(2, 2));

		logger.debug("Done calculating  Non Cached {} ms ", stopwatch.getTime());

	}

	@After
	public void afterTest() {

	}

}
