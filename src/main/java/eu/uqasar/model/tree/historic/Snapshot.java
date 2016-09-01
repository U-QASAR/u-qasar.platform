package eu.uqasar.model.tree.historic;

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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.tree.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Snapshot extends AbstractEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4665534074086875768L;

	// Snapshot time stamp
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date date;

	// Values Last update date
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date lastUpdate;
	
	// Name of the snapshot provided by user
	@NotNull
	@Size(min = 2, max = 1024)
	private String name;

	// Project snapshot owner
	@ManyToOne
	private Project project;
	
	public Snapshot(String name) {
		super();
		
		this.name = name;
		date = new Date();
		
	}

	/**
	 * @return formated date ready to be printed without milliseconds
	 */
    private String getFormatedDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return  " [" + getFormatedDate() + "] " + name;
	}
	
}
