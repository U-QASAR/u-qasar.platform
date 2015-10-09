package eu.uqasar.web.pages.user.panels;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import eu.uqasar.model.company.Company;
import eu.uqasar.model.role.Role;
import eu.uqasar.model.user.Gender;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.company.CompanyService;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.i18n.Language;

import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.validation.validator.PatternValidator;
import org.joda.time.DateTime;

/**
 *
 *
 */
public class UserProfilePanel extends Panel {

    private final TextField<String> mail;
    private final TextField<String> userName;
    private final TextField<String> firstName;
    private final TextField<String> lastName;
    private final PasswordTextField password;
    private final PasswordTextField confirmPassword;
    private final DropDownChoice<Role> role;
    private final DropDownChoice<Gender> gender;
    private final DateTextField birthday;
    private final WebMarkupContainer roleContainer;
    private final WebMarkupContainer companyContainer;
    private final Label age;
    private final DropDownChoice<Company> company;
    
    private final User user;
    
    @Inject CompanyService companyService;

    public UserProfilePanel(String id, final User usr) {
        super(id);
        this.user = usr;

        mail = new TextField<>("mail", new PropertyModel<String>(user, "mail"));
        InputBorder<String> mailValidationBorder = new OnEventInputBeanValidationBorder<>(
                "mailValidationBorder", mail,
                new StringResourceModel("label.mail", this, null),
                new StringResourceModel("help.mail", this, null),
                HtmlEvent.ONBLUR);
        add(mailValidationBorder);
        add(new UniqueMailValidator(mail, user.getMail()));
        mail.setEnabled(UQSession.get().isUserAdmin());

        userName = new TextField<>("userName", new PropertyModel<String>(user, "userName"));
        InputBorder<String> userNameValidationBorder = new OnEventInputBeanValidationBorder<>(
                "userNameValidationBorder", userName,
                new StringResourceModel("label.userName", this, null),
                new StringResourceModel("help.userName", this, null),
                HtmlEvent.ONBLUR);
        add(userNameValidationBorder);
        add(new UniqueUsernameValidator(userName, user.getUserName()));
        userName.
                setEnabled(UQSession.get().isUserAdmin() || !user.isLdapBased());

        firstName = new TextField<>("firstName", new PropertyModel<String>(user, "firstName"));
        InputBorder<String> firstNameValidationBorder = new OnEventInputBeanValidationBorder<>(
                "firstNameValidationBorder", firstName,
                new StringResourceModel("label.firstName", this, null), HtmlEvent.ONBLUR);
        add(firstNameValidationBorder);

        lastName = new TextField<>("lastName", new PropertyModel<String>(user, "lastName"));
        InputBorder<String> lastNameValidationBorder = new OnEventInputBeanValidationBorder<>(
                "lastNameValidationBorder", lastName,
                new StringResourceModel("label.lastName", this, null), HtmlEvent.ONBLUR);
        add(lastNameValidationBorder);

        roleContainer = new WebMarkupContainer("roleContainer");
        role = new DropDownChoice<>("role", new PropertyModel<Role>(user, "role"), Arrays.
                asList(Role.userAssignableRoles()));
        roleContainer.add(role);
        add(roleContainer);
        
        
        companyContainer = new WebMarkupContainer("companyContainer");
        company = new DropDownChoice<>("company", new PropertyModel<Company>(user, "company"), companyService.getAllCompanys());
        companyContainer.add(company);
        add(companyContainer);
        
        gender = new DropDownChoice<>("gender", new PropertyModel<Gender>(user, "gender"), Arrays.
                asList(Gender.values()));
        gender.setNullValid(true);
        add(gender);

        birthday = newDateTextField("birthday", new PropertyModel<Date>(user, "birthDay"));
        add(birthday);
        add(age = new Label("age", getAgeModel()) {
            @Override
			protected void onConfigure() {
				super.onConfigure();
                setDefaultModel(getAgeModel());
                setOutputMarkupId(true);
            }
        });

        password = new PasswordTextField("password", Model.of(""));
        password.setRequired(user.getId() == null);
        password.add(new PatternValidator(AuthenticationService.PW_PATTERN));
        InputBorder<String> passwordValidationBorder = new InputBorder(
                "passwordValidationBorder", password,
                new StringResourceModel("label.password", this, null),
                new StringResourceModel("help.password", this, null));
        add(passwordValidationBorder);
        password.setEnabled(!user.isLdapBased());

        confirmPassword = new PasswordTextField("confirmPassword", Model.of(""));
        confirmPassword.setRequired(user.getId() == null);
        InputBorder<String> confirmPasswordValidationBorder = new InputBorder(
                "confirmPasswordValidationBorder", confirmPassword,
                new StringResourceModel("label.password.confirm", this, null));
        add(confirmPasswordValidationBorder);
        add(new EqualPasswordInputValidator(password, confirmPassword));
        confirmPassword.setEnabled(!user.isLdapBased());
        
    }
    
    private IModel<String> getAgeModel() {
        if(user.getAge() == null) {
            return Model.of("");
        } else {
            return new StringResourceModel("label.age", this, null, Model.of(user.getAge()));
        }
    }
    
    public String getPassword() {
        return password.getModelObject();
    }
    
    public String getPasswordConfirmation() {
        return confirmPassword.getModelObject();
    }

    private DateTextField newDateTextField(final String id, IModel<Date> model) {
        Language language = Language.fromSession();
        DateTextFieldConfig config = new DateTextFieldConfig()
                .withFormat(language.getDatePattern())
                .withLanguage(language.getLocale().getLanguage())
                .allowKeyboardNavigation(true).autoClose(false)
                .highlightToday(true).showTodayButton(true)
                .withEndDate(DateTime.now());

        DateTextField dateTextField = new DateTextField(id, model, config);
        dateTextField.add(new AttributeAppender("placeHolder", language
                .getLocalizedDatePattern()));
        dateTextField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = -7155018344656574747L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(age);
			}
		});
        return dateTextField;
    }

    @Override
    protected void onConfigure() {
        roleContainer.setVisible(UQSession.get().isUserAdmin());
        companyContainer.setVisible(UQSession.get().isUserAdmin());
        role.setEnabled(!editingMyself());
    }

    private boolean editingMyself() {
        return user != null && user.getId() != null
                && UQSession.exists() && UQSession.get().getLoggedInUser() != null
                && Objects.equal(user.getId(), UQSession.get().getLoggedInUser().getId());
    }
}
