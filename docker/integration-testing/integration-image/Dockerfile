FROM maven:3.5.0-jdk-8

WORKDIR /app

ADD . /app

RUN cp settings.xml /usr/share/maven/conf

RUN echo '{ "allow_root": true }' > /root/.bowerrc

RUN git clone https://github.com/phac-nml/irida.git

RUN cd irida/lib; ./install-libs.sh; cd ..; mvn dependency:copy-dependencies

RUN apt update

RUN apt install -qyy xvfb build-essential rubygems ruby-dev git mysql-client

RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb

RUN dpkg -i google-chrome-stable_current_amd64.deb; exit 0

RUN apt update

RUN apt install -qyy -f


RUN gem install --force jekyll pygments.rb
