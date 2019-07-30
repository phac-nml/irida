---
layout: default
search_title: "Troubleshooting IRIDA Pipelines"
description: "A troubleshooting guide for common pipeline issues in IRIDA."
---

# Troubleshooting Pipelines Guide
{:.no_toc}

This document describes common issues with IRIDA pipelines and how to resolve them.

* This comment becomes the table of contents
{:toc}

 When encountering an analysis pipeline error, the first step to troubleshooting is to figure out what sort of error was encountered. There are two types of errors that can occur: error with detailed information, and error with no detailed information.

# 1. Types of IRIDA job errors

## 1.1. Error with detailed information

This occurs when one of the Galaxy tools in the workflow reported an error and will show up with the status **Error** and a question mark **?** for more details. The stderr/stdout of the tool will be available in the preview.

![jobs-all-error-details.png][]

Additional details will be available after clicking on the job:

![job-error-details.png][]

Here, we get the exact tool **Prokka** and version `1.13` that is causing the error, along with additional details about the Galaxy instance where all these tools are being run.

## 1.2. Error with no detailed information

This occurs when there was an issue unrelated to a specific Galaxy tool (and so no detailed information can be obtained about a specific tool).

![job-error-nodetails.png][]

Examples where this error could occur include timeouts when transferring files to Galaxy or missing tools in Galaxy required by the pipeline. Normally, more details about these errors will be found in the IRIDA log file:

1. Timeout when uploading files to Galaxy

    ```
    25 Jul 2019 13:52:30,786 ERROR ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceAspect:65 - Error occured for submission: AnalysisSubmission [id=3, name=AssemblyAnnotation_20190725_SRR1952908, submitter=admin, workflowId=4673cf14-20eb-44e1-986b-ac7714f9a96f, analysisState=SUBMITTING, analysisCleanedState=NOT_CLEANED] changing to state ERROR
ca.corefacility.bioinformatics.irida.exceptions.UploadTimeoutException: Timeout while uploading, time limit = 2 seconds
        at ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService.filesToLibraryWait(GalaxyLibrariesService.java:245)
        at ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService.filesToLibraryToHistory(GalaxyHistoriesService.java:201)
        ...
        at java.lang.Thread.run(Thread.java:748)
Caused by: java.util.concurrent.TimeoutException
        at java.util.concurrent.FutureTask.get(FutureTask.java:205)
        at ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService.filesToLibraryWait(GalaxyLibrariesService.java:241)
        ... 28 more
    ```
    
    A quick solution for this particular error would be to increase the timeout limit for uploading files to Galaxy for pipelines (in this case, set low at 2 seconds). This can be done by setting `galaxy.library.upload.timeout=2` to some larger number in the `/etc/irida/irida.conf` file and restarting IRIDA.

2. Missing tools in Galaxy

    ```
    25 Jul 2019 14:16:16,393 ERROR ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceAspect:65 - Error occured for submission: AnalysisSubmission [id=4, name=AssemblyAnnotation_20190725_SRR1952908, submitter=admin, workflowId=4673cf14-20eb-4
4e1-986b-ac7714f9a96f, analysisState=SUBMITTING, analysisCleanedState=NOT_CLEANED] changing to state ERROR
ca.corefacility.bioinformatics.irida.exceptions.WorkflowException: GalaxyResponseException{status=400, responseBody={"err_msg": "Tool toolshed.g2.bx.psu.edu/repos/iuc/quast/quast/5.0.2 missing. Cannot add dummy datasets.", "err_code": 400014}, errorMessage=Tool toolshed.g2.b
x.psu.edu/repos/iuc/quast/quast/5.0.2 missing. Cannot add dummy datasets., errorCode=400014, traceback=null}
        at ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService.runWorkflow(GalaxyWorkflowService.java:123)
        at ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsync.executeAnalysis(AnalysisExecutionServiceGalaxyAsync.java:152)
        ...
        Caused by: GalaxyResponseException{status=400, responseBody={"err_msg": "Tool toolshed.g2.bx.psu.edu/repos/iuc/quast/quast/5.0.2 missing. Cannot add dummy datasets.", "err_code": 400014}, errorMessage=Tool toolshed.g2.bx.psu.edu/repos/iuc/quast/quast/5.0.2 missing. Cannot ad
d dummy datasets., errorCode=400014, traceback=null}
    ```
    
    A quick solution for this issue would be to log into Galaxy and make sure the required tool (and version of the tool) is installed. In this case it would be `toolshed.g2.bx.psu.edu/repos/iuc/quast/quast/5.0.2`.
    
    
