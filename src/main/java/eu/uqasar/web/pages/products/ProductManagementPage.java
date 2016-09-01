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


import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.model.product.Product;
import eu.uqasar.service.ProductService;
import eu.uqasar.web.components.JSTemplates;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.products.panels.ProductFilterStructure;
import eu.uqasar.web.pages.products.panels.ProductManagementPanel;
import eu.uqasar.web.provider.EntityProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ProductManagementPage extends BasePage {

	private static final long serialVersionUID = 4481705800235759540L;

	@Inject
	private ProductService productService;

	private final ProductManagementPanel filterPanel;

	private ProductFilterStructure productFilterStructure;

	// how many products do we show per page
	private static final int itemsPerPage = 10;

	// container holding the list of products
	private final WebMarkupContainer productsContainer = newProductsContainer();

	private final Modal deleteConfirmationModal;

	private final CheckGroup<Product> productGroup;

	private final ProductProvider productProvider;

	private final AjaxSubmitLink deleteSelectedButton;

	/**
	 * Constructor building the page
	 *
	 * @param parameters
	 */
	public ProductManagementPage(final PageParameters parameters) {

		super(parameters);

		productProvider = new ProductProvider();

		final Form<Product> deleteForm = new Form<>("deleteForm");
		add(deleteForm);

		filterPanel = new ProductManagementPanel("filter") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3833651464215590534L;

			@Override
			public void resetClicked(AjaxRequestTarget target, Form<?> form) {
				productProvider.setFilter(productFilterStructure);
				target.add(productsContainer);
			}

			@Override
			public void applyClicked(AjaxRequestTarget target, Form<?> form) {
				productProvider.setFilter(this.getFilter());
				target.add(productsContainer);

			}
		};
		deleteForm.add(filterPanel);

		productFilterStructure = new ProductFilterStructure(filterPanel);

		// add checkgroup for selecting multiple products
		deleteForm.add(productGroup = newProductCheckGroup());

		// add the container holding list of existing products
		productGroup.add(productsContainer.setOutputMarkupId(true));

		productsContainer.add(new CheckGroupSelector(
				"productGroupSelector", productGroup));

		DataView<Product> products = new DataView<Product>("products",
				productProvider, itemsPerPage) {

					private static final long serialVersionUID = 789669450347695209L;

					@Override
					protected void populateItem(final Item<Product> item) {
						final Product Product = item.getModelObject();

						item.add(new Check<>("productCheck", item
										.getModel(), productGroup));

						item.add(new Label("name", new PropertyModel<String>(
												Product, "name")));

						item.add(new Label("description", new PropertyModel<String>(
												Product, "description")));

						item.add(new Label("version", new PropertyModel<String>(
												Product, "version")));

						item.add(new DateLabel("releaseDate", new PropertyModel<Date>(
												Product, "releaseDate"), new PatternDateConverter(
												"dd.MM.yyyy", true)));

						// add button to show AddEditPage
						item.add(new BookmarkablePageLink<ProductAddEditPage>(
										"edit", ProductAddEditPage.class,
										ProductAddEditPage.linkToEdit(Product)));
					}
				};
		// add list of products to container
		productsContainer.add(products);

		// add button to create new Product
		productsContainer
				.add(new BookmarkablePageLink<ProductAddEditPage>(
								"addProductLink", ProductAddEditPage.class));

		// add links for table pagination
		productsContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorHead", products));
		productsContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorFoot", products));

		// add button to delete selected products
		productsContainer
				.add(deleteSelectedButton = newDeleteSelectedButton(productGroup));

		// add confirmation modal for deleting products
		add(deleteConfirmationModal = newDeleteConfirmationModal());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.uqasar.web.BasePage#getPageTitleModel()
	 */
	@Override
	protected IModel<String> getPageTitleModel() {
		return new StringResourceModel("page.title", this, null);
	}

	/**
	 *
	 * @return
	 */
	private WebMarkupContainer newProductsContainer() {
		return new WebMarkupContainer("productsContainer") {

			private static final long serialVersionUID = -6725820191388731244L;

			@Override
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				// add javascript to load tagsinput plugin
				response.render(OnLoadHeaderItem.forScript(String.format(
						JSTemplates.LOAD_TABLE_SORTER, "Product-list")));
			}
        }

	;
	}

	/**
	 * 
	 * @return
	 */
	private CheckGroup<Product> newProductCheckGroup() {
		CheckGroup<Product> checkGroup = new CheckGroup<>(
                "productGroup", new ArrayList<Product>());
		checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

			private static final long serialVersionUID = -6392535303739708646L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateDeleteSelectedButton(target);
			}
		});
		return checkGroup;
	}

	/**
	 *
	 * @param productGroup
	 * @return
	 */
	private AjaxSubmitLink newDeleteSelectedButton(
			final CheckGroup<Product> productGroup) {
		return new AjaxSubmitLink("deleteSelected") {
			private static final long serialVersionUID = 1162060284069587067L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				// only enabled if at least one Product is selected
				if (productGroup.getModelObject().isEmpty()) {
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
		final NotificationModal notificationModal = new NotificationModal(
				"deleteConfirmationModal", new StringResourceModel(
						"delete.confirmation.modal.header", this, null),
				new StringResourceModel("delete.confirmation.modal.message",
						this, null), false);
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Primary, new StringResourceModel(
						"delete.confirmation.modal.submit.text", this, null),
				true) {
					private static final long serialVersionUID = -8579196626175159237L;

					@Override
					protected void onAfterClick(AjaxRequestTarget target) {
						// confirmed --> delete
						deleteSelectedproducts(productGroup.getModelObject(),
								target);
						// close modal
						closeDeleteConfirmationModal(notificationModal, target);
					}
				});
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Default, new StringResourceModel(
						"delete.confirmation.modal.cancel.text", this, null),
				true) {
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
	 * @param products
	 * @param target
	 */
	private void deleteSelectedproducts(
			Collection<Product> products, AjaxRequestTarget target) {
		String message = new StringResourceModel("product.selected.deleted",
				this, null).getString();
		productService.delete(products);
		getPage().success(message);
		updateFeedbackPanel(target);
		updateProductList(target);
		// update the delete button
		productGroup.updateModel();
		updateDeleteSelectedButton(target);
	}

	/**
	 *
	 * @param modal
	 * @param target
	 */
	private void closeDeleteConfirmationModal(final Modal modal,
			AjaxRequestTarget target) {
		// close
		modal.appendCloseDialogJavaScript(target);
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
	 * @param target
	 */
	private void updateProductList(AjaxRequestTarget target) {
		target.add(productsContainer);
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
	 * @return
	 */
	private static PageParameters forProduct(Product product) {
		return new PageParameters().set("id", product.getId());
	}

	private final class ProductProvider extends EntityProvider<Product> {

		private static final long serialVersionUID = 1360608566900210699L;

		private ProductFilterStructure filter;

		public ProductProvider() {

		}

        public void setFilter(ProductFilterStructure filter) {
			this.filter = filter;
		}

		@Override
		public Iterator<? extends Product> iterator(long first, long count) {

			if (filterPanel.getSelected() == ProductManagementPanel.ASCENDING_IS_SELECTED) {
				return productService.getAllByAscendingNameFiltered(filter,
						Long.valueOf(first).intValue(),
						Long.valueOf(count).intValue()).iterator();
			} else {
				return productService.getAllByDescendingNameFiltered(filter,
						Long.valueOf(first).intValue(),
						Long.valueOf(count).intValue()).iterator();
			}
			// else
			// return productService.getAllByAscendingName(
			// Long.valueOf(first).intValue(),
			// Long.valueOf(count).intValue()).iterator();

		}

		@Override
		public long size() {
			return productService.countAll();
		}
	}
}
