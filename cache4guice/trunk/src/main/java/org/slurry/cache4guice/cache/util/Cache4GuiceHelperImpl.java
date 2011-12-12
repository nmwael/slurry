package org.slurry.cache4guice.cache.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slurry.cache4guice.annotation.Cached;
import org.slurry.cache4guice.aop.CacheInterceptor;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

public class Cache4GuiceHelperImpl implements Cache4GuiceHelper {

	/**
	 * Finds list of {@link Cache} for the {@link Cached} annotation
	 * 
	 * @return
	 */

	private CacheManager cacheManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.slurry.cache4guice.Cache4GuiceHelper#findCaches(java.lang.Class)
	 */
	public List<Ehcache> findCaches(Class<? extends Object> searchClass) {
		return find(searchClass, Cached.class);
	}

	/**
	 * Finds list of {@link Cache} for the given annotation
	 * 
	 * @param searchClass
	 * @param annotationToFind
	 * @return caches for the given class and annotation
	 */
	private List<Ehcache> find(Class<? extends Object> searchClass,
			Class<? extends Annotation> annotationToFind) {
		String name = searchClass.getCanonicalName() + ".*";
		Set<Method> methods = null;
		Predicate<String> filter = new FilterBuilder().include(name);

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.filterInputsBy(filter);
		configurationBuilder.setScanners(new MethodAnnotationsScanner());
		configurationBuilder.setUrls(Arrays.asList(ClasspathHelper
				.forClass(searchClass)));

		Reflections reflections = new Reflections(configurationBuilder);

		methods = reflections.getMethodsAnnotatedWith(annotationToFind);

		List<Ehcache> caches = new ArrayList<Ehcache>();

		for (Method method : methods) {
			String cacheName = CacheInterceptor.getCacheNameFromMethod(method);
			Ehcache cache = null;
			if (getCacheManager().cacheExists(cacheName)) {
				cache = getCacheManager().getEhcache(cacheName);

			} else {
				getCacheManager().addCache(cacheName);
				cache = getCacheManager().getEhcache(cacheName);

			}
			caches.add(cache);

		}
		return caches;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	@Inject
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}


	@Override
	public Ehcache getCache(String name) {
		Map<String, UUID> uuidMap = CacheInterceptor.getUuidMap();
		String uuidCacheName=name;
		for(Entry<String,UUID> entry:uuidMap.entrySet()){
			if(entry.getKey().compareToIgnoreCase(name)==0){
				uuidCacheName=entry.getValue().toString();
			}
			
		}
		Ehcache cache = getCacheManager().getEhcache(uuidCacheName);
		return cache;
	}

	
	

}
