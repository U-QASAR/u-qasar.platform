/*
 */
package eu.uqasar.web;

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


import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.web.pages.errors.EntityNotFoundErrorPage;
import eu.uqasar.web.pages.errors.ErrorPage;
import java.lang.reflect.InvocationTargetException;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

/**
 *
 *
 */
class UQasarExceptionRequestCycleListener extends AbstractRequestCycleListener {

	private final boolean deploymentMode;

	public UQasarExceptionRequestCycleListener(boolean runsInDeployment) {
		this.deploymentMode = runsInDeployment;
	}

	@Override
	public IRequestHandler onException(RequestCycle cycle, Exception ex) {
		if (ex instanceof EntityNotFoundException) {
			return createPageRequestHandler(new PageProvider(new EntityNotFoundErrorPage((EntityNotFoundException) ex)));
		} else if (ex.getCause() instanceof InvocationTargetException) {
			Throwable targetException = ((InvocationTargetException) ex.getCause()).getTargetException();
			if (targetException instanceof EntityNotFoundException) {
				return createPageRequestHandler(new PageProvider(new EntityNotFoundErrorPage((EntityNotFoundException) targetException)));
			}
		}
		if (deploymentMode) {
			return createPageRequestHandler(new PageProvider(new ErrorPage(ex)));
		} else {
			return super.onException(cycle, ex);
		}
	}

	private boolean exceptionCausedBy(Exception ex, Class<? extends Throwable> cc) {
		if (cc.isInstance(ex)) {
			return true;
		}
		if (ex.getCause() instanceof InvocationTargetException) {
			Throwable targetException = ((InvocationTargetException) ex.getCause()).getTargetException();
			return cc.isInstance(targetException);
		}
		return false;
	}

	private RenderPageRequestHandler createPageRequestHandler(PageProvider pageProvider) {
		RequestCycle requestCycle = RequestCycle.get();

		if (requestCycle == null) {
			throw new IllegalStateException("there is no current request cycle attached to this thread");
		}

		/*
		 * Use NEVER_REDIRECT policy to preserve the original page's URL for
		 * non-Ajax requests and always redirect for ajax requests
		 */
		RenderPageRequestHandler.RedirectPolicy redirect = RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT;

		if (isProcessingAjaxRequest()) {
			redirect = RenderPageRequestHandler.RedirectPolicy.AUTO_REDIRECT;
		}

		return new RenderPageRequestHandler(pageProvider, redirect);
	}

	private boolean isProcessingAjaxRequest() {
		RequestCycle rc = RequestCycle.get();
		Request request = rc.getRequest();
		if (request instanceof WebRequest) {
			return ((WebRequest) request).isAjax();
		}
		return false;
	}
}
