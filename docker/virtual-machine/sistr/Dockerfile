# Galaxy - Irida image with SISTR web tool installed

FROM apetkau/galaxy-irida-16.10:0.15.0

MAINTAINER Aaron Petkau, aaron.petkau@phac-aspc.gc.ca

ENV GALAXY_CONFIG_BRAND IRIDA Galaxy

WORKDIR /galaxy-central/

RUN sed -i -e 's@^</tool_sheds>@  <tool_shed name="Test Toolshed" url="https://testtoolshed.g2.bx.psu.edu/"/>\n</tool_sheds>@' /etc/galaxy/tool_sheds_conf.xml
RUN install-repository "-r 84560b3e1777 --url https://testtoolshed.g2.bx.psu.edu/ -o peterk87 --name sistr_web_analyze"
RUN bash -c "source .venv/bin/activate && pip install -r /shed_tools/testtoolshed.g2.bx.psu.edu/repos/peterk87/sistr_web_analyze/84560b3e1777/sistr_web_analyze/sistr_web_analyze/requirements.txt"
