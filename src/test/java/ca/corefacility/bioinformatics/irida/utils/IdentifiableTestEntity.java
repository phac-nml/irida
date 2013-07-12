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
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Entity
@Table(name="identifiable")
public class IdentifiableTestEntity implements IridaThing<IdentifiableTestEntity,Audit,Identifier>, Comparable<IdentifiableTestEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Transient
    private Identifier identifier;
    @NotNull
    private String nonNull;
    private Integer integerValue;
    @NotNull
    @Transient
    private Audit audit;
    private String label;
    
    private Boolean valid;

    public IdentifiableTestEntity() {
        this.identifier = new Identifier();
        this.audit = new Audit();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("created: ").append(audit.getCreated());
        return builder.toString();
    }

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
    public Identifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public Audit getAuditInformation() {
        return audit;
    }

    @Override
    public void setAuditInformation(Audit audit) {
        this.audit = audit;
    }

    @Override
    public int compareTo(IdentifiableTestEntity o) {
        return audit.compareTo(o.audit);
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
    public IdentifiableTestEntity copy() {
        IdentifiableTestEntity copy = new IdentifiableTestEntity(); 
        copy.setNonNull(getNonNull());
        copy.setLabel(getLabel());
        return copy;
   }

    @Override
    public Boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
