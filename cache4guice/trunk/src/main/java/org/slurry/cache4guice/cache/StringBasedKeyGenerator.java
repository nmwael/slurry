package org.slurry.cache4guice.cache;

public class StringBasedKeyGenerator extends BaseKeyGenerator {

	@Override
	protected String getKey(Object o) {

		return ":" + o.toString();
	}

}
