package eu.uqasar.web.pages.admin.settings.platform;

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


import javax.inject.Inject;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.settings.platform.PlatformSettings;
import eu.uqasar.service.PlatformSettingsService;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.pages.admin.AdminBasePage;

public class PlatformSettingsAddEditPage extends AdminBasePage {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6712189902433775235L;

    @Inject
    private PlatformSettingsService platformSettingsService;

    private final Form<PlatformSettings> platformSettingsForm;
    
    @SuppressWarnings("unused")
    private final InputBorder<String> keyValidationBorder;

    @SuppressWarnings("unused")
    private final InputBorder<String> valueValidationBorder;

    // The settings to edit/save
    private PlatformSettings platformSettings;

    /**
     * @param parameters
     */
    public PlatformSettingsAddEditPage(final PageParameters parameters) {
        super(parameters);

        // extract id parameter and set page title, header and adapter
        // depending on whether we are editing an existing adapter or
        // creating a new one
        loadSettings(parameters.get("id"));

        // add form to create new adapter
        add(platformSettingsForm = newPlatformSettingsForm());

        // add text field for key inside a border component that performs bean
        // validation
        platformSettingsForm.add(keyValidationBorder = newKeyTextField());

        // add text field for Value inside a border component that performs bean
        // validation
        platformSettingsForm.add(valueValidationBorder = newValueTextField());

        // add a button to create new adapter
        platformSettingsForm.add(newSubmitLink());

        // add cancel button to return to adapter list page
        platformSettingsForm.add(newCancelLink());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.markup.html.WebPage#onAfterRender()
     */
    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        // detach entity to avoid automatic update of changes in form.
        //platformSettingsService.detach(platformSettings);
    }

    /**
     * 
     * @param idParam
     */
    private void loadSettings(final StringValue idParam) {
        if (idParam.isEmpty()) {
            setPageTitle(new StringResourceModel("page.create.title", this, null));
            add(new Label("header", new StringResourceModel("form.create.header", this, null)));
            platformSettings = new PlatformSettings();
        } else {
            platformSettings = platformSettingsService.getById(idParam.toOptionalLong());
            if (platformSettings == null) {
                throw new EntityNotFoundException(PlatformSettings.class, idParam.toOptionalString());
            }

            setPageTitle(new StringResourceModel("page.edit.title", this, null));
            add(new Label("header", new StringResourceModel("form.edit.header", this, null)));
        }
    }

    /**
     * 
     * @return
     */
    private Form<PlatformSettings> newPlatformSettingsForm() {
        Form<PlatformSettings> form = new InputValidationForm<>("form");
        form.setOutputMarkupId(true);
        return form;
    }

    /**
     * 
     * @return
     */
    private InputBorder<String> newKeyTextField() {
        return new OnEventInputBeanValidationBorder<>("keyValidationBorder", new TextField<>("settingKey", new PropertyModel<String>(
            platformSettings, "settingKey")).setRequired(true), new StringResourceModel("key.input.label", this, null),
            HtmlEvent.ONCHANGE);
    }

    /**
     * 
     * @return
     */
    private InputBorder<String> newValueTextField() {
        return new OnEventInputBeanValidationBorder<>("valueValidationBorder", new TextField<>("settingValue",
            new PropertyModel<String>(platformSettings, "settingValue")).setRequired(true), new StringResourceModel(
            "value.input.label", this, null), HtmlEvent.ONCHANGE);
    }

    /**
     * 
     * @return
     */
    private AjaxSubmitLink newSubmitLink() {
        return new AjaxSubmitLink("submit", platformSettingsForm) {

            private static final long serialVersionUID = 6099483467114314555L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                save(target);
                // PlatformSettingsAddEditPage.this.onSubmit(platformSettings, passwordTextField.getModelObject());

            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                showErrors(target);
            }
        };
    }

    /**
     * 
     * @return
     */
    private Link<PlatformSettingsPage> newCancelLink() {
        return new Link<PlatformSettingsPage>("cancel") {

            /**
			 * 
			 */
            private static final long serialVersionUID = 6433075921789830757L;

            @Override
            public void onClick() {
                setResponsePage(PlatformSettingsPage.class);
            }
        };
    }

    /**
     * Save adapter settings, show message to the user and redirect to the adapter list
     * 
     * @param target
     */
    private void save(AjaxRequestTarget target) {

        // Persist the adapter settings
        saveSettings();

        // success message has to be associated to session so that it is shown
        // in the global feedback panel
        Session.get().success(new StringResourceModel("saved.message", this, Model.of(platformSettings)).getString());
        // redirect to adapter list page
        setResponsePage(
            PlatformSettingsPage.class,
            new PageParameters().set(MESSAGE_PARAM,
                new StringResourceModel("saved.message", this, Model.of(platformSettings)).getString()).set(LEVEL_PARAM,
                FeedbackMessage.SUCCESS));
    }

    /**
     * Save entity
     */
    private boolean saveSettings() {
        platformSettingsService.update(platformSettings);
        return true;
    }

    /**
     * 
     * @param target
     */
    private void showErrors(AjaxRequestTarget target) {
        // in case of errors (e.g. validation errors) show error
        // messages in form
        target.add(platformSettingsForm);
    }

    public void onSubmit(PlatformSettings adapterSettings, final String password) {
    }
}
