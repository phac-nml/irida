FROM maven:3.5.0-jdk-8

# Mount the CWD
WORKDIR /app
ADD . /app

#Copy the maven settings file into the config directory
RUN cp settings.xml /usr/share/maven/conf

# Allow bower to run as root since the docker build is done as root
RUN echo '{ "allow_root": true }' > /root/.bowerrc

#Clone a recent copy of IRIDA and install the maven dependencies to speed up builds
RUN git clone https://github.com/phac-nml/irida.git
RUN cd irida/lib; ./install-libs.sh; cd ..; mvn dependency:copy-dependencies

#Download google chrome and install so we can run chromedriver tests
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN dpkg -i google-chrome-stable_current_amd64.deb; exit 0
RUN apt update
RUN apt install -qyy -f

# install other required packages for tests
RUN apt install -qyy xvfb build-essential rubygems ruby-dev git mysql-client
RUN gem install --force jekyll pygments.rb
