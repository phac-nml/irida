# Galaxy - IRIDA Integration Testing Image

FROM phacnml/galaxy-irida-18.09:base 

# Add test tool configs
ADD ./galaxy/tool_conf_irida.xml /galaxy-central/config/tool_conf.xml

# Set up proper admin users
RUN sed -i -e 's/# *admin_users: .*/admin_users: admin@galaxy.org,workflowUser@irida.corefacility.ca/' /etc/galaxy/galaxy.yml

# Add custom Irida tools to Galaxy
ADD ./galaxy/assembly_annotation_pipeline_outputs.xml /galaxy-central/tools/irida/assembly_annotation_pipeline_outputs.xml
ADD ./galaxy/collection_list_paired.xml /galaxy-central/tools/irida/collection_list_paired.xml
ADD ./galaxy/core_pipeline_outputs.xml /galaxy-central/tools/irida/core_pipeline_outputs.xml
ADD ./galaxy/core_pipeline_outputs_paired.xml /galaxy-central/tools/irida/core_pipeline_outputs_paired.xml
ADD ./galaxy/core_pipeline_outputs_paired_with_multi_level_parameters.xml /galaxy-central/tools/irida/core_pipeline_outputs_paired_with_multi_level_parameters.xml
ADD ./galaxy/core_pipeline_outputs_paired_with_parameters.xml /galaxy-central/tools/irida/core_pipeline_outputs_paired_with_parameters.xml
ADD ./galaxy/core_pipeline_outputs_single_paired.xml /galaxy-central/tools/irida/core_pipeline_outputs_single_paired.xml
ADD ./galaxy/sleep.xml /galaxy-central/tools/irida/sleep.xml
