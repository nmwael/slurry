package org.slurry.cache4guice.quartz;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.inject.Inject;


public class CacheUpdatingJob implements Job {
	
	public static String selfPopulatingCacheKey="selfPopulatingCacheKey";
	
	@Inject
	private CacheManager cacheManager;
	
	public CacheUpdatingJob() {
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
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
