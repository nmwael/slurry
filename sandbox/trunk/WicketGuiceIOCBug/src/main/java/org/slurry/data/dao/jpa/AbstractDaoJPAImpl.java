package org.slurry.data.dao.jpa;

import java.io.Serializable;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.slurry.data.dao.interfaces.Dao;
import org.slurry.data.dataobjects.DomainObject;
import com.wideplay.warp.persist.Transactional;
/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 *
 */
public abstract class AbstractDaoJPAImpl<T extends DomainObject> implements Dao<T> {
	
	private Class<T> domainClass;
	
	@Inject
	Provider<EntityManager> em;

	public AbstractDaoJPAImpl(Class<T> domainClass) {
		this.domainClass = domainClass;
	}
	
	@Transactional
	public void delete(T object)
	{ 
		em.get().remove(object);
	}
	@Transactional
	public T load(Serializable id)
	{
		return (T) em.get().find(domainClass, id);
	}
	
	@Transactional
	public void save(T object)
	{
		em.get().merge(object);
	}
	
}

