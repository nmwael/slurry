package org.slurry.config.modules;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slurry.DataInitialiser;
import org.slurry.data.dao.hibernate.EventDaoHibernateImp;
import org.slurry.data.dao.interfaces.EventDao;
import org.slurry.data.dataobjects.Event;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.TransactionStrategy;
import com.wideplay.warp.persist.UnitOfWork;

/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 *
 */
public class Module extends AbstractModule {

	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		bind(Initializer.class).asEagerSingleton();
		if(initData())
		{
			bind(DataInitialiser.class).asEagerSingleton();
		}
		
		//warp persist stuff
		install(PersistenceService.usingHibernate()
				.across(UnitOfWork.REQUEST)
				.transactedWith(TransactionStrategy.LOCAL)
				.buildModule());

		////hibernate stuff
		AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
		annotationConfiguration.configure();
		annotationConfiguration.addAnnotatedClass(Event.class);
		bind(Configuration.class).toInstance(annotationConfiguration);

		//dao stuff
		bind(EventDao.class).to(EventDaoHibernateImp.class);
	}

	@Singleton
	public static class Initializer {
		@Inject
		Initializer(com.wideplay.warp.persist.PersistenceService service) {
			service.start();
		}
	}
	
	protected boolean initData()
	{
		return true;
	}

}
