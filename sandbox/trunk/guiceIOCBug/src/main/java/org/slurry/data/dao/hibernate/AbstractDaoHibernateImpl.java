package org.slurry.data.dao.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.slurry.data.dao.interfaces.Dao;
import org.slurry.data.dataobjects.DomainObject;
import com.wideplay.warp.persist.TransactionType;
import com.wideplay.warp.persist.Transactional;
/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 *
 */
@SuppressWarnings("unchecked")
public abstract class AbstractDaoHibernateImpl<T extends DomainObject> implements Dao<T> {
	
	private Class<T> domainClass;
	
	@Inject
	Provider<Session> session;

	public AbstractDaoHibernateImpl(Class<T> domainClass) {
		this.domainClass = domainClass;
	}
	
	@Transactional
	public void delete(T object)
	{ 
		session.get().delete(object);
	}
	@Transactional(type=TransactionType.READ_ONLY)
	public T load(Serializable id)
	{
		return (T) session.get().get(domainClass, id);
	}
	
	@Transactional
	public void save(T object)
	{
		session.get().saveOrUpdate(object);
	}
	@Transactional(type=TransactionType.READ_ONLY)
	public List<T> findAll()
	{
		Criteria criteria = session.get().createCriteria(domainClass);
		return (List<T>) criteria.list();
	}
	@Transactional(type=TransactionType.READ_ONLY)
	public int countAll() {
		Criteria criteria = session.get().createCriteria(domainClass);
		criteria.setProjection(Projections.rowCount());
		return (Integer) criteria.uniqueResult();
	}
	
}

