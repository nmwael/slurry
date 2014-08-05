package org.slurry.cache4guice.cache.util;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slurry.cache4guice.module.CacheModule;

public class CleanupInvocationsJob implements Job {

	public Map<String, MethodInvocationHolder> getInvocations() {
		return invocations;
	}


	private Map<String, MethodInvocationHolder> invocations;

	@Inject
	public void setInvocations(@Named(CacheModule.INVOCATION_MAP_NAME) Map<String, MethodInvocationHolder> invocations) {
		this.invocations = invocations;
	}


	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		for(Entry<String, MethodInvocationHolder> e: getInvocations().entrySet()){
			if(e.getValue().getTimestamp()-System.currentTimeMillis()<20000){
				//purge the method
				getInvocations().remove(e.getKey());
			}
		}



	}

}
