package org.slurry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slurry.data.dao.interfaces.EventDao;
import org.slurry.data.dataobjects.Event;
import com.wideplay.warp.persist.WorkManager;

/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 *
 */
@Singleton
public class DataInitialiser {
	
	private static String[] dummyTitles = {"Wicket Event", "Party", "Breakfast At Tiffany's", "Holiday"};
	private static String[] dummyLocations = {"London", "Paris", "Pub", "New York"};
	
	@Inject
	public DataInitialiser(EventDao eventDAO, WorkManager wm)
	{
		wm.beginWork();
		for(int i = 0; i < 10; i++)
		{
			Event event = new Event();
			event.setTitle(dummyTitles[(int)(Math.random() * dummyTitles.length)]);
			event.setLocation(dummyLocations[(int)(Math.random() * dummyLocations.length)]);
			eventDAO.save(event);
		}
		wm.endWork();
	}

}
