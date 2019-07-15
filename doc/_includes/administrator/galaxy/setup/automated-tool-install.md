The tool [Ephemeris](https://ephemeris.readthedocs.io) can be used to automate installing of tools in Galaxy. A list of tools to install is provided with the `irida-[version].zip` download on the [IRIDA releases][] page.  Instructions can be accessed on the [Automated tools install][] page.

The short version is to:

1. Install Ephemeris

   ```bash
   conda install -c bioconda ephemeris
   ```

2. Install tools

   ```bash
   shed-tools install --toolsfile tools-list.yml --galaxy [http://url-to-galaxy] --api_key [api key]
   ```

   Please replace **url-to-galaxy** and **api key** with appropriate values for your Galaxy instance.

You may want to monitor the Galaxy log files (e.g., `galaxy/*.log`) as the installation is proceeding.  This may take a while to download, build, and install all tools.

*Note: please make sure to check if `tbl2asn` requires updating. You can read more about this issue in our [FAQ][faq-tbl2asn]. Also please take a look through the __Manual installation of tools__ instructions to see if there are any additional dependencies you may need to install.  These can be added to the conda __galaxy__ environment setup previously.*

[IRIDA releases]: https://github.com/phac-nml/irida/releases
[Automated tools install]: https://github.com/phac-nml/irida/tree/development/packaging#automated-processupgrading
[faq-tbl2asn]: ../../faq/#tbl2asn-out-of-date
