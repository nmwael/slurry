package org.slurry.data.dao.jpa;

import java.util.List;

import javax.persistence.Query;

import org.slurry.data.dao.interfaces.EventDao;
import org.slurry.data.dataobjects.Event;
import com.wideplay.warp.persist.Transactional;

/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 * 
 */
public class EventDaoJPAImp extends AbstractDaoJPAImpl<Event> implements EventDao {

	public EventDaoJPAImp() {
		super(Event.class);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Event> findAll() {
		Query query = em.get().createQuery("select e from Event e");
		return query.getResultList();
	}

	@Transactional
	public int countAll() {
		Query query = em.get().createQuery("select count (e) from Event e");
		return ((Long) query.getSingleResult()).intValue();
	}
}
