package org.slurry.data.dao.interfaces;

import org.slurry.data.dataobjects.Event;
/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 *
 */
public interface EventDao extends Dao<Event> {

	public abstract Event find(Long id);

}
