package org.slurry.cache4guice.aop;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.NotImplementedException;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slurry.cache4guice.annotation.Cached;
import org.slurry.cache4guice.quartz.CacheUpdatingJob;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class CacheInterceptor implements MethodInterceptor {

	private CacheManager cacheManager;
	private CacheKeyGenerator cacheKeyGenerator;

	private static Logger logger = LoggerFactory
			.getLogger(CacheInterceptor.class);

	private static Map<String, UUID> uuidMap = new ConcurrentHashMap<String, UUID>();

	private static Map<String, List<String>> categoryMap = new ConcurrentHashMap<String, List<String>>();

	private static Injector injector;

	public Object invoke(MethodInvocation invocation) throws Throwable {
		setupCacheIfNecessary(invocation);

		return getResultFromCacheOrMethod(invocation);

	}

	private Object getResultFromCacheOrMethod(MethodInvocation invocation)
			throws Throwable {
		Ehcache cache = getCache(invocation);
		String cacheKey = getCacheKey(invocation);
		Element element = cache.get(cacheKey);
		if (element != null) {
			logger.debug("Cache HIT >" + cache.getName() + "<");
			return element.getValue();
		} else {
			logger.debug("Cache MISS >" + cache.getName() + "<");
			return getResultAndCache(invocation, cache, cacheKey);
		}

	}

	private Object getResultAndCache(MethodInvocation invocation,
			Ehcache cache, String cacheKey) throws Throwable {
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

	/**
	 * @TODO implement a way to help the user to generate different caches like
	 *       nonblocking or blocking cache.
	 * @param invocation
	 */
	private void createCache(final MethodInvocation invocation) {

		Boolean selfPopulatingScheduledCache = invocation.getMethod()
				.getAnnotation(Cached.class).SelfPopulatingScheduledCache();
		if (selfPopulatingScheduledCache) {
			Long refresh = invocation.getMethod().getAnnotation(Cached.class)
					.refreshTime();
			final String cacheNameFromMethodInvocation = getCacheNameFromMethodInvocation(invocation);
			getCacheManager().addCache(cacheNameFromMethodInvocation);
			final Ehcache rawCache = getCacheManager().getEhcache(
					cacheNameFromMethodInvocation);
			rawCache.getCacheConfiguration().setEternal(true);
			rawCache.getCacheConfiguration().setDiskPersistent(false);
			CacheEntryFactory cacheEntryFactory = new CacheEntryFactory() {



				@Override
				public Object createEntry(Object key) throws Exception {
					logger.debug("refreshing " + key.toString());
					 MethodInvocation invocationDebug = invocation;
					 Class<? extends Object> executingClass = invocationDebug.getThis().getClass();
					 
					Object result = null;
					try {
						Object instance = getInjector().getInstance(executingClass);
						Method method = invocation.getMethod();
						Method methodExecute = instance.getClass().getMethod(method.getName(), method.getParameterTypes());
						
						
						result = methodExecute.invoke(instance, invocation.getArguments());
						logger.debug("result", result);
						
					} catch (Throwable e) {
						logger.error("critical", e);
						
					}
					return result;
				}
			};
			SelfPopulatingCache selfPopulatingCache = new SelfPopulatingCache(
					rawCache, cacheEntryFactory);
			getCacheManager().replaceCacheWithDecoratedCache(rawCache,
					selfPopulatingCache);

			String classAndMethod = invocation.getMethod().getDeclaringClass()
					.getCanonicalName()
					+ invocation.getMethod().toString();
			String packageString = invocation.getMethod().getDeclaringClass()
					.getPackage().toString();

			JobDetail cacheUpdatingJob = newJob(CacheUpdatingJob.class)
					.withIdentity(classAndMethod + "_Updatejob", packageString)
					.build();
			Trigger trigger = newTrigger()
					.withIdentity(classAndMethod + "_Trigger", packageString)
					.withSchedule(
							simpleSchedule()
									.withIntervalInMilliseconds(refresh)
									.repeatForever()).build();
			trigger.getJobDataMap().put(
					CacheUpdatingJob.selfPopulatingCacheKey,
					cacheNameFromMethodInvocation);

			try {
				StdSchedulerFactory.getDefaultScheduler().scheduleJob(
						cacheUpdatingJob, trigger);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}

		} else {
			getCacheManager().addCache(
					getCacheNameFromMethodInvocation(invocation));
		}

	}

	private boolean cacheCreated(MethodInvocation invocation) {
		return getCacheManager().cacheExists(
				getCacheNameFromMethodInvocation(invocation));
	}

	private Ehcache getCache(MethodInvocation invocation) {
		return getCacheManager().getEhcache(
				getCacheNameFromMethodInvocation(invocation));
	}

	private String getCacheNameFromMethodInvocation(MethodInvocation invocation) {

		return getCacheNameFromMethod(invocation.getMethod());
	}

	public static String getCacheNameFromMethod(Method method) {
		if (Modifier.isPrivate(method.getModifiers())) {
			throw new NotImplementedException(
					"Method is private "
							+ method.toGenericString()
							+ " only protected public and package private are supported");
		}
		String potentialName = method.getAnnotation(Cached.class).name();
		String potentialCategoryName = method.getAnnotation(Cached.class)
				.category();
		String cacheName = null;

		if (potentialName.length() > 0) {
			cacheName = potentialName;
			logger.debug("using annotation specified name >" + potentialName
					+ "<");
		} else {
			cacheName = method.getDeclaringClass().getCanonicalName() + " "
					+ method.toGenericString();
		}

		if (cacheName.length() > 32) {
			if (!getUuidMap().containsKey(cacheName)) {
				UUID uuid = UUID.randomUUID();
				getUuidMap().put(cacheName, uuid);
				logger.debug("mapping " + cacheName + ">>>" + uuid.toString());
				cacheName = uuid.toString();
			} else {
				cacheName = getUuidMap().get(cacheName).toString();
			}

		}
		if (potentialCategoryName != null && potentialCategoryName.length() > 0) {
			if (getCategoryMap().containsKey(potentialCategoryName)) {
				List<String> list = getCategoryMap().get(potentialCategoryName);
				if (!list.contains(cacheName)) {
					list.add(cacheName);
				}
			} else {
				List<String> categoryList = new ArrayList<String>();
				categoryList.add(cacheName);
				getCategoryMap().put(potentialCategoryName, categoryList);
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

	public static Map<String, List<String>> getCategoryMap() {
		return categoryMap;
	}

	public static void setCategoryMap(Map<String, List<String>> categoryMap) {
		CacheInterceptor.categoryMap = categoryMap;
	}

	public Injector getInjector() {
		return CacheInterceptor.injector;
	}

	@Inject
	public static void setInjector(Injector injector) {
		CacheInterceptor.injector = injector;
	}

}
