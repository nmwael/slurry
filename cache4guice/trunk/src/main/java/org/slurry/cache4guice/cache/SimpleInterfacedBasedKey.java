package org.slurry.cache4guice.cache;

import org.aopalliance.intercept.MethodInvocation;
import org.slurry.cache4guice.aop.CacheKeyGenerator;

public class SimpleInterfacedBasedKey implements CacheKeyGenerator {

	@Override
	public String getCacheKey(MethodInvocation invocation) {
		// TODO Auto-generated method stub
		return null;
	}

}