In general, though, you may want to take a look at the specific Galaxy history used by this IRIDA analysis pipeline to run jobs.

# 2. Finding the Galaxy History used by an IRIDA pipeline

As mentioned in section [(1)][section-1], there are two types of errors that can occur in a pipeline: those with detailed information, and those without. Getting the Galaxy History used by the pipeline depends on which type of error you encounter. Once we have the Galaxy History id, we can log into Galaxy to find more information about what was going on with this particular jobs in this Galaxy history.

## 2.1. Getting Galaxy History when a job has error details

In this case, it's pretty straightforward to get the Galaxy History id, it's displayed in the error details.

![job-details-galaxy-history.png][]

This tells us that the **Galaxy History ID** used by this pipeline is `e85a3be143d5905b`.

## 2.2. Getting Galaxy History when job has no error details

In this case, it's a bit more difficult to find the Galaxy History id. We will have to log into the IRIDA database to search for it.

### 2.2.1. Logging into the IRIDA database

If you log into the machine running the IRIDA instance, you can find the database connection details in the `/etc/irida/irida.conf` file. For example:

```
jdbc.url=jdbc:mysql://localhost:3306/irida_test
jdbc.username=test
jdbc.password=test
```

This tells us the database software is running on the machine `localhost` and we want to use the database named **irida_test**, with username **test** and password **test**.

To log into this database, you can run:

```bash
mysql -u test -p --host localhost --database irida_test
```

### 2.2.2. Finding the Galaxy History id

Once we've logged into the database, we can run a query to get the Galaxy History id, but first we need the IRIDA Analysis pipeline id. This can be found in the page listing the pipelines:

![irida-job-id.png][]

In this case, our job id is `3`.

So, now back to the MySQL database query, we want to run:

```sql
SELECT id,name,analysis_state,remote_analysis_id FROM analysis_submission WHERE id = 3;
```

This should give us:

```
+----+----------------------------------------+----------------+--------------------+
| id | name                                   | analysis_state | remote_analysis_id |
+----+----------------------------------------+----------------+--------------------+
|  3 | AssemblyAnnotation_20190725_SRR1952908 | ERROR          | 2a56795cad3c7db3   |
+----+----------------------------------------+----------------+--------------------+
```

The field containing the Galaxy History id is `remote_analysis_id` (so the value we are looking for is `2a56795cad3c7db3`).

### 2.2.3. What if the Galaxy History id is NULL

If this value is `NULL`, then it's possible that the error occurred before a Galaxy History was created. You can get more information about the history of this IRIDA analysis pipeline execution from the audit tables (`analysis_submission_AUD`). Please try running:

```sql
SELECT id,name,analysis_state,modified_date,remote_analysis_id FROM analysis_submission_AUD WHERE id = 3;
```

```
+----+----------------------------------------+----------------+---------------------+--------------------+
| id | name                                   | analysis_state | modified_date       | remote_analysis_id |
+----+----------------------------------------+----------------+---------------------+--------------------+
|  3 | AssemblyAnnotation_20190725_SRR1952908 | NEW            | 2019-07-25 13:48:33 | NULL               |
|  3 | AssemblyAnnotation_20190725_SRR1952908 | PREPARING      | 2019-07-25 13:52:26 | NULL               |
|  3 | AssemblyAnnotation_20190725_SRR1952908 | PREPARED       | 2019-07-25 13:52:27 | 2a56795cad3c7db3   |
|  3 | AssemblyAnnotation_20190725_SRR1952908 | SUBMITTING     | 2019-07-25 13:52:27 | 2a56795cad3c7db3   |
|  3 | AssemblyAnnotation_20190725_SRR1952908 | ERROR          | 2019-07-25 13:52:30 | 2a56795cad3c7db3   |
+----+----------------------------------------+----------------+---------------------+--------------------+
```

