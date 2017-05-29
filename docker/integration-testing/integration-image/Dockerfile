FROM maven:3.5.0-jdk-8

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
ADD . /app

RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb

RUN dpkg -i google-chrome-stable_current_amd64.deb; exit 0

RUN apt update

RUN apt install -qyy -f

RUN apt install -qyy xvfb

RUN echo '{ "allow_root": true }' > /root/.bowerrc
