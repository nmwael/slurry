package org.slurry.cache4guice.quartz;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slurry.cache4guice.aop.CacheInterceptor;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

@DisallowConcurrentExecution
public class CacheUpdatingJob implements Job {
	
	public static String selfPopulatingCacheKey="selfPopulatingCacheKey";
	
	private static Logger logger = Logger
			.getLogger(CacheUpdatingJob.class);

	
	@Inject
	private CacheManager cacheManager;
	
	public CacheUpdatingJob() {
		cacheManager=CacheManager.getInstance();
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.info("executing job >"+context.getJobDetail().getKey().toString()+"<");
		String cacheName = context.getMergedJobDataMap().getString(selfPopulatingCacheKey);
		Ehcache cache = getCacheManager().getEhcache(cacheName);
		SelfPopulatingCache selfPopulatingCache=(SelfPopulatingCache)  cache;
		selfPopulatingCache.refresh();
		
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	
	
	
	
}
