# Galaxy - IRIDA Base Image

FROM bgruening/galaxy-stable:18.09

MAINTAINER Aaron Petkau, aaron.petkau@canada.ca

ENV GALAXY_CONFIG_BRAND IRIDA Galaxy (18.09)

WORKDIR /galaxy-central

ENV GALAXY_CONFIG_TOOL_SHEDS_CONFIG_FILE /etc/galaxy/tool_sheds_conf.xml

ADD ./galaxy/tool_sheds_conf.xml /etc/galaxy/tool_sheds_conf.xml

# Add my custom toolshed to /home/galaxy so that install-tools will work with IRIDA toolshed
ADD ./galaxy/tool_sheds_conf.xml /home/galaxy/tool_sheds_conf.xml

# Install IRIDA tools
ADD ./galaxy/irida-tools.yml $GALAXY_ROOT/irida-tools.yml
RUN install-tools $GALAXY_ROOT/irida-tools.yml
RUN chown -R galaxy:galaxy /tool_deps

RUN sed -i -e 's/#\( *allow_path_paste:\).*/\1 True/' /etc/galaxy/galaxy.yml

# Mark folders as imported from the host.
VOLUME ["/export/", "/data/", "/var/lib/docker"]

# Expose port 80 (webserver), 21 (FTP server), 8800 (Proxy)
EXPOSE :80
EXPOSE :21
EXPOSE :8800

# Autostart script that is invoked during container start
CMD ["/usr/bin/startup"]
