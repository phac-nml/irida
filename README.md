IRIDA API
===============================
This project contains *only* the back-end Java API and database interaction projects. This project is the core of project IRIDA. You can use an existing front-end for IRIDA (such as ```irida-rest-api```) or implement your own front-end.

Setting Up MySQL for IRIDA
===============================
Your computer will probably have MySQL installed already.  If not, there are many tutorials online for how to install.

The IRIDA-api project uses Hibernate and hbm2ddl.auto to manage entities in the database.  hbm2ddl.auto will automatically create the tables that are required for entities to be stored as long as the expected schema and user is available.

Setting up the IRIDA schema
----------------------------
1. Open mysql as the root user 
   * mysql -u root -p
2. Create a schema named irida_test
   * CREATE DATABASE irida_test;

Creating the IRIDA test user
-----------------------------
1. Open mysql as the root user 
   * mysql -u root -p
2. Create a user named "test"
   * CREATE USER 'test'@'localhost' IDENTIFIED BY 'test';
3. Grant privileges to the test user
   * GRANT ALL PRIVILEGES ON irida_test.* to 'test'@'%';

The irida-api package should now be able to run.