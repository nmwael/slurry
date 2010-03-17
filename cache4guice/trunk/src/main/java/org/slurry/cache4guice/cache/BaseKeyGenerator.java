package org.slurry.cache4guice.cache;

import org.aopalliance.intercept.MethodInvocation;
import org.slurry.cache4guice.aop.CacheKeyGenerator;

public abstract class BaseKeyGenerator implements CacheKeyGenerator {


	public String getCacheKey(MethodInvocation invocation) {
		String key = "";
		for (Object o : invocation.getArguments()) {
			key += getKey(o);
		}
		return key;
	}

	protected abstract String getKey(Object o);

}
