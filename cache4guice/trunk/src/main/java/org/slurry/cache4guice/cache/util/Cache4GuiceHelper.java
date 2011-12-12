package org.slurry.cache4guice.cache.util;

import java.util.List;

import net.sf.ehcache.Ehcache;

public interface Cache4GuiceHelper {

	/**
	 * finds all caches for the class
	 * 
	 * @param searchClass
	 * @return
	 */
	public abstract List<Ehcache> findCaches(Class<? extends Object> searchClass);

	
	
	/**
	 * Works only if this method containing the cachename have been called or are eagerly loaded.
	 * Otherwise you will get null 
	 * @param name given name, or name derived from method
	 * @return
	 */
	public abstract Ehcache getCache(String name);

}