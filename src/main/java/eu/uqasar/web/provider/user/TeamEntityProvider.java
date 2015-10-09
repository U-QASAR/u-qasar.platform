/*
 */
package eu.uqasar.web.provider.user;

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

	Collection<Team> teams;

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
