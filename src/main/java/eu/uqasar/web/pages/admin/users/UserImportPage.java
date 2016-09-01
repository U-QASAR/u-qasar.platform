package eu.uqasar.web.pages.admin.users;

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


import eu.uqasar.exception.auth.register.UserMailAlreadyExistsException;
import eu.uqasar.exception.auth.register.UserNameAlreadyExistsException;
import eu.uqasar.exception.notification.NotificationException;
import eu.uqasar.model.role.Role;
import eu.uqasar.model.settings.ldap.LdapSettings;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.notification.message.auth.register.RegistrationMessageService;
import eu.uqasar.service.settings.LdapSettingsService;
import eu.uqasar.util.ldap.LdapManager;
import eu.uqasar.util.ldap.LdapUser;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.components.AnchorableBookmarkablePageLink;
import eu.uqasar.web.components.CSSAppender;
import eu.uqasar.web.pages.admin.AdminBasePage;
import eu.uqasar.web.pages.admin.settings.LdapSettingsPage;
import eu.uqasar.web.pages.admin.settings.panels.LdapUserListPanel;
import eu.uqasar.web.upload.UserProfilePictureUploadHelper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

/**
 *
 *
 */
public class UserImportPage extends AdminBasePage {

    private static final Logger logger = Logger.getLogger(UserImportPage.class);

    private Label userRetrievalInfo;
    private final SubmitLink importButton;
    private LdapSettings ldapSettings;
    private LdapManager manager;
    private LdapUserListPanel ldapUsersList;
    private final Role userRole = Role.User;
    private final Boolean needToConfirmRegistration = false;

    @Inject
    LdapSettingsService settingsService;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private RegistrationMessageService registrationMessageService;
    
    @Inject
    private UserProfilePictureUploadHelper userPictureHelper;

    public UserImportPage(PageParameters pageParameters) {
        super(pageParameters);
        add(new AnchorableBookmarkablePageLink("change.settings", LdapSettingsPage.class, "userSettings"));
        Form<Void> userForm = new Form("form");
        IndicatingAjaxButton retrieveUsers = new IndicatingAjaxButton("retrieveUsers") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    ldapUsersList.setHeaderFixed(true);
                    ldapUsersList.setSelectionEnabled(true);
                    manager = ldapUsersList.update(ldapSettings);
                    userRetrievalInfo.setDefaultModel(
                            new StringResourceModel("label.retrieval.success", this, null, ldapUsersList.
                                    getNoOfCurrentlyListedEntities()));
                    userRetrievalInfo.add(new CSSAppender("success"));
                } catch (Throwable ex) {
                    ldapUsersList.reset();
                    logger.warn(ex.getMessage(), ex);
                    StringWriter errors = new StringWriter();
                    ex.printStackTrace(new PrintWriter(errors));
                    userRetrievalInfo.setDefaultModel(Model.
                            of(errors.toString()));
                    userRetrievalInfo.add(new CSSAppender("error"));
                }
                target.add(userRetrievalInfo);
                target.add(ldapUsersList);
            }
        };
        userForm.add(retrieveUsers);
        userRetrievalInfo = new Label("userRetrievalInfo");
        userForm.add(userRetrievalInfo.setOutputMarkupId(true));

        importButton = new SubmitLink("import") {
            @Override
            public void onSubmit() {
                List<String> successUsers = new ArrayList<>((int)ldapUsersList.getNoOfCurrentlyListedEntities());
                List<String> errorUsers = new ArrayList<>((int)ldapUsersList.getNoOfCurrentlyListedEntities());
                for (LdapUser user : ldapUsersList.
                        getCurrentlySelectedEntities()) {
                    if(tryToRegister(user)) {
                        successUsers.add(user.getFullName());
                    } else {
                        errorUsers.add(user.getFullName());
                    }
                }
                if(!errorUsers.isEmpty()) {
                    final String users = StringUtils.join(errorUsers, ", ");
                    final String message = new StringResourceModel("message.import.result.error", UserImportPage.this, null, errorUsers.size(), users).getString();
                    error(message);
                }
                if(!successUsers.isEmpty()) {
                    final String users = StringUtils.join(successUsers, ", ");
                    final String message = new StringResourceModel("message.import.result.success", UserImportPage.this, null, successUsers.size(), users).getString();
                    success(message);
                }
            }
        };
        importButton.setOutputMarkupId(true);
        userForm.add(importButton);

        ldapUsersList = new LdapUserListPanel("usersList", manager) {
            @Override
            public void selectionChanged(AjaxRequestTarget target) {
                updateImportButton();
                target.add(importButton);
            }
        };
        userForm.add(ldapUsersList);

        userForm.
                add(new DropDownChoice<>("userRole", new PropertyModel<>(this, "userRole"),
                                Arrays.asList(Role.userAssignableRoles())));
        userForm.
                add(new CheckBox("registrationMail", new PropertyModel<Boolean>(this, "needToConfirmRegistration")));

        add(userForm);
        addOrReplace(feedbackPanel);
    }

    private boolean tryToRegister(LdapUser ldapUser) {
        User newUser = ldapUser.toUser();
        try {
            authenticationService.checkMailAlreadyRegistered(newUser.getMail());
            authenticationService.checkUserNameAlreadyRegistered(newUser.
                    getUserName());
            newUser.setPreferredLocale(UQasar.getSession().getLocale());
            newUser.setRole(this.userRole);

            User registeredUser = authenticationService.
                    registerLdapBasedUser(newUser, needToConfirmRegistration);
            if (registeredUser != null) {
                if(ldapUser.getProfilePicture() != null && ldapUser.getProfilePicture().length > 0) {
                    userPictureHelper.processLdapUserPictureUpload(ldapUser.getProfilePicture(), registeredUser);
                }
                if (needToConfirmRegistration) {
                    registrationMessageService.
                            sendRegistrationConfirmationMessage(registeredUser);
                } else {
                    registrationMessageService.
                            sendRegistrationConfirmedMessage(registeredUser);
                }
            }
            return true;
        } catch (UserMailAlreadyExistsException ex) {
            UQSession.get().error(new StringResourceModel("error.user.exists.mail", this, Model.
                    of(newUser)).getString());
        } catch (UserNameAlreadyExistsException ex) {
            UQSession.get().error(new StringResourceModel("error.user.exists.userName", this, Model.
                    of(newUser)).getString());
        } catch (NotificationException ex) {
            UQSession.get().error(new StringResourceModel("error.user.register.notification", this, Model.
                    of(newUser)).getString());
        }
        return false;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        ldapSettings = LdapSettings.getDefault();
        ldapSettings = settingsService.get(ldapSettings);
        updateImportButton();
    }

    private void updateImportButton() {
        importButton.
                setEnabled(ldapUsersList.getNoOfCurrentlyListedEntities() > 0 && ldapUsersList.
                        getCurrentlySelectedEntities().size() > 0);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forUrl("assets/css/admin/user.css"));
    }
}
