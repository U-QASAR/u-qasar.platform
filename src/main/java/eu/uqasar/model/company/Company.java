package eu.uqasar.model.company;

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


import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

import eu.uqasar.model.AbstractEntity;

@Entity(name = "Company")
@XmlRootElement
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
@Indexed
public class Company extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2670989347675539224L;
	
	
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String name;
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String shortName;
	private String street;
	private int zipcode;
	private String city;
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String country;	
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String phone;
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String fax;
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}
	
	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public int getZipcode() {
		return zipcode;
	}

	public void setZipcode(int zipcode) {
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	/**
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}
	
	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}
	
	public Company() {
		this.name = null;
		this.shortName = null;
	}
	
	public Company(String name)  {
		this.name = name;
		this.shortName = null;
	}
	
	public Company(String name, String shortName) {
		this.name = name;
		this.shortName = shortName;		
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
