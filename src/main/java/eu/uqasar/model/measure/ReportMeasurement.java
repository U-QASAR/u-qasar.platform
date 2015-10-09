package eu.uqasar.model.measure;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.search.annotations.Indexed;
import com.google.gson.annotations.SerializedName;
import eu.uqasar.model.AbstractEntity;

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

    public ReportMeasurement() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    public String getReportValue() {
        return reportValue;
    }

    public void setReportValue(String reportValue) {
        this.reportValue = reportValue;
    }

    /**
     * @return the sonarMetric
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * @param report the sonarMetric to set
     */
    public void setReportType(String report) {
        this.reportType = report;
    }

    @Override
    public String toString() {
        return "ReportMeasurement [name=" + name + ", value=" + count + ", reportKey=" + reportValue + ", reportMetric="
            + reportType + "]";
    }
}
