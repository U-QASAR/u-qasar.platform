package eu.uqasar.web.components;

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

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.util.time.Duration;

/**
 *
 * 
 */
public class StyledFeedbackPanel extends Panel implements IFeedback {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2886209539152719806L;

	private final WebMarkupContainer feedbackContainer;

	/** Message view */
	private final MessageListView messageListView;

	private boolean hasError = false;

	/**
	 * @see org.apache.wicket.Component#Component(String)
	 */
	public StyledFeedbackPanel(final String id) {
		this(id, null);
	}

	/**
	 * @see org.apache.wicket.Component#Component(String)
	 * 
	 * @param id
	 * @param filter
	 */
	public StyledFeedbackPanel(final String id, IFeedbackMessageFilter filter) {
		super(id);
		setOutputMarkupId(true);
		feedbackContainer = new WebMarkupContainer("feedbackdiv") {

			private static final long serialVersionUID = -8736469161089673840L;

			@Override
			protected void onConfigure() {
				setVisible(anyMessage());
			}
		};
		add(feedbackContainer);

		AjaxLink<Void> closeLink = new AjaxLink<Void>("closeLink") {

			private static final long serialVersionUID = -6970077602640428876L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				Effects.fadeOutElem(target,
						StyledFeedbackPanel.this.getMarkupId());
			}
		};
		feedbackContainer.add(closeLink);

		final WebMarkupContainer messagesContainer = new WebMarkupContainer(
				"feedbackul");
		feedbackContainer.add(messagesContainer);
		messageListView = new MessageListView("messages");
		messageListView.setVersioned(false);
		messagesContainer.add(messageListView);

