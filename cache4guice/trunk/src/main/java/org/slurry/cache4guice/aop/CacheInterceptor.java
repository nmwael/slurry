package org.slurry.cache4guice.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;

public class CacheInterceptor implements MethodInterceptor {

	private CacheManager cacheManager;

	private CacheKeyGenerator cacheKeyGenerator;

	private static Map<String, UUID> uuidMap = new HashMap<String, UUID>();

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
		getCacheManager()
				.addCache(getCacheNameFromMethodInvocation(invocation));
	}

	private boolean cacheCreated(MethodInvocation invocation) {
		return getCacheManager().cacheExists(
				getCacheNameFromMethodInvocation(invocation));
	}

	private Cache getCache(MethodInvocation invocation) {
		return getCacheManager().getCache(
				getCacheNameFromMethodInvocation(invocation));
	}

	private String getCacheNameFromMethodInvocation(MethodInvocation invocation) {

		return getCacheNameFromMethod(invocation.getMethod());
	}

	public static String getCacheNameFromMethod(Method method) {
		String cacheName = method.getDeclaringClass().getCanonicalName() + " "
				+ method.toGenericString();
		if (cacheName.length() > 32) {
			if (!getUuidMap().containsKey(cacheName)) {
				UUID uuid = UUID.randomUUID();
				getUuidMap().put(cacheName, uuid);
				cacheName = uuid.toString();
			}

		}
		return cacheName;

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

	public static Map<String, UUID> getUuidMap() {
		return uuidMap;
	}

	public static void setUuidMap(Map<String, UUID> uuidMap) {
		CacheInterceptor.uuidMap = uuidMap;
	}

}
