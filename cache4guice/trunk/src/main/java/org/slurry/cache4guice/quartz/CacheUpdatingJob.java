package org.slurry.cache4guice.quartz;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInvocation;
import org.slurry.cache4guice.cache.util.MethodInvocationHolder;
import org.slurry.cache4guice.module.CacheModule;

@DisallowConcurrentExecution
public class CacheUpdatingJob implements Job {

    public static String selfPopulatingCacheKey = "selfPopulatingCacheKey";


    private static Logger logger = Logger
            .getLogger(CacheUpdatingJob.class);


    private CacheManager cacheManager;

    @Inject
    @Named(CacheModule.INVOCATION_MAP_NAME)
    public static ConcurrentHashMap<String, ConcurrentHashMap<String, MethodInvocationHolder>> invocations;

    private static Injector injector;

    public CacheUpdatingJob() {
        cacheManager = CacheManager.getInstance();
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, MethodInvocationHolder>> getInvocations() {
        return invocations;
    }

    public void setInvocations(ConcurrentHashMap<String, ConcurrentHashMap<String, MethodInvocationHolder>> invocations) {
        this.invocations = invocations;
    }

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        logger.info("executing job >" + context.getJobDetail().getKey().toString() + "<");
        
        
        

        String cacheName = context.getMergedJobDataMap().getString(selfPopulatingCacheKey);
        Ehcache cache = getCacheManager().getEhcache(cacheName);

        if (getInvocations() != null && getInvocations().containsKey(cacheName)) {
            final Set<Map.Entry<String, MethodInvocationHolder>> entrySets = getInvocations().get(cacheName).entrySet();
            for (Map.Entry<String, MethodInvocationHolder> entrySet : entrySets) {
                MethodInvocationHolder methodInvocationHolder = entrySet.getValue();
                String key = entrySet.getKey();

                Object value = executeMethodWithValues(key, methodInvocationHolder.getMethodInvocation());

                Element element = new Element(key, value);
                cache.put(element);

            }
        }

        //TODO, iterate over all entries in invocation map for this cache type
        // for each entry generate ehcache element and update cache
    }

    public static Injector getInjector() {
        return injector;
    }

    @Inject
    public static void setInjector(Injector injectorObject) {
        injector = injectorObject;
    }

    public Object executeMethodWithValues(final String key, final MethodInvocation currentInvocation) {
        Object result = null;
        try {

            Callable<Object> task;
            task = new Callable<Object>() {
                @Override
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
                        
                        result = currentInvocation.proceed();
                    } catch (IllegalArgumentException e) {
                        logger.error("arguments not matching", e);
                    } catch (Throwable ex) {
                        logger.error(ex);

                    }
                    return result;
                }
            };
            try {
                // result = future.get(getRefreshTime()+20, TimeUnit.MINUTES);
                result = task.call();
            } catch (TimeoutException ex) {
                logger.warn("timed out aborting", ex);
            } catch (InterruptedException e) {
                logger.error("interupted", e);
            } catch (Exception e) {
                logger.error("Exception", e);
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
            throw new CacheException("failed", e);

        }

        return result;
    }

    public Object executeMethodWithValuesCurrentlyNotWorking(final String key, final MethodInvocation currentInvocation) {
//		logger.debug(key.getClass().getCanonicalName() + " refreshing "
//				+ key.toString());
        Class<? extends Object> executingClass = currentInvocation.getMethod().getDeclaringClass();

//
//		final MethodInvocation currentInvocation = getInvocations().get(
//				getCacheInterceptor().getCacheNameFromMethodInvocation(
//						getInvocation())
//						+ key).getMethodInvocation();
        Object result = null;
        try {
            final Object instance = getInjector().getInstance(executingClass);
            Method method = currentInvocation.getMethod();

            final Method methodExecute = instance.getClass().getMethod(
                    method.getName(), method.getParameterTypes());

            Callable<Object> task = new Callable<Object>() {
                @Override
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
            try {
                // result = future.get(getRefreshTime()+20, TimeUnit.MINUTES);
                result = task.call();
            } catch (TimeoutException ex) {
                logger.warn("timed out aborting", ex);
            } catch (InterruptedException e) {
                logger.error("interupted", e);
            } catch (Exception e) {
                logger.error("Exception", e);
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
            throw new CacheException("failed", e);

        } catch (NoSuchMethodException e) {
            logger.error("critical", e);
            throw new CacheException("failed", e);
        }

        return result;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

}
