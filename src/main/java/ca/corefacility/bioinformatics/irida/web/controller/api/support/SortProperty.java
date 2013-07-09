package ca.corefacility.bioinformatics.irida.web.controller.api.support;

import java.util.Objects;

/**
 * The sort property to use for some {@link Resource}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SortProperty {
    /**
     * No sort order is specified, the resources should be sorted using plain compareTo.
     */
    public static final SortProperty DEFAULT = new SortProperty(null);
    /**
     * the property to use for sorting
     */
    private String sortProperty;

    /**
     * Construct an instance of sortProperty with the specified property.
     *
     * @param sortProperty the property that should be used for sorting.
     */
    public SortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    /**
     * Get the sort property.
     *
     * @return the sort property.
     */
    public String getSortProperty() {
        return this.sortProperty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(sortProperty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof SortProperty) {
            SortProperty sp = (SortProperty) o;
            return Objects.equals(sortProperty, sp.sortProperty);
        }
        return false;
    }
}
