/*
 */
package eu.uqasar.web.pages.admin.settings;

import eu.uqasar.model.settings.ldap.LdapSettings;
import eu.uqasar.service.settings.LdapSettingsService;
import eu.uqasar.util.ldap.LdapManager;
import eu.uqasar.web.components.CSSAppender;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.pages.admin.AdminBasePage;
import eu.uqasar.web.pages.admin.settings.panels.LdapGroupListPanel;
import eu.uqasar.web.pages.admin.settings.panels.LdapUserListPanel;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

/**
 *
 *
 */
public class LdapSettingsPage extends AdminBasePage {

	@Inject
	Logger logger;

	@Inject
	LdapSettingsService ldapService;

	private TextField<String> userDNField, hostField;
	private TextField<String> userFilterBaseDNField, userFilterField, usernameMappingField, userMailMappingField, userFirstnameMappingField, userLastnameMappingField, userPhotoMappingField;
	private TextField<String> groupFilterBaseDNField, groupFilterField, groupNameMappingField, groupDescriptionMappingField, groupMemberMappingField;
	private NumberTextField<Integer> portField;
	private PasswordTextField passwordField;

	private LdapUserListPanel ldapUsersList;
	private LdapGroupListPanel ldapGroupList;

	private Label validationResults, userRetrievalInfo, groupRetrievalInfo;
	private LdapSettings ldapSettings;
	private LdapManager manager;

	public LdapSettingsPage(final PageParameters pageParameters) {
		super(pageParameters);
		ldapSettings = LdapSettings.getDefault();
		ldapSettings = ldapService.get(ldapSettings);

		Form<Void> form = new InputValidationForm<>("form");
		createConnectionSettingsForm(form);
		createUsersForm(form);
		createGroupsForm(form);
		add(form);
	}

