package org.slurry.cache4guice.cache;

/**
 * use this if all your objects in cacheable methods implements a secure id for caching
 * @author nino.martinez.wael@gmail.com
 *
 */
public class SimpleInterfacedBasedKeyGenerator extends BaseKeyGenerator {

	
	@Override
	protected String getKey(Object o) {
		HasCacheID hasCacheID= HasCacheID.class.cast(o);
		return hasCacheID.GetCacheableID();
	}



}
