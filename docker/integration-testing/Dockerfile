# Galaxy - Irida Integration Testing Image
#
#

FROM apetkau/galaxy-irida-15.10:1.0

MAINTAINER Aaron Petkau, aaron.petkau@gmail.com

ENV GALAXY_CONFIG_BRAND Irida Integration Testing

WORKDIR /galaxy-central

ENV GALAXY_TEST_TOOL_CONF /galaxy-central/config/tool_conf.xml.sample

# Add test tool and tool shed configs
ADD ./galaxy/tool_conf_irida.xml /galaxy-central/config/tool_conf.xml.sample
ADD ./galaxy/tool_sheds_conf.xml /etc/galaxy/tool_sheds_conf.xml

RUN sed -i -e 's/#admin_users = None/admin_users = admin@galaxy.org,workflowUser@irida.corefacility.ca/' /etc/galaxy/galaxy.ini
RUN sed -i -e 's/#allow_library_path_paste = False/allow_library_path_paste = True/' /etc/galaxy/galaxy.ini
RUN sed -i -e 's/force_beta_workflow_scheduled_for_collections=True/force_beta_workflow_scheduled_for_collections=False/' /etc/galaxy/galaxy.ini

# Add custom Irida tools to Galaxy
ADD ./galaxy/assembly_annotation_pipeline_outputs.xml /galaxy-central/tools/irida/assembly_annotation_pipeline_outputs.xml
ADD ./galaxy/collection_list_paired.xml /galaxy-central/tools/irida/collection_list_paired.xml
ADD ./galaxy/core_pipeline_outputs.xml /galaxy-central/tools/irida/core_pipeline_outputs.xml
ADD ./galaxy/core_pipeline_outputs_paired.xml /galaxy-central/tools/irida/core_pipeline_outputs_paired.xml
ADD ./galaxy/core_pipeline_outputs_paired_with_multi_level_parameters.xml /galaxy-central/tools/irida/core_pipeline_outputs_paired_with_multi_level_parameters.xml
ADD ./galaxy/core_pipeline_outputs_paired_with_parameters.xml /galaxy-central/tools/irida/core_pipeline_outputs_paired_with_parameters.xml
ADD ./galaxy/core_pipeline_outputs_single_paired.xml /galaxy-central/tools/irida/core_pipeline_outputs_single_paired.xml
ADD ./galaxy/sleep.xml /galaxy-central/tools/irida/sleep.xml

# Mark folders as imported from the host.
VOLUME ["/export/", "/data/", "/var/lib/docker"]

# Expose port 80 (webserver), 21 (FTP server), 8800 (Proxy)
EXPOSE :80
EXPOSE :21
EXPOSE :8800
EXPOSE :9001

# Autostart script that is invoked during container start
CMD ["/usr/bin/startup"]
