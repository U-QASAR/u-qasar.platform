package eu.uqasar.web.pages.admin.meta;

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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import eu.uqasar.model.meta.MetaData;
import eu.uqasar.service.meta.MetaDataService;
import eu.uqasar.web.components.Effects;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.InputBeanValidationBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.components.StyledFeedbackPanel;

/**
 *
 *
 * @param <T>
 * @param <S>
 */
class MetaDataEditPanel<T extends MetaData, S extends MetaDataService<T>> extends Panel {

    private final S service;
    
    private final Class<T> clazz;
    private final TextField<String> nameField;
    private final CheckGroup<T> checkGroup;
    private final DataView<T> existingList;
    private final IndicatingAjaxButton deleteSelectedButton;
    private final Form<T> existingListForm;
    private final NotificationModal deleteConfirmationModal;
    private T metaData;

    public MetaDataEditPanel(String id, Class<T> clazz, S service, final StyledFeedbackPanel feedbackPanel) {
        super(id);
        this.clazz = clazz;
        this.service = service;
        metaData = MetaData.newInstance(clazz);
        add(new Label("head.type.add", MetaData.getLabelForNew(this.clazz)));
        Form<Void> form = new InputValidationForm<>("form");
        nameField = new TextField<>("name", new PropertyModel<String>(metaData, "name"));
        InputBeanValidationBorder<String> nameValidationBorder = new OnEventInputBeanValidationBorder<>(
                "nameValidationBorder", nameField, MetaData.
                getLabelModelForNew(this.clazz), HtmlEvent.ONBLUR);
        form.add(nameValidationBorder);
        AjaxSubmitLink addObject = new AjaxSubmitLink("add") {

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
                target.add(form);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                addNewMetaData(target, form);
            }
        };
        nameValidationBorder.add(addObject);
        add(form);

        existingListForm = new InputValidationForm<>("form.existing");
        existingListForm.add(new Label("head.type.existing", MetaData.
                getLabelForExisting(this.clazz)));

