# Galaxy - Irida Integration Testing Image
#
#

FROM phacnml/galaxy-irida-16.10:0.16.0

LABEL maintainer "aaron.petkau@phac-aspc.gc.ca"

RUN sed -i -e 's@^#conda_prefix.*@conda_prefix = /tool_deps/_conda@' /etc/galaxy/galaxy.ini && \
    sed -i -e 's@^#conda_ensure_channels@conda_ensure_channels@' /etc/galaxy/galaxy.ini && \
    sed -i -e 's@^#use_cached_dependency_manager = False@use_cached_dependency_manager = True@' /etc/galaxy/galaxy.ini
RUN install-repository "-r 9d7e381dfa5a --url https://toolshed.g2.bx.psu.edu/ -o nml --name sistr_cmd --tool-deps"
RUN /tool_deps/_conda/bin/conda config --add channels conda-forge && \
    /tool_deps/_conda/bin/conda config --add channels defaults && \
    /tool_deps/_conda/bin/conda config --add channels r && \
    /tool_deps/_conda/bin/conda config --add channels bioconda && \
    /tool_deps/_conda/bin/conda create -y --name __sistr_cmd@0.3.4 sistr_cmd=0.3.4 && \
    /tool_deps/_conda/bin/conda create -y --name __samtools@_uv_ samtools && \
    /tool_deps/_conda/bin/conda clean -y -itps