	private Form<Void> createConnectionSettingsForm(Form<Void> form) {
		hostField = new TextField<>("host", new PropertyModel<String>(ldapSettings, "host"));
		InputBorder<String> hostValidationBorder = new InputBorder<>(
				"hostValidationBorder", hostField,
				new StringResourceModel("label.host", this, null),
				new StringResourceModel("help.host", this, null));
		form.add(hostValidationBorder);

		portField = new NumberTextField<>("port", new PropertyModel<Integer>(ldapSettings, "port"));
		InputBorder<Integer> portValidationBorder = new InputBorder<>(
				"portValidationBorder", portField,
				new StringResourceModel("label.port", this, null));
		form.add(portValidationBorder);

		userDNField = new TextField<>("userDN", new PropertyModel<String>(ldapSettings, "authUserDN"));
		InputBorder<String> userNameValidationBorder = new InputBorder<>(
				"userDNValidationBorder", userDNField,
				new StringResourceModel("label.userDN", this, null),
				new StringResourceModel("help.userDN", this, null));
		form.add(userNameValidationBorder);

		passwordField = new PasswordTextField("password", new PropertyModel<String>(ldapSettings, "authUserPassword"));
		InputBorder<String> passwordValidationBorder = new InputBorder(
				"passwordValidationBorder", passwordField,
				new StringResourceModel("label.password", this, null));
		passwordField.setResetPassword(false);
		form.add(passwordValidationBorder);

		Button validateConnection = new IndicatingAjaxButton("validateConnection") {

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.add(form);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					manager = LdapManager.getInstance(ldapSettings);
					validationResults.setDefaultModel(new StringResourceModel("label.validation.success", this, null));
					validationResults.add(new CSSAppender("success"));
				} catch (Throwable ex) {
					logger.warn(ex.getMessage(), ex);
					StringWriter errors = new StringWriter();
					ex.printStackTrace(new PrintWriter(errors));
					validationResults.setDefaultModel(Model.of(errors.toString()));
					validationResults.add(new CSSAppender("error"));
				}
				target.add(validationResults);
			}
		};
		form.add(validateConnection);

		validationResults = new Label("validationResults");
		form.add(validationResults.setOutputMarkupId(true));
		return form;
	}

	private Form<Void> createUsersForm(Form<Void> form) {
		userFilterBaseDNField = new TextField<>("userFilterBaseDN", new PropertyModel<String>(ldapSettings, "userFilterBaseDN"));
		InputBorder<String> userFilterBaseDNValidationBorder = new InputBorder<>(
				"userFilterBaseDNValidationBorder", userFilterBaseDNField,
				new StringResourceModel("label.user.filter.baseDN", this, null),
				new StringResourceModel("help.user.filter.baseDN", this, null));
		form.add(userFilterBaseDNValidationBorder);

		userFilterField = new TextField<>("userFilter", new PropertyModel<String>(ldapSettings, "userFilter"));
		InputBorder<String> userFilterValidationBorder = new InputBorder<>(
				"userFilterValidationBorder", userFilterField,
				new StringResourceModel("label.user.filter", this, null),
				new StringResourceModel("help.user.filter", this, null));
		form.add(userFilterValidationBorder);

		usernameMappingField = new TextField<>("userUsernameMapping", new PropertyModel<String>(ldapSettings, "userUserNameMapping"));
		InputBorder<String> usernameMappingValidationBorder = new InputBorder<>(
				"userUsernameMappingValidationBorder", usernameMappingField,
				new StringResourceModel("label.user.mapping.username", this, null),
				new StringResourceModel("help.user.mapping.username", this, null));
		form.add(usernameMappingValidationBorder);

		userMailMappingField = new TextField<>("userMailMapping", new PropertyModel<String>(ldapSettings, "userMailMapping"));
		InputBorder<String> userMailMappingValidationBorder = new InputBorder<>(
				"userMailMappingValidationBorder", userMailMappingField,
				new StringResourceModel("label.user.mapping.mail", this, null),
				new StringResourceModel("help.user.mapping.mail", this, null));
		form.add(userMailMappingValidationBorder);

		userFirstnameMappingField = new TextField<>("userFirstnameMapping", new PropertyModel<String>(ldapSettings, "userFirstNameMapping"));
		InputBorder<String> userFirstnameMappingValidationBorder = new InputBorder<>(
				"userFirstnameMappingValidationBorder", userFirstnameMappingField,
				new StringResourceModel("label.user.mapping.firstname", this, null),
				new StringResourceModel("help.user.mapping.firstname", this, null));
		form.add(userFirstnameMappingValidationBorder);

		userLastnameMappingField = new TextField<>("userLastnameMapping", new PropertyModel<String>(ldapSettings, "userLastNameMapping"));
		InputBorder<String> userLastnameMappingValidationBorder = new InputBorder<>(
				"userLastnameMappingValidationBorder", userLastnameMappingField,
				new StringResourceModel("label.user.mapping.lastname", this, null),
				new StringResourceModel("help.user.mapping.lastname", this, null));
		form.add(userLastnameMappingValidationBorder);

		userPhotoMappingField = new TextField<>("userPhotoMapping", new PropertyModel<String>(ldapSettings, "userPhotoMapping"));
		InputBorder<String> userPhotoMappingValidationBorder = new InputBorder<>(
				"userPhotoMappingValidationBorder", userPhotoMappingField,
				new StringResourceModel("label.user.mapping.photo", this, null),
				new StringResourceModel("help.user.mapping.photo", this, null));
		form.add(userPhotoMappingValidationBorder);

		IndicatingAjaxButton retrieveUsers = new IndicatingAjaxButton("retrieveUsers") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					ldapUsersList.setHeaderFixed(true);
					manager = ldapUsersList.update(ldapSettings);
					userRetrievalInfo.setDefaultModel(new StringResourceModel("label.retrieval.user.success", this, null, ldapUsersList.getNoOfCurrentlyListedEntities()));
					userRetrievalInfo.add(new CSSAppender("success"));
				} catch (Throwable ex) {
					ldapUsersList.reset();
					logger.warn(ex.getMessage(), ex);
					StringWriter errors = new StringWriter();
					ex.printStackTrace(new PrintWriter(errors));
					userRetrievalInfo.setDefaultModel(Model.of(errors.toString()));
					userRetrievalInfo.add(new CSSAppender("error"));
				}
				target.add(ldapUsersList);
				target.add(userRetrievalInfo);
			}
		};
		form.add(retrieveUsers);
		userRetrievalInfo = new Label("userRetrievalInfo");
		form.add(userRetrievalInfo.setOutputMarkupId(true));

		ldapUsersList = new LdapUserListPanel("usersList", manager);
		form.add(ldapUsersList);

		return form;
	}

	private Form<Void> createGroupsForm(Form<Void> form) {
		groupFilterBaseDNField = new TextField<>("groupFilterBaseDN", new PropertyModel<String>(ldapSettings, "groupFilterBaseDN"));
		InputBorder<String> groupFilterBaseDNValidationBorder = new InputBorder<>(
				"groupFilterBaseDNValidationBorder", groupFilterBaseDNField,
				new StringResourceModel("label.group.filter.baseDN", this, null),
				new StringResourceModel("help.group.filter.baseDN", this, null));
		form.add(groupFilterBaseDNValidationBorder);

		groupFilterField = new TextField<>("groupFilter", new PropertyModel<String>(ldapSettings, "groupFilter"));
		InputBorder<String> groupFilterValidationBorder = new InputBorder<>(
				"groupFilterValidationBorder", groupFilterField,
				new StringResourceModel("label.group.filter", this, null),
				new StringResourceModel("help.group.filter", this, null));
		form.add(groupFilterValidationBorder);

		groupNameMappingField = new TextField<>("groupNameMapping", new PropertyModel<String>(ldapSettings, "groupNameMapping"));
		InputBorder<String> groupNameMappingValidationBorder = new InputBorder<>(
				"groupNameMappingValidationBorder", groupNameMappingField,
				new StringResourceModel("label.group.mapping.name", this, null),
				new StringResourceModel("help.group.mapping.name", this, null));
		form.add(groupNameMappingValidationBorder);

		groupDescriptionMappingField = new TextField<>("groupDescriptionMapping", new PropertyModel<String>(ldapSettings, "groupDescriptionMapping"));
		InputBorder<String> groupDescriptionMappingValidationBorder = new InputBorder<>(
				"groupDescriptionMappingValidationBorder", groupDescriptionMappingField,
				new StringResourceModel("label.group.mapping.description", this, null),
				new StringResourceModel("help.group.mapping.description", this, null));
		form.add(groupDescriptionMappingValidationBorder);

		groupMemberMappingField = new TextField<>("groupMemberMapping", new PropertyModel<String>(ldapSettings, "groupMemberMapping"));
		InputBorder<String> groupMemberMappingValidationBorder = new InputBorder<>(
				"groupMemberMappingValidationBorder", groupMemberMappingField,
				new StringResourceModel("label.group.mapping.member", this, null),
				new StringResourceModel("help.group.mapping.member", this, null));
		form.add(groupMemberMappingValidationBorder);

		IndicatingAjaxButton retrieveGroups = new IndicatingAjaxButton("retrieveGroups") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					ldapGroupList.setHeaderFixed(true);
					manager = ldapGroupList.update(ldapSettings);
					groupRetrievalInfo.setDefaultModel(new StringResourceModel("label.retrieval.group.success", this, null, ldapGroupList.getNoOfCurrentlyListedEntities()));
					groupRetrievalInfo.add(new CSSAppender("success"));
				} catch (Throwable ex) {
					ldapGroupList.reset();
					logger.warn(ex.getMessage(), ex);
					StringWriter errors = new StringWriter();
					ex.printStackTrace(new PrintWriter(errors));
					groupRetrievalInfo.setDefaultModel(Model.of(errors.toString()));
					groupRetrievalInfo.add(new CSSAppender("error"));
				}
				target.add(ldapGroupList);
				target.add(groupRetrievalInfo);
			}
		};
		form.add(retrieveGroups);
		groupRetrievalInfo = new Label("groupRetrievalInfo");
		form.add(groupRetrievalInfo.setOutputMarkupId(true));

		ldapGroupList = new LdapGroupListPanel("groupsList", manager);
		form.add(ldapGroupList);

		return form;
	}
}
