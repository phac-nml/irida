wait_for_galaxy() {
	echo "Waiting patiently for Galaxy to start..."
	# sleep 10
	while ! bash -c "echo 2> /dev/null > /dev/tcp/localhost/9090" ; do
		echo -n '.'
		sleep 1
	done
	echo "Galaxy has (hopefully) started by now..."
}

# install tools that we need to build and run galaxy:
yum -y install epel-release
yum -y install mercurial pwgen python zlib-devel ncurses-devel tcsh db4-devel expat-devel java-1.8.0-openjdk-headless python-pip perl-App-cpanminus gnuplot libyaml-devel python-devel cmake
yum -y groupinstall "Development Tools"

# install perl dependencies (force because bioperl fails spuriously)
cpanm --force Clone Parallel::ForkManager http://search.cpan.org/CPAN/authors/id/C/CJ/CJFIELDS/BioPerl-1.6.901.tar.gz Time::Piece XML::Simple Data::Dumper
pip install bioblend

# prepare the directories and check out galaxy
mkdir -p /opt/irida/galaxy/{shed_tools,tool_dependencies,install}

useradd --no-create-home --system galaxy-irida --home-dir /opt/irida/galaxy/
chown -R galaxy-irida /opt/irida/galaxy/

function config_galaxy () {
	cd /opt/irida/galaxy/install
	curl -L -O http://downloads.sourceforge.net/project/mummer/mummer/3.23/MUMmer3.23.tar.gz
	tar xf MUMmer3.23.tar.gz
	cd MUMmer3.23
	make

	cat >> /opt/irida/galaxy/env.sh <<EOF
PATH=/opt/irida/galaxy/install/MUMmer3.23:\$PATH
EOF

	cd /opt/irida/galaxy/install/
	curl -L -O http://downloads.sourceforge.net/project/samtools/samtools/0.1.18/samtools-0.1.18.tar.bz2
	tar xf samtools-0.1.18.tar.bz2
	cd samtools-0.1.18
	make

	cat >> /opt/irida/galaxy/env.sh <<EOF
PATH=/opt/irida/galaxy/install/samtools-0.1.18:/opt/irida/galaxy/install/samtools-0.1.18/bcftools:\$PATH
EOF

	cd /opt/irida/galaxy/

	hg clone https://bitbucket.org/apetkau/galaxy-dist
	cd galaxy-dist
	hg update b065a7a422d72c5436ba62bfc6d831a9df82a79f

	sh scripts/common_startup.sh 2>&1 | tee common_startup.log

	cp config/galaxy.ini.sample config/galaxy.ini
	cp config/tool_sheds_conf.xml.sample config/tool_sheds_conf.xml

	# begin configuring galaxy

	## config.ini
        sed -i 's_#host.*_host = 0.0.0.0_' /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	sed -i 's_#port.*_port = 9090_' /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	# use 127.0.0.1 instead of localhost; localhost tries to connect over a socket, 127.0.0.1 uses tcp
	sed -i 's_#database\_connection.*_database\_connection = mysql://irida:irida@127.0.0.1/galaxy\_irida_' /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	sed -i 's/#allow_library_path_paste.*/allow_library_path_paste = True/' /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	sed -i 's/#admin_users.*/admin_users = admin@localhost.localdomain, workflow@localhost.localdomain/' /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	sed -i 's_debug = True_debug = False_' /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	sed -i 's/use_interactive = True/use_interactive = False/' /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	sed -i 's_^filter-with = gzip_#&_' /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	sed -i 's@#tool_dependency_dir.*@tool_dependency_dir = /opt/irida/galaxy/tool_dependencies@' /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	sed -i 's/#\(tool_sheds_config_file.*\)/\1/' /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	SECRET=$(pwgen --secure -N 1 56)
	sed -i "s/#id_secret.*/id_secret = $SECRET/" /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	MASTER_API_KEY=$(pwgen --secure -N 1 40)
	sed -i "s/#master_api_key.*/master_api_key = $MASTER_API_KEY/" /opt/irida/galaxy/galaxy-dist/config/galaxy.ini
	sed -i 's@#environment_setup_file = None@environment_setup_file = /opt/irida/galaxy/env.sh@' /opt/irida/galaxy/galaxy-dist/config/galaxy.ini

	## tool_sheds_conf.xml
	sed -i 's@</tool_sheds>@    <tool_shed name="IRIDA tool shed" url="https://irida.corefacility.ca/galaxy-shed/"/>\n</tool_sheds>@' /opt/irida/galaxy/galaxy-dist/config/tool_sheds_conf.xml
}

export -f config_galaxy
su galaxy-irida -c 'config_galaxy'

systemctl start mariadb
echo "grant all privileges on galaxy_irida.* to 'irida'@'localhost' identified by 'irida';" | mysql -u root
echo "create database galaxy_irida;" | mysql -u root

# Write out a systemd startup script for galaxy
cat > /etc/systemd/system/galaxy.service <<EOF
[Unit]
Description=Galaxy workflow execution manager
Requires=mariadb.service
After=mariadb.service
EnvironmentFile=/opt/irida/galaxy/env.sh

[Service]
ExecStart=/opt/irida/galaxy/galaxy-dist/run.sh
Type=Simple
User=galaxy-irida

[Install]
WantedBy=multi-user.target
EOF

## startup galaxy for the first time
systemctl enable galaxy
systemctl start galaxy
wait_for_galaxy

mv /tmp/workflows /opt/irida/galaxy/install
mv /tmp/install-workflow-tools.py /opt/irida/galaxy/install

MASTER_API_KEY=$(grep master_api_key /opt/irida/galaxy/galaxy-dist/config/galaxy.ini | awk '{print $3}')

cd /opt/irida/galaxy/install
python install-workflow-tools.py --pipeline-xml-dir workflows/ --master-api-key $MASTER_API_KEY --master-api-url http://localhost:9090/api --galaxy-admin-user admin@localhost.localdomain --galaxy-admin-pass adminpassword --galaxy-workflow-user workflow@localhost.localdomain --galaxy-workflow-pass workflowpassword | tee install-workflow-tools.log

API_KEY=$(grep 'Galaxy API key created for user' install-workflow-tools.log | perl -pe 's/.*\[([^\]]+)\].*/\1/')

sed -i 's_galaxy.execution.url=.*_galaxy.execution.url=http://localhost:9090/_' /etc/irida/irida.conf
sed -i "s@galaxy.execution.apiKey=.*@galaxy.execution.apiKey=$API_KEY@" /etc/irida/irida.conf
sed -i 's_galaxy.execution.email=.*_galaxy.execution.email=workflow@localhost.localdomain_' /etc/irida/irida.conf

systemctl stop galaxy
systemctl stop mariadb

firewall-cmd --permanent --zone=public --add-port=9090/tcp
