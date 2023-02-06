---
layout: default
---

This guide describes how to set up a workstation for hacking on IRIDA.

* This comment becomes the table of contents
{:toc}

General Requirements
====================

You're required to install a few different pieces of software on your machine before you can get started on hacking IRIDA:

1. DB: A MySQL or MariaDB server,
2. Languages: Java 11 (we have a hard requirement on Java 11), Python, Node
4. SCM: Git
5. IDE: Eclipse, Netbeans, IntelliJ, vim... (whatever you want, really).

Install instructions
====================

Install instructions for Ubuntu
-------------------------------
These instructions work for a fresh, up-to-date install of Ubuntu 14.04 LTS. No guarantees if you're using some other version of Ubuntu or some derivative of Ubuntu.

    # required for using the `apt-add-repository` command
    sudo apt-get install --yes --no-install-recommends python-software-properties
    # update sources
    sudo apt-get update

    # You will be prompted to set a root password for mysql and to accept the license for the Java installer.
    sudo apt-get install --yes --no-install-recommends mysql-server git openjdk-11-jdk


Install instructions for CentOS
-------------------------------
These instructions work for a fresh, up-to-date install of CentOS 7. No guarantees if you're using some other version of CentOS or some other RedHat-like distro.

CentOS (unfortunately) isn't as easy to set up as Ubuntu, you will have to manually install several bits of software.

Post-software install setup (Ubuntu and CentOS)
-----------------------------------------------
Once you've installed all of the prerequisites with your package manager (or manually), you can proceed with configuring MySQL (creating a user account and setting permissions), checking out IRIDA from our git repository, and running IRIDA on the command-line. You're on your own for IDE configuration.

### Cloning IRIDA

You've *probably* already figured out how to clone IRIDA if you're reading this documentation. Nevertheless, for completeness sake, you can clone IRIDA on the command-line like so:

    git clone https://github.com/phac-nml/irida.git

Importing IRIDA into an IDE is left to the developer.

### Custom dependencies

IRIDA uses some custom libraries that are either a) not currently available in a Maven repository, or b) a custom version of the software available in Maven central. These Java dependencies are located in the `lib` directory and are automatically installed with Gradle.

### Custom configuration

Many of IRIDA's properties can be configured by creating a file `/etc/irida/irida.conf`. The file is a basic key/value file (keys and values are separated by an `=` sign). A short example:

    # configure liquibase to manage the database schema
    liquibase.update.database.schema=false
    # disable hibernate's automatic schema creation
    spring.jpa.hibernate.ddl-auto=
    # disallow hibernate from importing any data
    spring.jpa.properties.hibernate.hbm2ddl.import_files=

The sections below will include the keys that you can use to override the default behaviour.

### Configure MySQL

IRIDA is configured to use the following credentials by default:

* **Username** (`spring.datasource.username`): `test`
* **Password** (`spring.datasource.password`): `test`
* **Database** (`spring.datasource.url`): `jdbc:mysql://localhost:3306/irida_test`

You'll need to permit those user credentials to create the tables in the database. A quick one-liner for that is:

```
# Create the 'test' user:
echo "CREATE USER 'test'@'localhost' IDENTIFIED BY 'test';" | mysql -u root -p

# Create the database if it doesn't exist:
echo "create database irida_test;" | mysql -u root -p

# Grand 'test' user permissions to database:
echo "grant all privileges on irida_test.* to 'test'@'localhost';" | mysql -u root -p
```

In order to run local integration tests, the same steps need to be performed for the database name `irida_integration_test`
```
# Create the database if it doesn't exist:
echo "create database irida_integration_test;" | mysql -u root -p

# Grand 'test' user permissions to database:
echo "grant all privileges on irida_integration_test.* to 'test'@'localhost';" | mysql -u root -p
```

### Configure Docker

IRIDA uses Docker to create galaxy instances when running integration tests.

https://docs.docker.com/engine/install/ubuntu/

You will also need to ensure your user has permissions to run docker commands. Please note that doing this effectively gives your user root access, so this should only be done in development environments.

```
# Create the docker group if it does not exist
sudo groupadd docker
# Add your user to the docker group.
sudo usermod -aG docker $USER
# login to docker group so you don't have to sign in/out
newgrp docker
# restart docker
sudo systemctl restart docker
# test that you can create docker instances
docker run hello-world
```


### Configure Filesystem Locations

