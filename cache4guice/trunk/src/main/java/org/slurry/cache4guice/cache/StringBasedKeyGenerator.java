package org.slurry.cache4guice.cache;

import org.aopalliance.intercept.MethodInvocation;
import org.slurry.cache4guice.aop.CacheKeyGenerator;

public class StringBasedKeyGenerator implements CacheKeyGenerator {

	@Override
	public String getCacheKey(MethodInvocation invocation) {
		String key = "";
		for (Object o : invocation.getArguments()) {
			key += ":" + o.toString();
		}
		return key;
	}

}
