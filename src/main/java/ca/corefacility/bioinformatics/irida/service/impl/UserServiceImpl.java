/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.service.UserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;

/**
 * Implementation of the <ref>UserService</ref>.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Named
public class UserServiceImpl implements UserService {

    private static final Map<Long, User> users = new HashMap<>();
    private static Long currentId = 2l;

    static {
        users.put(0l, new User(0l, "fbristow", "fbristow@gmail.com", "password", "Franklin", "Bristow", "204-789-7029"));
        users.add(1l, new User(1l, "jsadam", "josh.s.adam@gmail.com", "123456789", "Josh", "Adam", "204-789-4518"));
    }
    
    public UserServiceImpl() {}

    @Override
    public User create(User user) throws IllegalArgumentException {
        user.setId(currentId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User read(Long id) throws IllegalArgumentException {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("The specified identifier does not exist in the database.");
        }
        return users.get(id);
    }

    @Override
    public User update(User user) throws IllegalArgumentException {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) throws IllegalArgumentException {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("The specified identifier does not exist in the database.");
        }
        users.remove(id);
    }

    @Override
    public List<User> list() {
        List<User> usersList = new ArrayList<>(users.values());
        Collections.sort(usersList);
        return usersList;
    }

    @Override
    public Boolean exists(Long id) {
        return users.containsKey(id);
    }
}
