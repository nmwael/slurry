package org.slurry.cache4guice.cache.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slurry.cache4guice.aop.CacheInterceptor;
import org.slurry.cache4guice.module.CacheModule;

import com.google.inject.Injector;

public class CacheEntryTimedFactory implements CacheEntryFactory {

	private static Logger logger = LoggerFactory
			.getLogger(CacheEntryTimedFactory.class);
	private static Injector injector;
	private static Map<String, MethodInvocationHolder> invocations;
	private static CacheInterceptor cacheInterceptor;
	private MethodInvocation initialInvocation;
	private final Long refreshTime;

	public CacheEntryTimedFactory(MethodInvocation initialInvocation,
			Long refreshTime) {
		super();
		this.refreshTime = refreshTime;
		this.setInvocation(initialInvocation);
	}

	@Override
	public synchronized Object createEntry(final Object key) throws Exception {
		logger.debug(key.getClass().getCanonicalName() + " refreshing "
				+ key.toString());
		Class<? extends Object> executingClass = getInvocation().getThis()
				.getClass();

		final MethodInvocation currentInvocation = getInvocations().get(
				getCacheInterceptor().getCacheNameFromMethodInvocation(
						getInvocation())
						+ key).getMethodInvocation();

		Object result = null;
		try {
			final Object instance = getInjector().getInstance(executingClass);
			Method method = currentInvocation.getMethod();

			final Method methodExecute = instance.getClass().getMethod(
					method.getName(), method.getParameterTypes());

			ExecutorService executor = Executors.newCachedThreadPool();
			Callable<Object> task = new Callable<Object>() {
				public Object call() throws ExecutionException {
					Object result = null;

					if (logger.isDebugEnabled()) {
						Object[] arguments = currentInvocation.getArguments();
						String arguementsString = "";
						for (Object object : arguments) {
							arguementsString += " " + object.toString();
						}
						logger.debug("arguments >" + arguementsString
								+ "<  key was >" + key.toString() + "<");
					}
					try {
						result = methodExecute.invoke(instance,
								currentInvocation.getArguments());
					} catch (IllegalArgumentException e) {
						logger.error("arguments not matching", e);
					} catch (IllegalAccessException e) {
						logger.error("unable to call", e);
					} catch (InvocationTargetException e) {
						logger.error("unable to update cache", e);

					}
					return result;
				}
			};
			// TODO remove future task
			Future<Object> future = executor.submit(task);
			try {
				// result = future.get(getRefreshTime()+20, TimeUnit.MINUTES);
				result = task.call();
			} catch (TimeoutException ex) {
				logger.warn("timed out aborting", ex);
			} catch (InterruptedException e) {
				logger.error("interupted", e);
			} 

			if (logger.isDebugEnabled()) {
				if (result != null) {
					logger.debug("result >" + result.toString() + "<");
				} else {
					logger.debug("result >null<");
				}
			}
			if (result == null) {
				throw new NullPointerException("Cache can never be null");
			}

		} catch (RuntimeException e) {
			logger.error("critical", e);
			throw new CacheException("failed",e);

		}
		
		return result;
	}

	public static Injector getInjector() {
		return injector;
	}

	@Inject
	public static void setInjector(Injector injector) {
		CacheEntryTimedFactory.injector = injector;
	}

	public MethodInvocation getInvocation() {
		return initialInvocation;
	}

	public void setInvocation(MethodInvocation invocation) {
		this.initialInvocation = invocation;
	}

	public Long getRefreshTime() {
		return refreshTime;
	}

	public Map<String, MethodInvocationHolder> getInvocations() {
		return invocations;
	}

	@Inject
	public static void setInvocations(
			@Named(CacheModule.INVOCATION_MAP_NAME) Map<String, MethodInvocationHolder> invocations) {
		CacheEntryTimedFactory.invocations = invocations;
	}

	public CacheInterceptor getCacheInterceptor() {
		return cacheInterceptor;
	}

	@Inject
	public static void setCacheInterceptor(CacheInterceptor cacheInterceptor) {
		CacheEntryTimedFactory.cacheInterceptor = cacheInterceptor;
	}

}
