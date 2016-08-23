IRIDA
=====

IRIDA (<https://www.irida.ca/>) is Canada’s Integrated Rapid Infectious Disease Analysis Platform for Genomic Epidemiology.  This package contains the IRIDA software along with additional files to assist in getting IRIDA installed.  Documentation on IRIDA can be found at <https://irida.corefacility.ca/documentation/> and code can be found at <https://github.com/phac-nml/irida>.

Files
=====

The following is a list of files within this package.

* `*.war`:  The IRIDA Java WAR file.
* `tools-list.yml`:  A list of tools required to exist within a [Galaxy](https://galaxyproject.org/) instance before IRIDA can be deployed.
* `install_tool_shed_tools.py`: A script for installing tools in a Galaxy instance.  Originally from <https://github.com/galaxyproject/ansible-galaxy-tools/blob/7787f210c37963bf09a83f757487770e8fa2df32/files/install_tool_shed_tools.py>.
* `LICENSE.install_tool_shed_tools`: License for tool installation script.
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
* Perl modules - `Readonly Time::Piece XML::Simple Data::Dumper Bio::Perl`
* [Java](https://java.com/) 1.6+
* [Gnuplot](http://www.gnuplot.info/)

More information can be found at <https://irida.corefacility.ca/documentation/administrator/galaxy/#galaxy-tools-installation>.

### Automated Process/Upgrading

To install any required Galaxy tools for this IRIDA version please run the following:

```bash
# Installs dependency modules for script
pip install -r install-tools-requirements.txt

# Do installation of Galaxy tools
python install_tool_shed_tools.py --toolsfile tools-list.yml --galaxy [http://url-to-galaxy] --apikey [api key]
```
Where `[http://url-to-galaxy]` is the URL to your Galaxy instance for IRIDA and `[api key]` is the API key for your IRIDA Galaxy instance.

Following installation/upgrade of tools, some small updates to software may need to be applied.  In particular the software `tbl2asn`, used by Prokka, needs to be kept up to date with the latest copy at least once per year.  If `tbl2asn` is not kept up to date, eventually a Prokka run will report an error message when running tbl2asn that it has expired.

The most recently updated version is available at <http://www.ncbi.nlm.nih.gov/genbank/tbl2asn2/>.  To update, please log into the Galaxy server and run:

```bash
find tool_dependencies/ -wholename '*tbl2asn'
```

Where **tool_dependencies/** is the Galaxy directory storing tool dependencies.  This will list all the `tbl2asn` binaries currently installed.  To enable these to run they must be updated to the latest version from NCBI.  More information can be found at <https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/assembly-annotation/#step-2-install-galaxy-tools>.

### Manual Process

If the automated installation process does not work, or to test out the tools, please see the online documentation at <https://irida.corefacility.ca/documentation/administrator/galaxy/>.

Installing IRIDA
----------------

### New Installation

For details on installation of IRIDA from scratch, please refer to the online documentation at <https://irida.corefacility.ca/documentation/>.

### Upgrading IRIDA

Please refer to the `UPGRADING.md` file for details on upgrading between IRIDA versions as well as the online documentation at <https://irida.corefacility.ca/documentation/>.  For the most part, upgrading will involve copying over and re-deploying the `*.war` file included in this distribution.
