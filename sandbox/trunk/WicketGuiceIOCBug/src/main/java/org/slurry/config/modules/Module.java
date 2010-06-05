package org.slurry.config.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slurry.DataInitialiser;
import org.slurry.data.dao.interfaces.EventDao;
import org.slurry.data.dao.jpa.EventDaoJPAImp;
import com.wideplay.warp.jpa.JpaUnit;
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
		install(PersistenceService.usingJpa()
				.across(UnitOfWork.REQUEST)
				.transactedWith(TransactionStrategy.LOCAL)
				.buildModule());

		//dao stuff
		bind(EventDao.class).to(EventDaoJPAImp.class);
		
		bindConstant().annotatedWith(JpaUnit.class).to("myFirstJpaUnit");
	}

	@Singleton
	public static class Initializer {
		@Inject
		Initializer(com.wideplay.warp.persist.PersistenceService service) {
			service.start();
		}
	}
	
	/**
	 * Should the data initialisation step be run?  This populates data in the database
	 * 
	 * @return true or false
	 */
	protected boolean initData()
	{
		return true;
	}

}