        existingList = getListing();
        existingList.setItemReuseStrategy(ReuseIfModelsEqualStrategy.
                getInstance());
        existingListForm.add(checkGroup = newCheckGroup());
        final WebMarkupContainer existingItemsContainer = new WebMarkupContainer("dataContainer");
        existingItemsContainer.add(new Label("table.head.type", MetaData.
                getLabel(this.clazz)));
        checkGroup.add(existingItemsContainer.setOutputMarkupId(true));
        CheckGroupSelector checkGroupSelector = new CheckGroupSelector("dataGroupSelector", checkGroup);
        existingItemsContainer.add(checkGroupSelector);
        existingItemsContainer.add(existingList);
        existingItemsContainer.
                add(deleteSelectedButton = newDeleteSelectedButton(checkGroup));
        existingItemsContainer.add(new IndicatingAjaxButton("update") {
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
                target.add(existingItemsContainer);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                updateExistingMetaData(target, form);
                Effects.replaceWithFading(target, existingItemsContainer);
            }
        });
        add(existingListForm);
        add(deleteConfirmationModal = newDeleteConfirmationModal());
    }
    
    private void addNewMetaData(AjaxRequestTarget target, Form<?> form) {
        service.create(metaData);
        
        metaData = MetaData.newInstance(clazz);
        nameField.clearInput();
        nameField.setModel(new PropertyModel<String>(metaData, "name"));
        target.add(form);
        Effects.replaceWithFading(target, existingListForm);
    }

    private void updateExistingMetaData(AjaxRequestTarget target, Form<?> form) {
    	String message = new StringResourceModel("update.confirmed", this, null).getString();
        Iterator<Item<T>> items = existingList.getItems();
        while (items.hasNext()) {
            T item = items.next().getModelObject();
            service.update(item);
        }
        getPage().success(message);
    	setResponsePage(getPage());
    }

    private CheckGroup<T> newCheckGroup() {
        CheckGroup<T> group = new CheckGroup<>("checkGroup", new ArrayList<T>());
        group.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(deleteSelectedButton);
            }
        });
        return group;
    }

    private DataView<T> getListing() {
        return new DataView<T>("data", getMetaDataProvider(this.clazz)) {
            @Override
            protected void populateItem(Item<T> item) {
                Check<T> check = new Check<>("check", item.getModel(), checkGroup);
                item.add(check);
                TextField nameField = new TextField("name", new PropertyModel(item.
                        getModel(), "name"));
                item.
                        add(new InputBeanValidationBorder<>("nameValidationBorder", nameField));
                item.add(newIsInUseIndicator("inUse", item.getModelObject()));
            }
        };
    }

    private WebMarkupContainer newIsInUseIndicator(final String id, final T object) {
        return new AjaxLazyLoadPanel(id) {
            @Override
            public Component getLazyLoadComponent(String markupId) {
                boolean inUse = service.isInUse(object);
                return new MetaDataInUseIndicatorPanel(markupId, inUse, Model.of(object.getName()));
            }
        };
    }
    
    private IndicatingAjaxButton newDeleteSelectedButton(
            final CheckGroup<T> checkGroup) {
        IndicatingAjaxButton submitLink = new IndicatingAjaxButton("deleteSelected") {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                if (checkGroup.getModelObject().isEmpty()) {
                    add(new CssClassNameAppender(Model.of("disabled")) {
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
                deleteConfirmationModal.message(getDeletionModalMessage());
                target.add(deleteConfirmationModal);
                deleteConfirmationModal.appendShowDialogJavaScript(target);
            }
        };
        submitLink.setOutputMarkupId(true);
        return submitLink;
    }
    
    private IModel<String> getSelectedForDeletion() {
       return Model.of(join(checkGroup.getModelObject()));
    }
    
    private String join(Collection<T> items) {
        StringBuilder builder = new StringBuilder();
        if(!items.isEmpty()) {
            Iterator<T> iter = items.iterator();
            builder.append("<em>");
            while (iter.hasNext()) {
                builder.append(iter.next().getName());
                if (!iter.hasNext()) {
                    break;
                }
                builder.append(", ");
            }
            builder.append("</em>");
        }
        return builder.toString();
    }

    private IModel<String> getDeletionModalMessage() {
        final IModel<String> selected = MetaData.getLabelModelForSelected(this.clazz);
        return new StringResourceModel(
                "delete.confirmation.modal.message", this, null, selected, getSelectedForDeletion());
    }
    
    private NotificationModal newDeleteConfirmationModal() {
        final IModel<String> selected = MetaData.getLabelModelForSelected(this.clazz);
        final IModel<String> header = new StringResourceModel(
                "delete.confirmation.modal.header", this, null, selected);

        final NotificationModal notificationModal = new NotificationModal(
                "deleteConfirmationModal", header, getDeletionModalMessage(), false);

        notificationModal.addButton(new ModalActionButton(notificationModal,
                Buttons.Type.Primary, new StringResourceModel(
                        "delete.confirmation.modal.submit.text", this, null),
                true) {

                    @Override
                    protected void onAfterClick(AjaxRequestTarget target) {
                        // confirmed --> delete
                    	String message = new StringResourceModel("delete.confirmed", this, null).getString();
                        Collection<T> dataToBeRemoved = checkGroup.
                        getModelObject();
                        if (!dataToBeRemoved.isEmpty()) {
                            for (T entity : dataToBeRemoved) {
                            	service.delete(entity);
                            }
                        	getPage().success(message);
                        	setResponsePage(getPage());
                        }
                        // close modal
                        closeDeleteConfirmationModal(notificationModal, target);
                        Effects.replaceWithFading(target, existingListForm);
                    }
                });
        notificationModal.addButton(new ModalActionButton(notificationModal,
                Buttons.Type.Default, new StringResourceModel(
                        "delete.confirmation.modal.cancel.text", this, null),
                true) {
                    @Override
                    protected void onAfterClick(AjaxRequestTarget target) {
                        // Cancel clicked --> do nothing, close modal
                        closeDeleteConfirmationModal(notificationModal, target);
                    }
                });
        return notificationModal;
    }

    private void closeDeleteConfirmationModal(final Modal modal,
            AjaxRequestTarget target) {
        modal.appendCloseDialogJavaScript(target);
    }

    private IDataProvider<T> getMetaDataProvider(final Class<T> clazz) {
        return new IDataProvider<T>() {

            @Override
            public Iterator<T> iterator(long first, long count) {
                return service.
                        getAllAscendingByName(clazz, first, count).iterator();
            }

            @Override
            public long size() {
                return service.countAll(clazz);
            }

            @Override
            public IModel<T> model(T object) {
                return Model.of(object);
            }

            @Override
            public void detach() {

            }
        };
    }
}
