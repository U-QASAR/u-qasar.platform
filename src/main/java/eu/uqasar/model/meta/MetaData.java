package eu.uqasar.model.meta;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.wicket.model.IModel;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.util.resources.ResourceBundleLocator;
import eu.uqasar.web.UQasar;

@NoArgsConstructor
@Setter
@Getter
@MappedSuperclass
@Indexed
public abstract class MetaData extends AbstractEntity implements IMetaData {

	
	@NotNull
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
    private String name;

    public MetaData(final String name) {
        this.name = name;
    }

    public static <T extends MetaData> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MetaData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static <T extends MetaData> T newInstance(Class<T> clazz, final String name) {
        T entity = newInstance(clazz);
        if(entity != null) {
            entity.setName(name);
        }
        return entity;
    }

    public static <T extends MetaData> Collection<Class<T>> getAllClasses() {
        List<Class<T>> classes = new ArrayList<>();
        classes.add((Class<T>) ContinuousIntegrationTool.class);
        classes.add((Class<T>) CustomerType.class);
        classes.add((Class<T>) IssueTrackingTool.class);
        classes.add((Class<T>) ProgrammingLanguage.class);
        classes.add((Class<T>) ProjectType.class);
        classes.add((Class<T>) SoftwareLicense.class);
        classes.add((Class<T>) SoftwareType.class);
        classes.add((Class<T>) SourceCodeManagementTool.class);
        classes.add((Class<T>) StaticAnalysisTool.class);
        classes.add((Class<T>) TestManagementTool.class);
        classes.add((Class<T>) Topic.class);
        classes.add((Class<T>) SoftwareDevelopmentMethodology.class);
        Collections.sort(classes, new MetaDataComparator<T>());
        return classes;
    }
    
    private static class MetaDataComparator<T extends MetaData> implements Comparator<Class<T>> {

        @Override
        public int compare(Class<T> o1, Class<T> o2) {
            final String o1String = getLabel(o1);
            final String o2String = getLabel(o2);
            if(o1String == null && o2String == null) return 0;
            if(o1String == null && o2String != null) return 1;
            if(o1String != null && o2String == null) return -1;
            return o1String.compareTo(o2String);
        }
    }
    

    private static final String PREFIX = "label.metadata.";
    private static final String EXISTING_SUFFIX = ".existing";
    private static final String SELECTED_SUFFIX = ".selected";
    private static final String NEW_SUFFIX = ".new";

    public static <T extends MetaData> IModel<String> getLabelModel(Class<T> clazz) {
        return ResourceBundleLocator.
                getLabelModel(MetaData.class, PREFIX + getLabelKey(clazz));
    }

    public static <T extends MetaData> String getLabel(Class<T> clazz) {
        return getLabelModel(clazz).getObject();
    }

    public static <T extends MetaData> IModel<String> getLabelModelForNew(Class<T> clazz) {
        return ResourceBundleLocator.
                getLabelModel(MetaData.class, PREFIX + getLabelKey(clazz) + NEW_SUFFIX);
    }

    public static <T extends MetaData> String getLabelForNew(Class<T> clazz) {
        return getLabelModelForNew(clazz).getObject();
    }

    private static <T extends MetaData> IModel<String> getLabelModelForExisting(Class<T> clazz) {
        return ResourceBundleLocator.
                getLabelModel(MetaData.class, PREFIX + getLabelKey(clazz) + EXISTING_SUFFIX);
    }

    public static <T extends MetaData> String getLabelForExisting(Class<T> clazz) {
        return getLabelModelForExisting(clazz).getObject();
    }
    
      public static <T extends MetaData> IModel<String> getLabelModelForSelected(Class<T> clazz) {
        return ResourceBundleLocator.
                getLabelModel(MetaData.class, PREFIX + getLabelKey(clazz) + SELECTED_SUFFIX);
    }

    public static <T extends MetaData> String getLabelForSelected(Class<T> clazz) {
        return getLabelModelForSelected(clazz).getObject();
    }

    protected String getLabelKey() {
        return getLabelKey(getClass());
    }

    private static String getLabelKey(Class clazz) {
        return clazz.getSimpleName();
    }

    @Override
    public String toString() {
        if (UQasar.exists()) {
            return name;
        } else {
            return super.toString();
        }
    }

    @JsonIgnore
    public IModel<String> getLabelModel() {
        return getLabelModel(this.getClass());
    }

}
