wait_for_galaxy() {
	echo "Waiting patiently for Galaxy to start..."
	# sleep 10
	while ! bash -c "curl http://localhost:9090 2> /dev/null > /dev/null" ; do
		echo -n '.'
		sleep 1
	done
	echo "Galaxy has (hopefully) started by now..."
}

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
mkdir -p /home/vagrant/docker
# switch to `devicemapper` for docker as I found the that the IRIDA Galaxy docker image failed to run with the default storage driver (overlay)
sed -i -e 's@ExecStart=/usr/bin/dockerd@ExecStart=/usr/bin/dockerd --storage-driver=devicemapper --storage-opt dm.basesize=40G -g /home/vagrant/docker@' /usr/lib/systemd/system/docker.service
systemctl enable docker
systemctl start docker

# run the galaxy container, --restart=always makes sure it starts up on boot
mkdir -p /home/irida/data/galaxy-export
docker pull phacnml/galaxy-irida-18.09
docker run --name galaxy -d -p 9090:80 -v /home/irida/data/galaxy-export/:/export/ -v /home/irida/data/sequencing:/home/irida/data/sequencing phacnml/galaxy-irida-18.09

# wait for galaxy to succeed starting up for the first time, so we don't have to wait for postgres to start up next time
wait_for_galaxy
docker kill galaxy
docker rm galaxy

cat > /etc/systemd/system/galaxy.service <<EOF
[Unit]
Description=Galaxy
Requires=docker.service
After=docker.service

[Service]
ExecStartPre=-/usr/bin/docker rm --force galaxy
ExecStart=/usr/bin/docker run --name galaxy -d -p 9090:80 -v /home/irida/data/galaxy-export/:/export/ -v /home/irida/data/sequencing:/home/irida/data/sequencing phacnml/galaxy-irida-18.09

[Install]
WantedBy=multi-user.target
EOF

systemctl enable galaxy

# configure IRIDA to connect to the Galaxy Docker container:
sed -i 's_galaxy.execution.url=.*_galaxy.execution.url=http://localhost:9090/_' /etc/irida/irida.conf
sed -i "s@galaxy.execution.apiKey=.*@galaxy.execution.apiKey=admin@" /etc/irida/irida.conf
sed -i 's_galaxy.execution.email=.*_galaxy.execution.email=admin@galaxy.org_' /etc/irida/irida.conf

# Allow external access to port 9090 (yay multi-level port forwarding!):
firewall-cmd --permanent --zone=public --add-port=9090/tcp
