package eu.uqasar.model.meta;

import javax.persistence.Entity;

/**
 *
 *
 */
@Entity
public class CustomerType extends MetaData {

    public CustomerType() {
    }

    public CustomerType(String name) {
        super(name);
    }

}
