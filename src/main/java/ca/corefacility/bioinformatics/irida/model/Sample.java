package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;


/**
 * A biological sample. Each sample may correspond to many files.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name="sample")
@Audited
public class Sample implements IridaThing, Comparable<Sample> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    
    private String sampleId;
    
    @NotNull
    @Size(min = 3)
    private String sampleName;
    private String samplePlate;
    private String sampleWell;
    private String i7IndexId;
    private String i7Index;
    private String i5IndexId;
    private String i5Index;
    private String description;
    
    private Boolean enabled = true;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;    

    public Sample() {
        createdDate = new Date();
        modifiedDate = createdDate;
    }
    
    /**
     * Create a new {@link Sample} with the given name
     * @param name The name of the sample
     */
    public Sample(String sampleName) {
        this.sampleName = sampleName;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Sample) {
            Sample sample = (Sample) other;
            return Objects.equals(createdDate, sample.createdDate) 
                    && Objects.equals(modifiedDate, sample.modifiedDate) 
                    && Objects.equals(sampleName, sample.sampleName);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdDate,sampleName,modifiedDate);
    }

    @Override
    public int compareTo(Sample other) {
        return modifiedDate.compareTo(other.modifiedDate);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getSamplePlate() {
        return samplePlate;
    }

    public void setSamplePlate(String samplePlate) {
        this.samplePlate = samplePlate;
    }

    public String getSampleWell() {
        return sampleWell;
    }

    public void setSampleWell(String sampleWell) {
        this.sampleWell = sampleWell;
    }

    public String getI7IndexId() {
        return i7IndexId;
    }

    public void setI7IndexId(String i7IndexId) {
        this.i7IndexId = i7IndexId;
    }

    public String getI7Index() {
        return i7Index;
    }

    public void setI7Index(String i7Index) {
        this.i7Index = i7Index;
    }

    public String getI5IndexId() {
        return i5IndexId;
    }

    public void setI5IndexId(String i5IndexId) {
        this.i5IndexId = i5IndexId;
    }

    public String getI5Index() {
        return i5Index;
    }

    public void setI5Index(String i5Index) {
        this.i5Index = i5Index;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLabel() {
        return sampleName;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean valid) {
        this.enabled = valid;
    }    

    @Override
    public Date getTimestamp() {
        return createdDate;
    }

    @Override
    public void setTimestamp(Date date) {
        this.createdDate = date;
    }
    
    @Override
    public Date getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }    
}
