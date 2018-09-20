Contributing to IRIDA
=====================

Thank you for your interest in contributing to the Integrated Rapid Infectious Disease Analysis (IRIDA) project.  IRIDA is primarily developed as by the IRIDA consortium, but the development team is happy to accept contributions from the community.  If you have changes or additions you would like to suggest we would love to talk about it!


Contacting the IRIDA Team
-------------------------
* Ask usage questions on __ADD FORUM LINK HERE__
* Report bugs, suggest features, or contribute code at on [GitHub](http://github.com/phac-nml/irida)
* Chat with the developers on [Gitter](https://gitter.im/irida-project/)


Contributing to IRIDA
---------------------

### Reporting a bug

Reporting a bug is the easiest (and possibly most useful) way you can contribute to IRIDA.  IRIDA uses GitHub to track our issues.

Note: IRIDA's GitHub issue board is for bug reports or feature requests only.  You should not use it for usage issues, problems with analyzing your data, or problems setting up IRIDA.  For issues of this nature, please contact the IRIDA team by one of the other means described at the top of this document.

If you find an issue while using IRIDA that you feel is a problem with the software, first check out our [GitHub issues board](https://github.com/phac-nml/irida/issues) to see if someone has already reported the issue.  If you find a similar issue, we would still like to hear about your specific problem.  Add a comment to the issue stating how your problem occurred.

If you do not find a similar issue, please create a new issue with (at least) the following information:

1. Where were you using IRIDA? (public IRIDA, private install, virtual machine, etc)
2. What were you doing when the issue happened?
3. What did you expect to happen?
4. What actually happened?

When you're done, please label the new issue as a `bug` and we will triage it as soon as we can.


### Contributing Code

Before contributing any code, you should read our development guide and set up an IRIDA development environment so you can test your changes.  A good place to start is the [IRIDA Development Primer](https://irida.corefacility.ca/documentation/developer/getting-started/) in the IRIDA developer documentation.

#### Code submission requirements

The IRIDA team works to maintain a uniform style, development standards, and formatting across the project.  This includes specific code formatting options, comment guidelines, and testing requirements.

For Java code submissions, we require the following:
* Use one of the IDE specific code formatter files.  [Eclipse](https://irida.corefacility.ca/documentation/developer/files/eclipse-code-formatter.xml) or [IntelliJ Idea](https://irida.corefacility.ca/documentation/developer/files/intellij-code-style-schemes.xml).
* Write full JavaDoc for all functions, classes, and files you add or edit.  Our continuous integration suite will automatically fail for missing or incorrect JavaDoc.
* Write inline documentation for any complicated code blocks.
* Write tests for your bugfix or feature.  If the change is anything user facing, a full integration test should be written to test the change.  See more about our testing environment in the [IRIDA Development Primer](https://irida.corefacility.ca/documentation/developer/getting-started/).

For JavaScript or HTML submissions, the standards are similar:
* `prettier` is automatically run on commit to format your JavaScript files.
* Document all files, functions, and complicated code blocks.
* Write tests for your bugfix or feature.  If the change is anything user facing, an integration test should be written to test the change.

#### Fixing a bug

If there is an issue in IRIDA


#### Submitting a feature

Before writing a bunch of code for a new feature, we would love to discuss the feature you're trying to build!  We may have a similar feature in the works where you can help contribute.

#### Submitting a pull request

Once your code meets all the standards and requirements outlined above,

**The final decision on whether to accept a pull request comes from the core IRIDA development team.**