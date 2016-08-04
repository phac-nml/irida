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

Updating Galaxy Tools
---------------------

To install any required Galaxy tools by this IRIDA instance please run the following:

```bash
pip install -r install-tools-requirements.txt # Installs dependency modules for script

python install_tool_shed_tools.py --toolsfile tools-list.yml --galaxy [http://url-to-galaxy] --apikey [api key]
```
Where **[http://url-to-galaxy]** is the URL to your Galaxy instance for IRIDA and **[api key]** is the API key for your IRIDA Galaxy instance.

Please refer to the [Documentation](https://irida.corefacility.ca/documentation/) for more details.
