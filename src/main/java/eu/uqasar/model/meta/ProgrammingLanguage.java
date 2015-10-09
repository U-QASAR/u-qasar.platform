package eu.uqasar.model.meta;

import javax.persistence.Entity;

/**
 *
 *
 */
@Entity
public class ProgrammingLanguage extends MetaData {

    public ProgrammingLanguage() {

    }

    public ProgrammingLanguage(final String name) {
        super(name);
    }

}
