package eu.uqasar.web.components.navigation.notification;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Date;

import lombok.Setter;
import org.apache.wicket.Page;
import org.apache.wicket.core.util.lang.WicketObjects;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.notification.INotification;
import eu.uqasar.web.UQasar;

@Setter
public class NotificationBookmarkablePageLink<T extends INotification, C extends Page> extends Link<T> {

	private static final long serialVersionUID = 9134635901931928968L;
	
	protected static org.jboss.solder.logging.Logger logger = Logger.getLogger(NotificationBookmarkablePageLink.class);

	/**
	 * The page class that this link links to.
	 */
	protected final String pageClassName;

	/**
	 * The parameters to pass to the class constructor when instantiated.
	 */
	protected PageParameters parameters;

	protected Icon icon;
	protected WebMarkupContainer gotoContainer;
	protected Label notificationDate;
	
	private NotificationBookmarkablePageLink(String id, Class<C> pageClass, IModel<T> model) {
		this(id, pageClass, new PageParameters(), model);
	}

	protected NotificationBookmarkablePageLink(final String id, final Class<C> pageClass, final PageParameters parameters, IModel<T> model) {
		
		super(id, model);
		this.parameters = parameters;

		if (pageClass == null) {
			throw new IllegalArgumentException(
					"Page class for bookmarkable link cannot be null");
		} else if (!Page.class.isAssignableFrom(pageClass)) {
			throw new IllegalArgumentException(
					"Page class must be derived from " + Page.class.getName());
		}
		pageClassName = pageClass.getName();

		final String className = (model != null) && (model.getObject() != null) ? model
				.getObject().getClass().getSimpleName()
				: this.getClass().getSimpleName();

		add(new CssClassNameAppender("notification", className));
		TransparentWebMarkupContainer notificationContainer = new TransparentWebMarkupContainer(
				"notification.container");
		add(notificationContainer);
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, UQasar
				.getSession().getLocale());

		
		// Add a check whether the creation date is null
		Date creationDate = new Date();
		if (model.getObject().getCreationDate() == null) {
			creationDate = model.getObject().getCreationDate();
		}
		
		gotoContainer = new WebMarkupContainer("notification.goto.container");
		gotoContainer.add(notificationDate = new Label("notification.date", df
				.format(creationDate)));
		notificationContainer.add(icon = new Icon("notification.icon",
				new IconType("group")));
		notificationContainer.add(gotoContainer);
	}

	protected NotificationBookmarkablePageLink<T, C> setIcon(IconType iconType) {
		icon.setType(iconType);
		return this;
	}

	/**
	 * @return page parameters
	 */
    protected PageParameters getPageParameters() {
		if (parameters == null) {
			parameters = new PageParameters();
		}
		return parameters;
	}

	public void setPageParameters(PageParameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * Get the page class registered with the link
	 *
	 * @return Page class
	 */
    private Class<? extends Page> getPageClass() {
		return WicketObjects.resolveClass(pageClassName);
	}

	/**
	 * Whether this link refers to the given page.
	 *
	 * @param page the page
	 * @see
	 * org.apache.wicket.markup.html.link.Link#linksTo(org.apache.wicket.Page)
	 */
	@Override
	public boolean linksTo(final Page page) {
		return page.getClass() == getPageClass();
	}

	@Override
	protected boolean getStatelessHint() {
		return true;
	}

	
	/**
	 * Gets the url to use for this link.
	 *
	 * @return The URL that this link links to
	 * @see org.apache.wicket.markup.html.link.Link#getURL()
	 */
	@Override
	protected CharSequence getURL() {
		return urlFor(getPageClass(), getPageParameters());
	}

	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return new PanelMarkupSourcingStrategy(true);
	}

	public static <N extends INotification, P extends Page, T extends NotificationBookmarkablePageLink<N, P>> T getLink(Class<? extends NotificationBookmarkablePageLink> clazz, String id, IModel<N> model) {
		try {
			Constructor<? extends NotificationBookmarkablePageLink> constructor = clazz.getConstructor(String.class, IModel.class);
			return (T) constructor.newInstance(id, model);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	public static <N extends INotification, P extends Page, T extends NotificationBookmarkablePageLink<N, P>> T getLink(Class<N> clazz, 
			String id, PageParameters parameters, IModel<N> model) {
		try {
			Constructor<N> constructor = clazz.getConstructor(String.class, PageParameters.class, IModel.class);
			return (T) constructor.newInstance(id, parameters, model);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	public void onClick() {
	}

}
