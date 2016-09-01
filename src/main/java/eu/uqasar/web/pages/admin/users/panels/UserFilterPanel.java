/*
 */
package eu.uqasar.web.pages.admin.users.panels;

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


import eu.uqasar.model.role.Role;
import eu.uqasar.model.user.RegistrationStatus;
import eu.uqasar.model.user.UserSource;
import java.util.Arrays;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 *
 */
public abstract class UserFilterPanel extends Panel {

	private final TextField<String> userField;
	private final DropDownChoice<RegistrationStatus> statusChoice;
	private final DropDownChoice<UserSource> sourceChoice;
	private final DropDownChoice<Role> roleChoice;

	private final Form<Void> form;
	private final IndicatingAjaxButton apply, reset;

	private Role role;
	private UserSource source;
	private RegistrationStatus status;
	private String name;

	public UserFilterPanel(final String markupId) {
		super(markupId);

		form = new Form<>("form");

		userField = new TextField<>("name", new PropertyModel<String>(this, "name"));
		form.add(userField);

		roleChoice = new DropDownChoice<>("role.choice",
				new PropertyModel<Role>(this, "role"),
				Arrays.asList(Role.userAssignableRoles()));
		form.add(roleChoice);

		sourceChoice = new DropDownChoice<>("source.choice",
				new PropertyModel<UserSource>(this, "source"),
				Arrays.asList(UserSource.values()));
		form.add(sourceChoice);

		statusChoice = new DropDownChoice<>("status.choice",
				new PropertyModel<RegistrationStatus>(this, "status"),
				Arrays.asList(RegistrationStatus.values()));
		form.add(statusChoice);
		form.add(apply = new IndicatingAjaxButton("apply") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				UserFilterPanel.this.applyClicked(target, form);
			}
		});
		form.add(reset = new IndicatingAjaxButton("reset") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				UserFilterPanel.this.resetClicked(target, form);
				resetForm();
				target.add(form);
			}
		});
		add(form);
	}

	public UserFilterStructure getFilter() {
		return new UserFilterStructure(this);
	}

	public String getName() {
		return name;
	}

	public Role getRole() {
		return role;
	}

	public UserSource getSource() {
		return source;
	}

	public RegistrationStatus getStatus() {
		return status;
	}

	private void resetForm() {
		userField.clearInput();
		userField.setModelObject(null);
		statusChoice.clearInput();
		statusChoice.setModelObject(null);
		roleChoice.clearInput();
		roleChoice.setModelObject(null);
		sourceChoice.clearInput();
		sourceChoice.setModelObject(null);
	}

	public abstract void applyClicked(AjaxRequestTarget target, Form<?> form);

	public abstract void resetClicked(AjaxRequestTarget target, Form<?> form);
}
