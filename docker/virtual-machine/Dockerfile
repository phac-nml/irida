# Galaxy - IRIDA Galaxy Image

FROM phacnml/galaxy-irida-20.09:base

ADD ./data/tools-list.yml $GALAXY_ROOT/irida-tools.yml

# Install tools
RUN install-tools $GALAXY_ROOT/irida-tools.yml \
      && /tool_deps/_conda/bin/conda install --name __sistr_cmd@1.0.2 pandas==1.0.5 -y \
      && chown -R galaxy:galaxy /tool_deps/_conda/envs/__sistr_cmd@1.0.2 \
      && /tool_deps/_conda/bin/conda clean --packages -t -i \
      && rm -rf /tmp/* /root/.cache/ /var/cache/* $GALAXY_ROOT/client/node_modules/ $GALAXY_VIRTUAL_ENV/src/ /home/galaxy/.cache/ /home/galaxy/.npm

# Fix up shovill memory
RUN sed -i -e 's@\(<destination .*\)@\1\n\t<env id="SHOVILL_RAM">4</env>@' /etc/galaxy/job_conf.xml

# Fix up prokka issues (install 'less' and 'libidn11')
RUN apt-get update -y && apt-get install less libidn11 \
      && apt-get autoremove -y && apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* && rm -rf ~/.cache/ \
      && rm -rf /tmp/* /root/.cache/ /var/cache/*

# Install some example mentalist databases
ADD --chown=galaxy:galaxy ./data/tool-data.tar.gz /galaxy-central/

# Update mentalist environment
ADD ./data/mentalist.env /tmp/mentalist.env
RUN /tool_deps/_conda/bin/conda remove --name __mentalist@0.1.9 --all -y \
      && /tool_deps/_conda/bin/conda create -c defaults -c bioconda -c conda-forge --name __mentalist@0.1.9 --file /tmp/mentalist.env -y \
      && echo -e "@\nATCG\n+\nIIII" > /tmp/file_1.fastq \
      && echo -e "@\nATCG\n+\nIIII" > /tmp/file_2.fastq \
      && bash -c "source /tool_deps/_conda/bin/activate /tool_deps/_conda/envs/__mentalist\@0.1.9/ && mentalist call -o /tmp/mentalist-test -s x --db /galaxy-central/tool-data/mentalist_databases/salmonella_enterica_pubmlst_k31_2018-04-04/salmonella_enterica_pubmlst_k31_2018-04-04.jld /tmp/file_1.fastq /tmp/file_2.fastq" \
      && chown -R galaxy:galaxy /tool_deps/_conda/envs/__mentalist@0.1.9 \
      && /tool_deps/_conda/bin/conda create -c defaults -c bioconda -c conda-forge --name __r-base@3.4.1 r-base==3.4.1 libiconv -y \
      && chown -R galaxy:galaxy /tool_deps/_conda/envs/__r-base@3.4.1 \
      && /tool_deps/_conda/bin/conda create -c defaults -c bioconda -c conda-forge --name __biopython@1.70 biopython==1.70 -y \
      && chown -R galaxy:galaxy /tool_deps/_conda/envs/__biopython@1.70 \
      && /tool_deps/_conda/bin/conda clean --packages -t -i \
      && rm -rf /tmp/file_*.fastq

# I cannot get MentaLiST to install via the command-line like the other tools
# To get MentaLiST working you will have to install after Docker Galaxy is started.

# Expose port 80 (webserver), 21 (FTP server), 8800 (Proxy)
EXPOSE :80
EXPOSE :21
EXPOSE :8800

# Autostart script that is invoked during container start
CMD ["/usr/bin/startup"]
