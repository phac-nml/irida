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

import ca.corefacility.bioinformatics.irida.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.alibaba.SequenceFileIF;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class SequenceFileSesameRepository extends GenericAlibabaRepository<Identifier, SequenceFileIF, SequenceFile>{

    public SequenceFileSesameRepository(){}
    
    public SequenceFileSesameRepository(TripleStore store) {
        super(store,SequenceFileIF.class,SequenceFile.PREFIX,SequenceFile.TYPE);
    }
    
    @Override
    public SequenceFile buildObject(SequenceFileIF base, Identifier i) {
        SequenceFile f = new SequenceFile();
        f.setIdentifier(i);
        f.setFile(base.getFile());
        
        return f;
    }
    
}
