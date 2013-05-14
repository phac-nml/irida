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
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Load example data into the database.
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ExampleDataLoader {
    private UserRepository userRepo;
    private ProjectRepository projRepo;
    private PasswordEncoder passwordEncoder;

    public ExampleDataLoader() {
    }

    public ExampleDataLoader(UserRepository userRepo, ProjectRepository projRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.projRepo = projRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public void addSampleData() {
        userRepo.create(new User("tomX", "tom@nowhere.com", passwordEncoder.encode("PASSWOD!1"), "Tom", "Matthews", "1234"));
        userRepo.create(new User("franklin", "franklin@nowhere.com", passwordEncoder.encode("PASSWOD!2"), "Franklin", "Bristow", "2345"));
        userRepo.create(new User("joshX", "josh@nowhere.com", passwordEncoder.encode("PASSWOD!3"), "Josh", "Adam", "3456"));
        userRepo.create(new User("matt", "matt@nowhere.com", passwordEncoder.encode("PASSWOD!4"), "Matthew", "Stuart-Edwards", "4567"));
        userRepo.create(new User("aaron", "aaron@nowhere.com", passwordEncoder.encode("PASSWOD!5"), "Aaron", "Petkau", "5678"));
        userRepo.create(new User("fbristow", "fbristow@gmail.com", passwordEncoder.encode("password1"), "Franklin", "Bristow", "7029"));
        userRepo.create(new User("josh", "jsadam@gmail.com", passwordEncoder.encode("password1"), "Josh", "Adam", "5678"));
        userRepo.create(new User("admin", "admin@admin.com", passwordEncoder.encode("password1"), "Admin", "Admin", "5678"));


        for (int i = 1; i <= 100; i++) {
            projRepo.create(new Project("Project " + i));
            userRepo.create(new User("user" + i, "user" + i + "@nowhere.com", "PASSWOD!" + i, "User", "Number" + i,
                    i + "04-123-4567"));
        }

    }
}
