package eu.uqasar.web.pages.qmtree.panels.filter;

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
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import eu.uqasar.model.qmtree.QModelStatus;

public abstract class QMFilterPanel extends Panel {

	private static final long serialVersionUID = 7542340690884580790L;

	private final Form<Void> form;
	private final AjaxSubmitLink apply, reset;
	private final QModelStatus activeSelected = QModelStatus.NotActive;
	private List<String> activeOptList;
	private final DropDownChoice<QModelStatus> activeChoice;

	public QMFilterPanel(String id) {
		super(id);
		form = new Form<>("form");

		form.add(activeChoice = new DropDownChoice<>("activeOpt",
				new PropertyModel<QModelStatus>(this, "activeSelected"),
				Arrays.asList(QModelStatus.values())));

		activeChoice.clearInput();
		activeChoice.setModelObject(null);

		form.add(apply = new AjaxSubmitLink("apply") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				QMFilterPanel.this.applyClicked(target, form);
			}
		});
		form.add(reset = new AjaxSubmitLink("reset") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				QMFilterPanel.this.resetClicked(target, form);
				resetForm();
				target.add(form);
			}
		});
		add(form);
	}

	public QMTreeFilterStructure getFilter() {
		return new QMTreeFilterStructure(this);
	}

	private void resetForm() {
		activeChoice.clearInput();
		activeChoice.setModelObject(null);
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}

	public QModelStatus getActiveSelected() {
		return activeSelected;
	}

	public abstract void applyClicked(AjaxRequestTarget target, Form<?> form);

	public abstract void resetClicked(AjaxRequestTarget target, Form<?> form);
}
