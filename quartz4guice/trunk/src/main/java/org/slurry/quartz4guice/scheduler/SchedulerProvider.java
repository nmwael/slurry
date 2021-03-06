/*
 *    Copyright 2009-2010 The slurry Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.slurry.quartz4guice.scheduler;

import java.text.ParseException;
import java.util.Set;
import java.util.TimeZone;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerListener;
import org.quartz.TriggerListener;
import org.quartz.spi.JobFactory;
import org.slurry.quartz4guice.Global;
import org.slurry.quartz4guice.Scheduled;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * 
 * @version $Id$
 */
final class SchedulerProvider implements Provider<Scheduler> {

    private final Scheduler scheduler;

    @Inject
    public SchedulerProvider(SchedulerFactory schedulerFactory) throws SchedulerException {
        this.scheduler = schedulerFactory.getScheduler();
        this.scheduler.start();
    }

    @Inject(optional = true)
    public void setJobFactory(JobFactory jobFactory) throws SchedulerException {
        this.scheduler.setJobFactory(jobFactory);
    }

    @Inject(optional = true)
    public void addGlobalJobListeners(@Global Set<JobListener> jobListeners) throws SchedulerException {
        for (JobListener jobListener : jobListeners) {
            this.scheduler.addGlobalJobListener(jobListener);
        }
    }

    @Inject(optional = true)
    public void addJobListeners(Set<JobListener> jobListeners) throws SchedulerException {
        for (JobListener jobListener : jobListeners) {
            this.scheduler.addJobListener(jobListener);
        }
    }

    @Inject(optional = true)
    public void addSchedulerListeners(Set<SchedulerListener> schedulerListeners) throws SchedulerException {
        for (SchedulerListener schedulerListener : schedulerListeners) {
            this.scheduler.addSchedulerListener(schedulerListener);
        }
    }

    @Inject(optional = true)
    public void addGlobalTriggerListeners(@Global Set<TriggerListener> triggerListeners) throws SchedulerException {
        for (TriggerListener triggerListener : triggerListeners) {
            this.scheduler.addGlobalTriggerListener(triggerListener);
        }
    }

    @Inject(optional = true)
    public void addTriggerListeners(Set<TriggerListener> triggerListeners) throws SchedulerException {
        for (TriggerListener triggerListener : triggerListeners) {
            this.scheduler.addTriggerListener(triggerListener);
        }
    }

    @Inject(optional = true)
    public void addJobs(Set<Class<? extends Job>> jobClasses) throws SchedulerException, ParseException {
        for (Class<? extends Job> jobClass : jobClasses) {
            Scheduled scheduled = jobClass.getAnnotation(Scheduled.class);

            JobDetail jobDetail = new JobDetail(scheduled.jobName(), // job name
                    scheduled.jobGroup(), // job group (you can also specify 'null'
                                       // to use the default group)
                    jobClass, // the java class to execute
                    scheduled.volatility(),
                    scheduled.durability(),
                    scheduled.recover());

            for (String jobListenerName : scheduled.jobListenerNames()) {
                jobDetail.addJobListener(jobListenerName);
            }

            String triggerName = null;
            if (Scheduled.DEFAULT.equals(scheduled.triggerName())) {
                triggerName = jobClass.getCanonicalName();
            } else {
                triggerName = scheduled.triggerName();
            }

            TimeZone timeZone = null;
            if (Scheduled.DEFAULT.equals(scheduled.timeZoneId())) {
                timeZone = TimeZone.getDefault();
            } else {
                timeZone = TimeZone.getTimeZone(scheduled.timeZoneId());
                if (timeZone == null) {
                    timeZone = TimeZone.getDefault();
                }
            }

            CronTrigger trigger = new CronTrigger(triggerName,
                    scheduled.triggerGroup(),
                    scheduled.jobName(),
                    scheduled.jobGroup(),
                    scheduled.cronExpression(),
                    timeZone);

            this.scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    public Scheduler get() {
        return this.scheduler;
    }

}
