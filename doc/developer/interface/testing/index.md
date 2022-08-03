---
layout: default
---


User interface testing using [Selenium] and [chromedriver]
==========================================================

In order to run the UI tests with [Selenium], you will need the appropriate version of [chromedriver] for your version of [Chrome] or [Chromium], or you can use a [selenium/standalone-chrome] docker image.




Initialize DB with Liquibase and run UI test server
---------------------------------------------------


### Create integration test DB

MariaDB/MySQL SQL for dropping and creating `irida_integration_test` database and granting user `test` all privileges on that DB:
```sql
sudo mysql << EOF
DROP DATABASE irida_integration_test;
CREATE DATABASE irida_integration_test;
GRANT ALL PRIVILEGES ON irida_integration_test.* TO 'test'@'localhost';
EOF
```


### DB initialization with Liquibase

You'll need the DB in the proper state to run the UI tests. To do this, you will need to run Liquibase in order to apply all of the necessary DB migration scripts. This can be done with the following command:

```
./gradlew clean bootRun --args="\
    --spring.profiles.active=it \ # run IRIDA with the `it` Spring profile active
    --spring.datasource.url=jdbc:mysql://localhost:3306/irida_integration_test \
    --irida.it.rootdirectory=/tmp/irida/ \
    --spring.jpa.properties.hibernate.hbm2ddl.import_files= \
    --spring.jpa.hibernate.ddl-auto= \
    --liquibase.update.database.schema=true"
```

**NOTE:**
- Running Liquibase is required to get the DB into the right state! `--spring.jpa.properties.hibernate.hbm2ddl.import_files= `, `--spring.jpa.hibernate.ddl-auto= ` and `--liquibase.update.database.schema=true` are required to run Liquibase. You might need to wipe the integration test DB and create it again.
- **BEWARE:** `--spring.datasource.url=jdbc:mysql://localhost:3306/irida_integration_test` is VERY IMPORTANT. Specify explicitly so you don't point at your development DB and have it accidentally wiped...
- You don't need to specify `--sequence.file.base.directory` and `*reference*` and `*output*` if they all have the same root and dirnames of `sequence`, `reference` and `output`. It's automatically implied.
- Set `--irida.it.headless=false` so you can see the UI tests in action!


### [chromedriver] and [Chrome]/[Chromium]

You need to match up the version of [Chrome] with [chromedriver]. For example, [Chrome] v66 needs [chromedriver] v2.39 or v2.40 (see https://sites.google.com/a/chromium.org/chromedriver/downloads)

You can specify which [chromedriver] to use with `-Dwebdriver.chrome.driver=/PATH/TO/chromedriver` otherwise, [chromedriver] is automatically detected from your PATH.


### Using [selenium/standalone-chrome]

To use [selenium/standalone-chrome] you will need to have docker installed on your machine.

Then when running the ui tests make sure to specify the `--selenium-docker` option otherwise, the default [chromedriver] is used for running UI tests.


Running specific UI tests through [IntelliJ] IDEA
-----------------------------------------------


*Recommended [IntelliJ] Default/Template JUnit VM Options Configuration:*

```
-ea
-Dspring.profiles.active=it
-Dspring.datasource.url=jdbc:mysql://localhost:3306/irida_integration_test
-Dirida.it.rootdirectory=/tmp/irida/
-Dirida.db.profile=it
-Djunit.platform.execution.listeners.deactivate=ca.corefacility.bioinformatics.irida.junit5.listeners.Unit*
-Dirida.it.nosandbox=true
-Dirida.it.headless=true
-Dwebdriver.chrome.driver=/PATH/TO/chromedriver
```

![](images/intellij-ui-tests-default-junit.png)



**TIP:** Re-build (Ctrl+Shift+F9) to register changes to tests rather than re-building whole project!



[chromedriver]: http://chromedriver.chromium.org/
[Chrome]: https://www.google.com/chrome/
[Chromium]: https://www.chromium.org/
[Selenium]: http://www.seleniumhq.org/
[selenium/standalone-chrome]: https://hub.docker.com/r/selenium/standalone-chrome
[IntelliJ]: https://www.jetbrains.com/idea/