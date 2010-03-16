package org.slurry.cache4guice.cache;

public interface HasCacheID {
	
	/**
	 * 
	 * @return an id that can represent the object for caching
	 */
	public String GetCacheableID() ;

}
