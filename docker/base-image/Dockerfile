# Galaxy - IRIDA Base Image

FROM bgruening/galaxy-stable:20.09

LABEL maintainer="Aaron Petkau <aaron.petkau@canada.ca>"

ENV GALAXY_CONFIG_BRAND IRIDA (Galaxy 20.09)

WORKDIR /galaxy-central

RUN sed -i -e 's/#\( *allow_path_paste:\).*/\1 True/' /etc/galaxy/galaxy.yml

# Install IRIDA tools
# Some cleanup commands from Galaxy Dockerfile
ADD ./galaxy/irida-tools.yml $GALAXY_ROOT/irida-tools.yml
RUN install-tools $GALAXY_ROOT/irida-tools.yml \
      && /tool_deps/_conda/bin/conda clean --packages -t -i \
      && rm -rf /tmp/* /root/.cache/ /var/cache/* $GALAXY_ROOT/client/node_modules/ $GALAXY_VIRTUAL_ENV/src/ /home/galaxy/.cache/ /home/galaxy/.npm

# Mark folders as imported from the host.
VOLUME ["/export/", "/data/", "/var/lib/docker"]

# Expose port 80 (webserver), 21 (FTP server), 8800 (Proxy)
EXPOSE :80
EXPOSE :21
EXPOSE :8800

# Autostart script that is invoked during container start
CMD ["/usr/bin/startup"]
