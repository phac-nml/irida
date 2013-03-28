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
package ca.corefacility.bioinformatics.irida.repositories.memory;

import ca.corefacility.bioinformatics.irida.model.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A base class for CRUD memory repositories. DO NOT USE THIS CLASS IN
 * PRODUCTION.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class CRUDMemoryRepository<Type> implements CRUDRepository<Identifier, Type> {

    Map<Identifier, Type> store = new HashMap<>();

    @Override
    public Type create(Type object) throws IllegalArgumentException {
        try {
            Identifier id = new Identifier();
            Method identifierSetter = object.getClass().getMethod("setId", Identifier.class);
            identifierSetter.invoke(object, id);
            store.put(id, object);
        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("This type does not have an identifier method: " + e);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("The key type does not match the identifier method signature.");
        }
        return object;
    }

    @Override
    public Type read(Identifier id) throws IllegalArgumentException {
        if (exists(id)) {
            return store.get(id);
        }

        throw new IllegalArgumentException("No such object with the given identifier exists.");
    }

    @Override
    public Type update(Type object) throws IllegalArgumentException {
        Identifier id = null;
        try {
            Method identifierMethod = object.getClass().getMethod("getId");
            id = (Identifier) identifierMethod.invoke(object);
        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("This type does not have an identifier method.");
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("The key type does not match the identifier method signature.");
        }
        if (exists(id)) {
            return store.put(id, object);
        }

        throw new IllegalArgumentException("No such object with the given identifier exists.");
    }

    @Override
    public void delete(Identifier id) {
        store.remove(id);
    }

    @Override
    public List<Type> list() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Boolean exists(Identifier id) {
        return store.containsKey(id);
    }
}
