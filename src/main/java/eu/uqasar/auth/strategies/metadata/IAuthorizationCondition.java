/*
 */
package eu.uqasar.auth.strategies.metadata;

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


import java.io.Serializable;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;

/**
 *
 *
 */
public interface IAuthorizationCondition extends Serializable {

	/**
	 *
	 * @return <code>true</code> if this condition authorizes a specific
	 * {@link Action} for a {@link Component}, <code>false</code> otherwise.
	 */
	public boolean isAuthorized();
}
