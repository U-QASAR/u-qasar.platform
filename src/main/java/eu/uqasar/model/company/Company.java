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

import lombok.Getter;
import lombok.Setter;
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

@Setter
@Getter
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
