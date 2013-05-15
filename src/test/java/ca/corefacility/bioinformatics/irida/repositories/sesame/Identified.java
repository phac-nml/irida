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

import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.roles.Auditable;
import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import static ca.corefacility.bioinformatics.irida.repositories.sesame.IdentifiedIF.PREFIX;
import org.openrdf.annotations.Iri;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Iri(Identified.PREFIX + Identified.TYPE)
public class Identified implements IridaThing<Audit,Identifier>{
    public static final String PREFIX = "http://nowhere/";
    public static final String TYPE = "Identified";  
    
    private Identifier id;
    @Iri(PREFIX + "data")
    private String data;
    private Audit audit;
    
    public Identified(){}
    
    public Identified(String data){
        this.data = data;
        this.audit = new Audit();
    }
    
    public String getData(){
        return data;
    }
    
    public void setData(String data){
        this.data = data;
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
    public Audit getAuditInformation() {
        return audit;
    }

    @Override
    public void setAuditInformation(Audit audit) {
        this.audit = audit;
    }

    @Override
    public String getLabel() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return "I am identified";
    }
}
