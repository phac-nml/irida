/*
 * Copyright 2013 Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>.
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

import ca.corefacility.bioinformatics.irida.model.roles.Auditable;
import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;

/**
 * Modelling a relationship between two different entities in the database.
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class Relationship implements Auditable<Audit>, Identifiable<Identifier>, Comparable<Relationship> {

    Identifier identifier;
    Identifier subject;
    RdfPredicate predicate;
    Identifier object;
    private Audit audit;

    public Relationship() {
        audit = new Audit();
    }

    public Relationship(Identifier subject, Identifier object) {
        this();
        this.subject = subject;
        this.object = object;
    }

    public Identifier getSubject() {
        return subject;
    }

    public void setSubject(Identifier subject) {
        this.subject = subject;
    }

    public RdfPredicate getPredicate() {
        return predicate;
    }

    public void setPredicate(RdfPredicate relationship) {
        this.predicate = relationship;
    }

    public Identifier getObject() {
        return object;
    }

    public void setObject(Identifier object) {
        this.object = object;
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
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public int compareTo(Relationship o) {
        return audit.compareTo(o.audit);
    }


}
