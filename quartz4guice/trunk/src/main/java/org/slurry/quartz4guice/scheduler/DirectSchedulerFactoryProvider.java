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

import org.quartz.SchedulerFactory;
import org.quartz.impl.DirectSchedulerFactory;

import com.google.inject.Provider;

/**
 * WARNING this class is still a prototype.
 *
 * @version $Id$
 */
public final class DirectSchedulerFactoryProvider implements Provider<SchedulerFactory> {

    private final SchedulerFactory schedulerFactory = DirectSchedulerFactory.getInstance();

    /**
     * {@inheritDoc}
     */
    public SchedulerFactory get() {
        return this.schedulerFactory;
    }

}