This lets us see the history of the job as it was processed through IRIDA (and includes the `modified_date` giving an idea of when each stage occurred). You can see that the first two states **NEW** and **PREPARING** have a `NULL` value for `remote_analysis_id`. If the IRIDA pipeline errored in these states, there would not have been a Galaxy History created. So, you can skip trying to check the Galaxy History for more details about the job.

# 3. Viewing the Galaxy History used by the IRIDA analysis pipeline

## 3.1. Logging into Galaxy

Once we have the Galaxy History id, we can move on to logging into Galaxy to view more details about what went wrong with the IRIDA analysis pipeline. The first step is logging into Galaxy as the same user used by IRIDA.

*Note: if you do not know which Galaxy instance IRIDA is making use of, you can find this in the `/etc/irida/irida.conf` file as `galaxy.execution.url=http://GALAXY`. You can find the username as `galaxy.execution.email=galaxy-user@galaxy.org`. IRIDA uses the Galaxy API to login, which is different from the password, so you may have to check with your administrator for the password used to log into Galaxy.*

![galaxy-home.png][]

## 3.2. Viewing all Galaxy histories

Galaxy will default to one of the histories run by IRIDA. To see all the Galaxy histories, you can go to the **Saved Histories** page.

![galaxy-saved-histories.png][]

This should bring us to a list of all the Galaxy histories.

![galaxy-histories-list.png][]

## 3.3. Viewing the correct history

To view the correct History in Galaxy, we can skip directly to it using the following URL `http://GALAXY/histories/view?id=[Galaxy History id]` Where **Galaxy History id** is the id we discovered from step 1.2 (e.g., `e85a3be143d5905b`). For example, for me, going to <http://localhost:48888/histories/view?id=e85a3be143d5905b> brings up:

![galaxy-view-history.png][]

This is the History corresponding to the analysis pipeline in IRIDA that failed. I can now see all the failed jobs (in red). Clicking on the bug icon in one of these jobs will show me the error message (which in this case, is the same as was recorded by IRIDA).

![galaxy-job-debug.png][]

Clicking the **i** icon gives me more information about the Galaxy job.

![galaxy-job-information.png][]

Srolling to the bottom of this screen there is a lot of information about the underlying infrastructure and software:

![galaxy-job-information2.png][]

For example, this contains the exact command-line that was run, system resources uses, the **Runner Job ID** (in this case `50` which is the slurm job id if using slurm to run jobs), as well as the **Path** to the dependency software (in this case `/export/tool_deps/_conda/envs/__prokka@1.13`, which is the location of the conda environment containing Prokka).

## 3.4. Diagnosing the problem

All of this information could be useful to figure out the underlying issue for this IRIDA pipeline. In this case, from the **Prokka** error message:

>[tbl2asn] This copy of tbl2asn is more than a year old.  Please download the current version.

