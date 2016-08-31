package eu.uqasar.model.measure;

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


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.annotations.Indexed;
import com.google.gson.annotations.SerializedName;
import eu.uqasar.model.AbstractEntity;
@Setter
@Getter
@NoArgsConstructor
@Entity
@XmlRootElement
@Table(name = "reportmeasurement")
@Indexed
public class ReportMeasurement extends AbstractEntity {

    private static final long serialVersionUID = 13394761234433421L;

    private String reportType; // reportType
    private String name;
    private float count;
    @SerializedName("key")
    private String reportValue;

    @Override
    public String toString() {
        return "ReportMeasurement [name=" + name + ", value=" + count + ", reportKey=" + reportValue + ", reportMetric="
            + reportType + "]";
    }
}
