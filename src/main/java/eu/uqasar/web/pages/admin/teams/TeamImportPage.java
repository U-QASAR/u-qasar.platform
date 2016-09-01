package eu.uqasar.web.pages.admin.teams;

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
import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.TeamMembership;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.notification.message.auth.register.RegistrationMessageService;
import eu.uqasar.service.settings.LdapSettingsService;
import eu.uqasar.service.user.TeamMembershipService;
import eu.uqasar.service.user.TeamService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.util.ldap.LdapGroup;
import eu.uqasar.util.ldap.LdapManager;
import eu.uqasar.util.ldap.LdapUser;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.components.AnchorableBookmarkablePageLink;
import eu.uqasar.web.components.CSSAppender;
import eu.uqasar.web.pages.admin.AdminBasePage;
import eu.uqasar.web.pages.admin.settings.LdapSettingsPage;
import eu.uqasar.web.pages.admin.settings.panels.LdapGroupListPanel;
import eu.uqasar.web.upload.UserProfilePictureUploadHelper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.naming.NamingException;
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
public class TeamImportPage extends AdminBasePage {

    private static final Logger logger = Logger.getLogger(TeamImportPage.class);

    private Label groupRetrievalInfo;
    private final SubmitLink importButton;
    private LdapSettings ldapSettings;
    private LdapManager manager;
    private LdapGroupListPanel ldapGroupsList;
    private final Role userRole = Role.User;
    private final Role groupRole = Role.User;
    private final Boolean needToConfirmRegistration = false;

    @Inject
    LdapSettingsService settingsService;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private UserService userService;

    @Inject
    private TeamService teamService;

    @Inject
    private TeamMembershipService teamMembershipService;

    @Inject
    private RegistrationMessageService registrationMessageService;

    @Inject
    private UserProfilePictureUploadHelper userPictureHelper;

