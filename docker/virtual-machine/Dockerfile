# Galaxy - IRIDA Galaxy Image

FROM phacnml/galaxy-irida-17.01:base

Label maintainer Aaron Petkau, aaron.petkau@phac-aspc.gc.ca

ENV GALAXY_CONFIG_BRAND IRIDA (0.21.0) Galaxy (17.01)

WORKDIR /galaxy-central

RUN install-repository "-r 4287dd541327 --url https://irida.corefacility.ca/galaxy-shed/ -o irida --name flash" && \
	install-repository "-r f9fc830fa47c --url https://irida.corefacility.ca/galaxy-shed/ -o irida --name filter_spades_repeats" && \
	install-repository "-r 51b76a5d78a5 --url https://irida.corefacility.ca/galaxy-shed/ -o irida --name assemblystats" && \
	install-repository "-r 35cb17bd8bf9 --url https://toolshed.g2.bx.psu.edu -o nml --name spades" && \
	install-repository "-r f5e44aad6498 --url https://toolshed.g2.bx.psu.edu -o crs4 --name prokka" && \
	install-repository "-r 9ea374bb0350 --url https://toolshed.g2.bx.psu.edu -o jjohnson --name regex_find_replace" && \
	install-repository "-r 7bc329e1ada4 --url https://irida.corefacility.ca/galaxy-shed/ -o irida --name bundle_collections" && \
	install-repository "-r c970d74729e9 --url https://irida.corefacility.ca/galaxy-shed/ -o irida --name combine_assembly_stats" && \
	install-repository "-r 57d5928f456e --url https://toolshed.g2.bx.psu.edu -o iuc --name shovill" && \
	install-repository "-r 5c8ff92e38a9 --url https://toolshed.g2.bx.psu.edu -o nml --name sistr_cmd" && \
	install-repository "-r 26df66c32861 --url https://toolshed.g2.bx.psu.edu -o nml --name refseq_masher" && \
	install-repository "-r b815081988b5 --url https://toolshed.g2.bx.psu.edu -o nml --name combine_tabular_collection" && \
	install-repository "-r a6cd59f35832 --url https://toolshed.g2.bx.psu.edu -o dfornika --name mentalist" && \
	install-repository "-r 4654c51dae72 --url https://toolshed.g2.bx.psu.edu -o nml --name bio_hansel" && \
	find /galaxy-central/tool_deps/ -iname '.git' | xargs -I {} rm -rf {}

RUN apt update && apt install --yes gnuplot && apt-get autoremove -y && apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Overwrite the tbl2asn that comes with prokka because it's expired!
RUN curl ftp://ftp.ncbi.nih.gov/toolbox/ncbi_tools/converters/by_program/tbl2asn/linux64.tbl2asn.gz > tbl2asn.gz && \
	gunzip --force tbl2asn.gz && \
	chmod +x tbl2asn && \
	find /export/tool_deps/ -executable -name tbl2asn -type f -exec cp tbl2asn '{}' \;

# Install sistr_cmd in conda
RUN /tool_deps/_conda/bin/conda config --add channels conda-forge && \
    /tool_deps/_conda/bin/conda config --add channels defaults && \
    /tool_deps/_conda/bin/conda config --add channels r && \
    /tool_deps/_conda/bin/conda config --add channels bioconda && \
    /tool_deps/_conda/bin/conda update conda && \
    /tool_deps/_conda/bin/conda create -y --name __shovill@0.9.0 shovill=0.9.0 && \
    /tool_deps/_conda/bin/conda install -y -c conda-forge --name __shovill@0.9.0 ncurses bzip2 && \
    /tool_deps/_conda/bin/conda create -y --name __sistr_cmd@1.0.2 sistr_cmd=1.0.2 && \
    /tool_deps/_conda/bin/conda create -y --name __mentalist@0.1.9 mentalist=0.1.9 && \
    /tool_deps/_conda/bin/conda create -y --name __biopython@_uv_ biopython && \
    /tool_deps/_conda/bin/conda create -y --name __r-base@_uv_ r-base && \
    /tool_deps/_conda/bin/conda create -y --name __bio_hansel@2.0.0 bio_hansel=2.0.0 && \
    /tool_deps/_conda/bin/conda create -y --name __refseq_masher@0.1.1 refseq_masher=0.1.1 && \
    /tool_deps/_conda/bin/conda create -y --name __samtools@_uv_ samtools && \
    /tool_deps/_conda/bin/conda clean -y -itps && \
    chown -R galaxy:galaxy /tool_deps/_conda

# Install some example mentalist databases
ADD data/tool-data.tar.gz /galaxy-central/

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
