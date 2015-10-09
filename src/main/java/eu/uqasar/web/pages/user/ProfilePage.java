/*
 */

package eu.uqasar.web.pages.user;

import eu.uqasar.model.user.User;
import static eu.uqasar.model.user.UserSource.UQASAR;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.user.panels.EditProfilePanel;
import javax.inject.Inject;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *
 */
public class ProfilePage extends BasePage {

	@Inject
	UserService userService;
	
	@Inject
	AuthenticationService authService;
	
	public ProfilePage(PageParameters parameters) {
		super(parameters);
		
		final User user = UQSession.get().getLoggedInUser();
		userService.detach(user);
		EditProfilePanel editPanel = new EditProfilePanel("edit.profile", user.getId()) {

			@Override
			public void onSubmit(User user, final String password, final String passwordConfirmation) {
				if(user.getSource() == UQASAR){
                    if(authService.checkNonEmptyPasswords(password, passwordConfirmation)) {
                    	// User wants to change his/her password!
                        if(authService.checkPasswordsEqual(password, passwordConfirmation)) {
                            authService.updateUserPassword(user, password, passwordConfirmation);
                            userService.update(user);
                            ProfilePage.this.success(new StringResourceModel("password.changed", this, null).getString());
                        } else {
                            error(new StringResourceModel("confirmPassword.EqualPasswordInputValidator", this, null));
                        }
                    } else {
                        userService.update(user);
                    }
                } else {
                    userService.update(user);
                }
			}

            @Override
            public void onCancel() {
                // TODO what todo on cancel?!
            }
		};
		add(editPanel);
	}
	
}
