# Galaxy - Irida Integration Testing Image
#
#

FROM apetkau/galaxy-irida-16.10:0.15.0

MAINTAINER Aaron Petkau, aaron.petkau@phac-aspc.gc.ca

ENV GALAXY_CONFIG_BRAND IRIDA Galaxy

WORKDIR /galaxy-central/

ADD ./dependency_resolvers_conf.xml /etc/galaxy/dependency_resolvers_conf.xml
RUN sed -i -e 's@^#conda_prefix.*@conda_prefix = /tool_deps/_conda@' /etc/galaxy/galaxy.ini && \
    sed -i -e 's@^#conda_ensure_channels@conda_ensure_channels@' /etc/galaxy/galaxy.ini && \
    sed -i -e 's@^#use_cached_dependency_manager = False@use_cached_dependency_manager = True@' /etc/galaxy/galaxy.ini && \
    sed -i -e 's@^#dependency_resolvers_config_file.*@dependency_resolvers_config_file = /etc/galaxy/dependency_resolvers_conf.xml@' /etc/galaxy/galaxy.ini
RUN install-repository "-r ebee10be4297 --url https://toolshed.g2.bx.psu.edu/ -o nml --name sistr_cmd --tool-deps"
RUN /tool_deps/_conda/bin/conda config --add channels conda-forge && \
    /tool_deps/_conda/bin/conda config --add channels defaults && \
    /tool_deps/_conda/bin/conda config --add channels r && \
    /tool_deps/_conda/bin/conda config --add channels bioconda && \
    /tool_deps/_conda/bin/conda create -y --name __sistr_cmd@0.3.4 sistr_cmd=0.3.4 && \
    /tool_deps/_conda/bin/conda create -y --name __samtools@_uv_ samtools
