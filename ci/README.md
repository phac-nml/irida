IRIDA CI Docker Image
=====================

The files in this directory are used to build a Docker image for testing in GitLab CI.

Building Docker Image
---------------------

From this directory run the following command:

``bash
docker build -t irida-testing .
```

This will build an image with the requirements for CI integration.  To enable for testing you should push this image to your local docker server.


GitLab CI Server Configuration
------------------------------

If using gitlab, the CI server must be setup to allow docker builds.  

Command:
```bash
gitlab-ci-multi-runner register --url "http://your.gitlab.server/" --registration-token "gitlabRegistrationToken" --description "docker-runner-name" --executor "docker" --docker-image "your.docker.hub/irida-testing:latest" --docker-services mariadb:10.0 --docker-services apetkau/galaxy-irida-16.10-it:0.1 --tag-list docker

```

After running this command you should have the following section in your GitLab configuration.

Example `/etc/gitlab-runner/config.toml`:

```toml
concurrent = 1
check_interval = 0

[[runners]]
  name = "runner-name"
  url = "http://ci.server.url/"
  token = "generated-token"
  executor = "docker"
  [runners.docker]
    tls_verify = false
    image = "your.docker.hub/irida-testing:latest"
    privileged = false
    disable_cache = false
    volumes = ["/cache"]
    services = ["mariadb:10.0", "apetkau/galaxy-irida-16.10-it:0.1"]
  [runners.cache]

```

Parameters
----------

* irida.it.rootdirectory: Root directory to store test data files.
* irida.it.nosandbox: Run Chrome in no sandbox mode for integration tests (WARNING: This mode is unsafe for running on your desktop.  Only use this mode for running in docker).
