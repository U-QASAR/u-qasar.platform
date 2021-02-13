package eu.uqasar.web.pages.products;

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

import java.util.Date;

import javax.inject.Inject;

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

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.product.Product;
import eu.uqasar.service.ProductService;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.pages.BasePage;

public class ProductAddEditPage extends BasePage {

	private static final long serialVersionUID = -6414649996746082119L;

	@Inject
	private ProductService productService;

	private final Form<Product> productForm;

	private final InputBorder<String> nameValidationBorder;
	private final InputBorder<String> descriptionValidationBorder;
	private final InputBorder<String> versionValidationBorder;
	private final DateTextField releaseDateTextField;

	// The Product to edit/save
    private Product product;

	/**
	 * @param parameters
	 */
	public ProductAddEditPage(final PageParameters parameters) {
		super(parameters);

		// extract id parameter and set page title, header and product
		// depending on whether we are editing an existing product or
		// creating
		// a new one
		loadProduct(parameters.get("id"));

		// add form to create new product
		add(productForm = newProductForm());

		// add text field for name inside a border component that performs bean
		// validation
		productForm.add(nameValidationBorder = newNameTextField());

		// add text field for description
		productForm.add(descriptionValidationBorder = newDescriptionTextField());

		// add text field for version
		productForm.add(versionValidationBorder = newVersionTextField());

		// add field for release date
		productForm.add(newReleaseDateTextField(releaseDateTextField = newDateTextField()));

		// add a button to create new product
		productForm.add(newSubmitLink());

		// add cancel button to return to product list page
		productForm.add(newCancelLink());
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
		productService.detach(product);
	}

	/**
	 * 
	 * @param idParam
	 */
    private void loadProduct(final StringValue idParam) {
		// If no ID is provided
		if (idParam.isEmpty()) {
			setPageTitle(new StringResourceModel("page.create.title", this,
					null));
			add(new Label("header", new StringResourceModel(
					"form.create.header", this, null)));
			product = new Product();
		} 
		// If a parameter is provided, attempt to load the product
		else {
			product = productService.getById(idParam.toLong());
			if (product == null) {
				throw new EntityNotFoundException(Product.class, idParam.toOptionalString());
			}

			setPageTitle(new StringResourceModel("page.edit.title", this, null));
			add(new Label("header", new StringResourceModel("form.edit.header",
					this, null)));
		}
	}

	/**
	 * 
	 * @return
	 */
	private Form<Product> newProductForm() {
		Form<Product> form = new InputValidationForm<>("form");
		form.setOutputMarkupId(true);
		return form;
	}

	/**
	 * 
	 * @return
	 */
	private InputBorder<String> newNameTextField() {
		return new OnEventInputBeanValidationBorder<>(
				"nameValidationBorder", new TextField<>("name",
						new PropertyModel<String>(product, "name"))
				.setRequired(true),
				new StringResourceModel("name.input.label", this, null),
				HtmlEvent.ONCHANGE);
	}

	/**
	 * 
	 * @return
	 */
	private InputBorder<String> newDescriptionTextField() {
		return new OnEventInputBeanValidationBorder<>(
				"descriptionValidationBorder", new TextField<>("description",
						new PropertyModel<String>(product, "description")),
				new StringResourceModel("description.input.label", this, null),
				HtmlEvent.ONCHANGE);
	}

	/**
	 * 
	 * @return
	 */
	private InputBorder<String> newVersionTextField() {
		return new OnEventInputBeanValidationBorder<>(
				"versionValidationBorder", new TextField<>("version",
						new PropertyModel<String>(product, "version"))
				.setRequired(true),
				new StringResourceModel("version.input.label", this, null),
				HtmlEvent.ONCHANGE);
	}


	/**
	 * 
	 * @return
	 */
	private InputBorder<Date> newReleaseDateTextField(
			final DateTextField releaseDateTextField) {
		return new OnEventInputBeanValidationBorder<>(
				"releaseDateValidationBorder", releaseDateTextField,
				new StringResourceModel("releasedate.input.label", this, null),
				HtmlEvent.ONCHANGE);
	}

	/**
	 * 
	 * @return
	 */
	private DateTextField newDateTextField() {
		DateTextFieldConfig config = new DateTextFieldConfig()
				.withFormat("dd.MM.yyyy")
				.allowKeyboardNavigation(true).autoClose(true)
				.highlightToday(true).showTodayButton(false);
		DateTextField dateTextField = new DateTextField("releaseDate",
				new PropertyModel<Date>(product, "releaseDate"), config);
		dateTextField.setRequired(true);
		return dateTextField;
	}

	/**
	 * 
	 * @return
	 */
	private AjaxSubmitLink newSubmitLink() {
		return new AjaxSubmitLink("submit", productForm) {

			private static final long serialVersionUID = 6099483467114314555L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				save(target);
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
	private Link<ProductManagementPage> newCancelLink() {
		return new Link<ProductManagementPage>("cancel") {
			private static final long serialVersionUID = -310533532532643267L;

			@Override
			public void onClick() {
				setResponsePage(ProductManagementPage.class,
						new PageParameters().set(
								MESSAGE_PARAM,
								new StringResourceModel("canceled.message", this, Model
										.of(product)).getString()).set(LEVEL_PARAM,
												FeedbackMessage.WARNING));
			}
		};
	}

	/** 
	 * Save product, show message to the user and redirect to the product 
	 * list
	 * @param target
	 */
    private void save(AjaxRequestTarget target) {
		// save product
		saveProduct();
		if (product.getId() == null) {
			// redirect to product list page
			setResponsePage(
					ProductManagementPage.class,
					new PageParameters().set(
							MESSAGE_PARAM,
							new StringResourceModel("add.confirmed", this, Model
									.of(product)).getString()).set(LEVEL_PARAM,
											FeedbackMessage.SUCCESS));
		} else {
			setResponsePage(ProductManagementPage.class,
					new PageParameters().set(MESSAGE_PARAM,
							new StringResourceModel("saved.message", this, Model
									.of(product)).getString()).set(LEVEL_PARAM,
											FeedbackMessage.SUCCESS));
		}
	}

	/**
	 * Save entity
	 */
	private boolean saveProduct() {
		productService.create(product);
		return true;
	}

	/**
	 * 
	 * @param target
	 */
    private void showErrors(AjaxRequestTarget target) {
		// in case of errors (e.g. validation errors) show error
		// messages in form
		target.add(productForm);
	}

	public static PageParameters linkToEdit(Product entity) {
		return linkToEdit(entity.getId());
	}

	private static PageParameters linkToEdit(Long entityId) {
		return new PageParameters().add("id", entityId);
	}

}
