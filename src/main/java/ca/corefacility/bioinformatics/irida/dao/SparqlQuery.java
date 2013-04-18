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
package ca.corefacility.bioinformatics.irida.dao;

import ca.corefacility.bioinformatics.irida.model.enums.Order;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public abstract class SparqlQuery {
    /**
     * Generate the SPARQL code to set the limit and offset of a query for paging
     * @param page The page to return
     * @param size The size of the page
     * @return A string of the SPARQL code to perform the paged query
     */
    public static String setLimitOffset(int page, int size){
        String qs = "";
        
        int startnum = 0;
        if(page > 0 && size > 0){   
            startnum = (page - 1) * size;
        }
        
        if(startnum > 0 || size > 0){
                qs += "LIMIT " + size +"\n"
                    + "OFFSET " + startnum + "\n";
        }
        
        return qs;
    }
    
    /**
     * Generate the SPARQL code to set the order of a query's results
     * @param sortProperty The property to sort on
     * @param order The order to sort in
     * @return A String of the SPARQL code to perform the ordering
     */
    public static String setOrderBy(String sortProperty, Order order){
        String qs = "";
        
        if(sortProperty != null){
            if(order == Order.ASCENDING){
                qs += "ORDER BY ?"+sortProperty+"\n";
            }
            else if(order == Order.DESCENDING){
                qs += "ORDER BY DESC(?"+sortProperty+")\n";
            }
        }
        
        return qs;
    }
}
