package org.slurry.cache4guice;

import com.google.inject.AbstractModule;
import net.sf.ehcache.CacheManager;

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
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.slurry.cache4guice.aop.CacheInterceptor;

public class Cache4SelfPopulatingScheduledExternalRefreshTimeTest {

    private static Logger logger = Logger.getLogger(Cache4SelfPopulatingScheduledExternalRefreshTimeTest.class);

    @Inject
    private CacheInterceptor cacheInterceptor;

    public CacheInterceptor getCacheInterceptor() {
        return cacheInterceptor;
    }

    public void setCacheInterceptor(CacheInterceptor cacheInterceptor) {
        this.cacheInterceptor = cacheInterceptor;
    }
    
    private Injector injector;

    private CalculatorMap cacheCalculator;

    private Cache4GuiceHelper cache4GuiceHelper;

    @Before
    public void beforeTest() {

        injector = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bind(CalculatorMap.class).asEagerSingleton();

            }
        },
                new CacheModule());

        injector.injectMembers(this);

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

    public CalculatorMap getCacheCalculatorMap() {
        return cacheCalculator;
    }

    @Inject
    public void setCacheCalculatorMap(
            CalculatorMap cacheCalculator) {
        this.cacheCalculator = cacheCalculator;
    }

    @Test
    public void refreshTimeWithOutExternalDefaultRefreshTimeIs10sec()
            throws InterruptedException {

        logger.info("first run");
        Assert.assertEquals("1-1", getCacheCalculatorMap().getAddNumberToAOldMap(1, 1));
        Assert.assertEquals("2-1", getCacheCalculatorMap().getAddNumberToAOldMap(2, 1));
        Assert.assertEquals("3-1", getCacheCalculatorMap().getAddNumberToAOldMap(3, 1));
        Thread.sleep(5000);
        logger.info("second run");
        Assert.assertEquals("1-1", getCacheCalculatorMap().getAddNumberToAOldMap(1, 1));
        Assert.assertEquals("2-1", getCacheCalculatorMap().getAddNumberToAOldMap(2, 1));
        Assert.assertEquals("3-1", getCacheCalculatorMap().getAddNumberToAOldMap(3, 1));
        Thread.sleep(16000);
        logger.info("third run");
        Assert.assertEquals("1-3", getCacheCalculatorMap().getAddNumberToAOldMap(1, 1));
        Assert.assertEquals("2-3", getCacheCalculatorMap().getAddNumberToAOldMap(2, 1));
        Assert.assertEquals("3-3", getCacheCalculatorMap().getAddNumberToAOldMap(3, 1));
    }

    @Test
    public void refreshTimeWithExternalDefaultRefreshTimeIs10sec()
            throws InterruptedException {
        Long refreshTime = 4l;

        getCacheInterceptor().putSpecialConfiguration(
                CalculatorMap.specialCacheTimeOut, (refreshTime * 1000) + "");
        logger.debug("setting timeout >" + refreshTime + "<");

        logger.info("first run");
        Assert.assertEquals("1-1", getCacheCalculatorMap().getAddNumberToAOldMap(1, 1)); //quartz + sched ~4
        Assert.assertEquals("2-1", getCacheCalculatorMap().getAddNumberToAOldMap(2, 1)); // ikke oprret ny cache
        Assert.assertEquals("3-1", getCacheCalculatorMap().getAddNumberToAOldMap(3, 1)); // ikke oprret ny cache
        Thread.sleep(5000); // kør job, 
        logger.info("second run");
        Assert.assertEquals("1-2", getCacheCalculatorMap().getAddNumberToAOldMap(1, 1));
        Assert.assertEquals("2-2", getCacheCalculatorMap().getAddNumberToAOldMap(2, 1));
        Assert.assertEquals("3-2", getCacheCalculatorMap().getAddNumberToAOldMap(3, 1));
        Thread.sleep(16000);
        logger.info("third run");
        Assert.assertEquals("1-6", getCacheCalculatorMap().getAddNumberToAOldMap(1, 1));
        Assert.assertEquals("2-6", getCacheCalculatorMap().getAddNumberToAOldMap(2, 1));
        Assert.assertEquals("3-6", getCacheCalculatorMap().getAddNumberToAOldMap(3, 1));
    }
    
//    @Test
    public void otherTest() throws InterruptedException{
        Assert.assertEquals("1-1", getCacheCalculatorMap().getAddNumberToAOldMap(1, 1)); //quartz + sched ~4
        getCacheInterceptor().putSpecialConfiguration(
                CalculatorMap.specialCacheTimeOut, (1 * 1000) + "");
        Thread.sleep(50000000l);
    }
}
