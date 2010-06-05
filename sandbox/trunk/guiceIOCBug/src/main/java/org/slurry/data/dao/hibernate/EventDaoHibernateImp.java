package org.slurry.data.dao.hibernate;

import org.slurry.data.dao.interfaces.EventDao;
import org.slurry.data.dataobjects.Event;
/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 *
 */
public class EventDaoHibernateImp extends AbstractDaoHibernateImpl<Event> implements EventDao {

	public EventDaoHibernateImp()
	{
		super(Event.class);
	}
}
