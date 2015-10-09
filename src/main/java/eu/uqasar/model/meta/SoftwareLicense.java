package eu.uqasar.model.meta;

import javax.persistence.Entity;

/**
 *
 *
 */
@Entity
public class SoftwareLicense extends MetaData {

    public SoftwareLicense() {
    }

    public SoftwareLicense(String name) {
        super(name);
    }

}
