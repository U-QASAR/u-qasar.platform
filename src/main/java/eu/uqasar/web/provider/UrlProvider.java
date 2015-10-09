package eu.uqasar.web.provider;

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
