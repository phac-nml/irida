package ca.corefacility.bioinformatics.irida.ria.web.search.dto;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableRequest;

public class SearchRequest extends AntTableRequest {
    private boolean global;

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public boolean isGlobal() {
        return global;
    }
}