    public TeamImportPage(PageParameters pageParameters) {
        super(pageParameters);
        add(new AnchorableBookmarkablePageLink("change.group.settings", LdapSettingsPage.class, "groupSettings"));
        add(new AnchorableBookmarkablePageLink("change.user.settings", LdapSettingsPage.class, "userSettings"));

        Form<Void> groupForm = new Form("form");
        IndicatingAjaxButton retrieveGroups = new IndicatingAjaxButton("retrieveGroups") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    ldapGroupsList.setHeaderFixed(true);
                    ldapGroupsList.setSelectionEnabled(true);
                    manager = ldapGroupsList.update(ldapSettings);
                    groupRetrievalInfo.setDefaultModel(
                            new StringResourceModel("label.retrieval.success", this, null, ldapGroupsList.
                                    getNoOfCurrentlyListedEntities()));
                    groupRetrievalInfo.add(new CSSAppender("success"));
                } catch (Throwable ex) {
                    ldapGroupsList.reset();
                    logger.warn(ex.getMessage(), ex);
                    StringWriter errors = new StringWriter();
                    ex.printStackTrace(new PrintWriter(errors));
                    groupRetrievalInfo.setDefaultModel(Model.
                            of(errors.toString()));
                    groupRetrievalInfo.add(new CSSAppender("error"));
                }
                target.add(groupRetrievalInfo);
                target.add(ldapGroupsList);
            }
        };
        groupForm.add(retrieveGroups);
        groupRetrievalInfo = new Label("groupRetrievalInfo");
        groupForm.add(groupRetrievalInfo.setOutputMarkupId(true));

        importButton = new SubmitLink("import") {
            @Override
            public void onSubmit() {
                List<String> successGroups = new ArrayList<>((int) ldapGroupsList.
                        getNoOfCurrentlyListedEntities());
                List<String> errorGroups = new ArrayList<>((int) ldapGroupsList.
                        getNoOfCurrentlyListedEntities());
                for (LdapGroup group : ldapGroupsList.
                        getCurrentlySelectedEntities()) {
                    if (tryToRegister(group)) {
                        successGroups.add(group.getName());
                    } else {
                        errorGroups.add(group.getName());
                    }
                }
                if (!errorGroups.isEmpty()) {
                    final String users = StringUtils.join(errorGroups, ", ");
                    final String message = new StringResourceModel("message.import.result.error", TeamImportPage.this, null, errorGroups.
                            size(), users).getString();
                    error(message);
                }
                if (!successGroups.isEmpty()) {
                    final String users = StringUtils.join(successGroups, ", ");
                    final String message = new StringResourceModel("message.import.result.success", TeamImportPage.this, null, successGroups.
                            size(), users).getString();
                    success(message);
                }
            }
        };
        importButton.setOutputMarkupId(true);
        groupForm.add(importButton);

        ldapGroupsList = new LdapGroupListPanel("groupsList", manager) {
            @Override
            public void selectionChanged(AjaxRequestTarget target) {
                updateImportButton();
                target.add(importButton);
            }
        };
        groupForm.add(ldapGroupsList);

        groupForm.
                add(new DropDownChoice<>("userRole", new PropertyModel<>(this, "userRole"),
                                Arrays.asList(Role.userAssignableRoles())));
        groupForm.
                add(new DropDownChoice<>("groupRole", new PropertyModel<>(this, "groupRole"),
                                Arrays.asList(Role.teamAssignableRoles())));
        groupForm.
                add(new CheckBox("registrationMail", new PropertyModel<Boolean>(this, "needToConfirmRegistration")));

        add(groupForm);
        addOrReplace(feedbackPanel);
    }

    private boolean tryToRegister(LdapGroup ldapGroup) {
        if (!teamService.ldapBasedGroupExists(ldapGroup)) {
            Team team = new Team();
            team.setName(ldapGroup.getName());
            team.setDescription(ldapGroup.getDescription());
            team = teamService.create(team);
            List<LdapUser> members;
            try {
                members = ldapGroup.getMembers();
                for (LdapUser ldapUser : members) {
                    User user = userService.getLdapBasedUser(ldapUser);
                    if (user == null) {
                        user = tryToRegister(ldapUser);
                    }
                    if (user != null) {
                        TeamMembership member = new TeamMembership();
                        member.setUser(user);
                        member.setTeam(team);
                        member.setRole(groupRole);
                        teamMembershipService.create(member);
                    }
                }
            } catch (NamingException ex) {
                java.util.logging.Logger.getLogger(TeamImportPage.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            return true;
        }
        return false;
    }

    private User tryToRegister(LdapUser ldapUser) {
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
                if (ldapUser.getProfilePicture() != null && ldapUser.
                        getProfilePicture().length > 0) {
                    userPictureHelper.processLdapUserPictureUpload(ldapUser.
                            getProfilePicture(), registeredUser);
                }
                if (needToConfirmRegistration) {
                    registrationMessageService.
                            sendRegistrationConfirmationMessage(registeredUser);
                } else {
                    registrationMessageService.
                            sendRegistrationConfirmedMessage(registeredUser);
                }
            }
            return registeredUser;
        } catch (UserMailAlreadyExistsException ex) {
            UQSession.get().
                    error(new StringResourceModel("error.user.exists.mail", this, Model.
                                    of(newUser)).getString());
        } catch (UserNameAlreadyExistsException ex) {
            UQSession.get().
                    error(new StringResourceModel("error.user.exists.userName", this, Model.
                                    of(newUser)).getString());
        } catch (NotificationException ex) {
            UQSession.get().
                    error(new StringResourceModel("error.user.register.notification", this, Model.
                                    of(newUser)).getString());
        }
        return null;
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
                setEnabled(ldapGroupsList.getNoOfCurrentlyListedEntities() > 0 && ldapGroupsList.
                        getCurrentlySelectedEntities().size() > 0);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forUrl("assets/css/admin/user.css"));
    }
}
