Contributing to IRIDA
=====================

Thank you for your interest in contributing to the Integrated Rapid Infectious Disease Analysis (IRIDA) project.  IRIDA is primarily developed by the IRIDA consortium, but the development team is happy to accept contributions from the community.  If you have changes or additions you would like to make we would love to talk about it!  Before contributing code, please first discuss the change you are planning to make with us by creating or commenting on a GitHub issue, or you can contact us by one of the other options below.


Contacting the IRIDA Team
-------------------------
* To ask general or usage questions, email the IRIDA team at IRIDA-mail@sfu.ca
* Report bugs, suggest features, or contribute code on [GitHub](http://github.com/phac-nml/irida)
* Chat with the developers on [Gitter](https://gitter.im/irida-project/)


Types of Contributions
----------------------

### Reporting a bug

Reporting a bug is the easiest, and one of the most useful ways you can contribute to IRIDA.  IRIDA uses a [GitHub issue board] to track our issues.  If you find an issue while using IRIDA that you feel is a problem with the software, first check out our [GitHub issue board] to see if someone has already reported the issue.  If you find a similar issue, we would still like to hear about your problem.  Add a comment to the issue stating how your problem occurred.

**If you find a security vulnerability, please contact the IRIDA team by email.**

If you do not find a similar issue, please create a new issue with (at least) the following information:

1. Where were you using IRIDA? (public IRIDA, private install, virtual machine, etc)
2. What were you doing when the issue happened?
3. What did you expect to happen?
4. What actually happened?

When you're done, please label the new issue as a `bug` and we will triage it as soon as we can.

Note: IRIDA's GitHub issue board is for bug reports or feature requests only.  You should not use it for usage issues, problems with analyzing your data, or problems setting up IRIDA.  For issues of this nature, please contact the IRIDA team by one of the other options in the [Contacting the IRIDA Team](#contacting-the-irida-team) section.

### Contributing an analysis pipeline

If you have an analysis tool that you would like to build for IRIDA we'd like to hear about it!  IRIDA has a plug-in style system for analysis workflows.  This helps make it easier for tool developers to get the benefits of IRIDA's data management system and easy to use interface and incorporate their analysis workflows into IRIDA's growing suite of tools.  For an analysis pipeline to be used in IRIDA, it must be developed as a Galaxy workflow and operate on sequencing reads.  Read more about building tools for IRIDA in the [IRIDA Pipeline Development Guide](https://irida.corefacility.ca/documentation/developer/tools/pipelines/).  An example plugin pipeline and additional documentation can be found at [IRIDA Example Pipeline Plugin GitHub repository](https://github.com/phac-nml/irida-plugin-example).

### Suggesting a feature

Before writing code for a new feature, we would love to discuss the feature you're trying to build!  First you should search our [GitHub issue board] to see if anyone else has suggested your feature.  We may have a similar feature in the works where you can contribute.

If you cannot find an existing issue that details your suggestion, please create a GitHub issue detailing your idea, how you would expect a user to interact with the new feature, and how you would expect your feature to work.

When you're done, please label the new issue as a `request` and we will triage it as soon as we can.


### Contributing Code

Before contributing any code, you should read our development guide and set up an IRIDA development environment so you can test your changes.  A good place to start is the [IRIDA Development Primer] in the IRIDA developer documentation.

Once your development environment is set up, you can find an issue on our [GitHub issue board].  It is expected that **all pull requests should reference a GitHub issue**.

#### Code submission requirements

The IRIDA team works to maintain a uniform style, development standards, and formatting across the project.  This includes specific code formatting options, comment guidelines, and testing requirements.

For any changes, a message should be added to our `CHANGELOG.md` file briefly detailing the change.

For Java code submissions, we require the following:
* Use one of the IDE specific code formatter files.  [Eclipse](https://irida.corefacility.ca/documentation/developer/files/eclipse-code-formatter.xml) or [IntelliJ Idea](https://irida.corefacility.ca/documentation/developer/files/intellij-code-style-schemes.xml) to ensure a consistent code style is applied.
* Write JavaDoc for all functions, classes, and files you add or edit.  Our continuous integration suite will fail for missing or invalid JavaDoc.
* Write inline documentation for any complicated code blocks.
* Write tests for your bugfix or feature.  If the change is anything user facing, a full integration test should be written to test the change.  See more about our testing environment in the [IRIDA Development Primer].

For JavaScript or HTML submissions, the standards are similar:
* [Prettier](https://prettier.io/) is automatically run on commit to format your JavaScript files when you make a commit to ensure a consistent code style is applied.
* Document all files, functions, and complicated code blocks.
* Write tests for your bugfix or feature.  If the change is anything user facing, an integration test should be written to test the change.

#### Submitting a pull request

When you have completed development on your issue, you can submit it to the project for review as a GitHub pull request.  You should reference the issue you were working on from IRIDA's [GitHub issue board] in your pull request.  The core IRIDA development team will review the content of the merge for functionality, fit to IRIDA's priorities, and coding standards listed above.  All tests in the CI suite must pass before a pull request is merged.  After review the core development team may suggest changes to your submission.  After feedback, if the development team's questions or suggestions are not addressed in a timely manner, your pull request may be closed.

**The final decision on whether to accept a pull request comes from the core IRIDA development team.**

### Contributing Internationalizations

IRIDA needs help supporting non-English language users!  To help internationalize IRIDA, see the [IRIDA internationalization developer guide](https://irida.corefacility.ca/documentation/developer/interface/i18n).  To submit internationalization files back to IRIDA, submit a pull request as outlined above.


Thank you for your interest in contributing to IRIDA.  For any questions or comments on this guide, or other ways you can help contribute to IRIDA, please contact the development team through one of the options outlined in the [Contacting the IRIDA Team](#contacting-the-irida-team) section.

[GitHub issue board]: https://github.com/phac-nml/irida/issues
[IRIDA Development Primer]: https://irida.corefacility.ca/documentation/developer/getting-started/
