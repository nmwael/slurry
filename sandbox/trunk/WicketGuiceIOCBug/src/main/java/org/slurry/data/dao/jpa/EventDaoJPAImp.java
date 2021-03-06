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
public class EventDaoJPAImp extends AbstractDaoJPAImpl<Event> implements
		EventDao {

	public EventDaoJPAImp() {
		super(Event.class);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Event> findAll() {
		em.get().flush();
		Query query = em.get().createQuery("select e from Event e");
		return query.getResultList();
	}

	@Transactional
	public Event find(Long id) {
		em.get().flush();
		Query query = em.get().createQuery(
				"select e from Event e where e.id =" + id  );
		return ((Event) query.getSingleResult());
	}

	@Transactional
	public int countAll() {
		em.get().flush();
		Query query = em.get().createQuery("select count (e) from Event e");
		return ((Long) query.getSingleResult()).intValue();
	}
}
