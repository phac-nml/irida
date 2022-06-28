Custom IRIDA Libraries
======================

These libraries are included as `.jar` files since they're not easily available as Maven dependencies.

FastQC has been modified from its original repository at <https://github.com/s-andrews/FastQC> to allow us to use it as a library instead of running the executable.

cisd-jhdf5 is included in the above FastQC repository and is required for reading `.fast5` files.  The `.jar` file has the `commons-lang` and `commons-io` libraries built in to the jar.  Since IRIDA already imports these libraries, it causes issues since Maven scans the library files twice.  The `cisid-jhdf5.jar` file included here has those `commons-*` class files removed. 

The included samtools and jbzip2 jars included are directly from the FastQC repository unmodified.

Note: These are automatically installed via Gradle