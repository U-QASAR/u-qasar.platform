package eu.uqasar.service.meta;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.QModelTagData;

/**
 *
 *
 */
@Stateless
public class QModelTagDataService extends MetaDataService<QModelTagData> {

    public QModelTagDataService() {
        super(QModelTagData.class);
    }

}
