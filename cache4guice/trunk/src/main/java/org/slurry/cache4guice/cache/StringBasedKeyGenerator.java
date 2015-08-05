package org.slurry.cache4guice.cache;

/**
 * Remember that this uses !toString!
 * @author mhoum
 */
public class StringBasedKeyGenerator extends BaseKeyGenerator {

	@Override
	protected String getKey(Object o) {

		return ":" + o.toString();
	}

}