IRIDA stores much of its metadata in the relational database. As of IRIDA 23.01, you can use cloud based storage (BETA) as well as a local filesystem to store the sequencing, reference, and analysis output files. Currently, Azure Blob and AWS S3 storage is supported.

Directory configuration is:

* **Sequencing Data**: `sequence.file.base.directory`
* **Reference Files**: `reference.file.base.directory`
* **Analysis Output**: `output.file.base.directory`

If using a local filesystem and these directories that are configured do not exist (they don't likely exist if you don't configure them), IRIDA will default to automatically creating a temporary directory using Java's [`Files.createTempDirectory`](http://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createTempDirectory-java.lang.String-java.nio.file.attribute.FileAttribute...-).
However, if you are using cloud based storage you will still need to set these directories in the configuration as these will make up the virtual path to the file, but no local directories will be created.

To setup IRIDA to use cloud based file storage, follow the instructions below for the storage type.

### Setup using Azure Storage Blob

In the configuration file (such as irida.conf) you will need to add these configuration values:

* `irida.storage.type=azure`
* `azure.container.name=CONTAINER_NAME` where the CONTAINER_NAME is a container previously setup on Azure
* `azure.container.url=CONTAINER_ENDPOINT_URL`
* `azure.sas.token=SAS_TOKEN` where the SAS_TOKEN has both read/write permissions

See [Azure Storage Setup](https://learn.microsoft.com/en-us/azure/storage/blobs/) for instructions on how to setup Blob storage.

Microsoft has also made available a storage emulator,`Azurite`, which can be used to develop and test Azure storage functionality on a local machine instead of requiring the use of an Azure Storage account. See [Microsoft Azurite](https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite).

For you to be able to use `Azurite` you will need to get the `SAS` token for the blob container. This can be retrieved by setting up [Azure Storage Explorer](https://learn.microsoft.com/en-us/azure/vs-azure-tools-storage-manage-with-storage-explorer) and adding a new resource (`Local Storage Emulator`). Once that is set-up, click on the `Blob Containers menu` item in the explorer and then right click on the container (created by default) `test`, and click Get Shared Access Signature. From the popup window, you can select the date range of validity of the token and permissions (Read, Write, Delete, List, Add, and Create) for the container, and then click create.

Once you have Azurite setup and container created, you can update these configuration values (in irida.conf etc)
* `irida.storage.type=azure`
* `azure.container.name=test`
* `azure.container.url=http://127.0.0.1:10000/devstoreaccount1/test?SAS_TOKEN_RETRIEVED_ABOVE`
* `azure.sas.token=SAS_TOKEN_RETRIEVED_ABOVE` where the SAS_TOKEN has both read/write permissions

If using `Azurite` make sure you have it running before starting up IRIDA.

### Setup using Amazon AWS S3 Bucket Storage

In the configuration file (such as irida.conf) you will need to add these configuration values:

* `irida.storage.type=aws`
* `aws.bucket.name=BUCKET_NAME` where the BUCKET_NAME is the S3 Bucket previously setup and has read/write permissions.
* `aws.bucket.region=BUCKET_REGION`
* `aws.access.key=ACCESS_KEY`
* `aws.secret.key=SECRET_KEY`

See [AWS S3 Bucket Storage Setup](https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html) for instructions on how to setup S3 storage.

There is no other configuration necessary in IRIDA to use cloud based storage. After adding these values to the configuration file you should be able to start up IRIDA, and it will use the cloud based storage that is configured.

There isn't a local storage emulator available from Amazon AWS but we can make use of localstack to develop locally. The best way to make use of localstack is to use it with a docker container.

1) Install `docker`, `docker-compose`, and the `awslocal-cli`.
2) Create a directory on your machine to store the files necessary for the localstack docker container
3) In this directory create a file `docker-compose.yml` and place the following contents in it:

```
version: '3.8'
services:
  localstack:
    image: localstack/localstack:latest
    environment:
      - DEFAULT_REGION=ap-northeast-2
      - EDGE_PORT=4566
      - SERVICES=s3,lambda,logs
    ports:
      - '4566-4583:4566-4583'
```

4) In the terminal run aws configure to set the access key, secret key, and the region for the bucket. The access key and secret key can be anything as long as they aren't left empty. For the region you can use `us-east-2`
5) From the directory you created above, in a terminal run `docker-compose up -d`
6) Create a bucket `awslocal s3api create-bucket --bucket BUCKET_NAME`
7) Set these values in your config file (eg. irida.conf). The aws.access.key and aws.secret.key are from step 4

