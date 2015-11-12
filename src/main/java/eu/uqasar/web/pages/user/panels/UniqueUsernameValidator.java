/*
 */
package eu.uqasar.web.pages.user.panels;

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


import eu.uqasar.model.user.User;
import eu.uqasar.service.user.UserService;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.cdi.CdiContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.util.lang.Args;

/**
 *
 *
 */
public class UniqueUsernameValidator extends AbstractFormValidator {

	@Inject
	UserService userService;

	private final TextField<String> usernameField;
	private final String allowedValue;

	public UniqueUsernameValidator(TextField<String> usernameField, final String allowedValue) {
		Args.notNull(usernameField, "usernameField");
		this.usernameField = usernameField;
		this.allowedValue = allowedValue;
		CdiContainer.get().getNonContextualManager().inject(this);
	}

	public UniqueUsernameValidator(TextField<String> usernameField) {
		this(usernameField, null);
	}

	@Override
	public FormComponent<?>[] getDependentFormComponents() {
		return new FormComponent<?>[]{usernameField};
	}

	@Override
	public void validate(Form<?> form) {
		final String input = usernameField.getInput();
		if (!StringUtils.isEmpty(input)) {
			User user = userService.getByUserName(input);
			if (user == null) {
				return;
			}
			if (!StringUtils.isEmpty(allowedValue) && !allowedValue.equals(user.getUserName())) {
				error(usernameField);
			}
		}
	}
}
