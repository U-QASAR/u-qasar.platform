/*
 */
package eu.uqasar.web.components;

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


import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.StringAutoCompleteRenderer;
import org.apache.wicket.model.IModel;

/**
 *
 *
 * @param <T>
 */
public abstract class IndicatingAjaxAutoCompleteTextField<T> extends AutoCompleteTextField<T> implements IAjaxIndicatorAware {

	private final AjaxIndicatorAppender indicator = new AjaxIndicatorAppender();

	/**
	 * Constructor for the given type with default settings.
	 *
	 * @param id component id
	 * @param type model objec type
	 */
	public IndicatingAjaxAutoCompleteTextField(final String id, final Class<T> type) {
		this(id, null, type, new AutoCompleteSettings());
	}

	/**
	 * Constructor for the given model and type.
	 *
	 * @param id component id
	 * @param model model
	 * @param type model object type
	 * @param settings settings for autocomplete
	 */
	@SuppressWarnings("unchecked")
    private IndicatingAjaxAutoCompleteTextField(final String id, final IModel<T> model, final Class<T> type,
                                                final AutoCompleteSettings settings) {
		this(id, model, type, StringAutoCompleteRenderer.INSTANCE, settings);
	}

	/**
	 * Constructor for given model.
	 *
	 * @param id component id
	 * @param model model
	 * @param settings settings for autocomplete
	 */
    private IndicatingAjaxAutoCompleteTextField(final String id, final IModel<T> model,
                                                final AutoCompleteSettings settings) {
		this(id, model, null, settings);
	}

	/**
	 * Constructor for the given model.
	 *
	 * @param id component id
	 * @param model model
	 */
	public IndicatingAjaxAutoCompleteTextField(final String id, final IModel<T> model) {
		this(id, model, null, new AutoCompleteSettings());
	}

	/**
	 * Constructor.
	 *
	 * @param id component id
	 * @param settings settings for autocomplete
	 */
	public IndicatingAjaxAutoCompleteTextField(final String id, final AutoCompleteSettings settings) {
		this(id, null, settings);
	}

	/**
	 * Constructor.
	 *
	 * @param id component id
	 */
	public IndicatingAjaxAutoCompleteTextField(final String id) {
		this(id, null, new AutoCompleteSettings());
	}

	/**
	 * Constructor using the given renderer.
	 *
	 * @param id component id
	 * @param renderer renderer for autocomplete
	 */
	public IndicatingAjaxAutoCompleteTextField(final String id, final IAutoCompleteRenderer<T> renderer) {
		this(id, (IModel<T>) null, renderer);
	}

	/**
	 * Constructor for the given type using the given renderer
	 *
	 * @param id component id
	 * @param type model object type
	 * @param renderer renderer for autocomplete
	 */
	public IndicatingAjaxAutoCompleteTextField(final String id, final Class<T> type,
			final IAutoCompleteRenderer<T> renderer) {
		this(id, null, type, renderer, new AutoCompleteSettings());
	}

	/**
	 * Constructor for the given model using the given renderer.
	 *
	 * @param id component id
	 * @param model model
	 * @param renderer renderer for autocomplete
	 */
    private IndicatingAjaxAutoCompleteTextField(final String id, final IModel<T> model,
                                                final IAutoCompleteRenderer<T> renderer) {
		this(id, model, null, renderer, new AutoCompleteSettings());
	}

	/**
	 * Constructor for the given model using the given renderer.
	 *
	 * @param id component id
	 * @param model model
	 * @param type model object type
	 * @param renderer renderer for autocomplete
	 * @param settings settings for autocomplete
	 */
	public IndicatingAjaxAutoCompleteTextField(final String id, final IModel<T> model, final Class<T> type,
			final IAutoCompleteRenderer<T> renderer, final AutoCompleteSettings settings) {
		super(id, model, type, renderer, settings);
		add(indicator);
	}

	@Override
	public String getAjaxIndicatorMarkupId() {
		return indicator.getMarkupId();
	}
}
