# Galaxy - Irida Integration Testing Image
#
#

FROM apetkau/snvphyl-galaxy-1.0

MAINTAINER Aaron Petkau, aaron.petkau@phac-aspc.gc.ca

ENV GALAXY_CONFIG_BRAND IRIDA Galaxy

WORKDIR /galaxy-central

RUN cpan App::cpanminus && rm -rf ~/.cpan
RUN cpanm Time::Piece XML::Simple Data::Dumper Bio::SeqIO && rm -rf ~/.cpanm

RUN sed -i -e 's/#admin_users = None/admin_users = admin@galaxy.org,workflowUser@irida.corefacility.ca/' /etc/galaxy/galaxy.ini && \
	sed -i -e 's/#allow_library_path_paste = False/allow_library_path_paste = True/' /etc/galaxy/galaxy.ini && \
	sed -i -e 's/force_beta_workflow_scheduled_for_collections=True/force_beta_workflow_scheduled_for_collections=False/' /etc/galaxy/galaxy.ini

# Add spades and prokka to the image:
RUN install-repository "-r 4287dd541327 --url https://irida.corefacility.ca/galaxy-shed/ -o irida --name flash" && \
	install-repository "-r f9fc830fa47c --url https://irida.corefacility.ca/galaxy-shed/ -o irida --name filter_spades_repeats" && \
	install-repository "-r 51b76a5d78a5 --url https://irida.corefacility.ca/galaxy-shed/ -o irida --name assemblystats" && \
	install-repository "-r 21734680d921 --url https://toolshed.g2.bx.psu.edu -o lionelguy --name spades" && \
	install-repository "-r f5e44aad6498 --url https://toolshed.g2.bx.psu.edu -o crs4 --name prokka" && \
	install-repository "-r 9ea374bb0350 --url https://toolshed.g2.bx.psu.edu -o jjohnson --name regex_find_replace" && \
	install-repository "-r 7bc329e1ada4 --url https://irida.corefacility.ca/galaxy-shed/ -o irida --name bundle_collections" && \
	install-repository "-r c970d74729e9 --url https://irida.corefacility.ca/galaxy-shed/ -o irida --name combine_assembly_stats" && \
	find /galaxy-central/tool_deps/ -iname '.git' | xargs -I {} rm -rf {}

RUN apt update && apt install --yes gnuplot && apt-get autoremove -y && apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Overwrite the tbl2asn that comes with prokka because it's expired!
RUN curl ftp://ftp.ncbi.nih.gov/toolbox/ncbi_tools/converters/by_program/tbl2asn/linux64.tbl2asn.gz > tbl2asn.gz && \
	gunzip --force tbl2asn.gz && \
	chmod +x tbl2asn && \
	find tool_deps/ -executable -name tbl2asn -type f -exec cp tbl2asn '{}' \;

# Expose port 80 (webserver), 21 (FTP server), 8800 (Proxy)
EXPOSE :80
EXPOSE :21
EXPOSE :8800

# Autostart script that is invoked during container start
CMD ["/usr/bin/startup"]
