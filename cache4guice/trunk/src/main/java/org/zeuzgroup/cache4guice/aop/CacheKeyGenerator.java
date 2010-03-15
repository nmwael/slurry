package org.zeuzgroup.cache4guice.aop;

import org.aopalliance.intercept.MethodInvocation;

public interface CacheKeyGenerator {

	public String getCacheKey(MethodInvocation invocation);

}
