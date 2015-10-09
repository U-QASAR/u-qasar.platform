package eu.uqasar.model.meta;

import javax.persistence.Entity;

/**
 *
 *
 */
@Entity
public class ProjectType extends MetaData {

    public ProjectType() {
    }

    public ProjectType(String name) {
        super(name);
    }

}
