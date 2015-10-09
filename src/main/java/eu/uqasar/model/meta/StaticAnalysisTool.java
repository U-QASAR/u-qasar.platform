package eu.uqasar.model.meta;

import javax.persistence.Entity;

/**
 *
 *
 */
@Entity
public class StaticAnalysisTool extends MetaData {

    public StaticAnalysisTool() {
    }

    public StaticAnalysisTool(String name) {
        super(name);
    }

}
