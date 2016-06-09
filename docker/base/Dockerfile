#Galaxy - SNVPhyl
#
# VERSION       1.0

FROM bgruening/galaxy-stable:15.10

MAINTAINER Aaron Petkau, aaron.petkau@gmail.com

ENV GALAXY_CONFIG_BRAND IRIDA/SNVPhyl v1.0

WORKDIR /galaxy-central

ENV GALAXY_CONFIG_TOOL_SHEDS_CONFIG_FILE /etc/galaxy/tool_sheds_conf.xml
ADD ./galaxy/tool_sheds_conf.xml /etc/galaxy/tool_sheds_conf.xml

ADD ./galaxy/install_workflow_wrapper.sh /usr/bin/install-workflow
RUN chmod +x /usr/bin/install-workflow
ADD ./galaxy/irida_workflow_structure.ga /tmp/irida_workflow_structure.ga

# workaround for a Docker AUFS bug: https://github.com/docker/docker/issues/783#issuecomment-56013588
RUN mkdir /etc/ssl/private-copy; mv /etc/ssl/private/* /etc/ssl/private-copy/; rm -r /etc/ssl/private; mv /etc/ssl/private-copy /etc/ssl/private; chmod -R 0700 /etc/ssl/private; chown -R postgres /etc/ssl/private

# Install SNVPhyl
RUN install-repository \
    "-r 4e41de612a14 --url https://irida.corefacility.ca/galaxy-shed/ -o nml --name suite_snvphyl_1_0_0 --panel-section-id SNVPhyl" && \
    install-workflow \
    "/tmp/irida_workflow_structure.ga" && \
    rm /tmp/irida_workflow_structure*.ga && \
    find /galaxy-central/tool_deps/ -iname '.git' | xargs -I {} rm -rf {}

# Mark folders as imported from the host.
VOLUME ["/export/", "/data/", "/var/lib/docker"]

# Expose port 80 (webserver), 21 (FTP server), 8800 (Proxy)
EXPOSE :80
EXPOSE :21
EXPOSE :8800

# Autostart script that is invoked during container start
CMD ["/usr/bin/startup"]
