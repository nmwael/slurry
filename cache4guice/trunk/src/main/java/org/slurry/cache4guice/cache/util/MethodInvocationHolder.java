package org.slurry.cache4guice.cache.util;

import org.aopalliance.intercept.MethodInvocation;

public class MethodInvocationHolder {

	private MethodInvocation methodInvocation;

	private long timestamp;

	public MethodInvocationHolder(MethodInvocation methodInvocation) {
		setMethodInvocation(methodInvocation);
		setTimestamp(System.currentTimeMillis());


	}


	public MethodInvocation getMethodInvocation() {
		return methodInvocation;
	}

	public void setMethodInvocation(MethodInvocation methodInvocation) {
		this.methodInvocation = methodInvocation;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
