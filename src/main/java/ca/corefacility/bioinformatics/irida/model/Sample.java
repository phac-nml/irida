/*
 * Copyright 2013 Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>.
 *
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
 */
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

import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;

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
    
    private Boolean valid = true;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public Sample() {
        createdDate = new Date();
    }

    public Sample(String sampleName) {
        this.sampleName = sampleName;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Sample) {
            Sample sample = (Sample) other;
            return Objects.equals(createdDate, sample.createdDate) 
                    && Objects.equals(sampleName, sample.sampleName);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdDate,sampleName);
    }

    @Override
    public int compareTo(Sample other) {
        return createdDate.compareTo(other.createdDate);
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
    public Boolean isValid() {
        return valid;
    }
    
    @Override
    public void setValid(Boolean valid) {
        this.valid = valid;
    }    

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(Date date) {
        this.createdDate = date;
    }
}
