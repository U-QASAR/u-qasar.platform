package eu.uqasar.web.pages.tree.panels;

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


import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import eu.uqasar.model.tree.ITreeNode;

public abstract class BaseTreePanel<T extends ITreeNode<String>> extends Panel {

	private static final long serialVersionUID = 8251835089980525461L;

	protected BaseTreePanel(String id, IModel<T> model) {
		super(id, model);
	}

}
