cat > /etc/yum.repos.d/docker.repo <<EOF
[dockerrepo]
name=Docker Repository
baseurl=https://yum.dockerproject.org/repo/main/centos/\$releasever/
enabled=1
gpgcheck=1
gpgkey=https://yum.dockerproject.org/gpg
EOF

# install docker so that we can pull down the IRIDA Galaxy Docker container:
yum -y install docker-engine
systemctl enable docker
systemctl start docker

# run the galaxy container, --restart=always makes sure it starts up on boot
docker run --restart=always -d -p 9090:80 -v /home/irida/data/sequencing:/home/irida/data/sequencing fbristow/irida-galaxy-docker

# configure IRIDA to connect to the Galaxy Docker container:
sed -i 's_galaxy.execution.url=.*_galaxy.execution.url=http://localhost:9090/_' /etc/irida/irida.conf
sed -i "s@galaxy.execution.apiKey=.*@galaxy.execution.apiKey=admin@" /etc/irida/irida.conf
sed -i 's_galaxy.execution.email=.*_galaxy.execution.email=admin@galaxy.org_' /etc/irida/irida.conf

# Allow external access to port 9090 (yay multi-level port forwarding!):
firewall-cmd --permanent --zone=public --add-port=9090/tcp
