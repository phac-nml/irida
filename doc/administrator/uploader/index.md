---
layout: default
---

This document describes how the IRIDA data uploader for the Illumina MiSeq instrument can be used.

* This comment becomes the table of contents
{:toc}

Overview
========
The NGS Archive Miseq Uploader is to be used in conjunction with a NGS Archive REST API to store newly run Miseq sequencing runs in a repository.  The uploader will scan a directory of Miseq runs, scan the Samplesheet.csv file to collection information about the run, then upload the files to the REST API.

The uploader can be run in a single directory mode which will upload individual Miseq run directories, or in a scanning mode which will look for directories that have not been previously uploaded then send them to the API.

When a directory completes uploading, it will be marked with a *.miseqUploaderComplete* file that prevents it from being uploaded twice when using scanning mode.

Running NGS Archive Miseq Uploader
==================================

Arguments
---------

* `-b,--base-url [ARG]`
  Base NGS Archive REST API URL (required)

* `-u,--username [ARG]`
  Username for NGS Archive REST API

* `-p,--password [ARG]`
  Password for NGS Archive REST API

* `--project [ARG]`
  Project ID to upload a directory of files to.  This option will override the
 project ID in a Miseq run Samplesheet.csv file.  (Optional)

* `-s,--scan [ARG]`
  Directory to scan for new Miseq runs.  This option enables the directory scanning mode.

* `-v,--verbose`
  Prints more information about each request

* `-h,--help`
  Print help documentation

Usage Examples
--------------

### Upload a single Miseq directory

To upload a single Miseq directory you must provide the application with the path to the REST API and path to the directory you want to upload.

```bash
java -jar irida-tools-0.0.1-SNAPSHOT-jar-with-dependencies.jar miseq-upload -b http://irida.ca/api --username test_user --password test_password /path/to/example_data/example_run
Uploading directory /path/to/example_data/example_run
Created Miseq Run at http://localhost:8080/miseqrun/1
Created sample http://localhost:8080/projects/1/samples/6
Created sample http://localhost:8080/projects/1/samples/7
Created sample http://localhost:8080/projects/1/samples/8
...
sent fiel1_1.fastq.gz to http://localhost:8080/projects/1/samples/28/sequenceFiles/1
sent fiel1_2.fastq.gz to http://localhost:8080/projects/1/samples/24/sequenceFiles/2
sent fiel2_1.fastq.gz to http://localhost:8080/projects/1/samples/22/sequenceFiles/3
...
Marking directory /path/to/example_data/example_run completed.
```

### Upload multiple directories

Uploading multiple directories functions exactly like uploading a single directory.  A user is able to specify multiple directories to be uploaded at once using the same username, password, and REST API URL.

```bash
java -jar irida-tools-0.0.1-SNAPSHOT-jar-with-dependencies.jar miseq-upload -b http://irida.ca/api --username test_user --password test_password /path/to/example_data/example_run /path/to/example_data/another_run
```

### Upload all new Miseq directories

To upload all new Miseq directories that haven't been uploaded before, you must use the scan (-s or --scan) option.  This will look for directories that haven't been marked with a *.miseqUploaderComplete* file and append them to the list to be uploaded.

```bash
java -jar irida-tools-0.0.1-SNAPSHOT-jar-with-dependencies.jar miseq-upload -b http://irida.ca/api --username test_user --password test_password --scan /path/to/example_data/
Checking /path/to/example_data/data1
Adding directory for upload: /path/to/example_data/data1
Checking /path/to/example_data/data2
Adding directory for upload: /path/to/example_data/data2
Checking /path/to/example_data/data
Adding directory for upload: /path/to/example_data/data
Uploading directory /path/to/example_data/data1
Created Miseq Run at http://localhost:8080/miseqrun/1
Created sample http://localhost:8080/projects/4/samples/6
Created sample http://localhost:8080/projects/4/samples/7
Created sample http://localhost:8080/projects/4/samples/8
...
sent fiel1_1.fastq.gz to http://localhost:8080/projects/4/samples/6/sequenceFiles/1
sent fiel1_2.fastq.gz to http://localhost:8080/projects/4/samples/14/sequenceFiles/2
sent fiel2_1.fastq.gz to http://localhost:8080/projects/4/samples/11/sequenceFiles/3
...
Marking /path/to/example_data/data1 completed.
```

Errors and Warnings
-------------------

* /path/to/example_data/Data/Intensities/BaseCalls/Undetermined_S0_L001_R2_001.fastq.gz: Sample named Undetermined doesn't exist in sample worksheet.  Skipping file.
  >  Some Miseq runs have a file named *Undetermied* in the BaseCalls directory.  This file is produced when there are reads that are unable to be placed in a sample.  This file is ignored because it cannot be associatd with a sample or project.

* warning: directory /path/to/example_data is not a valid MiSeq directory, skipping...
  > The directory was not able to be parsed as a valid Miseq directory.  It may not have a SampleSheet.csv file.
