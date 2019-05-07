# Galaxy - IRIDA Galaxy Image

FROM phacnml/galaxy-irida-18.09:base

ADD ./data/tools-list.yml $GALAXY_ROOT/irida-tools.yml
RUN install-tools $GALAXY_ROOT/irida-tools.yml

# Install some example mentalist databases
ADD ./data/tool-data.tar.gz /galaxy-central/

# Fix up permissions/mentalist erroring out on first execution by running mentalist during build
RUN echo -e "@\nATCG\n+\nIIII" > /tmp/file_1.fastq && \
	echo -e "@\nATCG\n+\nIIII" > /tmp/file_2.fastq && \
	bash -c "source /tool_deps/_conda/bin/activate /tool_deps/_conda/envs/__mentalist\@0.1.9/ && mentalist call -o /tmp/mentalist-test -s x --db /galaxy-central/tool-data/mentalist_databases/salmonella_enterica_pubmlst_k31_2018-04-04/salmonella_enterica_pubmlst_k31_2018-04-04.jld /tmp/file_1.fastq /tmp/file_2.fastq" && \
	chown -R galaxy:galaxy /galaxy-central/tool-data/ && \
	chown -R galaxy:galaxy /tool_deps/_conda/ && \
	rm -rf /tmp/file_*.fastq

# Expose port 80 (webserver), 21 (FTP server), 8800 (Proxy)
EXPOSE :80
EXPOSE :21
EXPOSE :8800

# Autostart script that is invoked during container start
CMD ["/usr/bin/startup"]
