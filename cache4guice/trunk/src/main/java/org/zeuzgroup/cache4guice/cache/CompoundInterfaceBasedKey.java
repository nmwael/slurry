package org.zeuzgroup.cache4guice.cache;

import org.aopalliance.intercept.MethodInvocation;
import org.zeuzgroup.cache4guice.aop.CacheKeyGenerator;

public class CompoundInterfaceBasedKey implements CacheKeyGenerator {

	@Override
	public String getCacheKey(MethodInvocation invocation) {
		// TODO Auto-generated method stub
		return null;
	}

}
