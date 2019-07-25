---
layout: default
search_title: "Troubleshooting IRIDA"
description: "A troubleshooting guide for common issues in IRIDA."
---

# Troubleshooting Guide
{:.no_toc}

This document describes common issues with IRIDA and how to resolve them.

* This comment becomes the table of contents
{:toc}

# 1. Analysis Pipeline Errors

When encountering an analysis pipeline error, the first step to troubleshooting is to figure out what sort of error was encountered. There are two types of errors that can occur: error with detailed information, and error with no detailed information.

## 1.1. Types of IRIDA job errors

### 1.1.1. Error with detailed information

This occurs when one of the Galaxy tools in the workflow reported an error and will show up with the status **Error** and a question mark **?** for more details. The stderr/stdout of the tool will be available in the preview.

![jobs-all-error-details.png][]

Additional details will be available after clicking on the job:

![job-error-details.png][]

Here, we get the exact tool **Prokka** and version `1.13` that is causing the error, along with additional details about the Galaxy instance where all these tools are being run.

### 1.1.2. Error with no detailed information

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

## 1.2. Finding the Galaxy History used by an IRIDA pipeline

As mentioned in section (1), there are two types of errors that can occur in a pipeline: those with detailed information, and those without. Getting the Galaxy History used by the pipeline depends on which type of error you encounter. Once we have the Galaxy History id, we can log into Galaxy to find more information about what was going on with this particular jobs in this Galaxy history.

### 1.2.1. Getting Galaxy History when a job has error details

In this case, it's pretty straightforward to get the Galaxy History id, it's displayed in the error details.

![job-details-galaxy-history.png][]

This tells us that the **Galaxy History ID** used by this pipeline is `e85a3be143d5905b`.

### 1.2.2. Getting Galaxy History when job has no error details

In this case, it's a bit more difficult to find the Galaxy History id. We will have to log into the IRIDA database to search for it.

#### 1.2.2.1. Logging into the IRIDA database
{:.no_toc}

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

#### 1.2.2.2. Finding the Galaxy History id
{:.no_toc}

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

#### 1.2.2.3. What if the Galaxy History id is NULL
{:.no_toc}

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

## 1.3. Viewing the Galaxy History used by the IRIDA analysis pipeline

### 1.3.1. Logging into Galaxy

Once we have the Galaxy History id, we can move on to logging into Galaxy to view more details about what went wrong with the IRIDA analysis pipeline. The first step is logging into Galaxy as the same user used by IRIDA.

*Note: if you do not know which Galaxy instance IRIDA is making use of, you can find this in the `/etc/irida/irida.conf` file as `galaxy.execution.url=http://GALAXY`. You can find the username as `galaxy.execution.email=galaxy-user@galaxy.org`. IRIDA uses the Galaxy API to login, which is different from the password, so you may have to check with your administrator for the password used to log into Galaxy.*

![galaxy-home.png][]

### 1.3.2. Viewing all Galaxy histories

Galaxy will default to one of the histories run by IRIDA. To see all the Galaxy histories, you can go to the **Saved Histories** page.

![galaxy-saved-histories.png][]

This should bring us to a list of all the Galaxy histories.

![galaxy-histories-list.png][]

### 1.3.3. Viewing the correct history

To view the correct History in Galaxy, we can skip directly to it using the following URL `http://GALAXY/histories/view?id=[Galaxy History id]` Where **Galaxy History id** is the id we discovered from step 1.2 (e.g., `e85a3be143d5905b`). For example, for me, going to <http://localhost:48888/histories/view?id=e85a3be143d5905b> brings up:

![galaxy-view-history.png][]

This is the History corresponding to the analysis pipeline in IRIDA that failed. I can now see all the failed jobs (in red). Clicking on the bug icon in one of these jobs will show me the error message (which in this case, is the same as was recorded by IRIDA).

![galaxy-job-debug.png][]

Clicking the **i** icon gives me more information about the Galaxy job.

![galaxy-job-information.png][]

Srolling to the bottom of this screen there is a lot of information about the underlying infrastructure and software:

![galaxy-job-information2.png][]

For example, this contains the exact command-line that was run, system resources uses, the **Runner Job ID** (in this case `50` which is the slurm job id if using slurm to run jobs), as well as the **Path** to the dependency software (in this case `/export/tool_deps/_conda/envs/__prokka@1.13`, which is the location of the conda environment containing Prokka).

### 1.3.4. Diagnosing the problem

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



[jobs-all-error-details.png]: images/jobs-all-error-details.png
[job-error-details.png]: images/job-error-details.png
[job-error-nodetails.png]: images/job-error-nodetails.png
[job-details-galaxy-history.png]: images/job-details-galaxy-history.png
[irida-job-id.png]: images/irida-job-id.png
[galaxy-home.png]: images/galaxy-home.png
[galaxy-saved-histories.png]: images/galaxy-saved-histories.png
[galaxy-histories-list.png]: images/galaxy-histories-list.png
[galaxy-view-history.png]: images/galaxy-view-history.png
[galaxy-job-debug.png]: images/galaxy-job-debug.png
[galaxy-job-information.png]: images/galaxy-job-information.png
[galaxy-job-information2.png]: images/galaxy-job-information2.png
[prokka-tbl2asn]: {{ site.baseurl }}/administrator/faq/#1-tbl2asn-out-of-date