/*
 */
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


import eu.uqasar.model.user.Team;
import eu.uqasar.service.user.TeamService;
import eu.uqasar.web.provider.EntityProvider;
import java.util.Collection;
import java.util.Iterator;
import javax.inject.Inject;

/**
 *
 *
 */
public class TeamEntityProvider extends EntityProvider<Team> {

	@Inject
	TeamService teamService;

	private Collection<Team> teams;

	public TeamEntityProvider() {
		this.teams = teamService.getAll();
	}
	
	public void update() {
		this.teams = teamService.getAll();
	}

	public TeamEntityProvider(Collection<Team> teams) {
		this.teams = teams;
	}

	@Override
	public Iterator<? extends Team> iterator(long first, long count) {
		return teams.iterator();
	}

	@Override
	public long size() {
		return teams.size();
	}
}
