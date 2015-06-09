# install java and apr
yum -y install apr tomcat java-1.8.0-openjdk-headless mariadb-server mariadb-client

mkdir -p /opt/irida/data/{sequencing,reference,analysis,remote}
mkdir -p /etc/irida/analytics

chown -R tomcat:tomcat /opt/irida/

cd /opt/irida
curl -O https://irida.corefacility.ca/distributables/irida-latest.war
ln -s /opt/irida/irida-latest.war /var/lib/tomcat/webapps/irida.war

curl -O https://irida.corefacility.ca/documentation/administrator/web/config/irida.conf
ln -s /opt/irida/irida.conf /etc/irida/irida.conf

curl -O https://irida.corefacility.ca/documentation/administrator/web/config/web.conf
ln -s /opt/irida/web.conf /etc/irida/web.conf

## Set up the directories in /etc/irida/irida.conf

sed -i 's_sequence.file.base.directory=.*_sequence.file.base.directory=/opt/irida/data/sequencing_' /etc/irida/irida.conf
sed -i 's_reference.file.base.directory=.*_reference.file.base.directory=/opt/irida/data/reference_' /etc/irida/irida.conf
sed -i 's_output.file.base.directory=.*_output.file.base.directory=/opt/irida/data/analysis_' /etc/irida/irida.conf
sed -i 's_remote.file.temporary.directory=.*_remote.file.temporary.directory=/opt/irida/data/remote_' /etc/irida/irida.conf

## Set up a local database and point /etc/irida/irida.conf at that database
systemctl enable mariadb
systemctl start mariadb
echo "grant all privileges on irida.* to 'irida'@'localhost' identified by 'irida';" | mysql -u root
echo "create database irida;" | mysql -u root

sed -i 's_jdbc.url=.*_jdbc.url=jdbc:mysql://localhost:3306/irida_' /etc/irida/irida.conf
sed -i 's_jdbc.username=.*_jdbc.username=irida_' /etc/irida/irida.conf
sed -i 's_jdbc.password=.*_jdbc.password=irida_' /etc/irida/irida.conf

systemctl enable tomcat
systemctl start tomcat
systemctl stop tomcat
systemctl stop mariadb

## Set up pass-thru for port 8080 (tomcat default) to port 80, and allow traffic through port 80:

firewall-cmd --permanent --zone=public --add-service=http
firewall-cmd --permanent --zone=public --add-forward-port=port=80:toport=8080:proto=tcp
