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
package ca.corefacility.bioinformatics.irida.repositories.sesame.dao;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;

import java.util.HashMap;

/**
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class DefaultLinks {
    HashMap<Class, HashMap<Class, RdfPredicate>> links = new HashMap<>();

    public DefaultLinks() {
        links.put(Project.class, new HashMap<Class, RdfPredicate>());
        links.get(Project.class).put(User.class, new RdfPredicate("irida", "hasUser"));
        links.get(Project.class).put(Sample.class, new RdfPredicate("irida", "hasSample"));
        links.get(Project.class).put(SequenceFile.class, new RdfPredicate("irida", "hasSequenceFile"));

        links.put(User.class, new HashMap<Class, RdfPredicate>());
        links.get(User.class).put(Project.class, new RdfPredicate("irida", "hasProject"));

        links.put(Sample.class, new HashMap<Class, RdfPredicate>());
        links.get(Sample.class).put(Project.class, new RdfPredicate("irida", "sampleHasProject"));
        links.get(Sample.class).put(SequenceFile.class, new RdfPredicate("irida", "sampleHasSequenceFile"));

        links.put(SequenceFile.class, new HashMap<Class, RdfPredicate>());
        links.get(SequenceFile.class).put(Project.class, new RdfPredicate("irida", "sequenceFileHasProject"));
        links.get(SequenceFile.class).put(Sample.class, new RdfPredicate("irida", "sequenceFileHasSample"));
    }

    public HashMap<Class, HashMap<Class, RdfPredicate>> getLinks() {
        return links;
    }

    public <S extends IridaThing, O extends IridaThing> RdfPredicate getLink(Class subject, Class object) {
        RdfPredicate link = null;
        if (links.containsKey(subject)) {
            HashMap<Class, RdfPredicate> preds = links.get(subject);
            if (preds.containsKey(object)) {
                link = preds.get(object);
            } else {
                throw new IllegalArgumentException("Link for subject doesn't exist");
            }
        } else {
            throw new IllegalArgumentException("Link for object doesn't exist");
        }

        return link;
    }

    public <S extends IridaThing, O extends IridaThing> void addLink(Class subject, RdfPredicate pred, Class object) {
        HashMap<Class, RdfPredicate> preds;

        if (!links.containsKey(subject)) {
            links.put(subject, new HashMap<Class, RdfPredicate>());
        }

        preds = links.get(subject);

        if (preds.containsKey(object)) {
            throw new IllegalArgumentException(
                    "Default link already exists for " + subject.getName() + " and " + object.getName());
        } else {
            preds.put(object, pred);
        }
    }
}
