package org.slurry.config.modules;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import org.slurry.data.dao.interfaces.EventDao;
import org.slurry.data.dataobjects.Event;

/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 *
 */
public class MockDAOModule extends AbstractModule {

	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		
		Event event = new Event();
		event.setLocation("location");
		event.setTitle("title");
		event.setId(1l);
		
		List<Event> events = new ArrayList<Event>();
		events.add(event);
		
		EventDao mockDAO = mock(EventDao.class);
		when(mockDAO.countAll()).thenReturn(1);
		when(mockDAO.findAll()).thenReturn(events);
		when(mockDAO.load(anyInt())).thenReturn(event);
		
		bind(EventDao.class).toInstance(mockDAO);

	}

}
