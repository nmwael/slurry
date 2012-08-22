package org.slurry.cache4guice;

import java.util.List;

import junit.framework.Assert;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.BlockingCache;

import org.apache.commons.lang.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slurry.cache4guice.cache.util.Cache4GuiceHelper;
import org.slurry.cache4guice.module.CacheModule;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

public class Cache4GuiceMultibinderTest {

	private Injector injector;

	private Calculator cacheCalculator;

	private Calculator nonCacheCalculator = new CalculatorImplParent();

	private Cache4GuiceHelper cache4GuiceHelper;

	private Logger logger;

	@Before
	public void beforeTest() {

		logger = LoggerFactory.getLogger(Cache4GuiceMultibinderTest.class);

		injector = Guice.createInjector(new GuiceMultibinderModule(),new CacheModule());

		injector.injectMembers(this);

	}

	
	
	@Test
	public void SelfPopulatingScheduledCacheTest() throws InterruptedException{
		int result=getCacheCalculator().serveStaleAndRefreshedData(1, 1);
		int resultAfterSleep=getCacheCalculator().serveStaleAndRefreshedData(1, 1);
		Assert.assertEquals("Should be stale",result, resultAfterSleep);
		Thread.sleep(2000);
		resultAfterSleep=getCacheCalculator().serveStaleAndRefreshedData(1, 1);
		Assert.assertTrue("Should be updated",result!=resultAfterSleep);
		
	}

	
	@Test
	public void SlowSelfPopulatingScheduledCacheTest() throws InterruptedException{
		Integer result1=getCacheCalculator().sloowOperation(1);
		Integer result2=getCacheCalculator().sloowOperation(1);
		Assert.assertEquals(result1,result2);
		
		
	}
	
	
	
	@After
	public void afterTest() {

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
	public void setCacheCalculator(@Named(value=GuiceMultibinderModule.calculatorId01) Calculator cacheCalculator) {
		this.cacheCalculator = cacheCalculator;
	}

}
