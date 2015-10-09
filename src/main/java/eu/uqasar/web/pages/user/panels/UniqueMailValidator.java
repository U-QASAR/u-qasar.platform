/*
 */
package eu.uqasar.web.pages.user.panels;

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
public class UniqueMailValidator extends AbstractFormValidator {

	@Inject
	UserService userService;

	private final TextField<String> mailField;
	private final String allowedValue;

	public UniqueMailValidator(TextField<String> mailField, final String allowedValue) {
		Args.notNull(mailField, "mailField");
		this.mailField = mailField;
		this.allowedValue = allowedValue;
		CdiContainer.get().getNonContextualManager().inject(this);
	}

	public UniqueMailValidator(TextField<String> mailField) {
		this(mailField, null);
	}

	@Override
	public FormComponent<?>[] getDependentFormComponents() {
		return new FormComponent<?>[]{mailField};
	}

	@Override
	public void validate(Form<?> form) {
		final String input = mailField.getInput();
		if (!StringUtils.isEmpty(input)) {
			User user = userService.getByMail(input);
			if (user == null) {
				return;
			}
			if (!StringUtils.isEmpty(allowedValue) && !allowedValue.equals(user.getMail())) {
				error(mailField);
			}
		}
	}
}
