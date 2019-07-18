IRIDA
=====

IRIDA (<https://www.irida.ca/>) is Canada’s Integrated Rapid Infectious Disease Analysis Platform for Genomic Epidemiology.  This package contains the IRIDA software along with additional files to assist in getting IRIDA installed.  Documentation on IRIDA can be found at <https://irida.corefacility.ca/documentation/> and code can be found at <https://github.com/phac-nml/irida>.

Files
=====

The following is a list of files within this package.

* `*.war`:  The IRIDA Java WAR file.
* `tools-list.yml`:  A list of tools required to exist within a [Galaxy](https://galaxyproject.org/) instance before IRIDA can be deployed.
* `CHANGELOG.md`:  The log of most recent changes.
* `UPGRADING.md`: A list of specific instructions for upgrading between IRIDA versions.
* `LICENSE`:  The license IRIDA is distributed under.

Installation
============

Installing Galaxy Tools
-----------------------

This directory includes files to quickly install all necessary tools within an existing Galaxy instance for IRIDA.  Please see <https://irida.corefacility.ca/documentation/administrator/galaxy/> for details on how to set up an IRIDA Galaxy instance.  In particular, if this is a new Galaxy installation, the [IRIDA Toolshed](https://irida.corefacility.ca/galaxy-shed/) will need to be added to the Galaxy `config/tool_sheds_conf.xml` file along with some specific Galaxy configuration settings, listed at <https://irida.corefacility.ca/documentation/administrator/galaxy/#step-3-modify-configuration-file>.

Some additional dependencies will need to be installed outside of Galaxy in order to get all tools to build.  These include:

* [Perl 5](https://www.perl.org/)
* Perl modules - `Readonly`

More information can be found at <https://irida.corefacility.ca/documentation/administrator/galaxy/setup/#galaxy-tools-installation>.

### Automated Process/Upgrading

To install all required Galaxy tools for this IRIDA version you can make use of [Ephemeris](https://ephemeris.readthedocs.io). To install Ephemeris, you can either use [Conda](https://conda.io/) or Python Pip. For example `conda install -c bioconda ephemeris` or `pip install ephemeris`. Please see the [Installation](https://ephemeris.readthedocs.io/en/latest/installation.html) instructions for more details.

Once Ephemeris is installed, please run the following to install all Galaxy tools for IRIDA:

```bash
shed-tools install --toolsfile tools-list.yml --galaxy [http://url-to-galaxy] --api_key [api key]
```

Where `[http://url-to-galaxy]` is the URL to your Galaxy instance for IRIDA and `[api key]` is the API key for your IRIDA Galaxy instance. Please see the [usage of shed-tools](https://ephemeris.readthedocs.io/en/latest/commands/shed-tools.html#usage) for more details.

Following installation/upgrade of tools, some small updates/configurations to software may need to be applied. One such configuration change is modification of the memory given to the genome assembler Shovill if running on a cluster. Please see the [SISTR Pipeline Documentation](https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/sistr/) as well as documentation for each tool in IRIDA <https://irida.corefacility.ca/documentation/administrator/galaxy/setup/#manual-installation-of-tools> for more details about this particular issue.

In addition the software `tbl2asn`, used by Prokka, needs to be kept up to date with the latest copy at least once per year. If `tbl2asn` is not kept up to date, eventually a Prokka run will report an error message when running tbl2asn that it has expired. If `tbl2asn` is not working for you when running the Assembly/Annotation pipeline, please refer to the [IRIDA Installation FAQ](https://irida.corefacility.ca/documentation/administrator/faq/#tbl2asn-out-of-date) for details on how to update `tbl2asn`.

### Manual Process

If the automated installation process does not work, or to test out the tools, please see the online documentation at <https://irida.corefacility.ca/documentation/administrator/galaxy/setup/#manual-installation-of-tools>.

Installing IRIDA
----------------

### New Installation

For details on installation of IRIDA from scratch, please refer to the online documentation at <https://irida.corefacility.ca/documentation/>.

### Upgrading IRIDA

Please refer to the `UPGRADING.md` file for details on upgrading between IRIDA versions as well as the online documentation at <https://irida.corefacility.ca/documentation/>.  For the most part, upgrading will involve copying over and re-deploying the `*.war` file included in this distribution.
