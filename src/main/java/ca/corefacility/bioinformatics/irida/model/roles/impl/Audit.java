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
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * Information that can be used to audit any object persisted to the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class Audit implements Comparable<Audit> {

    @NotNull
    private Date created;
    @NotNull
    private User createdBy;
    private Date updated;

    public Audit() {
        this.created = new Date();
    }

    public Audit(User user) {
        this();
        this.createdBy = user;
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
}
