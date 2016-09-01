package eu.uqasar.web.pages.qmtree.qmodels;

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


import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.util.lang.Objects;

import eu.uqasar.service.QMTreeNodeService;

public class QModelFormValidator extends AbstractFormValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8608522031876675892L;
	/** form components to be validated. */
   	private final FormComponent[] components;
   	
   	private final String previousKey;
   	
   	private boolean newEntity;
   	
   	@Inject
	private QMTreeNodeService qmodelService;


   	public QModelFormValidator(FormComponent f1, FormComponent f2, String oldKey, boolean f3) {
  		if (f1 == null) {
     			throw new IllegalArgumentException(
     				"FormComponent1 cannot be null");
  		}
  		if (f2 == null) {
     			throw new IllegalArgumentException(
     				"FormComponent2 cannot be null");
  		}
  		components = new FormComponent[] { f1, f2};
  		
  		newEntity = f3;
  		
  		previousKey = oldKey;
  		
	}
   	
	@Override
	public FormComponent<?>[] getDependentFormComponents() {
		return components;
	}

	@Override
	public void validate(Form<?> form) {
		final FormComponent name = components[0];
   		final FormComponent key = components[1];
   		final boolean isNew = newEntity;
   		final String oldKey = previousKey;
   		
   		Localizer loc = getLocalizer(form);
   		String f1Value = Objects.stringValue(name.getInput(), true);
   		String f2Value = Objects.stringValue(key.getInput(), true);
   		
   		if ("".equals(f1Value)) {
   			name.error(loc.getString("form.name.required", name));
   		} else if (f1Value.length()>255){
   			name.error(loc.getString("form.name.max", name));
   		}
   		if ("".equals(f2Value)) {
   			key.error(loc.getString("form.key.required", key));
   		}
   		
   		List<String> nodeKeyList = qmodelService.getAllNodeKeys();
   		if (!previousKey.equals(f2Value) && !isNew && Collections.frequency(nodeKeyList, f2Value)>=1) {
   			key.error(loc.getString("form.key.repeated", key));
   		} else if (!previousKey.equals(f2Value) && isNew && (Collections.frequency(nodeKeyList, f2Value)>0)){
   			key.error(loc.getString("form.key.repeated", key));
   		}
	}

	private Localizer getLocalizer(Form<?> form)
	{
		return form.getApplication().getResourceSettings().getLocalizer();
	}	

}
