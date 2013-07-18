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
package ca.corefacility.bioinformatics.irida.utils;

import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Entity
@Table(name="identifiable")
public class IdentifiableTestEntity implements IridaThing, Comparable<IdentifiableTestEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String nonNull;
    private Integer integerValue;

    private String label;
    
    private Boolean valid;
    
    private Date createdDate;

    public IdentifiableTestEntity() {
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("created: ").append(createdDate);
        return builder.toString();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNonNull() {
        return nonNull;
    }

    public void setNonNull(String nonNull) {
        this.nonNull = nonNull;
    }

    @Override
    public int compareTo(IdentifiableTestEntity o) {
        return createdDate.compareTo(o.createdDate);
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    @Override
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label){
        this.label = label;
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
