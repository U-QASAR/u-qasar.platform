package eu.uqasar.web.pages.products.panels;

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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import eu.uqasar.model.product.Product;
import eu.uqasar.service.ProductService;

/**
 * 
 */
public abstract class ProductManagementPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final List<String> orderSelection = Arrays
			.asList("Ascending Date Order",
                    "Descending Date Order");
	public static final String ASCENDING_IS_SELECTED = "Ascending Date Order";
	public static String DESCENDING_IS_SELECTED = "Descending Date Order";

	private String selected = ASCENDING_IS_SELECTED;

	@Inject
	private ProductService productService;
	
	private final DropDownChoice dateOrder;
	private final TextField<String> productName;
	private final DropDownChoice versionChoice;
	private final IndicatingAjaxButton apply, reset;

	private List<String> versionList;

	private String name;
	private String version;
	private Date releaseDate;

	@SuppressWarnings("unchecked")
	public ProductManagementPanel(String id) {
		super(id);

		Form form = new Form<>("form");
		add(form);

		productName = (TextField) new TextField<>("name",
                new PropertyModel(this, "name"))
				.add(new OnChangeAjaxBehavior() {

					@Override
					protected void onUpdate(AjaxRequestTarget target) {

					}

				});
		form.add(productName);

		versionChoice = new DropDownChoice("version.choice",
				new PropertyModel<String>(this, "version"), getVersions()) {
			@Override
			protected void onSelectionChanged(final Object newSelection) {
				selected = (String) newSelection;
			}

			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}

		};
		versionChoice.setNullValid(true);
		form.add(versionChoice);

		dateOrder = new DropDownChoice("date.order", new PropertyModel<String>(
				this, "selected"), orderSelection) {

			final long serialVersionUID = 1L;

			@Override
			protected void onSelectionChanged(final Object newSelection) {

				selected = (String) newSelection;

			}

			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}

		};
		form.add(dateOrder);

		form.add(apply = new IndicatingAjaxButton("apply") {

			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setSelected(dateOrder.getValue());
				ProductManagementPanel.this.applyClicked(target, form);
			}
		});

		form.add(reset = new IndicatingAjaxButton("reset") {
			
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ProductManagementPanel.this.resetClicked(target, form);
				resetForm();
				target.add(form);
			}
		});

	}

	public String getSelected() {
		return this.selected;
	}

	private void setSelected(String selected) {
		this.selected = selected;
	}

	private void resetForm() {
		productName.clearInput();
		productName.setModelObject(null);
		dateOrder.clearInput();
		dateOrder.setModelObject(null);
	}

	public ProductFilterStructure getFilter() {
		return new ProductFilterStructure(this);
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public abstract void applyClicked(AjaxRequestTarget target, Form<?> form);

	public abstract void resetClicked(AjaxRequestTarget target, Form<?> form);

	private List<String> getVersions() {
		
		versionList = new ArrayList<>();
		
		for (Product p : productService.getAll()) {
			if (p.getVersion() != null && !p.getVersion().isEmpty()) {
				versionList.add(p.getVersion());
			}
			
		}
		
		
		return versionList;
	}
	
}
