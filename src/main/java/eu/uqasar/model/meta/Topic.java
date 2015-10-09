
package eu.uqasar.model.meta;

import javax.persistence.Entity;

/**
 *
 *
 */
@Entity
public class Topic extends MetaData {

    public Topic() {
    }

    public Topic(String name) {
        super(name);
    }
    
}
