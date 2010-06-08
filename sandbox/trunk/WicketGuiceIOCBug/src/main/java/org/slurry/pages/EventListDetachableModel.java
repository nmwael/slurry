package org.slurry.pages;

import java.util.List;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slurry.data.dao.interfaces.EventDao;
import org.slurry.data.dataobjects.Event;

import com.google.inject.Inject;

public class EventListDetachableModel extends LoadableDetachableModel<List<Event>> {
	@Inject
	private EventDao eventDao;

	public EventListDetachableModel() {
		InjectorHolder.getInjector().inject(this);
	}

	@Override
	protected List<Event> load() {
		return eventDao.findAll();
	}

}