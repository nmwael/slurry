package org.slurry.cache4guice;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slurry.cache4guice.module.CacheModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.internal.Stopwatch;

public class Cache4GuiceBenchMarkTest {
	

	private Injector injector;

	private Calculator cacheCalculator;

	private Calculator nonCacheCalculator = new CalculatorImpl();
	
	private Logger logger;

	@Before
	public void beforeTest() {
		
		logger=LoggerFactory.getLogger(Cache4GuiceBenchMarkTest.class);

		injector = Guice.createInjector(new CacheModule(), new TestModule());

		cacheCalculator = injector.getInstance(Calculator.class);

	}

	@Test
	public void minimalTest() throws InterruptedException {
		
		Stopwatch stopwatch=new Stopwatch();
		//Got to cache!
		cacheCalculator.calculateSomethingWild(2);
		
		logger.debug("Done calculating Cached Warmup {} ms ",stopwatch.reset());

		stopwatch=new Stopwatch();
		
		cacheCalculator.calculateSomethingWild(2);
		
		logger.debug("Done calculating Cached {} ms ",stopwatch.reset());
		
		stopwatch=new Stopwatch();
		
		nonCacheCalculator.calculateSomethingWild(2);
		
		logger.debug("Done calculating  Non Cached {} ms ",stopwatch.reset());

	}

	@After
	public void afterTest() {

	}

}
