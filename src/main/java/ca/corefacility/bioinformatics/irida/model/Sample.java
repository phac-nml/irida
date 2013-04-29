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

import ca.corefacility.bioinformatics.irida.model.alibaba.SampleIF;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.Auditable;
import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * A biological sample. Each sample may correspond to many files.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class Sample implements SampleIF, Auditable<Audit>, Identifiable<Identifier>, Comparable<Sample> {

    private Identifier id;
    @NotNull
    private Audit audit;
    @NotNull
    @Size(min = 3)
    private String sampleName;
    @NotEmpty // samples must belong to at least one project
    private Collection<Project> projects;
    private Collection<SequenceFile> files;

    public Sample() {
        audit = new Audit();
        projects = new HashSet<>();
        files = new HashSet<>();
    }

    public Sample(Identifier id) {
        this();
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Sample) {
            Sample sample = (Sample) other;
            return Objects.equals(sampleName, sample.sampleName)
                    && Objects.equals(files, sample.files);
        }

        return false;
    }

    @Override
    public int compareTo(Sample other) {
        return audit.compareTo(other.audit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sampleName, files);
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    public void setProject(Collection<Project> projects) {
        this.projects = projects;
    }

    public Collection<SequenceFile> getFiles() {
        return files;
    }

    public void setFiles(Collection<SequenceFile> files) {
        this.files = files;
    }

    @Override
    public Audit getAuditInformation() {
        return audit;
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }

    @Override
    public void setIdentifier(Identifier identifier) {
        this.id = identifier;
    }

    @Override
    public void setAuditInformation(Audit audit) {
        this.audit = audit;
    }
}
