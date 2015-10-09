/*
 */
package eu.uqasar.model.user;

import static javax.persistence.CascadeType.MERGE;
import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.Namable;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.user.TeamMembership;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;


/**
 *
 *
 */
@Entity(name = "Teams")
@XmlRootElement
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@Indexed
public class Team extends AbstractEntity implements Namable {

	@NotNull
	@Size(min = 2)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String name;

	private String description;

	@OneToMany(orphanRemoval = false, mappedBy = "team")
	private Set<TeamMembership> members = new HashSet<>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<TeamMembership> getMembers() {
		return members;
	}

	public void setMembers(Set<TeamMembership> members) {
		this.members = members;
	}

	public Collection<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		for (TeamMembership member : members) {
			users.add(member.getUser());
		}
		return users;
	}
	
	@Override
	public String getUniqueName() {
		return getName();
	}
}
