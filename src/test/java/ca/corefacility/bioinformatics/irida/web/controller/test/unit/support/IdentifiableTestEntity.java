package ca.corefacility.bioinformatics.irida.web.controller.test.unit.support;

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
    @NotNull
    private Audit audit;

    public IdentifiableTestEntity() {
        this.id = new Identifier();
        this.audit = new Audit();
    }

    public String getNonNull() {
        return this.nonNull;
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
}
