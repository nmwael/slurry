package org.slurry.pages;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slurry.data.dao.interfaces.EventDao;
import org.slurry.data.dataobjects.Event;

import com.google.inject.Inject;

/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 * 
 */
public class EventPage extends WebPage {
	@Inject
	private EventDao eventDao;
	
	public EventPage(final PageParameters pp) {
		Form<Event> eventForm = new Form<Event>("eventForm",
				new CompoundPropertyModel<Event>(new Event()));
		eventForm.add(new TextField<String>("title").setRequired(true));
		eventForm.add(new TextField<String>("location").setRequired(true));

		final WebMarkupContainer wmc = new WebMarkupContainer("listContainer");

		wmc.add(new ListView<Event>("list", new EventDetachable()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Event> item) {
				Event event = item.getModelObject();
				item.add(new Label("eventName", event.getTitle()));
				item.add(new Label("eventLocation", event.getLocation()));
			}

		});
		wmc.setOutputMarkupId(true);
		add(wmc);

		eventForm.add(new AjaxSubmitLink("submit") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Event event = (Event) form.getModelObject();
				Event newEvent = new Event();
				newEvent.setLocation(event.getLocation());
				newEvent.setTitle(event.getTitle());
				eventDao.save(newEvent);
				target.addComponent(wmc);
			}
		});

		add(eventForm);

	}

	public class EventDetachable extends LoadableDetachableModel<List<Event>> {
		@Inject
		private EventDao eventDao;

		public EventDetachable() {
			InjectorHolder.getInjector().inject(this);
		}

		@Override
		protected List<Event> load() {
			return eventDao.findAll();
		}

	}
}