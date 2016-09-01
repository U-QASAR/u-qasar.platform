/*
 */
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.model.settings.platform.PlatformSettings;
import eu.uqasar.service.PlatformSettingsService;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.admin.AdminBasePage;
import eu.uqasar.web.provider.EntityProvider;

public class PlatformSettingsPage extends AdminBasePage {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Inject
    private PlatformSettingsService settingsService;

    private TextField<String> keyField;

    private final TextField<String> valueField;

    private final Form<Void> form1;

    private final WebMarkupContainer container;
    // private ListView<ProjectSettings> settingsView;
    private final PlatformSettings settings = new PlatformSettings();

    private final CheckGroup<PlatformSettings> settingsGroup;
    private final AjaxSubmitLink deleteSelectedButton;
    private final Modal deleteConfirmationModal;

    // how many adapters do we show per page
    private static final int itemsPerPage = 10;

    public PlatformSettingsPage(PageParameters pageParameters) {
        super(pageParameters);

        final Form<PlatformSettings> form = new Form<>("form");
        add(form);

        // add checkgroup for selecting multiple project-settings
        form.add(settingsGroup = newAdapterSettingsCheckGroup());

        container = new WebMarkupContainer("container", new Model<PlatformSettings>());
        // add the container holding list of existing products
        settingsGroup.add(container.setOutputMarkupId(true));

        container.add(new CheckGroupSelector("adapterGroupSelector", settingsGroup));

        DataView<PlatformSettings> settingsView = new DataView<PlatformSettings>("adapters",
            new ProjectSettingsProvider(), itemsPerPage) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<PlatformSettings> item) {

                final PlatformSettings proposedProjectSetting = item.getModelObject();

                item.add(new Check<>("adapterCheck", item.getModel(), settingsGroup));

                item.add(new Label("settingKey", new PropertyModel<String>(proposedProjectSetting, "settingKey")));

                item.add(new Label("settingValue", new PropertyModel<String>(proposedProjectSetting, "settingValue")));

                // add button to show AddEditPage
                item.add(new BookmarkablePageLink<PlatformSettingsAddEditPage>("edit", PlatformSettingsAddEditPage.class,
                    forSetting(proposedProjectSetting)));

            }

        };

        // add list of adapters to container
        container.add(settingsView);

        // add button to new adapter settings
        container.add(new BookmarkablePageLink<PlatformSettingsAddEditPage>("addAdapterLink", PlatformSettingsAddEditPage.class));

        // add links for table pagination
        container.add(new BootstrapAjaxPagingNavigator("navigatorHead", settingsView));
        container.add(new BootstrapAjaxPagingNavigator("navigatorFoot", settingsView));
        // add button to delete selected adapters
        container.add(deleteSelectedButton = newDeleteSelectedButton(settingsGroup));

        // add confirmation modal for deleting settings
        add(deleteConfirmationModal = newDeleteConfirmationModal());

        form1 = new Form<Void>("form1") {

            @Override
            protected void onSubmit() {
                settingsService.create(new PlatformSettings(settings.getSettingKey(), settings.getSettingValue()));
                keyField.setModelObject(null);
                valueField.setModelObject(null);
            }
        };

        keyField = new TextField<>("keyField", new PropertyModel<String>(settings, "settingKey"));
        keyField.setRequired(true);
        form1.add(keyField);

        valueField = new TextField<>("valueField", new PropertyModel<String>(settings, "settingValue"));
        valueField.setRequired(true);
        form1.add(valueField);

        add(form);
        add(form1);
    }

    /**
     *
     * @return
     */
    private static PageParameters forSetting(PlatformSettings setting) {
        return new PageParameters().set("id", setting.getId());
    }

    /**
     * 
     * @return
     */
    private CheckGroup<PlatformSettings> newAdapterSettingsCheckGroup() {
        CheckGroup<PlatformSettings> checkGroup = new CheckGroup<>("adapterGroup",
                new ArrayList<PlatformSettings>());
        checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                updateDeleteSelectedButton(target);
            }
        });
        return checkGroup;
    }

    /**
     *
     * @param target
     */
    private void updateDeleteSelectedButton(AjaxRequestTarget target) {
        target.add(deleteSelectedButton);
    }

    /**
     *
     * @return
     */
    private AjaxSubmitLink newDeleteSelectedButton(final CheckGroup<PlatformSettings> adapterGroup) {
        return new AjaxSubmitLink("deleteSelected") {
            private static final long serialVersionUID = 1162060284069587067L;

            @Override
            protected void onConfigure() {
                super.onConfigure();
                // only enabled if at least one Product is selected
                if (adapterGroup.getModelObject().isEmpty()) {
                    add(new CssClassNameAppender(Model.of("disabled")) {
                        private static final long serialVersionUID = 5588027455196328830L;

                        // remove css class when component is rendered again
                        @Override
                        public boolean isTemporary(Component component) {
                            return true;
                        }
                    });
                    setEnabled(false);
                } else {
                    setEnabled(true);
                }
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                deleteConfirmationModal.appendShowDialogJavaScript(target);
            }
        };
    }

    /**
     *
     * @return
     */
    private NotificationModal newDeleteConfirmationModal() {
        final NotificationModal notificationModal = new NotificationModal("deleteConfirmationModal", new StringResourceModel(
            "delete.confirmation.modal.header", this, null), new StringResourceModel("delete.confirmation.modal.message", this,
            null), false);
        notificationModal.addButton(new ModalActionButton(notificationModal, Buttons.Type.Primary, new StringResourceModel(
            "delete.confirmation.modal.submit.text", this, null), true) {
            private static final long serialVersionUID = -8579196626175159237L;

            @Override
            protected void onAfterClick(AjaxRequestTarget target) {
                // confirmed --> delete
                deleteSelectedAdapters(settingsGroup.getModelObject(), target);
                // close modal
                closeDeleteConfirmationModal(notificationModal, target);
            }
        });
        notificationModal.addButton(new ModalActionButton(notificationModal, Buttons.Type.Default, new StringResourceModel(
            "delete.confirmation.modal.cancel.text", this, null), true) {
            private static final long serialVersionUID = 8931306355855637710L;

            @Override
            protected void onAfterClick(AjaxRequestTarget target) {
                // Cancel clicked --> do nothing, close modal
                closeDeleteConfirmationModal(notificationModal, target);
            }
        });
        return notificationModal;
    }

    /**
     *
     * @param modal
     * @param target
     */
    private void closeDeleteConfirmationModal(final Modal modal, AjaxRequestTarget target) {
        // close
        modal.appendCloseDialogJavaScript(target);
    }

    /**
     *
     * @param target
     */
    private void deleteSelectedAdapters(Collection<PlatformSettings> adapters, AjaxRequestTarget target) {
        String message = new StringResourceModel("settings.selected.deleted", this, null).getString();

        // Delete the selected adapters
        for (PlatformSettings adapter : adapters) {

            // Delete the measurements belonging to the adapters
            settingsService.delete(settingsService.getById(adapter.getId()));

        }

        getPage().success(message);
        updateFeedbackPanel(target);
        updateAdapterList(target);
        // update the delete button
        settingsGroup.updateModel();
        updateDeleteSelectedButton(target);
    }

    /**
     *
     * @param target
     */
    private void updateFeedbackPanel(AjaxRequestTarget target) {
        target.add(feedbackPanel);
    }

    /**
     *
     * @param target
     */
    private void updateAdapterList(AjaxRequestTarget target) {
        target.add(container);
    }

    private final class ProjectSettingsProvider extends EntityProvider<PlatformSettings> {

        /**
        * 
        */
        private static final long serialVersionUID = -8769895854281922343L;

        @Override
        public Iterator<? extends PlatformSettings> iterator(long first, long count) {
            return settingsService.getAllByAscendingName(Long.valueOf(first).intValue(), Long.valueOf(count).intValue())
                .iterator();
        }

        @Override
        public long size() {
            return settingsService.countAll();
        }
    }

}
