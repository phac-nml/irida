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
package ca.corefacility.bioinformatics.irida.repositories.sesame;

import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.utils.Identified;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.lang.reflect.Method;
import java.util.Map;
import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.TupleQuery;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class IdentifiedRepo extends GenericRepository<Identifier, Identified> {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IdentifiedRepo.class); //Logger to use for this repository

    public IdentifiedRepo(){}
    
    public IdentifiedRepo(TripleStore store,AuditRepository auditRepo,RelationshipSesameRepository linksRepo) {
        super(store,Identified.class,Identified.PREFIX,Identified.TYPE,auditRepo,linksRepo);

    }

    @Override
    public Identified update(Identifier id, Map<String, Object> updatedFields) throws InvalidPropertyException {
        if(updatedFields.containsKey("unannotatedData")){
            Object field = updatedFields.get("unannotatedData");
            
            Method declaredMethod;
            try {
                declaredMethod = Identified.class.getDeclaredMethod("getAnnotatedGetter");
            } catch (NoSuchMethodException | SecurityException ex) {
                logger.error("No field unannotatedData exists.  Cannot update object.");
                throw new InvalidPropertyException("No field named unannotatedData exists for this object type");            
            }

            Iri annotation = declaredMethod.getAnnotation(Iri.class);

            logger.trace("Updating unannotatedData -- " + annotation.value());

            updateField(id, annotation.value(), field);
            
            updatedFields.remove("unannotatedData");
        }
        
        return super.update(id, updatedFields);
    }
    
    @Override
    protected void setListBinding(String fieldName, Map<String, String> fieldPredicates, int index, TupleQuery query, ValueFactory fac){
        if(fieldName.equals("unannotatedData")){
            String predStr = fieldPredicates.get("getAnnotatedGetter");
            URI pred = fac.createURI(predStr);
            query.setBinding("pred"+index, pred);
        }
        else{
            super.setListBinding(fieldName, fieldPredicates, index, query, fac);
        }
      
    }    
    
    
    
}
