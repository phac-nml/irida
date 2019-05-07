wait_for_tomcat() {
	echo "Waiting patiently for Tomcat to start..."
	while ! bash -c "curl http://localhost:8080 2> /dev/null > /dev/null" ; do
		echo -n '.'
		sleep 1
	done
	echo "Tomcat has (hopefully) started by now..."
}

# install java and apr
yum -y install epel-release
yum -y install apr tomcat java-1.8.0-openjdk-headless mariadb-server mariadb tomcat-native

mkdir -p /home/irida/data/{sequencing,reference,analysis,snapshot}
mkdir -p /etc/irida/analytics

chown -R tomcat:tomcat /home/irida/

cd /home/irida
curl --insecure -O https://irida.corefacility.ca/downloads/webapp/irida-latest.war
ln -s /home/irida/irida-latest.war /var/lib/tomcat/webapps/irida.war

curl --insecure -O https://irida.corefacility.ca/documentation/administrator/web/config/irida.conf
ln -s /home/irida/irida.conf /etc/irida/irida.conf

curl --insecure -O https://irida.corefacility.ca/documentation/administrator/web/config/web.conf
ln -s /home/irida/web.conf /etc/irida/web.conf

sed -i 's_server.base.url=.*_server.base.url=http://localhost:48888/irida/_' /etc/irida/web.conf

## Set up the directories in /etc/irida/irida.conf

sed -i 's_sequence.file.base.directory=.*_sequence.file.base.directory=/home/irida/data/sequencing_' /etc/irida/irida.conf
sed -i 's_reference.file.base.directory=.*_reference.file.base.directory=/home/irida/data/reference_' /etc/irida/irida.conf
sed -i 's_output.file.base.directory=.*_output.file.base.directory=/home/irida/data/analysis_' /etc/irida/irida.conf
sed -i 's_snapshot.file.base.directory=.*_snapshot.file.base.directory=/home/irida/data/snapshot_' /etc/irida/irida.conf

## Set up a local database and point /etc/irida/irida.conf at that database
systemctl enable mariadb
systemctl start mariadb
echo "grant all privileges on irida.* to 'irida'@'localhost' identified by 'irida';" | mysql -u root
echo "create database irida;" | mysql -u root

sed -i 's_jdbc.url=.*_jdbc.url=jdbc:mysql://localhost:3306/irida_' /etc/irida/irida.conf
sed -i 's_jdbc.username=.*_jdbc.username=irida_' /etc/irida/irida.conf
sed -i 's_jdbc.password=.*_jdbc.password=irida_' /etc/irida/irida.conf

cat >> /etc/tomcat/tomcat.conf <<EOF
JAVA_OPTS="-Dspring.profiles.active=prod"
EOF

systemctl enable tomcat
systemctl enable mariadb
systemctl start tomcat

wait_for_tomcat

systemctl stop tomcat
systemctl stop mariadb

## Set up pass-thru for port 8080 (tomcat default) to port 80, and allow traffic through port 80:

firewall-cmd --permanent --zone=public --add-service=http
firewall-cmd --permanent --zone=public --add-forward-port=port=80:toport=8080:proto=tcp
