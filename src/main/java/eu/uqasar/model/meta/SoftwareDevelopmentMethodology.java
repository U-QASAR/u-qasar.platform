package eu.uqasar.model.meta;

import javax.persistence.Entity;


@Entity
public class SoftwareDevelopmentMethodology extends MetaData {

    public SoftwareDevelopmentMethodology() {
    }

    public SoftwareDevelopmentMethodology(String name) {
        super(name);
    }

}
