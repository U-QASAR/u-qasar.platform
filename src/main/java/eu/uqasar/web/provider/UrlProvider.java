package eu.uqasar.web.provider;

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


import javax.enterprise.context.ApplicationScoped;
import org.apache.wicket.Page;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;

/**
 *
 *
 */
@ApplicationScoped
public class UrlProvider {

	/**
	 *
	 * @param clazz
	 * @return
	 */
	public String urlFor(final Class<? extends Page> clazz) {
		return urlFor(clazz, null);
	}

	/**
	 *
	 * @param clazz
	 * @param key
	 * @param value
	 * @return
	 */
	public String urlFor(final Class<? extends Page> clazz, final String key,
			final String value) {
		PageParameters parameters = new PageParameters();
		parameters.add(key, value);
		return urlFor(clazz, parameters);
	}

	/**
	 *
	 * @param clazz
	 * @param parameters
	 * @return
	 */
	public String urlFor(final Class<? extends Page> clazz,
			final PageParameters parameters) {
		return RequestCycle.get().getUrlRenderer().renderFullUrl(
				Url.parse(RequestCycle.get().urlFor(clazz, parameters)
						.toString()));
	}

	public String urlFor(final ResourceReference reference, final PageParameters parameters) {
		return RequestCycle.get().getUrlRenderer().renderFullUrl(
				Url.parse(RequestCycle.get().urlFor(reference, parameters)
						.toString()));
	}

}
