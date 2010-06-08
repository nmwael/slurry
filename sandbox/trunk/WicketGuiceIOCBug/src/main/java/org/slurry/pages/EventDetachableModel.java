package org.slurry.pages;

import org.apache.wicket.model.LoadableDetachableModel;
import org.slurry.data.dao.interfaces.EventDao;
import org.slurry.data.dataobjects.Event;

import com.google.inject.Inject;

public class EventDetachableModel extends LoadableDetachableModel<Event> {
	private EventDao eventDao;

	private Long id=null;

	public EventDetachableModel() {
		
	}

	public EventDetachableModel(Long id) {
		this.setId(id);
	}

	@Override
	protected Event load() {
		if(id!=null)
		{
		return getEventDao().find(getId());
		}
		else {
			return null;
		}
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	@Inject
	public void setEventDao(EventDao eventDao) {
		this.eventDao = eventDao;
	}

	public EventDao getEventDao() {
		return eventDao;
	}

}