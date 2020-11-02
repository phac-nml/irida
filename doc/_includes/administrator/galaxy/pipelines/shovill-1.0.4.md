### Address shovill related issues 

#### [PILON] Java/JVM heap allocation issues

[PILON] is a Java application and may require the JVM heap size to be set (e.g. `_JAVA_OPTIONS=-Xmx4g`).

#### Shovill memory issue

The memory allocated to Shovill should, by default, be automatically set by the Galaxy `$GALAXY_MEMORY_MB` environment variable (see the [planemo docs][planemo] for more information). However, this can be overridden by setting the `$SHOVILL_RAM` environment variable. One way you can adjust the `$SHOVILL_RAM` environment variable is via the [conda environment][]. That is, if you find the conda environment containing `shovill` you can set up files in `etc/conda/activate.d` and `etc/conda/deactivate.d` to set environment variables.

```bash
cd galaxy/deps/_conda/bin/activate galaxy/deps/_conda/envs/__shovill@1.0.4
mkdir -p etc/conda/activate.d
mkdir -p etc/conda/deactivate.d

echo -e "export _OLD_SHOVILL_RAM=\$SHOVILL_RAM\nexport SHOVILL_RAM=8" >> etc/conda/activate.d/shovill-ram.sh
echo -e "export SHOVILL_RAM=\$_OLD_SHOVILL_RAM" >> etc/conda/activate.d/shovill-ram.sh
```

Alternatively, you can set environment variables within the [Galaxy Job Configuration][].

[PILON]: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4237348/
[conda environment]: https://conda.io/docs/user-guide/tasks/manage-environments.html#saving-environment-variables
[planemo]: https://planemo.readthedocs.io/en/latest/writing_advanced.html#cluster-usage
[Galaxy Job Configuration]: https://docs.galaxyproject.org/en/release_20.05/admin/jobs.html#environment-modifications
