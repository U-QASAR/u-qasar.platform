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


import java.io.IOException;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;

import eu.uqasar.model.user.User;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.upload.Gravatar;
import eu.uqasar.web.upload.UserProfilePictureUploadHelper;

/**
 *
 *
 */
public class UserPicturePanel extends Panel {

    private final Form<Void> fileUploadForm;
	private final Image userPhoto;

	private final static int MAX_SIZE = 10240;
    private final User user;
	private final FileUploadField uploadField;

    @Inject
	private UserService userService;

	@Inject
	private UserProfilePictureUploadHelper uploadHelper;
	
	@Inject
	private Gravatar gv;
    
    public UserPicturePanel(final String id, final User usr) {
        super(id);
        this.user = usr;
        
        // Form for uploading profile images
		fileUploadForm = new Form<>("fileUploadForm");
		fileUploadForm.setOutputMarkupId(true);

		// User image
		userPhoto = new NonCachingImage("userImage", user.getProfilePicture(User.PictureDimensions.Large));
		userPhoto.setOutputMarkupId(true);
		fileUploadForm.add(userPhoto);
		uploadField = new FileUploadField("fileUploadField");
		fileUploadForm.add(uploadField.setOutputMarkupId(true));
		fileUploadForm.setMaxSize(Bytes.kilobytes(MAX_SIZE));
		fileUploadForm.add(new Label("max", Model.of(Bytes.kilobytes(MAX_SIZE))));
		fileUploadForm.add(new AjaxSubmitLink("submit") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// Check whether an image has been provided to be uploaded
				FileUpload fileUpload = uploadField.getFileUpload();

				if (fileUpload != null) {
					try {
						uploadHelper.processUserFileUpload(fileUpload, user);
						userPhoto.setImageResource(user.getProfilePicture(User.PictureDimensions.Large));
						uploadField.clearInput();
						target.add(uploadField);
						target.add(userPhoto);
					} catch (IOException e) {
						getPage().error(e.getLocalizedMessage());
						target.add(((BasePage) getPage()).getFeedbackPanel());
					}
				}
			}
		}
		);
		
		
		fileUploadForm.add(new AjaxSubmitLink("clear") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				uploadHelper.removeUserPictures(user);
				target.add(userPhoto);
			}
		});
		fileUploadForm.add(new UploadProgressBar("progress", fileUploadForm, uploadField));
		add(fileUploadForm);

		
		
		fileUploadForm.add(new AjaxSubmitLink("uploadGravatar") {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				
				// Attempt to download image from gravatar
				try {
					byte[] rawImage = gv.download(user.getMail());
					if (rawImage != null) {
						uploadHelper.processUserFileUpload(rawImage, user);
						userPhoto.setImageResource(user.getProfilePicture(User.PictureDimensions.Large));
						uploadField.clearInput();
						target.add(uploadField);
						target.add(userPhoto);
					}
				} catch (IOException e) {
					uploadHelper.removeUserPictures(user);
					target.add(userPhoto);
				}					
			}
		});
    }
}