This is likely the [Prokka tbl2asn out of date][prokka-tbl2asn] issue. Solving this requires updating `tbl2asn` used by **Prokka**, which will be located under the conda environment (which you can find from the job details information above, in this case it's `/export/tool_deps/_conda/envs/__prokka@1.13`).

Alternatively, if you are using a cluster scheduler to schedule jobs, you may wish to view information from this scheduler for this job. The id to use should be located in the job details information (id `50` shown above). Using this information, you could log into your cluster and run:

```bash
sacct -j 50 --format="jobid,jobname%20,maxrss,maxrssnode,ntasks,elapsed,state,exitcode"
```

```
       JobID              JobName     MaxRSS MaxRSSNode   NTasks    Elapsed      State ExitCode 
------------ -------------------- ---------- ---------- -------- ---------- ---------- -------- 
          50           g46_prokka                                  00:01:19  COMPLETED      0:0 
    50.batch                batch     25128K     node-5        1   00:01:19  COMPLETED      0:0
```

Here, `sacct` is a command that comes with slurm and lets you look up information about a job run on the cluster (specified as `-j 50`). The `--format=` option specifies what information to print (e.g., **JobID** and **JobName**). The `jobname%20` specifies that the **JobName** column should be 20 characters wide (useful for printing longer names). The **MaxRSSNode** tells you the cluster node the job executed on that used the maximum RSS (Resident Set Size, memory used by software). See documentation about your cluster scheduler for more information.

# 4. Viewing additional Galaxy job information

If the instructions for [(3)][section-3] do not lead to a solution, there are additional files you can check in Galaxy to help diagnose an issue.

## 4.1. Galaxy log files

The Galaxy log files are one possible source of additional information as to what went wrong with an analysis pipeline in IRIDA. These are often located in the files `galaxy/*.log` but this depends a lot on your specific Galaxy setup.

Looking through these log files at around the time of the pipeline error can give you clues as to what went wrong. For example:

```
galaxy.jobs.runners.drmaa DEBUG 2019-07-30 15:14:49,863 (3362/3363) state change: job finished normally
galaxy.jobs.output_checker INFO 2019-07-30 15:14:49,934 Job 3362: Fatal error: Exit code 2 ()
galaxy.jobs.output_checker DEBUG 2019-07-30 15:14:49,934 Tool exit code indicates an error, failing job.
galaxy.jobs.output_checker DEBUG 2019-07-30 15:14:49,934 job failed, standard error is - [Fatal error: Exit code 2 ()
/tool_deps/_conda/envs/__refseq_masher@0.1.1/lib/python3.6/importlib/_bootstrap.py:219: RuntimeWarning: numpy.dtype size changed, may indicate binary incompatibility. Expected 96, got 88
  return f(*args, **kwds)
2019-07-30 15:14:46,399 WARNING: which exited with non-zero code 1 with command "which mash" [in /tool_deps/_conda/envs/__refseq_masher@0.1.1/lib/python3.6/site-packages/refseq_masher/utils.py:44]
2019-07-30 15:14:46,399 WARNING:  [in /tool_deps/_conda/envs/__refseq_masher@0.1.1/lib/python3.6/site-packages/refseq_masher/utils.py:45]
Usage: refseq_masher contains [OPTIONS] INPUT...

Error: Invalid value for "--mash-bin": Mash does not exist at "mash". Please install Mash to your $PATH
]
```

These messages indicates that for Galaxy Job **3362** failed with an error. The standard error contains messages `WARNING: which exited with non-zero code 1 with command "which mash" [in /tool_deps/_conda/envs/__refseq_masher@0.1.1`. This suggests that the issue is that the `mash` binary is not available in the conda environment `__refseq_masher@0.1.1`. So, you could activate this environment and check to see what's going on. For example:

```
# First log into Galaxy machine then use commands like below
PATH=/tool_deps/_conda/bin/:$PATH conda activate /tool_deps/_conda/envs/__refseq_masher\@0.1.1/

mash
```

```
bash: mash: command not found
```

Huh!? `mash` is not found. You could try re-installing `mash` to this environment (`conda install mash`) and try the tool again.

### 4.1.1. Galaxy Job Numbers in log file

When scanning through the log file you will see lines like:

```
galaxy.jobs.runners.drmaa INFO 2019-07-30 15:14:44,983 (3362) queued as 3363
galaxy.jobs DEBUG 2019-07-30 15:14:44,984 (3362) Persisting job destination (destination id: slurm_cluster)
galaxy.jobs.runners.drmaa DEBUG 2019-07-30 15:14:45,646 (3362/3363) state change: job is running
...
galaxy.jobs.runners.drmaa DEBUG 2019-07-30 15:14:49,863 (3362/3363) state change: job finished normally
```

Here, the number **3362** in `(3362)` or `(3362/3363)` is the Galaxy Job id, which is also displayed in the information for an individual Galaxy Job in the Galaxy interface:

![galaxy-job-id.png][]

While the number **3363** in `queued as 3363` or `(3362/3363)` is the cluster/job runner id. This is also displayed in the information for an individual Galaxy Job in the Galaxy interface:

![job-runner-id.png][]

## 4.2. Galaxy job working directories

For each job that is run in Galaxy, a unique directory is made to store outputs of the job as well as additional information used to run the job. It can sometimes be useful to switch into this directory and explore the contained files to gain more insight about a particular failure.

### 4.2.1 Galaxy configuration

By default, Galaxy will clean up the files in each job directory after the job finishes, but Galaxy can be configured to keep these files. To determine if this information still exists on your filesystem for you to explore, you will have to check the [cleanup_job Galaxy configuration][cleanup_job] parameter in the `config/galaxy.yml` file.

```
  cleanup_job: onsuccess
```

Possible values are `always`, `onsuccess`, and `never`. You will want this to be set to either `onsuccess` or `never`, so that Galaxy leaves the job working directories around once the job is completed.

### 4.2.2. Find Galaxy job working directory parent

To find the Galaxy job working directory, you will have to find the parent directory storing these files. By default, this will be `galaxy/database/jobs_directory`, but this is configurable with the [job_working_directory][job_working_directory] configuration option in the `config/galaxy.yml` file.

### 4.2.3. Find Galaxy job id

You will also need the Galaxy job id, which should be available from the Galaxy interface in the job information page.

![galaxy-job-id.png][]

In this case, the job id is `3362`.

### 4.2.4. Find Galaxy job working directory

Once you have the Galaxy job id, and the parent to the working directories, you can change into the job working directory using the following pattern of directory names (where **1234** is the job id):

```
cd database/jobs_directory/001/1234
```

As another example, with job `3362` the directory should be:

```
cd database/jobs_directory/003/3362
```

For longer job ids (such as **1234567**) there may be more intermediate directories. For example:

```
cd database/jobs_directory/001/234/1234567
```

### 4.2.5. Examine job working directory files

Once inside the job working directory there are a number of files that may be useful to examine.

#### 4.2.5.1. Standard out/error

These files are named `galaxy_[JOBID].o` and `galaxy_[JOBID].e`. These should also be available from the Galaxy interface (in the job information page), but can also be inspected here.  For example:

```bash
cat galaxy_3362.e
```

```
/tool_deps/_conda/envs/__refseq_masher@0.1.1/lib/python3.6/importlib/_bootstrap.py:219: RuntimeWarning: numpy.dtype size changed, may indicate binary incompatibility. Expected 96, got 88
  return f(*args, **kwds)
2019-07-30 18:29:31,832 WARNING: which exited with non-zero code 1 with command "which mash" [in /tool_deps/_conda/envs/__refseq_masher@0.1.1/lib/python3.6/site-packages/refseq_masher/utils.py:44]
2019-07-30 18:29:31,832 WARNING:  [in /tool_deps/_conda/envs/__refseq_masher@0.1.1/lib/python3.6/site-packages/refseq_masher/utils.py:45]
Usage: refseq_masher contains [OPTIONS] INPUT...

Error: Invalid value for "--mash-bin": Mash does not exist at "mash". Please install Mash to your $PATH
```

#### 4.2.5.2. The `working` directory

This directory is the current working directory of the tool when it's run in Galaxy. It may contain temporary files, or input/output files that were actively being used by the job.

```
ls working/
conda_activate.log  SRR1952908_1.fastq  SRR1952908_2.fastq
```

#### 4.2.5.3. Galaxy and tool scripts

These are the files that get submitted to a cluster/executed by Galaxy on your machine.

1. `galaxy_[JOBID].sh`

    The main script submitted by Galaxy to your cluster.

2. `tool_script.sh`

    The actual file which loads up the environment for your tools and runs the tools. This file should contain the command that is printed by the Galaxy interface as the command-line used to execute the tool.
    
    ![galaxy-command-line.png][]
    
3. Others

    Other files contain information relating to the machine the tool executed on (memory and cpu info) as well as additional Galaxy files.

# 5. Rerunning Galaxy jobs

Sometimes it can be useful to rerun a Galaxy job that has failed previously to see if the error is reproducible. This can be accomplished through two ways: the Galaxy user interface (UI) or the command-line.

## 5.1. Rerunning jobs in Galaxy UI

To rerun jobs from the Galaxy UI, you will first have to log into Galaxy and find the appropriate history for the IRIDA analysis pipeline (see section [(2)][section-2] for details).

Once you have the Galaxy History in front of you, you can find the errored job and click the rerun job icon. All the parameters should be defaulted to the same as what the job was initially run with:

![rerun-galaxy-job.png][]

What you should check for when rerunning the job is whether the error is reproducible, or perhaps there is a different error now showing up.

## 5.2. Rerunning jobs from command-line

**DANGER: Using the instructions in this method (rerunning the `tool_script.sh` file) *will* overwrite the previously generated files by this tool in Galaxy. Please only do this for tools that have errored and where you are certain you do not need previously-generated output files.**

**Use this method only as a last resort and at your own risk.**

If rerunning from the Galaxy UI does not give any clues as to what's going on, as a last resort you can also rerun from the command-line using the same environment as what Galaxy used to load tool dependencies.

To do this, first find and change to the job working directory (as described in section [(4.2)][section-4.2]). For example, for job id `3362` we would change to:

```
cd database/jobs_directory/003/3362
```

Now, in here lets look at the `tool_script.sh` file:

```
#!/bin/bash
...
. /export/tool_deps/_conda/bin/activate '/export/tool_deps/_conda/envs/__refseq_masher@0.1.1' > conda_activate.log 2>&1
...
ln -s "/irida/sequence-files/1/2/SRR1952908_1.fastq" "SRR1952908_1.fastq" && ln -s "/irida/sequence-files/2/2/SRR1952908_2.fastq" "SRR1952908_2.fastq" &&  refseq_masher -vv contains --output refseq_masher-contains.tab --output-type tab --top-n-results 0 --parallelism "${GALAXY_SLOTS:-1}" --min-identity 0.9 --max-pvalue 0.01 "SRR1952908_1.fastq" "SRR1952908_2.fastq"
```

This file first tries to load up the tool dependencies (`. /export/tool_deps/_conda/bin/activate ...`). Then, this runs the actual command to produce the results.

If you are on a machine that has access to the same conda environment (has access to `/export/tool_deps/_conda/bin/activate ...`), then you could try executing this script yourself (or parts of this script).

```
tool_script.sh
```

This could help give you insight into exactly why the specific tool is failing. However, you may have to modify the script to get it to work properly.

**DANGER: Running `tool_script.sh` *will* overwrite previously generated files by this Galaxy job. Please only do this on jobs you are certain you do not need the output files for anymore.**

# 6. Examples

To tie everything together, let's work through troubleshooting a few example pipeline errors in IRIDA.

## 6.1. SNVPhyl pipeline error



[jobs-all-error-details.png]: ../images/jobs-all-error-details.png
[job-error-details.png]: ../images/job-error-details.png
[job-error-nodetails.png]: ../images/job-error-nodetails.png
[job-details-galaxy-history.png]: ../images/job-details-galaxy-history.png
[irida-job-id.png]: ../images/irida-job-id.png
[galaxy-home.png]: ../images/galaxy-home.png
[galaxy-saved-histories.png]: ../images/galaxy-saved-histories.png
[galaxy-histories-list.png]: ../images/galaxy-histories-list.png
[galaxy-view-history.png]: ../images/galaxy-view-history.png
[galaxy-job-debug.png]: ../images/galaxy-job-debug.png
[galaxy-job-information.png]: ../images/galaxy-job-information.png
[galaxy-job-information2.png]: ../images/galaxy-job-information2.png
[prokka-tbl2asn]: {{ site.baseurl }}/administrator/faq/#1-tbl2asn-out-of-date
[galaxy-job-id.png]: ../images/galaxy-job-id.png
[job-runner-id.png]: ../images/job-runner-id.png
[cleanup_job]: https://github.com/galaxyproject/galaxy/blob/v19.05/config/galaxy.yml.sample#L1619
[job_working_directory]: https://github.com/galaxyproject/galaxy/blob/v19.05/config/galaxy.yml.sample#L487
[galaxy-command-line.png]: ../images/galaxy-command-line.png
[rerun-galaxy-job.png]: ../images/rerun-galaxy-job.png
[section-1]: #1-types-of-irida-job-errors
[section-2]: #2-finding-the-galaxy-history-used-by-an-irida-pipeline
[section-3]: #3-viewing-the-galaxy-history-used-by-the-irida-analysis-pipeline
[section-4.2]: #42-galaxy-job-working-directories 