# Galaxy - IRIDA Base Image

FROM bgruening/galaxy-stable:17.01

Label maintainer Aaron Petkau, aaron.petkau@phac-aspc.gc.ca

ENV GALAXY_CONFIG_BRAND IRIDA Galaxy (17.01)

WORKDIR /galaxy-central

ENV GALAXY_CONFIG_TOOL_SHEDS_CONFIG_FILE /etc/galaxy/tool_sheds_conf.xml
ADD ./galaxy/tool_sheds_conf.xml /etc/galaxy/tool_sheds_conf.xml

RUN cpan App::cpanminus && rm -rf ~/.cpan
RUN cpanm Time::Piece XML::Simple Data::Dumper Bio::SeqIO && rm -rf ~/.cpanm

RUN sed -i -e 's/#allow_library_path_paste = False/allow_library_path_paste = True/' /etc/galaxy/galaxy.ini && \
        sed -i -e 's/force_beta_workflow_scheduled_for_collections=True/force_beta_workflow_scheduled_for_collections=False/' /etc/galaxy/galaxy.ini && \
        sed -i -e 's@^#conda_prefix.*@conda_prefix = /tool_deps/_conda@' /etc/galaxy/galaxy.ini && \
        sed -i -e 's@^#conda_ensure_channels@conda_ensure_channels@' /etc/galaxy/galaxy.ini && \
        sed -i -e 's@^#use_cached_dependency_manager = False@use_cached_dependency_manager = True@' /etc/galaxy/galaxy.ini && \
        sed -i -e 's/ --share//' /etc/galaxy/job_conf.xml

# Install SNVPhyl
RUN install-repository \
    "-r 4841b7148c44 --url https://irida.corefacility.ca/galaxy-shed/ -o nml --name suite_snvphyl_1_0_1 --panel-section-id SNVPhyl" && \
    find /galaxy-central/tool_deps/ -iname '.git' | xargs -I {} rm -rf {}

# Mark folders as imported from the host.
VOLUME ["/export/", "/data/", "/var/lib/docker"]

# Expose port 80 (webserver), 21 (FTP server), 8800 (Proxy)
EXPOSE :80
EXPOSE :21
EXPOSE :8800

# Autostart script that is invoked during container start
CMD ["/usr/bin/startup"]
