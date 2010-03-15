package org.slurry.cache4guice.module;

import net.sf.ehcache.CacheManager;

import org.slurry.cache4guice.annotation.Cached;
import org.slurry.cache4guice.aop.CacheInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class CacheModule extends AbstractModule {
	protected void configure() {
		
		bind(CacheManager.class).toInstance(CacheManager.create());
		
		CacheInterceptor cacheInterceptor = new CacheInterceptor();
		requestInjection(cacheInterceptor);
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cached.class),
				cacheInterceptor);
	}
}
