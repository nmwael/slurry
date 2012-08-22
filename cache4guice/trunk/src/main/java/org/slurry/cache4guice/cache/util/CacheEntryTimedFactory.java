package org.slurry.cache4guice.cache.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slurry.cache4guice.aop.CacheInterceptor;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class CacheEntryTimedFactory implements CacheEntryFactory {

	
	private static Logger logger = LoggerFactory
			.getLogger(CacheInterceptor.class);
	private static Injector injector;
	private MethodInvocation invocation;
	private final Long refreshTime;
	
	

	
	public CacheEntryTimedFactory(MethodInvocation invocation,Long refreshTime) {
		super();
		this.refreshTime = refreshTime;
		this.setInvocation(invocation);
	}


	@Override
	public Object createEntry(Object key) throws Exception {
		logger.debug("refreshing " + key.toString());
		 Class<? extends Object> executingClass = getInvocation().getThis().getClass();
		 
		Object result = null;
		try {
			final Object instance = getInjector().getInstance(executingClass);
			Method method = getInvocation().getMethod();
			final Method methodExecute = instance.getClass().getMethod(method.getName(), method.getParameterTypes());
			

			ExecutorService executor = Executors.newCachedThreadPool();
			Callable<Object> task = new Callable<Object>() {
			   public Object call() {
			      try {
					return methodExecute.invoke(instance, getInvocation().getArguments());
				} catch (Exception e) {
					logger.error("critical",e);
				}
			      return null;
			   }
			};
			Future<Object> future = executor.submit(task);
			try {
			   result = future.get(getRefreshTime(), TimeUnit.MILLISECONDS); 
			} catch (TimeoutException ex) {
				logger.warn("timed out aborting", ex);
			} catch (InterruptedException e) {
				logger.warn("interupted", e);
			} catch (ExecutionException e) {
				logger.warn("execution failed", e);
			}
			
			
			logger.debug("result", result);
			
		} catch (Throwable e) {
			logger.error("critical", e);
			
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
		return invocation;
	}


	public void setInvocation(MethodInvocation invocation) {
		this.invocation = invocation;
	}


	public Long getRefreshTime() {
		return refreshTime;
	}

	

}
