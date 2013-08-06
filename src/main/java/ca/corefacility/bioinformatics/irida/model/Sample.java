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

    @NotNull
    @Size(min = 3)
    private String sampleName;
    
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
