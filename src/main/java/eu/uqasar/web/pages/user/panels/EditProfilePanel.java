/*
 */
package eu.uqasar.web.pages.user.panels;

import eu.uqasar.model.user.User;
import eu.uqasar.service.user.UserService;
import javax.inject.Inject;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 *
 */
public abstract class EditProfilePanel extends Panel {

	private final Form<Void> form;
	private final User user;

    private final UserProfilePanel userProfilePanel;
    private final UserPicturePanel userPicturePanel;
    private final UserSkillsPanel userSkillsPanel;

	@Inject
	private UserService userService;

	public EditProfilePanel(final String markupId, final Long userId) {
		super(markupId);

		if (userId != null) {
			this.user = userService.getById(userId);
		} else {
			this.user = new User();
		}

		form = new Form<Void>("form") {
			@Override
			protected void onSubmit() {
				EditProfilePanel.this.onSubmit(user, 
                        userProfilePanel.getPassword(), 
                        userProfilePanel.getPasswordConfirmation());
			}
		};
		form.setOutputMarkupId(true);
		form.add(new SubmitLink("submitForm"));
        form.add(new SubmitLink("cancel"){
            @Override
            public void onSubmit() {
                EditProfilePanel.this.onCancel();
            }
        }.setDefaultFormProcessing(false));
        
        form.add(userProfilePanel = new UserProfilePanel("userPanel", user));
        form.add(userPicturePanel = new UserPicturePanel("picturePanel", user));
        form.add(userSkillsPanel = new UserSkillsPanel("skillsPanel", user));
		add(form);
	}
    
	public abstract void onSubmit(User user, final String passsword, final String passwordConfirmation);
    
    public abstract void onCancel();

	@Override
	protected void onAfterRender() {
		super.onAfterRender();
		// detach entity to avoid automatic update of changes in form.
		userService.detach(user);
	}
}
