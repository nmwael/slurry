package org.slurry.cache4guice;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slurry.cache4guice.module.CacheModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Cache4GuiceBenchMarkTest {

	private Injector injector;

	private Calculator cacheCalculator;

	private Calculator NonCacheCalculator = new CalculatorImpl();

	@Before
	public void beforeTest() {

		injector = Guice.createInjector(new CacheModule());

		cacheCalculator = injector.getInstance(Calculator.class);

	}

	@Test
	public void minimalTest() {

	}

	@After
	public void afterTest() {

	}

}
