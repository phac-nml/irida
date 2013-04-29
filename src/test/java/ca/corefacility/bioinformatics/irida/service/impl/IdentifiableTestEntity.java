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
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.roles.Auditable;
import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class IdentifiableTestEntity implements Identifiable<Identifier>, Auditable<Audit>, Comparable<IdentifiableTestEntity> {

    private Identifier id;
    @NotNull
    private String nonNull;
    private Integer integerValue;
    @NotNull
    private Audit audit;

    public IdentifiableTestEntity() {
        this.id = new Identifier();
        this.audit = new Audit();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("created: ").append(audit.getCreated());
        return builder.toString();
    }

    public String getNonNull() {
        return nonNull;
    }

    public void setNonNull(String nonNull) {
        this.nonNull = nonNull;
    }

    @Override
    public Identifier getIdentifier() {
        return this.id;
    }

    @Override
    public void setIdentifier(Identifier identifier) {
        this.id = identifier;
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
}
