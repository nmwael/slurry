package org.slurry.cache4guice.aop;

import org.aopalliance.intercept.MethodInvocation;

public interface CacheKeyGenerator {

	/**
	 * gets key with arguments
	 * @param invocation MethodInvocation
	 * @return key
	 */
    public String getCacheKey(MethodInvocation invocation);



}
