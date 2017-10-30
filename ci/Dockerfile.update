FROM docker.corefacility.ca:5000/irida-testing

WORKDIR /app
RUN cd irida; git pull; cd lib; ./install-libs.sh; cd ..; mvn dependency:copy-dependencies

#Download google chrome and install so we can run chromedriver tests
RUN rm google-chrome-stable_current_amd64.deb; wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN dpkg -i google-chrome-stable_current_amd64.deb; exit 0
RUN apt update
RUN apt install -qyy -f
