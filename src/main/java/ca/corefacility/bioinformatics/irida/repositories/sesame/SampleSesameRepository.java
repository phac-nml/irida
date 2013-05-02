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

import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.alibaba.SampleIF;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class SampleSesameRepository extends GenericRepository<Identifier, SampleIF, Sample>{
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SampleSesameRepository.class);
    
    public SampleSesameRepository(){}
    
    public SampleSesameRepository(TripleStore store,AuditRepository auditRepo) {
        super(store,SampleIF.class,Sample.PREFIX,Sample.TYPE,auditRepo);
    }      

    @Override
    public Sample buildObject(SampleIF base, Identifier i) {
        Sample s = new Sample();
        s.setIdentifier(i);
        s.setSampleName(base.getSampleName());
        
        return s;
    }
}
