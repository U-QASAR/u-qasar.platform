package eu.uqasar.web.pages.admin.companies.panels;

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

/**
 *
 */
public class CompanyFilterStructure implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1306629342832495716L;
	private String name;
	private String shortName;
	private String country;
	
	public CompanyFilterStructure() {

	}

	public CompanyFilterStructure(CompanyFilterPanel panel) {
		this.name = panel.getName();
		this.shortName = panel.getShortName();
		this.country = panel.getCountry();
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "CompanyFilterStructure{" + "name=" + name + "shortName=" + shortName + "country=" + country +"}";
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
