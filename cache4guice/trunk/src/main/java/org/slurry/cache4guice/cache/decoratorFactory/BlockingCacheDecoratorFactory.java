package org.slurry.cache4guice.cache.decoratorFactory;

import java.util.Properties;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.CacheDecoratorFactory;
import net.sf.ehcache.constructs.blocking.BlockingCache;

public class BlockingCacheDecoratorFactory extends CacheDecoratorFactory {

	@Override
	public Ehcache createDecoratedEhcache(Ehcache cache, Properties properties) {
		 
		return createBlockingCache(cache);
	}

	private BlockingCache createBlockingCache(Ehcache cache) {
		
		BlockingCache blockingCache = new BlockingCache(cache);
		blockingCache.setTimeoutMillis(0);
		return blockingCache;
	}

	@Override
	public Ehcache createDefaultDecoratedEhcache(Ehcache cache,
			Properties properties) {
		return createBlockingCache(cache);
	}

}
