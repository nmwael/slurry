package org.slurry.cache4guice.aop;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;

public class CacheInterceptor implements MethodInterceptor {

	private CacheManager cacheManager;

	private CacheKeyGenerator cacheKeyGenerator;

	public Object invoke(MethodInvocation invocation) throws Throwable {
		setupCacheIfNecessary(invocation);

		return getResultFromCacheOrMethod(invocation);

	}

	private Object getResultFromCacheOrMethod(MethodInvocation invocation)
			throws Throwable {
		Cache cache = getCache(invocation);
		String cacheKey = getCacheKey(invocation);
		Element element = cache.get(cacheKey);
		if (element != null) {
			return element.getValue();
		} else {
			return getResultAndCache(invocation, cache, cacheKey);
		}

	}

	private Object getResultAndCache(MethodInvocation invocation, Cache cache,
			String cacheKey) throws Throwable {
		Object methodResult = invocation.proceed();
		Element elementResult = new Element(cacheKey, methodResult);
		cache.put(elementResult);
		return methodResult;
	}

	private String getCacheKey(MethodInvocation invocation) {
		
		return getCacheKeyGenerator().getCacheKey(invocation);
	}

	private void setupCacheIfNecessary(MethodInvocation invocation) {
		if (!cacheCreated(invocation)) {
			createCache(invocation);

		}
	}

	private void createCache(MethodInvocation invocation) {
		getCacheManager().addCache(getCacheName(invocation));
	}

	private boolean cacheCreated(MethodInvocation invocation) {
		return getCacheManager().cacheExists(getCacheName(invocation));
	}

	private Cache getCache(MethodInvocation invocation) {
		return getCacheManager().getCache(getCacheName(invocation));
	}

	private String getCacheName(MethodInvocation invocation) {
		return invocation.getMethod().toString();
	}

	@Inject
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	@Inject
	public void setCacheKeyGenerator(CacheKeyGenerator cacheKeyGenerator) {
		this.cacheKeyGenerator = cacheKeyGenerator;
	}

	public CacheKeyGenerator getCacheKeyGenerator() {
		return cacheKeyGenerator;
	}

}
