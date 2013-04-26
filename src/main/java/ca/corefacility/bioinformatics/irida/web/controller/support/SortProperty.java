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
package ca.corefacility.bioinformatics.irida.web.controller.support;

import java.util.Objects;

/**
 * The sort property to use for some {@link Resource}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SortProperty {

    private String sortProperty;
    public static final SortProperty NONE = new SortProperty(null);

    public SortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public String getSortProperty() {
        return this.sortProperty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sortProperty);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SortProperty) {
            SortProperty sp = (SortProperty) o;
            return Objects.equals(sortProperty, sp.sortProperty);
        }
        return false;
    }
}
