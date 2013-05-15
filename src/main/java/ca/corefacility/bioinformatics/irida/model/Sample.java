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

import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.openrdf.annotations.Iri;

/**
 * A biological sample. Each sample may correspond to many files.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Iri(Sample.PREFIX + Sample.TYPE)
public class Sample implements IridaThing<Audit,Identifier>, Comparable<Sample> {
    public static final String PREFIX = "http://corefacility.ca/irida/";
    public static final String TYPE = "Sample";
    
    private Identifier id;
    @NotNull
    private Audit audit;
    @NotNull
    @Size(min = 3)
    @Iri("http://www.w3.org/2000/01/rdf-schema#label")
    private String sampleName;

    public Sample() {
        audit = new Audit();
    }

    public Sample(Identifier id) {
        this();
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Sample) {
            Sample sample = (Sample) other;
            return Objects.equals(sampleName, sample.sampleName);
        }

        return false;
    }

    @Override
    public int compareTo(Sample other) {
        return audit.compareTo(other.audit);
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
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

    @Override
    public String getLabel() {
        return sampleName;
    }
}