```
irida.storage.type=aws

# DEVELOPMENT AWS CONFIG
aws.bucket.name=BUCKET_NAME_FROM_ABOVE
aws.bucket.region=us-east-2
aws.access.key=ACCESS_KEY_FROM_ABOVE
aws.secret.key=SECRET_KEY_FROM_ABOVE
aws.bucket.url=http://localhost:4566
```

### Testing IRIDA

You can verify that you've installed everything correctly in one of two ways:

1. Minimal verification: Check to see that Tomcat starts, or
2. Maximal verification: Run the complete test suite.

Checking to see that Tomcat starts will ensure that you're able to start hacking on the UI or the REST API. If you're going to be working on Galaxy-related features, you should *probably* run the complete test suite as it runs a Docker version of Galaxy and verifies that communication between IRIDA and Galaxy will work properly. Keep in mind that the complete test suite execution currently takes approximately 1 hour to complete.

#### Checking to see that Tomcat starts

IRIDA uses Gradle for build and dependency management. You can check to see that Tomcat starts like so:

    ./gradlew clean bootRun

#### Database first-time setup

When first starting up Tomcat, you'll need to have the database created and populated, which can be done by running the
following script:

    ./run.sh --create-db

This will create the database schema and import some testing data. This can also be used to drop then recreate the
database and reimport the starting dataset when a clean database is needed.

For all subsequent runs, simply run the script with no options:

    ./run.sh

This will update the database if the schema has been changed, but without dropping all of the tables beforehand, which
will cause Tomcat to start up much faster.

Other arguments can be be passed to the script:

* `--no-webapp-build`: Skip running the `buildWebapp` task along with the build, useful during development.
* `--prod`: Run spring using the production profile.

#### Integration Testing

To run the full integration test suite for IRIDA please run the following:

    ./run-tests.sh all

This will run all the integration test profiles using Gradle, and print out reports for each profile.

#### Memory requirements

Your environment may throw java heap size errors when running tests. 

You can increase the heap size by editing the `gradle.properties` file in the project directory and adding the following line:

`org.gradle.jvmargs=-Xmx4096m`

Another method is to set the environment variable

`env GRADLE_OPTS='-Xmx4096m'`

And then running the following to kill active daemons to apply the change

`./gradlew --stop`

Setting up Galaxy
-----------------

Please refer to the [Galaxy Install Guide][galaxy-install] for information on setting up Galaxy to use with IRIDA. The simplest method is to use Docker, but if new tools are being developed for Galaxy and integrated into IRIDA it is recommended to install a non-Docker version of Galaxy.

Front End Development Setup
---------------------------

IRIDA uses [Pnpm](hhttps://pnpm.io/) for front-end dependency management. It is automatically installed when running gradle tasks that require the webapp to be built.

Tasks
=====

* `./gradlew buildWebapp` - compile all es6 files to es2015.
* `./gradlew startWebapp` - use when developing front end code.  Webpack will monitor for changes to the entry files, compile them, and then updates the browser with the changes.

JavaScript and SCSS Code Formatting and Linting
===============================================

IRIDA JavaScript follows the [Prettier](https://github.com/prettier/prettier) format guideline and is enforced using `eslint-plugin-prettier` (see below for enabling eslint in IntelliJ and VS Code).

There is good [editor integration for Prettier formatting](https://github.com/prettier/prettier#editor-integration)

### IntelliJ IDEA Setup

To enable eslinting (JavaScript linting) in IntelliJ, open the preferences panel > Languages & Frameworks > JavaScript > Code Quality Tools > ESLint

* Check the "Enable" checkbox.
* Node interpreter should be you system node by default.
* ESLint package is the `eslint` within the projects `node_modules` directory.
* Configuration file is the `src/main/webapp/.eslintrc.json`.

<video controls="controls" style="width: 960px">
    <source src="images/intellij-eslint.mp4" type="video/mp4" />
</video>

### Visual Studio Code Setup

To enable eslinting (JavaScript linting) in VS Code:

 * Open the quick open panel `Ctrl+P`.
 * Copy and paste: `ext install vscode-eslint` and press enter.
 * Select `ESLint` and click "Install".
 * Click "Enable" and allow VS Code to restart.

<video controls="controls" style="width: 960px">
    <source src="images/vs-code-eslint.mp4" type="video/mp4" />
</video>

[galaxy-install]: ../../administrator/galaxy