		if (filter != null) {
			setFilter(filter);
		}
	}

	@Override
	protected void onBeforeRender() {
		hasError = false;
		if (anyMessage()) {
			// add css class according to highest level of messages
			feedbackContainer.add(new CSSAppender(getCSSClass()));
			// if we're not an error message, disappear after 15 seconds
			this.add(new FadeOutBehavior(Duration.seconds(15)));
		}
		super.onBeforeRender();
	}

	private String getCSSClass() {
		if (anyErrorMessage()) {
			hasError = true;
			return "alert-error";
		}
		if (anyMessage(FeedbackMessage.WARNING)) {
			return "";
		}
		if (anyMessage(FeedbackMessage.SUCCESS)) {
			return "alert-success";
		}
		if (anyMessage(FeedbackMessage.INFO)) {
			return "alert-info";
		}
		return "";
	}

	/**
	 * Search messages that this panel will render, and see if there is any
	 * message of level ERROR or up. This is a convenience method; same as
	 * calling 'anyMessage(FeedbackMessage.ERROR)'.
	 * 
	 * @return whether there is any message for this panel of level ERROR or up
	 */
    private boolean anyErrorMessage() {
		return anyMessage(FeedbackMessage.ERROR);
	}

	/**
	 * Search messages that this panel will render, and see if there is any
	 * message.
	 * 
	 * @return whether there is any message for this panel
	 */
    private boolean anyMessage() {
		return anyMessage(FeedbackMessage.UNDEFINED);
	}

	/**
	 * Search messages that this panel will render, and see if there is any
	 * message of the given level.
	 * 
	 * @param level
	 *            the level, see FeedbackMessage
	 * @return whether there is any message for this panel of the given level
	 */
    private boolean anyMessage(int level) {
		List<FeedbackMessage> msgs = getCurrentMessages();

		for (FeedbackMessage msg : msgs) {
			if (msg.isLevel(level)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return Model for feedback messages on which you can install filters and
	 *         other properties
	 */
    private FeedbackMessagesModel getFeedbackMessagesModel() {
		return (FeedbackMessagesModel) messageListView.getDefaultModel();
	}

	/**
	 * @return The current message filter
	 */
	public final IFeedbackMessageFilter getFilter() {
		return getFeedbackMessagesModel().getFilter();
	}

	/**
	 * @return The current sorting comparator
	 */
	public final Comparator<FeedbackMessage> getSortingComparator() {
		return getFeedbackMessagesModel().getSortingComparator();
	}

	/**
	 * @see org.apache.wicket.Component#isVersioned()
	 */
	@Override
	public boolean isVersioned() {
		return false;
	}

	/**
	 * Sets a filter to use on the feedback messages model
	 * 
	 * @param filter
	 *            The message filter to install on the feedback messages model
	 * 
	 * @return FeedbackPanel this.
	 */
    private StyledFeedbackPanel setFilter(IFeedbackMessageFilter filter) {
		getFeedbackMessagesModel().setFilter(filter);
		return this;
	}

	/**
	 * @param maxMessages
	 *            The maximum number of feedback messages that this feedback
	 *            panel should show at one time
	 * 
	 * @return FeedbackPanel this.
	 */
	public final StyledFeedbackPanel setMaxMessages(int maxMessages) {
		messageListView.setViewSize(maxMessages);
		return this;
	}

	/**
	 * Sets the comparator used for sorting the messages.
	 * 
	 * @param sortingComparator
	 *            comparator used for sorting the messages.
	 * 
	 * @return FeedbackPanel this.
	 */
	public final StyledFeedbackPanel setSortingComparator(
			Comparator<FeedbackMessage> sortingComparator) {
		getFeedbackMessagesModel().setSortingComparator(sortingComparator);
		return this;
	}

	/**
	 * Gets the currently collected messages for this panel.
	 * 
	 * @return the currently collected messages for this panel, possibly empty
	 */
    private List<FeedbackMessage> getCurrentMessages() {
		final List<FeedbackMessage> messages = messageListView.getModelObject();
		return Collections.unmodifiableList(messages);
	}

	/**
	 * Gets a new instance of FeedbackMessagesModel to use.
	 * 
	 * @return Instance of FeedbackMessagesModel to use
	 */
    private FeedbackMessagesModel newFeedbackMessagesModel() {
		return new FeedbackMessagesModel(this);
	}

	/**
	 * Generates a component that is used to display the message inside the
	 * feedback panel. This component must handle being attached to
	 * <code>span</code> tags.
	 * 
	 * By default a {@link Label} is used.
	 * 
	 * Note that the created component is expected to respect feedback panel's
	 * {@link #getEscapeModelStrings()} value
	 * 
	 * @param id
	 *            parent id
	 * @param message
	 *            feedback message
	 * @return component used to display the message
	 */
    private Component newMessageDisplayComponent(String id,
                                                 FeedbackMessage message) {
		Serializable serializable = message.getMessage();
		Label label = new Label(id, (serializable == null) ? ""
				: serializable.toString());
		label.setEscapeModelStrings(StyledFeedbackPanel.this
				.getEscapeModelStrings());
		return label;
	}

	/**
	 * 
	 *
	 * 
	 */
	private final class FadeOutBehavior extends AbstractAjaxTimerBehavior {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2676701456817570537L;

		public FadeOutBehavior(Duration updateInterval) {
			super(updateInterval);
		}

		@Override
		protected void onTimer(AjaxRequestTarget target) {
			// only disappear if we're not an error message
			if (!hasError) {
				Effects.fadeOutElem(target,
						StyledFeedbackPanel.this.getMarkupId());
			}
			// stop after one execution
			stop(target);
		}
	}

	/**
	 * List for messages.
	 */
	private final class MessageListView extends ListView<FeedbackMessage> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6748868994909772096L;

		/**
		 * @see org.apache.wicket.Component#Component(String)
		 */
		public MessageListView(final String id) {
			super(id);
			setDefaultModel(newFeedbackMessagesModel());
		}

		@Override
		protected IModel<FeedbackMessage> getListItemModel(
				final IModel<? extends List<FeedbackMessage>> listViewModel,
				final int index) {
			return new AbstractReadOnlyModel<FeedbackMessage>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -1677943933639858263L;

				/**
				 * WICKET-4258 Feedback messages might be cleared already.
				 * 
				 * @see WebSession#cleanupFeedbackMessages()
				 */
				@Override
				public FeedbackMessage getObject() {
					if (index >= listViewModel.getObject().size()) {
						return null;
					} else {
						return listViewModel.getObject().get(index);
					}
				}
			};
		}

		@Override
		protected void populateItem(final ListItem<FeedbackMessage> listItem) {

			final FeedbackMessage message = listItem.getModelObject();
			message.markRendered();
			final Component label = newMessageDisplayComponent("message",
					message);
			listItem.add(label);
		}
	}

}
