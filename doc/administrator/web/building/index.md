---
layout: default
search_title: "Building IRIDA from source"
description: "Describes the necessary steps for building the IRIDA web interface from source."
---

Building IRIDA from source
==========================

This document describes the necessary steps for building the IRIDA web interface from source.

* this comment becomes the table of contents.
{:toc}

Prerequisites
-------------

The following software is required to build IRIDA from source:

* Java 11 JDK (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Git (http://git-scm.com/)
* Node (http://nodejs.org/)
* Yarn (https://yarnpkg.com/lang/en/)

Installing these dependencies varies greatly, depending on the host operating system that you are using, so no install instructions are provided. The following commands must be available on your `$PATH`:

* `java`
* `javac`
* `git`

Building IRIDA
--------------

### Getting the source code

The IRIDA source code is hosted at the NML:

```bash
git clone https://github.com/phac-nml/irida.git
```

### Installing dependencies

IRIDA uses Gradle and Yarn for managing external dependencies. Some of the libraries that we're using to build IRIDA are either not available in the Maven central repository, or we've had to seperately modify/update the code to accomplish the task we want, and the changes have not yet been accepted upstream.

These libraries are located in `/lib` folder and are automatically installed via gradle.

### Packaging IRIDA
Once you've installed the required dependencies (and if your `$PATH` is configured correctly), you should be able to compile and package IRIDA by running:

```bash
./gradlew clean build -xtest
```

From the root IRIDA directory.

You may (optionally) run the tests, but the complete test suite takes approximately 45 minutes to execute, and has several more dependencies than what's required for building IRIDA. If you would like to run the complete test suite, you may follow the instructions provided in the [developer documentation](../../../developer/setup).
