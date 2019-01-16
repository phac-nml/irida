### Address shovill related issues 

#### [PILON] Java/JVM heap allocation issues

[PILON] is a Java application and may require the JVM heap size to be set (e.g. `_JAVA_OPTIONS=-Xmx4g`).
If [shovill] under Galaxy submits jobs to a [SLURM] workload manager, it may be necessary to allot about 4G more through SLURM than through [shovill] `--ram` (default is `${SHOVILL_RAM:-4}` or 4G as of tool revision [865119fcb694]) so if you give [shovill] 4G, give the SLURM job 8G.

One way you can adjust the `$SHOVILL_RAM` environment variable is via the [conda environment][]. That is, if you find the conda environment containing `shovill` you can set up files in `etc/conda/activate.d` and `etc/conda/deactivate.d` to set environment variables.

```bash
cd galaxy/deps/_conda/bin/activate galaxy/deps/_conda/envs/__shovill@1.0.4
mkdir -p etc/conda/activate.d
mkdir -p etc/conda/deactivate.d

echo -e "export _OLD_SHOVILL_RAM=\$SHOVILL_RAM\nexport SHOVILL_RAM=8" >> etc/conda/activate.d/shovill-ram.sh
echo -e "export SHOVILL_RAM=\$_OLD_SHOVILL_RAM" >> etc/conda/activate.d/shovill-ram.sh
```

You could also get fancier with this by setting `SHOVILL_RAM` based on [GALAXY_MEMORY_MB][], which is assigned by Galaxy based on your job configuration and resource requirements. For example, by setting `SHOVILL_RAM=$($GALAXY_MEMORY_MB/1024)`.


[865119fcb694]: https://toolshed.g2.bx.psu.edu/view/iuc/shovill/865119fcb694
[SLURM]: https://slurm.schedmd.com
[PILON]: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4237348/
[shovill]: https://github.com/tseemann/shovill/
[conda environment]: https://conda.io/docs/user-guide/tasks/manage-environments.html#saving-environment-variables
[GALAXY_MEMORY_MB]: https://planemo.readthedocs.io/en/latest/writing_advanced.html#developing-for-clusters-galaxy-slots-galaxy-memory-mb-and-galaxy-memory-mb-per-slot
