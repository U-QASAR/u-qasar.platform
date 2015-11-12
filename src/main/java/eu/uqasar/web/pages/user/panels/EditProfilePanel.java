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
