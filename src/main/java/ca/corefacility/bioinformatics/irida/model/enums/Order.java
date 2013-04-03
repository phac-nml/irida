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
package ca.corefacility.bioinformatics.irida.model.enums;

/**
 * When sorting a collection of generic objects you should be able to specify
 * the order of the sort.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public enum Order {

    ASCENDING("ASCENDING"),
    DESCENDING("DESCENDING");
    private String code;

    private Order(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static Order fromString(String code) {
        switch (code) {
            case "ASCENDING":
                return ASCENDING;
            case "DESCENDING":
                return DESCENDING;
            default:
                throw new IllegalArgumentException("[" + code + "] is not a valid type of Order.");
        }
    }
}
