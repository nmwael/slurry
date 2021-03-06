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
package org.slurry.quartz4guice.internal;

import org.quartz.Job;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * 
 * @version $Id$
 */
@Singleton
final class InjectorJobFactory implements JobFactory {

    private final Injector injector;

    @Inject
    public InjectorJobFactory(Injector injector) {
        this.injector = injector;
    }

    @SuppressWarnings("unchecked")
    public Job newJob(TriggerFiredBundle bundle) throws SchedulerException {
        Class<? extends Job> jobClass = bundle.getJobDetail().getJobClass();
        return this.injector.getInstance(jobClass);
    }

}
