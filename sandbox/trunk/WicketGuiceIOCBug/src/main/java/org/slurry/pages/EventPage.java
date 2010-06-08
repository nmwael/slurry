package org.slurry.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
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

	private EventDetachableModel selectedEventModel;

	private MarkupContainer eventLabel;

	private MarkupContainer locationLabel;

	private EventListDetachableModel eventListDetachableModel;

	public EventPage(final PageParameters pp) {
		forTestPurposeOnlyNeverDoThis();

		Form<Event> eventForm = new Form<Event>("eventForm",
				new CompoundPropertyModel<Event>(new Event()));
		eventForm.add(new TextField<String>("title").setRequired(true));
		eventForm.add(new TextField<String>("location").setRequired(true));

		final WebMarkupContainer wmc = new WebMarkupContainer("listContainer");

		wmc.add(new ListView<Event>("list", getEventListDetachableModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Event> item) {
				Event event = item.getModelObject();
				item.add(new Label("eventName", event.getTitle()));
				item.add(new Label("eventLocation", event.getLocation()));
				final Long id = event.getId();
				item.add(new AjaxLink<Event>("selectLink") {

					@Override
					public void onClick(AjaxRequestTarget arg0) {
						getSelectedEventModel().setId(id);
						arg0.addComponent(eventLabel);
						arg0.addComponent(locationLabel);

					}
				});
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

		eventLabel = add(new Label("eventName", new PropertyModel<Event>(
				getSelectedEventModel(), "title")));
		eventLabel.setOutputMarkupPlaceholderTag(true);
		locationLabel = add(new Label("eventLocation",
				new PropertyModel<Event>(getSelectedEventModel(), "location")));
		locationLabel.setOutputMarkupPlaceholderTag(true);

	}

	private void forTestPurposeOnlyNeverDoThis() {
		getSelectedEventModel().setId(eventDao.findAll().get(0).getId());

	}

	@Inject
	public void setSelectedEventModel(EventDetachableModel selectedEventModel) {
		this.selectedEventModel = selectedEventModel;
	}

	public EventDetachableModel getSelectedEventModel() {
		return selectedEventModel;
	}

	@Inject
	public void setEventListDetachableModel(
			EventListDetachableModel eventListDetachableModel) {
		this.eventListDetachableModel = eventListDetachableModel;
	}

	public EventListDetachableModel getEventListDetachableModel() {
		return eventListDetachableModel;
	}
}