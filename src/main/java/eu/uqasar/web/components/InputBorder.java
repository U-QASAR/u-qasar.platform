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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;

/**
 * A simple border around a form component that will add twitter bootstrap
 * specific error and success CSS to the border's class attribute if the form
 * component contains invalid input (based on wicket's own validation mechanism.
 * The corresponding HTML mark-up obviously must contain a feedback panel
 * compatible component with wicket:id = "inputErrors" in order for this border
 * to work.
 * 
 *
 * 
 * @param <T>
 */
public class InputBorder<T> extends Border implements IFeedback {

	/**
	 * 
	 */
	private static final long serialVersionUID = -672647697815368300L;

	// the feedback panel's wicket ID in HTML markup
	private static final String feedbackPanelID = "inputErrors";

	// the input label's wicket ID in HTML markup
	private static final String labelID = "inputLabel";
	
	// the input label's container wicket ID HTML markup
	private static final String labelContainerID = "inputLabelContainer";

	// the input label'S wicket ID in HTML markup
	private static final String helpID = "helpLabel";

	// this borders feedback panel
	private final FeedbackPanel feedback;

	// the form component to validate
	final FormComponent<T> inputComponent;

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param inputComponent
	 */
	public InputBorder(final String id, final FormComponent<T> inputComponent) {
		this(id, inputComponent, new Model<String>(), new Model<String>());
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param inputComponent
	 * @param labelModel
	 *            optional
	 */
	public InputBorder(final String id, final FormComponent<T> inputComponent,
			final IModel<String> labelModel) {
		this(id, inputComponent, labelModel, new Model<String>());
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param inputComponent
	 * @param labelModel
	 *            optional
	 * @param helpModel
	 *            optional
	 */
	public InputBorder(final String id, final FormComponent<T> inputComponent,
			final IModel<String> labelModel, final IModel<String> helpModel) {
		super(id);

		Args.notNull(labelModel, "labelModel");
		Args.notNull(helpModel, "helpModel");

		// set html id so that this border can be refreshed by ajax
		this.setOutputMarkupId(true);

		// add the form component to the border
		this.inputComponent = inputComponent;
		add(this.inputComponent);

		
		// add the label
		WebMarkupContainer labelContainer = new WebMarkupContainer(labelContainerID);
		Label label = new Label(labelID, labelModel);
		label.setEscapeModelStrings(false);
		labelContainer.add(new AttributeModifier("for", Model.of(inputComponent.getMarkupId())));
		labelContainer.add(label);
		addToBorder(labelContainer);

		// add the help label
		addToBorder(new Label(helpID, helpModel).setEscapeModelStrings(false));

		// add the feedback panel with filter so that it only shows messages
		// relevant for this input component
		this.feedback = new FeedbackPanel(feedbackPanelID,
				new ContainerFeedbackMessageFilter(this));
		addToBorder(this.feedback.setOutputMarkupId(true));
	}

}
