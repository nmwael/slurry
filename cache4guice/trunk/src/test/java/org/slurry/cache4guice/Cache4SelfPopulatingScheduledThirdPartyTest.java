package org.slurry.cache4guice;

import static com.google.inject.name.Names.named;
import junit.framework.Assert;
import net.sf.ehcache.CacheManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slurry.cache4guice.aop.CacheInterceptor;
import org.slurry.cache4guice.cache.util.Cache4GuiceHelper;
import org.slurry.cache4guice.module.CacheModule;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.name.Named;

public class Cache4SelfPopulatingScheduledThirdPartyTest {

	private Injector injector;

	private Calculator cacheCalculator;


	private Cache4GuiceHelper cache4GuiceHelper;

	private Logger logger;

	@Before
	public void beforeTest() {

		logger = LoggerFactory
				.getLogger(Cache4SelfPopulatingScheduledThirdPartyTest.class);


		PrivateModule privateModule = new PrivateModule() {

			@Override
			protected void configure() {
				bind(ThirdPartyInjection.class);
				expose(ThirdPartyInjection.class);
			}
		};

		injector = Guice.createInjector(privateModule);

		injector = injector.createChildInjector(new GuiceMultibinderModule(),
				new CacheModule());

		injector.injectMembers(this);

	}

	@Test
	public void SelfPopulatingScheduledCacheThirdPartyTest() throws InterruptedException {
		String result = getCacheCalculator().delegatedMethod();
		String resultAfterSleep = getCacheCalculator().delegatedMethod();
		Assert.assertEquals("Should be stale", result, resultAfterSleep);
		Thread.sleep(2000);
		resultAfterSleep = getCacheCalculator().delegatedMethod();
		Assert.assertTrue("Should be updated", result != resultAfterSleep);

	}



	@After
	public void afterTest() throws SchedulerException {
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.shutdown();
		CacheManager.getInstance().shutdown();

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
