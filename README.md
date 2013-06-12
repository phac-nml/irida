IRIDA Web
---------
`irida-web` is the web-interface for IRIDA.

The IRIDA API project requires the creation of certain directories prior to execution:

    sudo mkdir -p /home/irida/sequence-files
    sudo chown -R you:bioinformatics /home/irida/

Client Side Testing Set-up
==========================
1. Download and install [node.js](http://nodejs.org).
   > **NOTE**: PhantomJS fails to start under the following versions of node: [ 0.10.8, 0.10.9, 0.10.10 ]. We recommend that you download version 0.10.6 (http://nodejs.org/dist/v0.10.6/node-v0.10.6.tar.gz).
2. Install `git`.
3. In irida-web main directory:
    1. `npm install -g karma@~0.9.2`
    2. `npm install -g phantomjs@~1.8`
    3. `npm install`

Package Client Side
===================
1. `mvn package`

`curl` Kung-Fu
==============
While integration and unit tests are very helpful for assessing the performance and functionality of our code, and a fancy UX/UI is essential for end-users, sometimes we want to be able to very quickly test out some functionality without opening a web browser or running a complete suite of tests. Below are some quick code examples that you can use to run some tests against the REST API exposed by this project.

### `POST`-ing a file to the server
#### Example `curl` command
```bash
curl -v -u fbristow:password1 http://localhost:8080/api/projects/*projectId*/sequenceFiles -F "file=@/tmp/sequenceFile.fasta"
```
#### Explanation
When `POST`-ing a file to the server, the server will respond with `201 Created`. Links to created resources can be found in the HTTP headers of the response. The `-v` option on `curl` increases the verbosity so that the HTTP headers will be printed to the terminal.

Our endpoint is protected by Basic HTTP Authentication, so the `-u fbristow:password1` option tells `curl` to use Basic HTTP authentication with the username `fbristow` and the password `password1`.

The URL should be a complete URL for a project (note that the *projectId* section should be filled with a valid project identifier).

Finally, the `-F "file=@/tmp/sequenceFile.fasta"` option tells `curl` to `POST` the URL with the HTTP header `Content-Type: multipart/form-data`. The form that is submitted to the server contains a field called `file`, and the contents of that field are filled in using the contents of the file that can be found at `/tmp/sequenceFile.fasta`. You should use the path of whatever file you are trying to upload to the server.
