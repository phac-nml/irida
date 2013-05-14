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
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the {@link UserService}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserServiceImpl extends CRUDServiceImpl<UserIdentifier, User> implements UserService {

    /**
     * A set containing only a reference to a "USER" role.
     */
    private static final Set<GrantedAuthority> USER_ONLY_SET = new HashSet<>();
    /**
     * The property name to use for passwords on the {@link User} class.
     */
    private static final String PASSWORD_PROPERTY = "password";
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    /**
     * A reference to the user repository used to manage users.
     */
    private UserRepository userRepository;
    /**
     * A reference to the password encoder used by the system for storing passwords.
     */
    private PasswordEncoder passwordEncoder;

    /**
     * Constructor, requires a handle on a validator and a repository.
     *
     * @param userRepository the repository used to store instances of {@link User}.
     * @param validator      the validator used to validate instances of {@link User}.
     */
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator) {
        super(userRepository, validator, User.class);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initializeRoles() {
        USER_ONLY_SET.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User create(User u) {
        Set<ConstraintViolation<User>> violations = validatePassword(u.getPassword());
        if (violations.isEmpty()) {
            String password = u.getPassword();
            u.setPassword(passwordEncoder.encode(password));
            return super.create(u);
        }

        throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User update(UserIdentifier uid, Map<String, Object> properties) {
        if (properties.containsKey(PASSWORD_PROPERTY)) {
            String password = properties.get(PASSWORD_PROPERTY).toString();
            Set<ConstraintViolation<User>> violations = validatePassword(password);
            if (violations.isEmpty()) {
                properties.put(PASSWORD_PROPERTY, passwordEncoder.encode(password));
            } else {
                throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
            }
        }

        return super.update(uid, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByUsername(String username) throws EntityNotFoundException {
        return userRepository.getUserByUsername(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<User> getUsersForProject(Project project) {
        return userRepository.getUsersForProject(project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user with username: [" + username + "].");
        org.springframework.security.core.userdetails.User userDetails = null;
        User u;
        try {
            u = userRepository.getUserByUsername(username);

            userDetails = new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(),
                    USER_ONLY_SET);
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
        return userDetails;
    }

    /**
     * Validate the password of a {@link User} *before* encoding the password and passing to super.
     *
     * @param password the password to validate.
     * @return true if valid, false otherwise.
     */
    private Set<ConstraintViolation<User>> validatePassword(String password) {
        return validator.validateValue(User.class, PASSWORD_PROPERTY, password);
    }
}
