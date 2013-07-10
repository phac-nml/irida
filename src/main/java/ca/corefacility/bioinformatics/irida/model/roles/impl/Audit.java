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
package ca.corefacility.bioinformatics.irida.model.roles.impl;

import ca.corefacility.bioinformatics.irida.model.User;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.openrdf.annotations.Iri;

/**
 * Information that can be used to audit any object persisted to the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Entity
@Table(name="audit")
@Iri(Audit.TYPE)
public class Audit implements Comparable<Audit> {
    public static final String TYPE = "http://corefacility.ca/irida/Audit";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull
    @Iri("http://corefacility.ca/irida/createdDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    //@NotNull
    //@Iri("http://corefacility.ca/IRIDA/Audit/createdBy")
    @Transient
    private User createdBy;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Iri("http://corefacility.ca/irida/updatedDate")
    private Date updated;
    
    @Transient
    private List<Audit> updates;

    public Audit() {
        this.created = new Date();
    }

    public Audit(User user) {
        this();
        this.createdBy = user;
    }
    
    public Audit copy(){
        Audit a = new Audit();
        a.setCreated(created);
        a.setUpdated(updated);
        a.setCreatedBy(createdBy);
        
        return a;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Audit) {
            Audit audit = (Audit) object;
            return Objects.equals(created, audit.created)
                    && Objects.equals(createdBy, audit.createdBy);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(created, createdBy);
    }

    @Override
    public int compareTo(Audit audit) {
        return this.created.compareTo(audit.created);
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public Date getUpdated() {
        return this.updated;
    }
    
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public List<Audit> getUpdates() {
        return updates;
    }

    public void setUpdates(List<Audit> updates) {
        this.updates = updates;
    }
}
