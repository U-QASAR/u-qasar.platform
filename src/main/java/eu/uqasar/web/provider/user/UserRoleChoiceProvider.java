package eu.uqasar.web.provider.user;

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
import java.util.Collection;

import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

import eu.uqasar.model.role.Role;

/**
 *
 *
 */
public class UserRoleChoiceProvider  extends TextChoiceProvider<Role> {


    @Override
    public Collection<Role> toChoices(Collection<String> ids) {    	
    	ArrayList<Role> roles = new ArrayList<>();
    	
    	// TODO: Mapping String to Enum with translation
    	for(String id : ids){
    		switch (id){
    			case "Entwickler": id = "Developer"; break;
    			case "Tester": id = "Tester"; break;
    			case "Anonym": id = "NoRole"; break;
    			case "Benutzer": id = "User"; break;
    			case "Requirements Engineer": id = "ReqsEngineer"; break;
    			case "Design Engineer": id = "DesignEngineer"; break;
    			case "Scrum Master": id = "ScrumMaster"; break;
    			case "Produkt Manager": id = "ProductManager"; break;
    			case "Prozess Manager": id = "ProcessManager"; break;
    		}
    		
    		
    		roles.add(Role.valueOf(id));
    	} 	
    	
    
    	
        return roles;
    }
    
    @Override
	protected String getDisplayText(Role choice) {
	    return choice.name();
    }
	@Override
	protected Object getId(Role choice) {
	    return choice;
	}

	@Override
	public void query(String term, int page, Response<Role> response) {
		response.addAll(Role.getAllRoles());
		response.setHasMore(response.size() == 9);
		
	}
}
