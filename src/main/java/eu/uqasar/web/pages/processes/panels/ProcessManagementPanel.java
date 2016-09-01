package eu.uqasar.web.pages.processes.panels;

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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * 
 */
public abstract class ProcessManagementPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final List<String> orderSelection = Arrays
			.asList("Ascending Date Order",
                    "Descending Date Order");
	private static final String ASCENDING_IS_SELECTED = "Ascending Date Order";
	public static final String DESCENDING_IS_SELECTED = "Descending Date Order";

	private String selected = ASCENDING_IS_SELECTED;

	private final TextField<String> processName;
	private final DropDownChoice startDate, endDate;

	private final Form<Void> form;
	private final IndicatingAjaxButton apply, reset;

	private String name;
	private Date dateEnd, dateStart;

	@SuppressWarnings("unchecked")
	public ProcessManagementPanel(String id) {
		super(id);

		form = new Form<>("form");
		add(form);

		processName = new TextField<>("name", new PropertyModel<String>(
                this, "name"));
		form.add(processName);

		startDate = new DropDownChoice("start.date", new PropertyModel(this,
				"selected"), orderSelection) {

			@Override
			protected void onSelectionChanged(Object newSelection) {
				selected = (String) newSelection;
				
			}

			@Override
			protected boolean wantOnSelectionChangedNotifications() {

				return true;
			}

		};
		form.add(startDate);

		endDate = new DropDownChoice("end.date", new PropertyModel(this,
				"selected"), orderSelection) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSelectionChanged(Object newSelection) {
				selected = (String) newSelection;
				System.out.println(selected);
			}

			@Override
			protected boolean wantOnSelectionChangedNotifications() {

				return true;
			}

		};
		form.add(endDate);

		form.add(apply = new IndicatingAjaxButton("apply") {
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setSelected(endDate.getValue());
				ProcessManagementPanel.this.applyClicked(target, form);
			}
		});
		// 4.90

		form.add(reset = new IndicatingAjaxButton("reset") {
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ProcessManagementPanel.this.resetClicked(target, form);
				resetForm();
				target.add(form);
			}
		});

	}

	public ProcessesFilterStructure getFilter() {
		return new ProcessesFilterStructure(this);
	}

	public String getName() {
		return name;
	}

	public Date getStartDate() {
		return dateStart;
	}

	public Date getEndDate() {
		return dateEnd;
	}

	private void resetForm() {
		processName.clearInput();
		processName.setModelObject(null);
		startDate.clearInput();
		startDate.setModelObject(null);
		endDate.clearInput();
		endDate.setModelObject(null);
	}

	private void setSelected(String selected) {
		this.selected = selected;
	}

	public String getSelected() {
		return this.selected;
	}

	public abstract void applyClicked(AjaxRequestTarget target, Form<?> form);

	public abstract void resetClicked(AjaxRequestTarget target, Form<?> form);

}
