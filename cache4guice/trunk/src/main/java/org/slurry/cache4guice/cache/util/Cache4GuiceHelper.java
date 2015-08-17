package org.slurry.cache4guice.cache.util;

import java.util.List;

import net.sf.ehcache.Ehcache;

public interface Cache4GuiceHelper {

	/**
	 * finds all caches for the class
	 * 
	 * @param searchClass class
	 * @return
	 */
	public List<Ehcache> findCaches(Class<? extends Object> searchClass);

	
	
	/**
	 * Works only if this method containing the cachename have been called or are eagerly loaded.
	 * Otherwise you will get null 
	 * @see com.google.inject.Singleton
	 * @param name given name, or name derived from method
	 * @return net.sf.ehcache.Ehcache or null
	 */
	public Ehcache getCache(String name);

	
	/**
	 * Works only if the methods adherent to this category have been called or are eagerly loaded.
	 * Otherwise you will get no caches
	 * @see com.google.inject.Singleton 
	 * @param category given name, or name derived from method
	 * @return List of net.sf.ehcache.Ehcache
	 */
	public List<Ehcache> getCaches(String category);


}