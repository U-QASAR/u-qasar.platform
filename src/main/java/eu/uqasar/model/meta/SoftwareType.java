package eu.uqasar.model.meta;

import javax.persistence.Entity;

/**
 *
 *
 */
@Entity
public class SoftwareType extends MetaData {

    public SoftwareType() {
    }

    public SoftwareType(String name) {
        super(name);
    }

}
