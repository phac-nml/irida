---
layout: "default"
---

Database Change Management
==========================

This document covers my search for a solution for database change management.

Document History
----------------
* November 14, 2013: Add reference to Flyway.
* November 12, 2013: Document creation.

Background
----------
The IRIDA NGS Archive and Bioinformatics Platform uses JPA and Hibernate for interacting with relational databases. In development, Hibernate provides a feature for dynamically generating a database schema. Hibernate also provides a feature to import the generated schema and a file with a SQL script with data to be inserted into the database.

These Hibernate features are suitable for development, but not suitable for production purposes (see: http://pragmatastic.blogspot.ca/2010/10/managing-database-changes-in-java.html and https://community.jboss.org/wiki/HibernateFAQ-MiscellaneousFAQs#jive_content_id_Hibernate_doesnt_generate_the_database_indexes_I_want_in_the_schema). Furthermore, these features cannot be adapted in a straightforward way to provide support for upgrading production instances from one version to the next.

* this comment becomes the toc
{:toc}

Requirements
------------
The change-management tool that we adopt _must_ have the following features:

1. Ability to create the latest database schema on a target database (any vendor).
    1. Ability to import data from a script into the target database (initial account data, ...)
2. Ability to upgrade from an arbitrary version of the database schema to the latest version. 
3. Works without requiring installing additional software (i.e., outside of the tools we already require; maven, java) for deployment.

The change-management tool that we adopt **may** have the following bonus features:

1. Ability to upgrade from an arbitrary version to an arbitrary (i.e., not the latest) version.
2. Ability to *down*grade from an arbitrary version to an arbitrary version.
3. Works automatically with JPA and/or Hibernate annotations (i.e., generates schema changes dynamically)

Solutions
---------

### LiquiBase
Site: http://www.liquibase.org/

Seems to be fairly well-supported. The user forums have posts (as of this writing) that were written within the last week. That said, many of the forum posts are left unanswered. A company exists around this project (Datical, see: http://www.datical.com/) that offers training and support agreements, so the product is not likely to disappear suddenly.

* Satisfies requirement 1: LiquiBase can run commands against a live instance of a database to create a database schema. LiquiBase also supports a wide variety of database vendors (see: http://www.liquibase.org/databases.html).
* Satisfies requirement 1.1: Arbitrary data can be inserted into existing tables (see: http://www.liquibase.org/documentation/changes/insert.html)
* Satisfies requirement 2: Changes are manually managed by the developer modifying a set of change-log files. LiquiBase creates and manages its own table (`DatabaseChangeLog`) in the database and allows upgrades to the database (see: http://www.liquibase.org/documentation/update.html)
* Satisfies requirement 3: liquibase has a maven plugin.
* Seems to satisfy bonus 1: Each change-set must have an identifier associated with it. The documentation regarding updating implies that updates can be done by count (i.e., make 10 changes from this point), but no clear way to do it by specific revision number (see: http://www.liquibase.org/documentation/update.html)
* Satisfies bonus 2: Has many options for rolling back database revisions, however the same no clear way to specify revisions holds for this bonus (see: http://www.liquibase.org/documentation/rollback.html)
* Seems to satisfy bonus 3: Requires third-party plugins for LiquiBase, but seems to be able into integrate with Spring and Hibernate (see: https://github.com/SergeyVasilyev/liquibase-hibernate, https://github.com/liquibase/liquibase-hibernate, https://github.com/paukiatwee/webapp-bootstrap)

Additional considerations:

* Complex; additional tool to use and understand.
* Number of unanswered questions in the forum is not promising.


### c5-db-migration
Site: http://code.google.com/p/c5-db-migration/, https://github.com/carbonfive/db-migration

First thought: abandoned. The last revisions to the version control are from 2010. This was released by a company called CarbonFive; CarbonFive seems to be a pretty strongly Ruby-oriented company. No apparent support forums (the Google Code page appears abandoned, the GitHub page looks like it was automatically migrated).

* Partially satisfies requirement 1: Looks like it can run against a live database, but the changes can only be stored as SQL files. In theory, the SQL files could be written in pure ANSI SQL (i.e., no vendor-specific notation), however, this requires developer discipline (why do I want to remember to do that if I don't have to?)
* Satisfies requirement 1.1: Since the changes are plain SQL, arbitrary insertions can be executed.
* Satisfies requirement 2: Changes are manually managed by the developer creating a set of change-log SQL scripts. c5-db-migration creates and manages its own table (`versionTable`) in the database and allows upgrades to the database (see: http://code.google.com/p/c5-db-migration/wiki/MavenPlugin)
* Satisfies requirement 3: c5-db-migration *is* a Maven plug-in.
* Does not satisfy bonus 1: No clear way to tell c5-db-migration when to stop upgrading. We *could* hack this in, but not sure if it's worth the effort.
* Does not satisfy bonus 2: No clear way to get c5-db-migration to downgrade, especially since it's just using SQL scripts.
* Does not satisfy bonus 3: Purely SQL-script-based upgrades.

Additional considerations:

* No need to learn a new management format, all update scripts are written in SQL.
* Definitely seems like it's unsupported at this point, if it doesn't work, we're on our own.

### Flyway
Site: http://flywaydb.org/

### Roll-your-own
Use some variation of SchemaExport to manage upgrades to the database. This is the least desirable option because it's the most work. It also means that we personally have to support the code that we put together.

Decision
--------
Choose to use LiquiBase because it satisfies all of the requirements, plus satisfies some of the bonus requirements. Furthermore, LiquiBase does not appear to be completely abandoned by its developers, like c5-db-migration. If we adopted c5-db-migration, we would be in almost the same place as if we had developed the software by ourselves.

By choosing to use LiquiBase, we do not claim that we will offer support for the bonus features (like downgrading), but having the option to provide that support is appealing.

Proceed with LiquiBase.
