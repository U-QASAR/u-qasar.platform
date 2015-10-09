/*
 */
package eu.uqasar.web.pages.errors;

import eu.uqasar.web.pages.BasePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *
 * @param <T>
 */
public class BaseErrorPage<T extends Throwable> extends BasePage {

	public BaseErrorPage(PageParameters parameters) {
		super(parameters);
	}

	public BaseErrorPage(T throwable) {
		this(throwable, new PageParameters());
	}

	public BaseErrorPage(T throwable, PageParameters parameters) {
		super(new PageParameters());
	}

	@Override
	public boolean isVersioned() {
		return false;
	}

	@Override
	public boolean isErrorPage() {
		return true;
	}
}
