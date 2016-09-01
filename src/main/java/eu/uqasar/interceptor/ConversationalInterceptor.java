package eu.uqasar.interceptor;

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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import eu.uqasar.qualifier.Conversational;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundRequest;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.jboss.weld.context.http.Http;

@Conversational
@Interceptor
public class ConversationalInterceptor {

	@Inject
	@Http
	ConversationContext context;

	@Inject
	@Bound
	BoundConversationContext boundContext;

	@AroundInvoke
	@AroundTimeout
	public Object wrapInConversation(InvocationContext invocation)
			throws Exception {

		BoundRequest storage = null;

		if (!context.isActive() && !boundContext.isActive()) {
			Map<String, Object> session = new HashMap<>();
			Map<String, Object> request = new HashMap<>();
			storage = new MutableBoundRequest(request, session);
			boundContext.associate(storage);
			boundContext.activate();
		}

		try {
			return invocation.proceed();
		} finally {
			if (storage != null) {
				boundContext.deactivate();
				boundContext.dissociate(storage);
			}
		}
	}
}
