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
		//TODO IF WE HAVE NO TIMEOUT AND SOMETHING GOES WRONG THEN NUMBER OF THREADS EXPLODE
		blockingCache.setTimeoutMillis(1000*30);
		return blockingCache;
	}

	@Override
	public Ehcache createDefaultDecoratedEhcache(Ehcache cache,
			Properties properties) {
		return createBlockingCache(cache);
	}

}
